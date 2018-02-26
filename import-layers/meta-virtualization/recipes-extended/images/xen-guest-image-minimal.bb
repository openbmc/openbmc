DESCRIPTION = "A Xen guest image."

inherit core-image distro_features_check

IMAGE_INSTALL += " \
    packagegroup-core-boot \
    ${@bb.utils.contains('MACHINE_FEATURES', 'acpi', 'kernel-module-xen-acpi-processor', '', d)} \
    "

IMAGE_INSTALL += "${@bb.utils.contains('IMAGE_FEATURES', 'x11', ' xf86-video-fbdev', '', d)}"

# Install xf86-video-vesa on x86 platforms.
IMAGE_INSTALL_append_x86-64 = "${@bb.utils.contains('IMAGE_FEATURES', 'x11', ' xf86-video-vesa', '', d)}"
IMAGE_INSTALL_append_x86    = "${@bb.utils.contains('IMAGE_FEATURES', 'x11', ' xf86-video-vesa', '', d)}"

REQUIRED_DISTRO_FEATURES += "${@bb.utils.contains('IMAGE_FEATURES', 'x11', ' x11', '', d)} xen"

LICENSE = "MIT"

# Send console messages to xen console
APPEND += "console=hvc0"
