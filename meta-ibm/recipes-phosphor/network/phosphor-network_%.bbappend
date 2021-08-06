FILESEXTRAPATHS:prepend := "${THISDIR}/network:"

SRC_URI += " file://ncsi-netlink.service"
SYSTEMD_SERVICE:${PN} += " ncsi-netlink.service"
FILES:${PN} += "${datadir}/network/*.json"

PACKAGECONFIG:append = " sync-mac"

install_network_configuration(){
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/ncsi-netlink.service ${D}${systemd_system_unitdir}
    install -d ${D}${datadir}/network/
    install -m 0644 ${WORKDIR}/inventory-object-map.json ${D}${datadir}/network/config.json
}

SRC_URI:append:p10bmc = " file://inventory-object-map.json"
do_install:append:p10bmc(){
    install_network_configuration
}

SRC_URI:append:ibm-ac-server = " file://inventory-object-map.json"
do_install:append:ibm-ac-server() {
    install_network_configuration
}

SRC_URI:append:mihawk = " file://inventory-object-map.json"
do_install:append:mihawk() {
    install_network_configuration
}

SRC_URI:append:witherspoon-tacoma = " file://inventory-object-map.json"
do_install:append:witherspoon-tacoma(){
    install_network_configuration
}
