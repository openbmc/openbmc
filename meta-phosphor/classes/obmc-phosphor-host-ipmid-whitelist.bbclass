# Common code for recipes that implement Phosphor OpenBMC IPMI Whitelist
# packages 

do_install_append_class-native() {
install -d ${D}/${sysconfdir}/host-ipmid-conf
install -m 0644 ${THISDIR}/${PN}/files/${PN}.conf ${D}/${sysconfdir}/host-ipmid-conf
}
