SUMMARY = "Initramfs image for kexecboot kernel"
DESCRIPTION = "This image provides kexecboot (linux as bootloader) and helpers."

inherit image

IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"

# avoid circular dependencies
EXTRA_IMAGEDEPENDS = ""
KERNELDEPMODDEPEND = ""

# We really need just kexecboot, kexec and ubiattach
IMAGE_INSTALL = "kexecboot kexec mtd-utils-ubifs"

# Do not pollute the initrd image with rootfs features
IMAGE_FEATURES = ""

IMAGE_LINGUAS = ""

FEED_DEPLOYDIR_BASE_URI = ""
LDCONFIGDEPEND = ""
IMAGE_ROOTFS_EXTRA_SPACE = "0"

# disable runtime dependency on run-postinsts -> update-rc.d
ROOTFS_BOOTSTRAP_INSTALL = ""

