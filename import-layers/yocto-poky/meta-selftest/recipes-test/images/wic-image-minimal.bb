SUMMARY = "An example of partitioned image."

SRC_URI = "file://${FILE_DIRNAME}/${BPN}.wks"

IMAGE_INSTALL = "packagegroup-core-boot ${ROOTFS_PKGMANAGE_BOOTSTRAP}"

IMAGE_FSTYPES = "wic"

DEPENDS = "syslinux syslinux-native dosfstools-native mtools-native gptfdisk-native"

LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

# core-image-minimal is referenced in .wks, so we need its rootfs
# to be ready before our rootfs
do_rootfs[depends] += "core-image-minimal:do_image core-image-minimal:do_rootfs_wicenv"

IMAGE_ROOTFS_EXTRA_SPACE = "2000"

inherit image
