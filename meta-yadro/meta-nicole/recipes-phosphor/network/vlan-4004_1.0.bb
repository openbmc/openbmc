SUMMARY = "Tatlin network configuration"
DESCRIPTION = "This is default network configuration for the Tatlin's node."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PR = "r1"

inherit allarch
inherit systemd

RDEPENDS_${PN} = "${VIRTUAL-RUNTIME_base-utils}"
SYSTEMD_SERVICE_${PN} = "setup-vlan4004.service"

S = "${WORKDIR}"
SRC_URI = " \
    file://00-bmc-eth0.4004.0.network \
    file://00-bmc-eth0.4004.1.network \
    file://00-bmc-eth0.network \
    file://eth0.4004.netdev \
    file://setup-vlan4004.service \
    file://setup-vlan4004.sh \
"

do_install() {
    mkdir -p ${D}${sysconfdir}/systemd/network
    install -m 0644 eth0.4004.netdev 00-bmc-eth0.network \
        00-bmc-eth0.4004.0.network 00-bmc-eth0.4004.1.network \
        ${D}${sysconfdir}/systemd/network/
    mkdir -p ${D}${bindir} ${D}${systemd_system_unitdir}
    install setup-vlan4004.sh ${D}${bindir}/
    install -m 0644 setup-vlan4004.service ${D}${systemd_system_unitdir}/
}

FILES_${PN} = " \
    ${sysconfdir}/systemd/network/00-bmc-eth0.4004.0.network \
    ${sysconfdir}/systemd/network/00-bmc-eth0.4004.1.network \
    ${sysconfdir}/systemd/network/00-bmc-eth0.network \
    ${sysconfdir}/systemd/network/eth0.4004.netdev \
    ${bindir}/setup-vlan4004.sh \
"
