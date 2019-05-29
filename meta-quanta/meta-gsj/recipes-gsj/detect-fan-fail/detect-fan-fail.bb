SUMMARY = "OpenBMC Quanta Detect Fan Fail Service"
DESCRIPTION = "OpenBMC Quanta Detect Fan Fail Daemon."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${QUANTABASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit systemd

DEPENDS += "systemd"
RDEPENDS_${PN} += "bash"

FILESEXTRAPATHS_append_gsj := "${THISDIR}/files:"
SRC_URI_append_gsj =  " file://detect-fan-fail.sh \
                        file://detect-fan-fail.service \
                      "

do_install_append_gsj() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/detect-fan-fail.sh ${D}${bindir}/

    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/detect-fan-fail.service ${D}${systemd_unitdir}/system
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "detect-fan-fail.service"
