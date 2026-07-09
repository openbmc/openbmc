SUMMARY = "Cross-recipe kernel module dependency test: exporter (Bug 12290)"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

inherit module

SRC_URI = "\
    file://testfoo.c \
    file://Makefile.foo \
"

S = "${UNPACKDIR}"

do_configure:prepend() {
    cp ${UNPACKDIR}/Makefile.foo ${UNPACKDIR}/Makefile
}
