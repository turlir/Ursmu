package ru.ursmu.application.Activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public final class ApplicationVersionHelper
{
    public static final String APP_VERSION_PREFS = "application_version";

    public static boolean isApplicationVersionCodeEqualsSavedApplicationVersionCode(Context context)
    {
        return getApplicationVersionCode(context) == getApplicationVersionCodeFromPreferences(context);
    }

    public static int getApplicationVersionCode(Context context)
    {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo;
        int applicationVersion = 1;
        try
        {
            packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            applicationVersion = packageInfo.versionCode;
        }
        catch (NameNotFoundException ignored)
        {
        }
        return applicationVersion;
    }

    public static int getApplicationVersionCodeFromPreferences(Context context)
    {
        return context.getSharedPreferences(APP_VERSION_PREFS, Context.MODE_PRIVATE).getInt("application_version_code", 0);
    }

    public static void putCurrentPackageVersionInPreferences(Context context)
    {
        context.getSharedPreferences(APP_VERSION_PREFS, Context.MODE_PRIVATE).edit().putInt("application_version_code", getApplicationVersionCode(context)).commit();
    }
}
