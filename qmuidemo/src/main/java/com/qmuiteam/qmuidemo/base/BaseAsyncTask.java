package com.qmuiteam.qmuidemo.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    protected Context context;
    private ProgressDialog dialog;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
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
