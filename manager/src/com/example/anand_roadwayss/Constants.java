package com.example.anand_roadwayss;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class Constants {

    public Constants() {

    }

    String registrationAuthKey = "regmnk";
    String ManagerApp = "MA";

    public String getRegistrationAuthKey() {
        return registrationAuthKey;
    }

    public String getAppType() {
        return ManagerApp;
    }

    public int getApplicationVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException ex)
        {
            ExceptionMessage.exceptionLog(context, this
                            .getClass().toString() + " " + "[getApplicationVersionCode()]",
                    ex.toString());
        }
        catch (Exception e)
        {
            ExceptionMessage.exceptionLog(context, this
                            .getClass().toString() + " " + "[getApplicationVersionCode()]",
                    e.toString());
        }
        return 0;
    }

    public String getApplicationVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException ex)
        {
            ExceptionMessage.exceptionLog(context, this
                            .getClass().toString() + " " + "[getApplicationVersionName()]",
                    ex.toString());
        }
        catch (Exception e)
        {
            ExceptionMessage.exceptionLog(context, this
                            .getClass().toString() + " " + "[getApplicationVersionName()]",
                    e.toString());
        }
        return "";
    }

}
