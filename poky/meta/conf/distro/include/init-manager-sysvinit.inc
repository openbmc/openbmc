# Use sysvinit for system initialization
DISTRO_FEATURES:append = " sysvinit"
DISTRO_FEATURES_BACKFILL_CONSIDERED:append = " systemd"
VIRTUAL-RUNTIME_init_manager ??= "sysvinit"
VIRTUAL-RUNTIME_initscripts ??= "initscripts"
VIRTUAL-RUNTIME_login_manager ??= "busybox"
