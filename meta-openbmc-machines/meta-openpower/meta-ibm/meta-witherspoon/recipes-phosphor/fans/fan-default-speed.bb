SUMMARY = "Set Witherspoon fan default speeds"
DESCRIPTION = "Sets all fans to a single speed"
PR = "r1"

inherit allarch
inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "i2c-tools"

S = "${WORKDIR}"
SRC_URI += "file://set_fan_speeds.sh"

TMPL = "fan-default-speed@.service"
INSTFMT = "fan-default-speed@{0}.service"
TGTFMT = "obmc-chassis-start@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.wants/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/set_fan_speeds.sh ${D}${bindir}/set_fan_speeds.sh
}
