SUMMARY = "Basic Weston compositor setup"
DESCRIPTION = "Packages required to set up a basic working Weston session"

inherit packagegroup features_check

# weston-init requires pam enabled if started via systemd
REQUIRED_DISTRO_FEATURES = "wayland ${@oe.utils.conditional('VIRTUAL-RUNTIME_init_manager', 'systemd', 'pam', '', d)}"

RDEPENDS:${PN} = "\
    weston \
    weston-init \
    weston-examples \
    wayland-utils \
    "
