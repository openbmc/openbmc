SUMMARY = "A very basic Wayland image with a terminal"

IMAGE_FEATURES += "splash package-management ssh-server-dropbear hwcodecs"

LICENSE = "MIT"

inherit core-image distro_features_check

REQUIRED_DISTRO_FEATURES = "wayland"

CORE_IMAGE_BASE_INSTALL += "weston weston-init weston-examples gtk+3-demo clutter-1.0-examples"
CORE_IMAGE_BASE_INSTALL += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'weston-xwayland matchbox-terminal', '', d)}"
