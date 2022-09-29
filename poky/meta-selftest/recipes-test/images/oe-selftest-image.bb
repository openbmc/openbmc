SUMMARY = "An image used during oe-selftest tests"

# libudev is needed for deploy mdadm via devtool
IMAGE_INSTALL = "packagegroup-core-boot packagegroup-core-ssh-dropbear libudev"
IMAGE_FEATURES = "debug-tweaks"

IMAGE_LINGUAS = " "

inherit core-image

