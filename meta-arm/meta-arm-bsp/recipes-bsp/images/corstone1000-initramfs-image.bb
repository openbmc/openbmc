SUMARY = "Corstone1000 platform Initramfs Image"
DESCRIPTION = "This is the main Linux image which includes an initramfs kernel/rootfs bundle."

LICENSE = "MIT"

COMPATIBLE_MACHINE = "corstone1000"

IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"

inherit core-image

# By default all basic packages required for a bootable system are installed
# by core-image . These packages are: packagegroup-core-boot and
# packagegroup-base-extended

inherit image-buildinfo

IMAGE_FEATURES += "debug-tweaks"

#package management is not supported in corstone1000
IMAGE_FEATURES:remove = "package-management"

# all optee packages
IMAGE_INSTALL += "optee-client"

# FF-A Debugfs driver
IMAGE_INSTALL += "ffa-debugfs-mod"

# psa-arch-tests linux userspace application
IMAGE_INSTALL += "secure-partitions-psa-api-tests"
