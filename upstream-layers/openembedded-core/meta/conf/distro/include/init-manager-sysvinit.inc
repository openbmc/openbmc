# Use sysvinit for system initialization
DISTRO_FEATURES:append = " sysvinit"
VIRTUAL-RUNTIME_init_manager ??= "sysvinit"
VIRTUAL-RUNTIME_initscripts ??= "initscripts"
VIRTUAL-RUNTIME_login_manager ??= "busybox"
VIRTUAL-RUNTIME_dev_manager ??= "udev"
