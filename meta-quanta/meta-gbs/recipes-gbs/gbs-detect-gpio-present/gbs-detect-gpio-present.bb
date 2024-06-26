SUMMARY = "OpenBMC Quanta Detect Present Service"
DESCRIPTION = "OpenBMC Quanta Detect Present Daemon."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

DEPENDS += "systemd"
RDEPENDS:${PN} += "bash"

SRC_URI = " file://detect-gpio-present.sh \
            file://detect-gpio-present.service \
          "

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/detect-gpio-present.sh ${D}${bindir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/detect-gpio-present.service ${D}${systemd_system_unitdir}
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "detect-gpio-present.service"
