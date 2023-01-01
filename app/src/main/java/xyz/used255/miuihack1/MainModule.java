package xyz.used255.miuihack1;

import android.content.Context;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import xyz.used255.miuihack1.utils.Helpers;
import xyz.used255.miuihack1.utils.Helpers.MethodHook;
import xyz.used255.miuihack1.utils.ResourceHooks;

import static de.robv.android.xposed.XposedHelpers.findClass;


public class MainModule implements IXposedHookZygoteInit, IXposedHookLoadPackage {
    private ResourceHooks resHooks;

    public void initZygote(StartupParam startParam) {
        resHooks = new ResourceHooks();
    }

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) {
        String pkg = lpparam.packageName;

        MethodHook statInitHook = new MethodHook() {
            @Override
            protected void after(MethodHookParam param) throws Throwable {
                XposedHelpers.callStaticMethod(findClass("com.xiaomi.stat.MiStat", lpparam.classLoader), "setStatisticEnabled", false);
                Helpers.findAndHookMethodSilently("com.xiaomi.stat.MiStat", lpparam.classLoader, "setStatisticEnabled", boolean.class, XC_MethodReplacement.DO_NOTHING);
            }
        };
        if (!pkg.equals("android")) {
            Helpers.findAndHookMethodSilently("com.xiaomi.onetrack.OneTrack", lpparam.classLoader, "isDisable", XC_MethodReplacement.returnConstant(true));
            Helpers.findAndHookMethodSilently("com.xiaomi.stat.MiStat", lpparam.classLoader, "initialize", Context.class, String.class, String.class, boolean.class, statInitHook);
        }

        if (!pkg.equals("android")) {
            Helpers.findAndHookMethodSilently("com.xiaomi.stat.MiStat", lpparam.classLoader, "initialize", Context.class, String.class, String.class, boolean.class, String.class, statInitHook);
        }

        if (pkg.equals("com.android.contacts")
                || pkg.equals("com.android.incallui")
                || pkg.equals("com.miui.weather2")
                || pkg.equals("com.android.deskclock")
                || pkg.equals("com.android.thememanager")
                || pkg.equals("com.miui.yellowpage")
                || pkg.equals("com.miui.personalassistant")
                || pkg.equals("com.android.calendar")
                || pkg.equals("com.android.settings")
        ) {
            Class<?> classBuild = XposedHelpers.findClass("miui.os.Build", lpparam.classLoader);
            XposedHelpers.setStaticBooleanField(classBuild, "IS_INTERNATIONAL_BUILD", false);
            XposedHelpers.setStaticBooleanField(classBuild, "IS_GLOBAL_BUILD", false);
        }
        if (pkg.equals("com.android.calendar")) {
            resHooks.setObjectReplacement(pkg, "bool", "is_greater_china", true);
            resHooks.setObjectReplacement(pkg, "bool", "is_mainland_china", true);
        } else if (pkg.equals("com.android.thememanager")) {
            Helpers.findAndHookMethodSilently("com.android.thememanager.basemodule.ad.model.AdInfoResponse", lpparam.classLoader, "getAdInfo", boolean.class, XC_MethodReplacement.returnConstant(null));
            Helpers.findAndHookMethodSilently("com.android.thememanager.basemodule.ad.model.AdInfoResponse", lpparam.classLoader, "checkAndGetAdInfo", String.class, boolean.class, XC_MethodReplacement.returnConstant(null));
        } else if (pkg.equals("com.android.mms")) {
            Helpers.findAndHookMethod("miui.provider.ExtraTelephony", lpparam.classLoader, "getSmsURLScanResult",
                    Context.class, String.class, String.class, XC_MethodReplacement.returnConstant(0));
        }
    }
}