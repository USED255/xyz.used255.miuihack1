package xyz.used255.miuihack1.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

@SuppressWarnings("WeakerAccess")
public class Helpers {

    public static final String modulePkg = "xyz.used255.miuihack1";
    @SuppressLint("StaticFieldLeak")
    public static Context mModuleContext = null;

    public static synchronized Context getModuleContext(Context context) throws Throwable {
        return getModuleContext(context, null);
    }

    public static synchronized Context getModuleContext(Context context, Configuration config) throws Throwable {
        if (mModuleContext == null)
            mModuleContext = context.createPackageContext(modulePkg, Context.CONTEXT_IGNORE_SECURITY)
                    .createDeviceProtectedStorageContext();
        return config == null ? mModuleContext : mModuleContext.createConfigurationContext(config);
    }

    public static synchronized Resources getModuleRes(Context context) throws Throwable {
        Configuration config = context.getResources().getConfiguration();
        Context moduleContext = getModuleContext(context);
        return (config == null ? moduleContext.getResources()
                : moduleContext.createConfigurationContext(config).getResources());
    }

    private static String getCallerMethod() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement el : stackTrace)
            if (el != null && el.getClassName().startsWith(modulePkg + ".mods"))
                return el.getMethodName();
        return stackTrace[4].getMethodName();
    }

    public static void log(String mod, String line) {
        XposedBridge.log("[CustoMIUIzer][" + mod + "] " + line);
    }

    public static void findAndHookMethod(Class<?> clazz, String methodName, Object... parameterTypesAndCallback) {
        try {
            XposedHelpers.findAndHookMethod(clazz, methodName, parameterTypesAndCallback);
        } catch (Throwable t) {
            log(getCallerMethod(), "Failed to hook " + methodName + " method in " + clazz.getCanonicalName());
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static Context findContext() {
        Context context = null;
        try {
            context = (Application) XposedHelpers.callStaticMethod(
                    XposedHelpers.findClass("android.app.ActivityThread", null), "currentApplication");
            if (context == null) {
                Object currentActivityThread = XposedHelpers.callStaticMethod(
                        XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread");
                if (currentActivityThread != null)
                    context = (Context) XposedHelpers.callMethod(currentActivityThread, "getSystemContext");
            }
        } catch (Throwable ignore) {
        }
        return context;
    }

    public static class MethodHook extends XC_MethodHook {
        public MethodHook() {
            super();
        }

        protected void before(MethodHookParam param) throws Throwable {
        }

        protected void after() throws Throwable {
        }

        @Override
        public final void beforeHookedMethod(MethodHookParam param) throws Throwable {
            try {
                this.before(param);
            } catch (Throwable t) {
                XposedBridge.log(t);
            }
        }

        @Override
        public final void afterHookedMethod(MethodHookParam param) throws Throwable {
            try {
                this.after();
            } catch (Throwable t) {
                XposedBridge.log(t);
            }
        }
    }

}