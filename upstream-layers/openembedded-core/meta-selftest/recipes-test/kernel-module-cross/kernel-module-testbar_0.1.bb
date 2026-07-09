SUMMARY = "Cross-recipe kernel module dependency test: consumer (Bug 12290)"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

inherit module

# This pulls in foo's Module.symvers via KBUILD_EXTRA_SYMBOLS (module.bbclass
# __anonymous) so modpost records testfoo as a dependency of testbar.
DEPENDS = "kernel-module-testfoo"

SRC_URI = "\
    file://testbar.c \
    file://Makefile.bar \
"

S = "${UNPACKDIR}"

do_configure:prepend() {
    cp ${UNPACKDIR}/Makefile.bar ${UNPACKDIR}/Makefile
}
