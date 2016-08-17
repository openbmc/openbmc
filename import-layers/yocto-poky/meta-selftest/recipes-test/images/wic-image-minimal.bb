SUMMARY = "An example of partitioned image."

SRC_URI = "file://${FILE_DIRNAME}/${BPN}.wks"

IMAGE_INSTALL = "packagegroup-core-boot ${ROOTFS_PKGMANAGE_BOOTSTRAP}"

IMAGE_FSTYPES = "wic"
RM_OLD_IMAGE = "1"

DEPENDS = "syslinux syslinux-native parted-native dosfstools-native mtools-native gptfdisk-native"

# core-image-minimal is referenced in .wks, so we need its rootfs
# to be ready before our rootfs
do_rootfs[depends] += "core-image-minimal:do_image core-image-minimal:do_rootfs_wicenv"

IMAGE_ROOTFS_EXTRA_SPACE = "2000"

inherit image
