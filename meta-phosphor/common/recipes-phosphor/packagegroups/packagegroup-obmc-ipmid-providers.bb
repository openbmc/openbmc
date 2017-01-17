SUMMARY = "OpenBMC - IPMI providers"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-license

PROVIDES = "${PACKAGES}"
PACKAGES = "${PN}-libs"

SUMMARY_${PN}-libs = "Extra providers for ipmid."
RDEPENDS_${PN}-libs = " \
        phosphor-ipmi-fru \
        "
