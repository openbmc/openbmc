SUMMARY = "An image used during oe-selftest tests"

# libudev is needed for deploy mdadm via devtool
IMAGE_INSTALL = "packagegroup-core-boot packagegroup-core-ssh-dropbear libudev"
IMAGE_FEATURES = "allow-empty-password empty-root-password allow-root-login"

IMAGE_LINGUAS = " "

inherit core-image

