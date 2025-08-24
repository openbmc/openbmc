FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:fb-compute = " \
    file://01-bmc-eth.network \
    "

SRC_URI:append:fb-fboss = " \
    file://10-eth0.network \
    file://20-eth0.4088.network \
    file://20-eth0.4088.netdev \
    file://30-usb0.network \
"

do_install:append:fb-compute() {
    install -d ${D}${systemd_unitdir}/network/
    install -m 0644 ${UNPACKDIR}/01-bmc-eth.network ${D}${systemd_unitdir}/network/01-bmc-eth.network
}

do_install:append:fb-fboss() {
    install -d ${D}${systemd_unitdir}/network
    install -d ${D}${sysconfdir}/systemd/network

    install -m 0644 ${UNPACKDIR}/10-eth0.network ${D}${sysconfdir}/systemd/network
    install -m 0644 ${UNPACKDIR}/20-eth0.4088.network ${D}${systemd_unitdir}/network/
    install -m 0644 ${UNPACKDIR}/20-eth0.4088.netdev ${D}${systemd_unitdir}/network/
    install -m 0644 ${UNPACKDIR}/30-usb0.network ${D}${systemd_unitdir}/network/
}

FILES:${PN}:append:fb-compute = " ${systemd_unitdir}/network/01-bmc-eth.network"

FILES:${PN}:append:fb-fboss = " \
    ${sysconfdir}/systemd/network/10-eth0.network \
    ${systemd_unitdir}/network/20-eth0.4088.network \
    ${systemd_unitdir}/network/20-eth0.4088.netdev \
    ${systemd_unitdir}/network/30-usb0.network \
"
