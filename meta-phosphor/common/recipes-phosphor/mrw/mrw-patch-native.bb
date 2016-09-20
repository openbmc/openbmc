SUMMARY = "Phosphor machine readable workbook patching script"
DESCRIPTION = "Retrieve the script that can patch the MRW XML"
PR = "r1"

S = "${WORKDIR}/git"

inherit obmc-phosphor-license
inherit native

DEPENDS += "python-native python-lxml-native"

SRC_URI += "git://github.com/openbmc/phosphor-mrw-tools"
SRCREV = "cb99d1ea2c77c704e7991733fa36848e211567af"

do_install() {
    install -d ${D}${bindir}/obmc-mrw
    install -m 0755 patchxml.py ${D}${bindir}/obmc-mrw
}
