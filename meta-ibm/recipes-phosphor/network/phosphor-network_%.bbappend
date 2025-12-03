FILESEXTRAPATHS:prepend := "${THISDIR}/network:"

inherit obmc-phosphor-systemd

RDEPENDS:${PN} += "bash"

OBMC_NETWORK_INTERFACES ?= "eth0"
OBMC_NETWORK_INTERFACES:append:ibm-enterprise = " eth1"
OBMC_NETWORK_INTERFACES:append:system1 = " eth1 eth2"

FAILOVER_TMPL = "ncsi-failover@.service"
LINKSPEED_TMPL = "ncsi-linkspeed@.service"

SRC_URI += " file://ncsi-netlink-ifindex"
SRC_URI += " file://ncsi-wait-and-set-speed"
SRC_URI:append:ibm-ac-server = " file://${FAILOVER_TMPL}"
SRC_URI:append:ibm-enterprise = " file://${LINKSPEED_TMPL}"

SYSTEMD_SERVICE:${PN}:append:ibm-ac-server = " ${FAILOVER_TMPL}"
SYSTEMD_SERVICE:${PN}:append:ibm-enterprise = " ${LINKSPEED_TMPL}"

FAILOVER_TGTFMT = "ncsi-failover@{0}.service"
LINKSPEED_TGTFMT = "ncsi-linkspeed@{0}.service"
FAILOVER_FMT = "../${FAILOVER_TMPL}:network.target.wants/${FAILOVER_TGTFMT}"
LINKSPEED_FMT = "../${LINKSPEED_TMPL}:network.target.wants/${LINKSPEED_TGTFMT}"

SYSTEMD_LINK:${PN}:append:ibm-ac-server = "${@compose_list(d, 'FAILOVER_FMT', 'OBMC_NETWORK_INTERFACES')}"
SYSTEMD_LINK:${PN}:append:ibm-enterprise = "${@compose_list(d, 'LINKSPEED_FMT', 'OBMC_NETWORK_INTERFACES')}"

FILES:${PN} += "${libexecdir}/ncsi-netlink-ifindex"
FILES:${PN} += "${libexecdir}/ncsi-wait-and-set-speed"
FILES:${PN} += "${datadir}/network/*.json"

PACKAGECONFIG:append = " sync-mac"
PACKAGECONFIG:append = " persist-mac"
PACKAGECONFIG:append:ibm-enterprise = " hyp-nw-config"

install_network_configuration(){
    install -d ${D}${datadir}/network/
    install -m 0644 ${UNPACKDIR}/ibm-basic-eth-map.json ${D}${datadir}/network/config.json
}

do_install:append() {
    install -d ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/ncsi-netlink-ifindex ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/ncsi-wait-and-set-speed ${D}${libexecdir}
}

SRC_URI:append:ibm-enterprise = " file://ibm-basic-eth-map.json"
do_install:append:ibm-enterprise(){
    install_network_configuration
}

SRC_URI:append:ibm-ac-server = " file://ibm-basic-eth-map.json"
do_install:append:ibm-ac-server() {
    install_network_configuration
}

SRC_URI:append:genesis3 = " file://ibm-basic-eth-map.json"
do_install:append:genesis3(){
    install_network_configuration
}

SRC_URI:append:sbp1 = " file://ibm-basic-eth-map.json"
do_install:append:sbp1(){
    install_network_configuration
}

SRC_URI:append:system1 = " file://ibm-basic-eth-map.json"
do_install:append:system1(){
    install_network_configuration
}
