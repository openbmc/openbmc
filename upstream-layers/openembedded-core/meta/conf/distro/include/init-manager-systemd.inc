# Use systemd for system initialization
DISTRO_FEATURES:append = " systemd usrmerge"
VIRTUAL-RUNTIME_init_manager ??= "systemd"
VIRTUAL-RUNTIME_initscripts ??= ""
VIRTUAL-RUNTIME_login_manager ??= "shadow-base"
VIRTUAL-RUNTIME_dev_manager ??= "systemd"
# systemd hardcodes /root in its source codes, other values are not offically supported
ROOT_HOME ?= "/root"
