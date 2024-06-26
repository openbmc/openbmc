FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://01-bmc-eth.network \
    "

FILES:${PN}:append = " ${systemd_unitdir}/network/01-bmc-eth.network"

do_install:append() {
    install -d ${D}${systemd_unitdir}/network/
    install -m 0644 ${UNPACKDIR}/01-bmc-eth.network ${D}${systemd_unitdir}/network/01-bmc-eth.network
}
