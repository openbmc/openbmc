# distro features required by weston recipes

inherit features_check

# requires pam enabled if started via systemd
#
REQUIRED_DISTRO_FEATURES = "wayland opengl ${@oe.utils.conditional('VIRTUAL-RUNTIME_init_manager', 'systemd', 'pam', '', d)}"

