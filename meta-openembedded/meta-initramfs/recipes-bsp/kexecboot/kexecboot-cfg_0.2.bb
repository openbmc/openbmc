SUMMARY = "Configuration files for kexecboot"
DESCRIPTION = "Default icon and boot.cfg for kexecboot linux-as-bootloader."
SECTION = "base"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://icon.xpm"

S = "${WORKDIR}"

do_install_prepend () {
echo '# /boot/boot.cfg - KEXECBOOT configuration file.
#
# First kernel stanza.
# Show this label in kexecboot menu.
#
LABEL=${KEXECBOOT_LABEL}
#
# Specify full kernel path on target.
KERNEL=/boot/${KERNEL_IMAGETYPE}
#
# Specify which device tree blob to use
# DTB=/boot/my-own-dtb
#
# Append this tags to the kernel cmdline.
APPEND=${CMDLINE} ${CMDLINE_DEBUG}
#
# Overwrite kernel command line instead of appending to it
# CMDLINE=console=/dev/tty0 root=/dev/sdb1
#
# Specify optional initrd/initramfs.
# INITRD=/boot/initramfs.cpio.gz
#
# Specify full path for a custom icon for the menu-item.
# If not set, use device-icons as default (NAND, SD, CF, ...).
# ICON=/boot/icon.xpm
#
# Priority of item in kexecboot menu.
# Items with highest priority will be shown at top of menu.
# Default: 0 (lowest, ordered by device ordering)
# PRIORITY=10
#
#
# Second kernel stanza.
# LABEL=${KEXECBOOT_LABEL}-test
# KERNEL=/boot/${KERNEL_IMAGETYPE}-test
# APPEND=${CMDLINE}
#' > ${S}/boot.cfg
}
do_install () {
    install -d ${D}/boot
    install -m 0644 boot.cfg ${D}/boot/boot.cfg
    install -m 0644 icon.xpm ${D}/boot/icon.xpm
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

FILES_${PN} += "/boot/*"

CMDLINE ?= ""
CMDLINE_DEBUG ?= "quiet"

INHIBIT_DEFAULT_DEPS = "1"

# Note: for qvga the label is currently limited to about 24 chars
KEXECBOOT_LABEL ?= "${@d.getVar('DISTRO') or d.getVar('DISTRO_VERSION')}-${MACHINE}"
