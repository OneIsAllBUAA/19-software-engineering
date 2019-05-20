package com.oneisall.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public abstract class BaseAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    protected Context context;
    private ProgressDialog dialog;
    private static final String TAG = "BaseAsyncTask";
    @Override
    protected void onPreExecute() {
            Log.i(TAG, "onPreExecute: "+context );
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            Log.i(TAG,"new progress dialog");
            dialog.setMessage("载入中...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(true);
            dialog.show();
    }

    public BaseAsyncTask(Context context){
        this.context = context;
    }
    @Override
    protected void onPostExecute(Result o) {
        super.onPostExecute(o);
        dialog.dismiss();
    }
}
