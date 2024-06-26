SUMMARY = "OpenBMC NCPLite Front Panel LED Monitor"
DESCRIPTION = "OpenBMC NCPLite Front Panel Monitor"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit systemd

DEPENDS += "systemd"
RDEPENDS:${PN} += "bash"

SRC_URI += " file://ncplite-led.sh \
             file://ncplite-led.service \
          "

do_install() {
    install -d ${D}${libexecdir}/{BPN}
    install -m 0755 ${UNPACKDIR}/ncplite-led.sh ${D}${libexecdir}/{BPN}

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/ncplite-led.service ${D}${systemd_system_unitdir}
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "ncplite-led.service"
