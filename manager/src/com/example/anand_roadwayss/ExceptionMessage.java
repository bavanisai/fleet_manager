package com.example.anand_roadwayss;

import android.content.Context;
import android.content.Intent;

public class ExceptionMessage {

    public static void exceptionLog(Context context, String className,
                                    String error) {

        Intent intent = new Intent(context, RemoteLogger.class);
        intent.putExtra("className", className);
        intent.putExtra("error", error);
        context.startService(intent);

    }
    }
