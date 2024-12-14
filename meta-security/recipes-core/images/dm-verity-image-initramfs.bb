DESCRIPTION = "Simple initramfs image for mounting the rootfs over the verity device mapper."

inherit core-image

PACKAGE_INSTALL = " \
    base-files \
    base-passwd \
    busybox \
    cryptsetup \
    initramfs-module-dmverity \
    initramfs-module-udev \
    lvm2 \
    udev \
    util-linux-mount \
"

# We want a clean, minimal image.
IMAGE_FEATURES = ""
IMAGE_LINGUAS = ""

IMAGE_NAME_SUFFIX ?= ""

# Can we somehow inspect reverse dependencies to avoid these variables?
python __anonymous() {
    verity_image = d.getVar('DM_VERITY_IMAGE')
    verity_type = d.getVar('DM_VERITY_IMAGE_TYPE')

    if verity_image and verity_type:
        dep = ' %s:do_image_%s' % (verity_image, verity_type.replace('-', '_'))
        d.appendVarFlag('do_image', 'depends', dep)
}

# Ensure dm-verity.env is updated also when rebuilding DM_VERITY_IMAGE
do_image[nostamp] = "1"

IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"

deploy_verity_hash() {
    install -D -m 0644 \
        ${STAGING_VERITY_DIR}/${DM_VERITY_IMAGE}.${DM_VERITY_IMAGE_TYPE}.verity.env \
        ${IMAGE_ROOTFS}${datadir}/misc/dm-verity.env
}
IMAGE_PREPROCESS_COMMAND += "deploy_verity_hash;"
