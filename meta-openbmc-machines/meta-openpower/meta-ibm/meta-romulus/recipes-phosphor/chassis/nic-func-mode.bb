SUMMARY = "Romulus BCM5719 NIC_FUNC_MODE"
DESCRIPTION = "Enable BCM5719 NIC_FUNC_MODE by pulling down GPIOD3/D4"
PR = "r0"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

RDEPENDS_${PN} += "obmc-pydevtools"

S = "${WORKDIR}"
SRC_URI += "file://set_nic_func_mode.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/set_nic_func_mode.sh ${D}${bindir}/set_nic_func_mode.sh
}

TMPL = "nic_func_mode@.service"
INSTFMT = "nic_func_mode@{0}.service"
TGTFMT = "obmc-power-chassis-on@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.requires/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"
