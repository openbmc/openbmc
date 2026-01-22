FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:append = " sync-mac"
EXTRA_OEMESON = "-Dforce-sync-mac=true"

SRC_URI:append = " \
    file://00-hmc-usb0.network \
    file://90-hmc-usb0-network.rules \
    file://config.json \
    "

FILES:${PN}:append = " \
    ${systemd_unitdir}/network/00-hmc-usb0.network \
    ${sysconfdir}/udev/rules.d/90-hmc-usb0-network.rules \
    ${datadir}/network/*.json \
    "

do_install:append() {
    install -d ${D}${systemd_unitdir}/network/
    install -m 0644 ${UNPACKDIR}/00-hmc-usb0.network ${D}${systemd_unitdir}/network/00-hmc-usb0.network

    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${UNPACKDIR}/90-hmc-usb0-network.rules ${D}${sysconfdir}/udev/rules.d/90-hmc-usb0-network.rules

    install -d ${D}${datadir}/network/
    install -m 0644 ${UNPACKDIR}/config.json ${D}${datadir}/network/
}
