# IMAGE_FSTYPES must appear before image.bbclass
# is inherited otherwise image.bbclass will inherit
# "live" image fstypes that we don't want.
IMAGE_FSTYPES = "tar.xz"

inherit image

LICENSE = "Apache-2.0"

IMAGE_INSTALL_append = " busybox packagegroup-obmc-phosphor-debugtools perf "

# Override from image_types.bbclass to restrict tarball to /usr tree.
IMAGE_CMD_tar = "${IMAGE_CMD_TAR} -cvf ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.tar -C ${IMAGE_ROOTFS}/usr ."

# Remove packages installed by 'extrausers'.
IMAGE_INSTALL_remove = "base-passwd shadow"
EXTRA_USERS_PARAMS = ""

# Remove extra packages defaulted by image.bbclass.
PACKAGE_INSTALL = "${IMAGE_INSTALL}"
IMAGE_LINGUAS = ""
