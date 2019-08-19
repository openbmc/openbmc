# enable mdev/busybox for init
DISTRO_FEATURES_BACKFILL_CONSIDERED_append = " systemd sysvinit"
VIRTUAL-RUNTIME_dev_manager ??= "busybox-mdev"
VIRTUAL-RUNTIME_init_manager ??= "busybox"
VIRTUAL-RUNTIME_initscripts ??= "initscripts"
VIRTUAL-RUNTIME_keymaps ??= "keymaps"
VIRTUAL-RUNTIME_login_manager ??= "busybox"
