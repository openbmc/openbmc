SUMMARY = "OpenBMC Quanta Detect Fan Fail Service"
DESCRIPTION = "OpenBMC Quanta Detect Fan Fail Daemon."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

DEPENDS += "systemd"
RDEPENDS_${PN} += "bash"

SRC_URI = " file://gbs-detect-fan-fail.sh \
            file://gbs-detect-fan-fail.service \
          "

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/gbs-detect-fan-fail.sh ${D}${bindir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/gbs-detect-fan-fail.service ${D}${systemd_system_unitdir}
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "gbs-detect-fan-fail.service"
