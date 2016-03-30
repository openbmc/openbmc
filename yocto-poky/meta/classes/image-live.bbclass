
AUTO_SYSLINUXCFG = "1"
INITRD_IMAGE ?= "core-image-minimal-initramfs"
INITRD ?= "${DEPLOY_DIR_IMAGE}/${INITRD_IMAGE}-${MACHINE}.cpio.gz"
SYSLINUX_ROOT ?= "root=/dev/ram0"
SYSLINUX_TIMEOUT ?= "50"
SYSLINUX_LABELS ?= "boot install"
LABELS_append = " ${SYSLINUX_LABELS} "

ROOTFS ?= "${DEPLOY_DIR_IMAGE}/${IMAGE_BASENAME}-${MACHINE}.ext4"

do_bootimg[depends] += "${INITRD_IMAGE}:do_rootfs"
do_bootimg[depends] += "${PN}:do_rootfs"

inherit bootimg

IMAGE_TYPEDEP_live = "ext4"
IMAGE_TYPES_MASKED += "live"
