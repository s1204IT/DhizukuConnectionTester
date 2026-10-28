package me.s1204.inspect.dhizuku;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.rosan.dhizuku.api.Dhizuku;
import com.rosan.dhizuku.api.DhizukuRequestPermissionListener;
import com.rosan.dhizuku.api.DhizukuUserServiceArgs;

import java.util.Arrays;

public class Tester extends Activity implements View.OnClickListener {

    private IUserService mUserService;
    private ServiceConnection mUserServiceConnection;
    private DhizukuUserServiceArgs mUserServiceArgs;

    private void makeText(String msg) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        mUserServiceArgs = new DhizukuUserServiceArgs(new ComponentName(this, UserService.class));
        mUserServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder iBinder) {
                mUserService = IUserService.Stub.asInterface(iBinder);
                makeText("UserService connected");
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mUserService = null;
                makeText("UserService disconnected");
            }
        };

        findViewById(R.id.btn_init).setOnClickListener(this);
        findViewById(R.id.btn_getVersionCode).setOnClickListener(this);
        findViewById(R.id.btn_getVersionName).setOnClickListener(this);
        findViewById(R.id.btn_getOwnerPackageName).setOnClickListener(this);
        findViewById(R.id.btn_getOwnerComponent).setOnClickListener(this);
        findViewById(R.id.btn_isPermissionGranted).setOnClickListener(this);
        findViewById(R.id.btn_requestPermission).setOnClickListener(this);
        findViewById(R.id.btn_startUserService).setOnClickListener(this);
        findViewById(R.id.btn_stopUserService).setOnClickListener(this);
        findViewById(R.id.btn_bindUserService).setOnClickListener(this);
        findViewById(R.id.btn_unbindUserService).setOnClickListener(this);
        findViewById(R.id.btn_resqueUserService).setOnClickListener(this);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            findViewById(R.id.btn_delegatedScopes).setEnabled(false);
        else
            findViewById(R.id.btn_delegatedScopes).setOnClickListener(this);
        findViewById(R.id.btn_lockTask).setOnClickListener(this);

        findViewById(R.id.btn_setApplicationHidden).setOnClickListener(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            findViewById(R.id.btn_setOrganizationName).setEnabled(false);
        else
            findViewById(R.id.btn_setOrganizationName).setOnClickListener(this);
        findViewById(R.id.btn_setMasterVolumeMuted).setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        try {
            EditText text = findViewById(R.id.text);
            String string = text.getText().toString();

            final int resId = v.getId();
            if (resId == R.id.btn_init)
                makeText("init：" + Dhizuku.init());
            else if (resId == R.id.btn_delegatedScopes)
                testDelegatedScopes();
            else if (resId == R.id.btn_getVersionCode)
                makeText("getVersionCode：" + Dhizuku.getVersionCode());
            else if (resId == R.id.btn_getVersionName)
                makeText("getVersionName：" + Dhizuku.getVersionName());
            else if (resId == R.id.btn_getOwnerPackageName)
                makeText("getOwnerPackageName：" + Dhizuku.getOwnerPackageName());
            else if (resId == R.id.btn_getOwnerComponent)
                makeText("getOwnerComponent：" + Dhizuku.getOwnerComponent().flattenToShortString());
            else if (resId == R.id.btn_isPermissionGranted)
                makeText("isPermissionGranted：" + Dhizuku.isPermissionGranted());
            else if (resId == R.id.btn_requestPermission) {
                Dhizuku.requestPermission(new DhizukuRequestPermissionListener() {
                    @Override
                    public void onRequestPermission(int grantResult) {
                        try {
                            makeText("Permission " + (grantResult == PackageManager.PERMISSION_GRANTED ? "grant" : "deni") + "ed.");
                        } catch (Throwable th) {
                            makeText(th.getMessage());
                        }
                    }
                });
            } else if (resId == R.id.btn_startUserService) {
                Dhizuku.startUserService(mUserServiceArgs);
                makeText("UserService started.");
            } else if (resId == R.id.btn_stopUserService) {
                Dhizuku.stopUserService(mUserServiceArgs);
                makeText("UserService stopped.");
            } else if (resId == R.id.btn_bindUserService)
                makeText("bindUserService：" + Dhizuku.bindUserService(mUserServiceArgs, mUserServiceConnection));
            else if (resId == R.id.btn_unbindUserService) {
                boolean result = Dhizuku.unbindUserService(mUserServiceConnection);
                makeText("unbindService：" + result);
            } else if (resId == R.id.btn_resqueUserService) {
                mUserService = null;
                makeText("mUserService was deallocated.");
            } else if (resId == R.id.btn_setApplicationHidden) {
                if (string.isEmpty()) {
                    checkText(text);
                } else {
                    makeText("ApplicationHidden：" + mUserService.setApplicationHidden(string));
                }
            } else if (resId == R.id.btn_setOrganizationName) {
                mUserService.setOrganizationName(string);
                makeText("Executed.");
            } else if (resId == R.id.btn_setMasterVolumeMuted) {
                makeText("MasterVolumeMuted：" + mUserService.setMasterVolumeMuted());
            } else if (resId == R.id.btn_lockTask) {
                toggleLockTask();
            }

        } catch (Exception e) {
            makeText(e.getMessage());
        }
    }

    private void toggleLockTask() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        try {
            if (manager.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE) {
                mUserService.setLockTaskPackages(new String[]{getPackageName()});
                startLockTask();
                makeText("LockTask started.");
            } else {
                stopLockTask();
                mUserService.setLockTaskPackages(new String[0]);
                makeText("LockTask stopped.");
            }
        } catch (Throwable th) {
            makeText(th.getMessage());
        }
    }

    private void testDelegatedScopes() {
        try {
            String[] before = Dhizuku.getDelegatedScopes();
            if (before != null && before.length > 0) {
                makeText("Cannot test because already set delegated scopes.");
                return;
            }

            boolean got = false;
            try {
                Dhizuku.setDelegatedScopes(new String[]{DevicePolicyManager.DELEGATION_APP_RESTRICTIONS});
                String[] after = Dhizuku.getDelegatedScopes();
                got = after != null && Arrays.asList(after).contains(DevicePolicyManager.DELEGATION_APP_RESTRICTIONS);
            } finally {
                Dhizuku.setDelegatedScopes(new String[0]);
            }
            makeText("DelegatedScopes：" + got);
        } catch (Throwable th) {
            makeText(th.getMessage());
        }
    }

    private void checkText(EditText box) {
        makeText("Please enter text.");
        box.post(() -> {
            // Focus
            box.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                // Launch keyboard
                imm.showSoftInput(box, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }
}
