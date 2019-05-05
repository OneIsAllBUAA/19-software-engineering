package com.qmuiteam.qmuidemo.utils;

import android.content.Context;
import android.view.View;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class DialogUtils {
    public static void showDialog(String message, int iconType, Context context, View viewToDelay){
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
}
