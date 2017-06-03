package com.shouduo.plant.view.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.shouduo.plant.R;
import com.shouduo.plant.esptouch.EspWifiAdminSimple;
import com.shouduo.plant.esptouch.EsptouchTask;
import com.shouduo.plant.esptouch.IEsptouchResult;
import com.shouduo.plant.esptouch.IEsptouchTask;
import com.shouduo.plant.esptouch.task.__IEsptouchTask;

/**
 * Created by 刘亨俊 on 17.3.28.
 */

public class SmartConfigDialog extends BaseDialogFragment
        implements View.OnClickListener, View.OnTouchListener, CompoundButton.OnCheckedChangeListener{

    //data
    private EspWifiAdminSimple wifiAdmin;

    //widget
    private ScrollView container;
    private View view;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    private TextView SSIDTextView;
    private EditText passwordEditText;
    private CheckBox showPasswordCheckBox;
    private CheckBox isSSIDHiddenCheckBox;

    private Button done;
    private Button cancel;

    /** <br> life cycle. */
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_smart_config, null, false);

        this.initWidget(view);
        this.initData();

        builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        alertDialog = builder.create();
        return alertDialog;
    }

    private void initData() {
        wifiAdmin = new EspWifiAdminSimple(getActivity());
        String apSSID = wifiAdmin.getWifiConnectedSsid();
        if (apSSID != null) {
            SSIDTextView.setText(" Connect to:  " + apSSID);
            done.setEnabled(true);
        } else {
            SSIDTextView.setText("Connect your phone to an available Wi-Fi network first !");
            SSIDTextView.setMaxLines(2);
            passwordEditText.setVisibility(View.GONE);
            showPasswordCheckBox.setVisibility(View.GONE);
            isSSIDHiddenCheckBox.setVisibility(View.GONE);
            done.setEnabled(false);
        }
    }

    private void initWidget(View view) {
        this.container = (ScrollView) view.findViewById(R.id.dialog_config_container);

        SSIDTextView = (TextView) view.findViewById(R.id.dialog_smart_config_ssid);

        passwordEditText = (EditText) view.findViewById(R.id.dialog_smart_config_password_edittext);
        passwordEditText.setOnTouchListener(this);

        showPasswordCheckBox = (CheckBox) view.findViewById(R.id.dialog_smart_config_show_password_checkbox);
        showPasswordCheckBox.setOnCheckedChangeListener(this);

        isSSIDHiddenCheckBox = (CheckBox) view.findViewById(R.id.dialog_smart_config_is_ssid_hidden_checkbox);
        isSSIDHiddenCheckBox.setOnCheckedChangeListener(this);

        done = (Button) view.findViewById(R.id.dialog_smart_config_done);
        done.setOnClickListener(this);

        cancel = (Button) view.findViewById(R.id.dialog_smart_config_cancel);
        cancel.setOnClickListener(this);
    }

    private void showSoftKeyboard() {
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                container.smoothScrollTo(0, container.getHeight());
            }
        }, 300);
    }

    private void hideSoftKeyboard() {
        View view = alertDialog.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.dialog_smart_config_password_edittext:
                    showSoftKeyboard();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.dialog_smart_config_show_password_checkbox:
                passwordEditText.setInputType(isChecked?
                        (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) :
                        (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
                passwordEditText.setSelection(passwordEditText.getText().length());
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_smart_config_cancel:
                dismiss();
                break;
            case R.id.dialog_smart_config_done:
                hideSoftKeyboard();

                String apSSID = wifiAdmin.getWifiConnectedSsid();
                String apPassword = passwordEditText.getText().toString();
                String apBSSID = wifiAdmin.getWifiConnectedBssid();
                Boolean isSSIDHidden = isSSIDHiddenCheckBox.isChecked();
                String isSSIDHiddenStr = "NO";
                if (isSSIDHidden) {
                    isSSIDHiddenStr = "YES";
                }
                new EsptouchAsyncTask2(handler).execute(apSSID, apBSSID, apPassword, isSSIDHiddenStr);
                break;
            default:
                break;
        }
    }

    private class EsptouchAsyncTask2 extends AsyncTask<String, Void, IEsptouchResult> {

        private ProgressDialog mProgressDialog;

        private IEsptouchTask mEsptouchTask;
        // without the lock, if the user tap confirm and cancel quickly enough,
        // the bug will arise. the reason is follows:
        // 0. task is starting created, but not finished
        // 1. the task is cancel for the task hasn't been created, it do nothing
        // 2. task is created
        // 3. Oops, the task should be cancelled, but it is running
        private final Object mLock = new Object();
        private Handler Task2Handler;
        private boolean isSuccessful = false;

        public EsptouchAsyncTask2(Handler handler){
            Task2Handler = handler;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
            mProgressDialog.setMessage("Pot is connecting, please wait for a minute...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    synchronized (mLock) {
                        if (__IEsptouchTask.DEBUG) {
//                            log.i(TAG, "progress dialog is canceled");
                        }
                        if (mEsptouchTask != null) {
                            mEsptouchTask.interrupt();
                        }
                    }
                }
            });
            mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    "Connecting...", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    if (isSuccessful) {
                                        alertDialog.dismiss();
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
            mProgressDialog.show();
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(false);

        }

        @Override
        protected IEsptouchResult doInBackground(String... params) {
            synchronized (mLock) {
                String apSsid = params[0];
                String apBssid = params[1];
                String apPassword = params[2];
                String isSsidHiddenStr = params[3];
                boolean isSsidHidden = false;
                if (isSsidHiddenStr.equals("YES")) {
                    isSsidHidden = true;
                }
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword,
                        isSsidHidden, getActivity(), Task2Handler);
            }
            IEsptouchResult result = mEsptouchTask.executeForResult();
            return result;
        }

        @Override
        protected void onPostExecute(IEsptouchResult result) {
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(true);
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(
                    "Confirm");
            // it is unnecessary at the moment, add here just to show how to use isCancelled()
            if (!result.isCancelled()) {
                if (result.isSuc()) {
                    isSuccessful = true;
                    mProgressDialog.setMessage("Connect successfully. \n"
                            + "Pot's IP Address is: \n"
                            + result.getInetAddress().getHostAddress());
                } else {
                    isSuccessful = false;
                    mProgressDialog.setMessage("Connect fail. \n"
                            + "Please hit the RESET button at the bottom of the pot and try again.");
                }
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
}
