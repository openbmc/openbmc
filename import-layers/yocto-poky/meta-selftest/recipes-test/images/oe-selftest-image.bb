SUMMARY = "An image used during oe-selftest tests"

IMAGE_INSTALL = "packagegroup-core-boot ${ROOTFS_PKGMANAGE_BOOTSTRAP} dropbear"
IMAGE_FEATURES = "debug-tweaks"

IMAGE_LINGUAS = " "

inherit core-image

