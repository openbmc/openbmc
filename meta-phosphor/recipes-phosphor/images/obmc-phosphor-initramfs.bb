DESCRIPTION = "Small image capable of booting a device. The kernel includes \
the Minimal RAM-based Initial Root Filesystem (initramfs), which finds the \
first 'init' program more efficiently."

PACKAGE_INSTALL = "${VIRTUAL-RUNTIME_base-utils} base-passwd ${ROOTFS_BOOTSTRAP_INSTALL} obmc-phosphor-initfs"

# Do not pollute the initrd image with rootfs features
IMAGE_FEATURES = "read-only-rootfs"

export IMAGE_BASENAME = "obmc-phosphor-initramfs"
IMAGE_LINGUAS = ""

LICENSE = "MIT"

IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"
inherit core-image

IMAGE_ROOTFS_SIZE = "8192"
IMAGE_ROOTFS_EXTRA_SPACE = "0"

# Needed for the set_user_group functions to succeed
DEPENDS += "shadow-native"

PACKAGE_INSTALL_remove = "shadow"
PACKAGE_EXCLUDE = "shadow"
BAD_RECOMMENDATIONS += "busybox-syslog"
