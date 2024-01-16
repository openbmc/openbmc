FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://00-bmc-eth.network \
    "

FILES:${PN}:append = " ${systemd_unitdir}/network/00-bmc-eth.network"

do_install:append() {
    install -d ${D}${systemd_unitdir}/network/
    install -m 0644 ${WORKDIR}/00-bmc-eth.network ${D}${systemd_unitdir}/network/00-bmc-eth.network
}