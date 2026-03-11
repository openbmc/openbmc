SUMMARY = "Boeaglebone Devicetrees"
DESCRIPTION = "Handle the dtc files of the beaglebone-yocto via devicetree.bbclass just for testing purpose"
SECTION = "kernel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit devicetree

COMPATIBLE_MACHINE  = "^(beaglebone-yocto)$"

# Take a copy of a small devicetree from the kernel's source directory for handling it externally
# Borrowed an example DTB overlay from
# https://raw.githubusercontent.com/beagleboard/linux/refs/heads/5.10/arch/arm/boot/dts/overlays/BBORG_RELAY-00A2.dts
SRC_URI = "\
    file://am335x-bonegreen-ext.dts \
    file://BBORG_RELAY-00A2.dts \
"

# The am335x-bonegreen-ext.dts needs also the ti directories
DT_INCLUDE:append = " ${STAGING_KERNEL_DIR}/arch/${ARCH}/boot/dts/ti/omap"

# Sym-links are handled as extra configuration nodes in FIT images.
do_install:append() {
    ln -sf am335x-bonegreen-ext.dtb "${D}/boot/devicetree/am335x-bonegreen-ext-alias.dtb"
}

do_deploy:append() {
    ln -sf am335x-bonegreen-ext.dtb "${DEPLOYDIR}/devicetree/am335x-bonegreen-ext-alias.dtb"
}
