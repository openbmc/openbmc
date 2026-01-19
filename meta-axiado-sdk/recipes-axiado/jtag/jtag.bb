FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM ?= "file://${COREBASE}/meta-axiado/COPYING.axiado;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI += "file://early-script.service"

inherit systemd

SYSTEMD_SERVICE:${PN} = "early-script.service"

do_install() {
    install -d ${D}${systemd_unitdir}/system
    install -m 644 ${UNPACKDIR}/early-script.service ${D}${systemd_unitdir}/system
}
