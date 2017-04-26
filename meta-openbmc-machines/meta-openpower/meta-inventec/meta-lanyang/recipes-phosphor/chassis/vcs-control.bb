SUMMARY = "Lanyang VCS rail control"
DESCRIPTION = "VCS voltage rail control implementation for Lanyang"
PR = "r0"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

PROVIDES += 'virtual/p9-vcs-workaround'
RPROVIDES_${PN} += 'virtual-p9-vcs-workaround'

TMPL_OFF = "vcs-off@.service"
TMPL_ON = "vcs-on@.service"
INSTFMT_OFF = "vcs-off@{0}.service"
INSTFMT_ON = "vcs-on@{0}.service"
TGTFMT_OFF = "obmc-host-stop@{0}.target"
TGTFMT_ON = "obmc-chassis-poweron@{0}.target"
FMT_OFF = "../${TMPL_OFF}:${TGTFMT_OFF}.wants/${INSTFMT_OFF}"
FMT_ON = "../${TMPL_ON}:${TGTFMT_ON}.requires/${INSTFMT_ON}"

SYSTEMD_SERVICE_${PN} += "${TMPL_OFF}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_OFF', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_SERVICE_${PN} += "${TMPL_ON}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT_ON', 'OBMC_CHASSIS_INSTANCES')}"

SRC_URI += "file://lanyang_vcs.sh"
RDEPENDS_${PN} += "i2c-tools"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/lanyang_vcs.sh ${D}${bindir}/lanyang_vcs.sh
}
