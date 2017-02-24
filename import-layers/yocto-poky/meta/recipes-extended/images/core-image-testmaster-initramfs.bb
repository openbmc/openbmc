DESCRIPTION = "Small image capable of booting a device with custom install scripts, \
adding a second rootfs, used for testing."

# use -testfs live-install scripts
PACKAGE_INSTALL = "initramfs-live-boot initramfs-live-install-testfs initramfs-live-install-efi-testfs busybox udev base-passwd ${ROOTFS_BOOTSTRAP_INSTALL}"

# Do not pollute the initrd image with rootfs features
IMAGE_FEATURES = ""

export IMAGE_BASENAME = "core-image-testmaster-initramfs"
IMAGE_LINGUAS = ""

LICENSE = "MIT"

IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"
inherit core-image

IMAGE_ROOTFS_SIZE = "8192"
IMAGE_ROOTFS_EXTRA_SPACE = "0"
BAD_RECOMMENDATIONS += "busybox-syslog"

# Use the same restriction as initramfs-live-install-testfs
COMPATIBLE_HOST = "(i.86|x86_64).*-linux"
