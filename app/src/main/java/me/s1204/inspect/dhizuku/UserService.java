package me.s1204.inspect.dhizuku;

import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInstaller;
import android.os.Build;
import android.os.RemoteException;

import com.rosan.dhizuku.shared.DhizukuVariables;

public class UserService extends IUserService.Stub {

    private final DevicePolicyManager devicePolicyManager;

    public static final ComponentName COMPONENT_NAME = new ComponentName(DhizukuVariables.OFFICIAL_PACKAGE_NAME, DhizukuVariables.OFFICIAL_PACKAGE_NAME + ".server.DhizukuDAReceiver");

    public UserService(Context context) {
        devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    @Override
    public boolean setApplicationHidden(String packageName) {
        boolean current = devicePolicyManager.isApplicationHidden(COMPONENT_NAME, packageName);
        devicePolicyManager.setApplicationHidden(COMPONENT_NAME, packageName, !current);
        return !current;
    }

    @Override
    public void setOrganizationName(CharSequence title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            devicePolicyManager.setOrganizationName(COMPONENT_NAME, title);
        }
    }

    @Override
    public void lockNow() {
        devicePolicyManager.lockNow();
    }

    @Override
    public boolean setMasterVolumeMuted() {
        boolean current = devicePolicyManager.isMasterVolumeMuted(COMPONENT_NAME);
        devicePolicyManager.setMasterVolumeMuted(COMPONENT_NAME, !current);
        return !current;
    }

}
