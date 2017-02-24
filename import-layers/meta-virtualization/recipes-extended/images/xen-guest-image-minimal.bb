DESCRIPTION = "A Xen guest image."

inherit core-image

IMAGE_INSTALL += " \
    packagegroup-core-boot \
    ${@bb.utils.contains('MACHINE_FEATURES', 'acpi', 'kernel-module-xen-acpi-processor', '', d)} \
    "

IMAGE_INSTALL += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', ' xf86-video-fbdev', '', d)}"
IMAGE_INSTALL += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', ' xf86-video-vesa', '', d)}"

LICENSE = "MIT"

# Send console messages to xen console
APPEND += "console=hvc0"
