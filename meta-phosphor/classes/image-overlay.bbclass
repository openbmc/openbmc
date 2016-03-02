PACKAGES = " "
EXCLUDE_FROM_WORLD = "1"

INITRD_IMAGE ?= "core-image-minimal-initramfs"
INITRD ?= "${DEPLOY_DIR_IMAGE}/${INITRD_IMAGE}-${MACHINE}.cpio.${INITRD_CTYPE}${uboot}"

IMAGE_BASETYPE ?= "squashfs-xz"
OVERLAY_BASETYPE ?= "jffs2"

IMAGE_TYPES += "overlay"

IMAGE_TYPEDEP_overlay = "${IMAGE_BASETYPE}"
IMAGE_TYPES_MASKED += "overlay"

ROOTFS ?= "${DEPLOY_DIR_IMAGE}/${IMAGE_BASENAME}-${MACHINE}.${IMAGE_BASETYPE}"

do_generate_flash[depends] += "${INITRD_IMAGE}:do_rootfs"
do_generate_flash[depends] += "${PN}:do_rootfs"

addtask generate_flash before do_build
