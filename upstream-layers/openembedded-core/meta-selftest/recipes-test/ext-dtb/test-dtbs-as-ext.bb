SUMMARY = "Standalone Devicetrees for OE selftest"
DESCRIPTION = "Standalone DTB and DTBO files used for external DTB FIT image testing. \
Intentionally self-contained with no kernel source dependencies so any OE-core \
build can run the FIT image selftests without a BSP layer."
SECTION = "kernel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit devicetree

COMPATIBLE_MACHINE = ".*"

SRC_URI = "\
    file://test-ext.dts \
    file://test-overlay.dts \
"

# Sym-links are handled as extra configuration nodes in FIT images.
do_install:append() {
    ln -sf test-ext.dtb "${D}/boot/devicetree/test-ext-alias.dtb"
}

do_deploy:append() {
    ln -sf test-ext.dtb "${DEPLOYDIR}/devicetree/test-ext-alias.dtb"
}
