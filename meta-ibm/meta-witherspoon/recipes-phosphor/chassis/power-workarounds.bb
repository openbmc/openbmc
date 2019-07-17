SUMMARY = "Power device Workarounds"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${IBMBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit allarch
inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "i2c-tools"

S = "${WORKDIR}"

SRC_URI += "file://power-workarounds.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${S}/power-workarounds.sh ${D}${bindir}/power-workarounds.sh
}

TMPL_WA = "power-workarounds@.service"
INSTFMT_WA = "power-workarounds@{0}.service"
TGTFMT = "obmc-chassis-poweron@{0}.target"
FMT_WA = "../${TMPL_WA}:${TGTFMT}.requires/${INSTFMT_WA}"

SYSTEMD_SERVICE_${PN} += "${TMPL_WA}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_WA', 'OBMC_CHASSIS_INSTANCES')}"
