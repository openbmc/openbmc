SUMMARY = "OpenBMC - IPMI providers"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = "${PN}-libs"

SUMMARY_${PN}-libs = "Extra providers for ipmid."
RDEPENDS_${PN}-libs = "${VIRTUAL-RUNTIME_phosphor-ipmi-providers}"
