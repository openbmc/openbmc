SUMMARY = "OpenBMC - IPMI providers"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = "${PN}-libs"

SUMMARY_${PN}-libs = "Extra providers for ipmid."
RDEPENDS_${PN}-libs = "${VIRTUAL-RUNTIME_phosphor-ipmi-providers}"

# Warning.  Add additional providers with whitelists to distro or machine
# configuration and not in recipe context (bbappend) otherwise ipmid will not
# know about your whitelist.
