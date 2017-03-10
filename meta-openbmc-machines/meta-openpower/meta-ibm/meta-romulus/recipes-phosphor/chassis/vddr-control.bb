SUMMARY = "Romulus VDDR Overrides"
DESCRIPTION = "Sets Rolumus VDDR to custom voltages"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

RDEPENDS_${PN} += "i2c-tools"

S = "${WORKDIR}"
SRC_URI += "file://vddr.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/vddr.sh ${D}${bindir}/vddr.sh
}

TMPL = "vddr-control@.service"
INSTFMT = "vddr-control@{0}.service"
TGTFMT = "obmc-power-chassis-on@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.requires/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"
