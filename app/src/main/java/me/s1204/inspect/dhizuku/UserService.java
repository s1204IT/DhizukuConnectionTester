package me.s1204.inspect.dhizuku;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

import com.rosan.dhizuku.shared.DhizukuVariables;

public class UserService extends IUserService.Stub {

    private final DevicePolicyManager devicePolicyManager;

    public static final ComponentName COMPONENT_NAME = DhizukuVariables.OFFICIAL_COMPONENT_NAME;

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
        devicePolicyManager.setOrganizationName(COMPONENT_NAME, title);
    }

    @Override
    public boolean setMasterVolumeMuted() {
        boolean current = devicePolicyManager.isMasterVolumeMuted(COMPONENT_NAME);
        devicePolicyManager.setMasterVolumeMuted(COMPONENT_NAME, !current);
        return !current;
    }

    @Override
    public void setLockTaskPackages(String[] packages) {
        devicePolicyManager.setLockTaskPackages(COMPONENT_NAME, packages);
    }

}
