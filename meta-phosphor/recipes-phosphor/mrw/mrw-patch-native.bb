SUMMARY = "Phosphor machine readable workbook patching script"
DESCRIPTION = "Retrieve the script that can patch the MRW XML"
PR = "r1"

S = "${WORKDIR}/git"

inherit obmc-phosphor-license
inherit native
inherit mrw-rev

DEPENDS += "python-native python-lxml-native"

SRC_URI += "${MRW_TOOLS_SRC_URI}"
SRCREV = "${MRW_TOOLS_SRCREV}"

do_install() {
    install -d ${D}${bindir}/obmc-mrw
    install -m 0755 patchxml.py ${D}${bindir}/obmc-mrw
}
