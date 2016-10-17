SUMMARY = "Phosphor Device Tree Generator"
DESCRIPTION = "Script to generate the BMC device tree from the MRW XML"
PR = "r1"

S = "${WORKDIR}/git"

inherit obmc-phosphor-license
inherit native
inherit mrw-rev

DEPENDS += "yaml-tiny mrw-api-native"

SRC_URI += "${MRW_TOOLS_SRC_URI}"
SRCREV = "${MRW_TOOLS_SRCREV}"

do_install() {
    install -d ${D}${STAGING_BINDIR_NATIVE}
    install -m 0755 gen_devtree.pl ${D}${STAGING_BINDIR_NATIVE}/gen_devtree.pl
}
