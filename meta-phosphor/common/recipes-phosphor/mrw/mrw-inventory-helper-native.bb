SUMMARY = "OpenBMC machine readable workbook inventory module"
DESCRIPTION = "Helper module to retrieve the inventory from the MRW"
PR = "r1"

S = "${WORKDIR}/git"

inherit obmc-phosphor-license
inherit native
inherit perlnative
inherit cpan-base
inherit mrw-rev

DEPENDS += "mrw-api-native"

SRC_URI += "${MRW_TOOLS_SRC_URI}"
SRCREV = "${MRW_TOOLS_SRCREV}"

do_install() {
    install -d ${D}${PERLLIBDIRS_class-native}/perl/site_perl/${PERLVERSION}/mrw
    install -m 0755 Inventory.pm ${D}${PERLLIBDIRS_class-native}/perl/site_perl/${PERLVERSION}/mrw/Inventory.pm
}
