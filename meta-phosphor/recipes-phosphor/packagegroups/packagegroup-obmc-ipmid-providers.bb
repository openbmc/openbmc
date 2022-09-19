# Warning.  Add additional providers with whitelists to distro or machine
# configuration and not in recipe context (bbappend) otherwise ipmid will not
# know about your whitelist.
SUMMARY = "OpenBMC - IPMI providers"
SUMMARY:${PN}-libs = "Extra providers for ipmid."
PROVIDES = "${PACKAGES}"
PR = "r1"

inherit packagegroup

PACKAGES = "${PN}-libs"

RDEPENDS:${PN}-libs = "${VIRTUAL-RUNTIME_phosphor-ipmi-providers}"
