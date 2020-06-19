FILESEXTRAPATHS_prepend := "${THISDIR}/network:"

SRC_URI += " file://ncsi-netlink.service"
SYSTEMD_SERVICE_${PN} += " ncsi-netlink.service"
FILES_${PN} += "${datadir}/network/*.json"

EXTRA_OECONF += "--enable-sync-mac"

install_network_configuration(){
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/ncsi-netlink.service ${D}${systemd_system_unitdir}
    install -d ${D}${datadir}/network/
    install -m 0644 ${WORKDIR}/inventory-object-map.json ${D}${datadir}/network/config.json
}

SRC_URI_append_rainier = " file://inventory-object-map.json"
do_install_append_rainier(){
    install_network_configuration
}

SRC_URI_append_ibm-ac-server = " file://inventory-object-map.json"
do_install_append_ibm-ac-server() {
    install_network_configuration
}

SRC_URI_append_mihawk = " file://inventory-object-map.json"
do_install_append_mihawk() {
    install_network_configuration
}

SRC_URI_append_witherspoon-tacoma = " file://inventory-object-map.json"
do_install_append_witherspoon-tacoma(){
    install_network_configuration
}
