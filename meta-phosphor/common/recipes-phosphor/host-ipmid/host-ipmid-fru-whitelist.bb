SUMMARY = "Whitelisted IPMI FRU Parser commands"
DESCRIPTION = "Whitelisted IPMI FRU Parser commands for OpenBMC"

inherit obmc-phosphor-license
inherit native

do_install_append() {
install -d ${D}/${sysconfdir}/host-ipmid-conf
install -m 0644 ${THISDIR}/${PN}/files/${PN}.conf ${D}/${sysconfdir}/host-ipmid-conf
}
