PACKAGES = " "
EXCLUDE_FROM_WORLD = "1"

INITRD_IMAGE ?= "obmc-phosphor-initramfs"

IMAGE_BASETYPE ?= "squashfs-xz"
OVERLAY_BASETYPE ?= "jffs2"

IMAGE_TYPES += "overlay"

IMAGE_TYPEDEP_overlay = "${IMAGE_BASETYPE}"
IMAGE_TYPES_MASKED += "overlay"

ROOTFS ?= "${DEPLOY_DIR_IMAGE}/${IMAGE_BASENAME}-${MACHINE}.${IMAGE_BASETYPE}"

do_generate_flash[depends] += "${INITRD_IMAGE}:do_image_complete"
do_generate_flash[depends] += "${PN}:do_image_complete"

addtask generate_flash before do_build
