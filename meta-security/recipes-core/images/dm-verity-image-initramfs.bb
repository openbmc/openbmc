DESCRIPTION = "Simple initramfs image for mounting the rootfs over the verity device mapper."

inherit core-image

PACKAGE_INSTALL = " \
    base-files \
    base-passwd \
    busybox \
    cryptsetup \
    initramfs-module-dmverity \
    initramfs-module-udev \
    lvm2-udevrules \
    udev \
    util-linux-mount \
"

# We want a clean, minimal image.
IMAGE_FEATURES = ""
IMAGE_LINGUAS = ""

# Can we somehow inspect reverse dependencies to avoid these variables?
do_image[depends] += "${DM_VERITY_IMAGE}:do_image_${DM_VERITY_IMAGE_TYPE}"

# Ensure dm-verity.env is updated also when rebuilding DM_VERITY_IMAGE
do_image[nostamp] = "1"

IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"

deploy_verity_hash() {
    install -D -m 0644 \
        ${STAGING_VERITY_DIR}/${DM_VERITY_IMAGE}.${DM_VERITY_IMAGE_TYPE}.verity.env \
        ${IMAGE_ROOTFS}${datadir}/misc/dm-verity.env
}
IMAGE_PREPROCESS_COMMAND += "deploy_verity_hash;"
