SUMMARY = "An example of partitioned image."

SRC_URI = "file://${FILE_DIRNAME}/${BPN}.wks"

IMAGE_INSTALL = "packagegroup-core-boot"

IMAGE_FSTYPES = "wic"

WKS_FILE_DEPENDS = "syslinux syslinux-native dosfstools-native mtools-native gptfdisk-native"

LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

IMAGE_ROOTFS_EXTRA_SPACE = "2000"

inherit image
