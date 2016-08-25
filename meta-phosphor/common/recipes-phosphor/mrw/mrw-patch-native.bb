SUMMARY = "Phosphor machine readable workbook patching script"
DESCRIPTION = "Retrieve the script that can patch the MRW XML"
PR = "r1"

S = "${WORKDIR}/git"

inherit obmc-phosphor-license
inherit native

DEPENDS += "python-native python-lxml-native"

SRC_URI += "git://github.com/openbmc/phosphor-mrw-tools"
SRCREV = "ab015d7e2a2eb87eab2ca7d731ebcb7a873442e9"

do_install() {
    install -d ${bindir}/obmc-mrw
    install -m 0755 patchxml.py ${bindir}/obmc-mrw
}
