package me.s1204.inspect.dhizuku;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Toast;

import com.rosan.dhizuku.api.Dhizuku;
import com.rosan.dhizuku.api.DhizukuRequestPermissionListener;
import com.rosan.dhizuku.api.DhizukuUserServiceArgs;

public class Tester extends Activity implements View.OnClickListener {

    private IUserService mUserService;

    private void makeText(String msg) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
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
        findViewById(R.id.btn_lockNow).setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        DhizukuUserServiceArgs args = new DhizukuUserServiceArgs(new ComponentName(this, UserService.class));
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder iBinder) {
                mUserService = IUserService.Stub.asInterface(iBinder);
                makeText("Successfully connected to UserService");
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                makeText("Disconnected from UserService");
            }
        };

        final int resId = v.getId();
        try {
            if (resId == R.id.btn_init)
                makeText("init：" + Dhizuku.init());
            else if (resId == R.id.btn_getVersionCode)
                makeText("getVersionCode：" + Dhizuku.getVersionCode());
            else if (resId == R.id.btn_getVersionName)
                makeText("getVersionName：" + Dhizuku.getVersionName());
            else if (resId == R.id.btn_getOwnerPackageName)
                makeText("getOwnerPackageName：" + Dhizuku.getOwnerPackageName());
            else if (resId == R.id.btn_getOwnerComponent)
                makeText("getOwnerComponent：" + Dhizuku.getOwnerComponent());
            else if (resId == R.id.btn_isPermissionGranted)
                makeText("isPermissionGranted：" +  Dhizuku.isPermissionGranted());
            else if (resId == R.id.btn_requestPermission) {
                Dhizuku.requestPermission(new DhizukuRequestPermissionListener() {
                    @Override
                    public void onRequestPermission(int grantResult) {
                        Dhizuku.bindUserService(args, connection);
                    }
                });
            }
            else if (resId == R.id.btn_startUserService)
                Dhizuku.startUserService(args);
            else if (resId == R.id.btn_stopUserService)
                Dhizuku.stopUserService(args);
            else if (resId == R.id.btn_bindUserService)
                makeText("bindUserService：" + Dhizuku.bindUserService(args, connection));
            else if (resId == R.id.btn_unbindUserService)
                makeText("unbindService：" + Dhizuku.unbindUserService(connection));
            else if (resId == R.id.btn_lockNow)
                mUserService.lockNow();
        } catch (NoClassDefFoundError | NoSuchMethodError | SecurityException | NullPointerException e) {
            makeText(e.getMessage());
        } catch (RemoteException e) {
            String msg = e.getMessage();
            makeText(msg == null ? "RemoteException was threw, but message is null." : msg);
        }
    }

}
