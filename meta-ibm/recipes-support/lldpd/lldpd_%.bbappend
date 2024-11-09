#LLDP factory reset default configuration
install_lldpd_configuration() {
    echo "configure system description BMC" >> ${D}${sysconfdir}/lldpd.conf
    echo "configure ports eth0 lldp status disabled" >> ${D}${sysconfdir}/lldpd.conf
    echo "configure ports eth1 lldp status disabled" >> ${D}${sysconfdir}/lldpd.conf
}

do_install:append:p10bmc() {
      install_lldpd_configuration
}
