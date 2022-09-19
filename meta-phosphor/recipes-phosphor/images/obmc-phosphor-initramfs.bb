DESCRIPTION = "Small image capable of booting a device. The kernel includes \
the Minimal RAM-based Initial Root Filesystem (initramfs), which finds the \
first 'init' program more efficiently."
LICENSE = "MIT"
# Needed for the set_user_group functions to succeed
DEPENDS += "shadow-native"

inherit core-image

export IMAGE_BASENAME = "obmc-phosphor-initramfs"

BAD_RECOMMENDATIONS += "busybox-syslog"

PACKAGE_INSTALL = "${VIRTUAL-RUNTIME_base-utils} base-passwd ${ROOTFS_BOOTSTRAP_INSTALL} ${INIT_PACKAGE}"
PACKAGE_INSTALL:remove = "shadow"

# Init scripts
INIT_PACKAGE = "obmc-phosphor-initfs"
INIT_PACKAGE:df-phosphor-mmc = "phosphor-mmc-init"
# Do not pollute the initrd image with rootfs features
IMAGE_FEATURES = "read-only-rootfs"
IMAGE_LINGUAS = ""
IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"
IMAGE_ROOTFS_SIZE = "8192"
IMAGE_ROOTFS_EXTRA_SPACE = "0"
PACKAGE_EXCLUDE = "shadow"
