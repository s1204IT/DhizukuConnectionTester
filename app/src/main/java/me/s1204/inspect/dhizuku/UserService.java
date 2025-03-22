package me.s1204.inspect.dhizuku;

import android.app.admin.DevicePolicyManager;
import android.content.Context;

public class UserService extends IUserService.Stub {

    private final DevicePolicyManager devicePolicyManager;

    public UserService(Context context) {
        devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    @Override
    public void lockNow() {
        devicePolicyManager.lockNow();
    }

}
