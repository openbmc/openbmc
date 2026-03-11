SUMMARY = "Initramfs image for kexecboot kernel"
DESCRIPTION = "This image provides kexecboot (linux as bootloader) and helpers."

# Some BSPs use IMAGE_FSTYPES:<machine override> which would override
# an assignment to IMAGE_FSTYPES so we need anon python
python () {
    d.setVar("IMAGE_FSTYPES", d.getVar("INITRAMFS_FSTYPES"))
}

inherit image

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

