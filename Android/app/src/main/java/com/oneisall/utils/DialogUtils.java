package com.oneisall.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class DialogUtils {
    public static void showDialog(String message, int iconType, Context context, View viewToDelay){
        Log.i("dialog", "context:"+context);
        final QMUITipDialog tipDialog= new QMUITipDialog.Builder(context)
                .setIconType(iconType)
                .setTipWord(message)
                .create();
        tipDialog.show();
        viewToDelay.postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
            }
        }, 1000);
    }
    public static void showDialogFinish(String message, int iconType, Context context, View viewToDelay){
        Log.i("dialog", "context:"+context);
        final QMUITipDialog tipDialog= new QMUITipDialog.Builder(context)
                .setIconType(iconType)
                .setTipWord(message)
                .create();
        tipDialog.show();
        viewToDelay.postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
                ((AppCompatActivity)context).finish();
            }
        }, 1000);
    }

    public static void showDialog2s(String message, int iconType, Context context, View viewToDelay){
        Log.i("dialog", "context:"+context);
        final QMUITipDialog tipDialog= new QMUITipDialog.Builder(context)
                .setIconType(iconType)
                .setTipWord(message)
                .create();
        tipDialog.show();
        viewToDelay.postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
            }
        }, 2000);
    }

}
