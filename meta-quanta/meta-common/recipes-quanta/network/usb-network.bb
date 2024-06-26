LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"
SRC_URI:append = " file://usb-network.sh"
SRC_URI:append = " file://usb-network.service"

inherit systemd

DEPENDS += "systemd"
RDEPENDS:${PN} += "libsystemd"
RDEPENDS:${PN} += "bash"

FILES:${PN}:append = " ${sysconfdir_native}/systemd/network/00-bmc-usb0.network"

do_install() {
    install -d ${D}/${sbindir}
    install -m 0755 ${UNPACKDIR}/usb-network.sh ${D}/${sbindir}

    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${UNPACKDIR}/usb-network.service \
        ${D}${systemd_unitdir}/system
}

SYSTEMD_SERVICE:${PN}:append = "usb-network.service"
