SUMMARY = "Init BMC MAC address"
DESCRIPTION = "Setup BMC MAC address read from VPD"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${OPENPOWERBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit allarch systemd

RDEPENDS_${PN} = "${VIRTUAL-RUNTIME_base-utils}"

SYSTEMD_SERVICE_${PN} = "first-boot-set-mac@.service"

SRC_URI = "file://${BPN}.sh file://${BPN}@.service"

S = "${WORKDIR}"
do_install() {
    install -d ${D}${bindir} ${D}${systemd_system_unitdir}
    install ${BPN}.sh ${D}${bindir}/
    install -m 644 ${BPN}@.service ${D}${systemd_system_unitdir}/
}
