# Use sysvinit for system initialization
DISTRO_FEATURES_append = " sysvinit"
DISTRO_FEATURES_BACKFILL_CONSIDERED_append = " systemd"
VIRTUAL-RUNTIME_init_manager ??= "sysvinit"
VIRTUAL-RUNTIME_initscripts ??= "initscripts"
VIRTUAL-RUNTIME_login_manager ??= "busybox"
