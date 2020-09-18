##################################################################
# Specific kernel creation info
# for recipes/bbclasses which need to reuse some of the kernel
# artifacts, but aren't kernel recipes themselves
##################################################################

inherit image-artifact-names

KERNEL_ARTIFACT_NAME ?= "${PKGE}-${PKGV}-${PKGR}-${MACHINE}${IMAGE_VERSION_SUFFIX}"
KERNEL_ARTIFACT_LINK_NAME ?= "${MACHINE}"

KERNEL_IMAGE_NAME ?= "${KERNEL_ARTIFACT_NAME}"
KERNEL_IMAGE_LINK_NAME ?= "${KERNEL_ARTIFACT_LINK_NAME}"

KERNEL_DTB_NAME ?= "${KERNEL_ARTIFACT_NAME}"
KERNEL_DTB_LINK_NAME ?= "${KERNEL_ARTIFACT_LINK_NAME}"

KERNEL_FIT_NAME ?= "${KERNEL_ARTIFACT_NAME}"
KERNEL_FIT_LINK_NAME ?= "${KERNEL_ARTIFACT_LINK_NAME}"

MODULE_TARBALL_NAME ?= "${KERNEL_ARTIFACT_NAME}"
MODULE_TARBALL_LINK_NAME ?= "${KERNEL_ARTIFACT_LINK_NAME}"
MODULE_TARBALL_DEPLOY ?= "1"

INITRAMFS_NAME ?= "initramfs-${KERNEL_ARTIFACT_NAME}"
INITRAMFS_LINK_NAME ?= "initramfs-${KERNEL_ARTIFACT_LINK_NAME}"
