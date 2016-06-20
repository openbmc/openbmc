DESCRIPTION = "A development image that builds the kernel and packages that are \
sensitive to kernel updates and version changes"

# Could also be core-image-basic, but we'll keep this small for now
require recipes-core/images/core-image-minimal.bb

KERNEL_DEV_UTILS ?= "dropbear connman"
KERNEL_DEV_TOOLS ?= "packagegroup-core-tools-profile packagegroup-core-buildessential kernel-devsrc"
KERNEL_DEV_MODULE ?= "kernel-modules"

CORE_IMAGE_EXTRA_INSTALL += "${KERNEL_DEV_MODULE} \
                             ${KERNEL_DEV_UTILS} \
                             ${KERNEL_DEV_TOOLS} \
                            "

# We need extra space for things like kernel builds, etc.
IMAGE_ROOTFS_EXTRA_SPACE_append += "+ 3000000"

# Let's define our own subset to test, we can later add a on-target kernel build even!
DEFAULT_TEST_SUITES = "ping ssh df connman syslog scp date parselogs"
