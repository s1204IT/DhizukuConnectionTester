package me.s1204.inspect.dhizuku;

import android.os.UserHandle;

interface IUserService {
    boolean setApplicationHidden(String packageName) = 22;

    void setOrganizationName(CharSequence title) = 23;

    void lockNow() = 24;

    boolean setMasterVolumeMuted() = 25;
}