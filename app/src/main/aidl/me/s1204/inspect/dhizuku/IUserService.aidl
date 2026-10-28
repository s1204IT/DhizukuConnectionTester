package me.s1204.inspect.dhizuku;

interface IUserService {
    boolean setApplicationHidden(String packageName) = 21;
    void setOrganizationName(CharSequence title) = 22;
    boolean setMasterVolumeMuted() = 23;
    void setLockTaskPackages(in String[] packages) = 24;
}
