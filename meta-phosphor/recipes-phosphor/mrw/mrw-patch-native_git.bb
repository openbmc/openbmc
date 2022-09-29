SUMMARY = "Phosphor machine readable workbook patching script"
DESCRIPTION = "Retrieve the script that can patch the MRW XML"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS += "python3-native python3-lxml-native"
SRCREV = "${MRW_TOOLS_SRCREV}"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI += "${MRW_TOOLS_SRC_URI}"

S = "${WORKDIR}/git"

inherit mrw-rev
inherit native

do_install() {
    install -d ${D}${bindir}/obmc-mrw
    install -m 0755 patchxml.py ${D}${bindir}/obmc-mrw
}
