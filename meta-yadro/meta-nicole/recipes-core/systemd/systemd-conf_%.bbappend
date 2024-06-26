FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://00-bmc-eth0.4004.0.network \
    file://00-bmc-eth0.4004.1.network \
    file://00-bmc-eth0.0.network \
    file://00-bmc-eth0.1.network \
    file://eth0.4004.netdev \
"

do_install:append() {
    install -m 0644 \
        ${UNPACKDIR}/00-bmc-eth0.4004.0.network \
        ${UNPACKDIR}/00-bmc-eth0.4004.1.network \
        ${UNPACKDIR}/00-bmc-eth0.0.network \
        ${UNPACKDIR}/00-bmc-eth0.1.network \
        ${UNPACKDIR}/eth0.4004.netdev \
        -D -t ${D}${sysconfdir}/systemd/network
}

FILES:${PN}:append = " \
    ${sysconfdir}/systemd/network/00-bmc-eth0.4004.0.network \
    ${sysconfdir}/systemd/network/00-bmc-eth0.4004.1.network \
    ${sysconfdir}/systemd/network/00-bmc-eth0.0.network \
    ${sysconfdir}/systemd/network/00-bmc-eth0.1.network \
    ${sysconfdir}/systemd/network/eth0.4004.netdev \
"
