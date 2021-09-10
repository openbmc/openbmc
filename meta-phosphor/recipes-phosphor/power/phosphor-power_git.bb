SUMMARY = "Phosphor Power services and utilities"
DESCRIPTION = "Configure and monitor power supplies, power sequencers, and \
voltage regulators, and analyzes power devices for faults"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit meson
inherit pkgconfig
inherit systemd
inherit python3native

require ${BPN}.inc

S = "${WORKDIR}/git"

POWER_SERVICE_PACKAGES = " \
    ${PN}-cold-redundancy \
    ${PN}-monitor \
    ${PN}-psu-monitor \
    ${PN}-regulators \
    ${PN}-sequencer \
"
POWER_UTIL_PACKAGES = "${PN}-utils"

PACKAGE_BEFORE_PN = "${POWER_SERVICE_PACKAGES} ${POWER_UTIL_PACKAGES}"
ALLOW_EMPTY:${PN} = "1"

SYSTEMD_PACKAGES = "${POWER_SERVICE_PACKAGES}"

DEPENDS += " \
         phosphor-logging \
         ${PYTHON_PN}-sdbus++-native \
         sdeventplus \
         nlohmann-json \
         cli11 \
         i2c-tools \
         ${PYTHON_PN}-native \
         ${PYTHON_PN}-pyyaml-native \
         ${PYTHON_PN}-setuptools-native \
         ${PYTHON_PN}-mako-native \
         boost \
         libgpiod \
         "

# The monitor package uses an org.open_power D-Bus interface and so
# should only build when told to.
PACKAGECONFIG[monitor] = "-Dsupply-monitor=true, -Dsupply-monitor=false"

SEQ_MONITOR_SVC = "pseq-monitor.service"
SEQ_PGOOD_SVC = "pseq-monitor-pgood.service"
PSU_MONITOR_TMPL = "power-supply-monitor@.service"
PSU_MONITOR_SVC = "phosphor-psu-monitor.service"
REGS_SVC = "phosphor-regulators.service"
REGS_CONF_SVC = "phosphor-regulators-config.service"
REGS_MON_ENA_SVC = "phosphor-regulators-monitor-enable.service"
REGS_MON_DIS_SVC = "phosphor-regulators-monitor-disable.service"

SYSTEMD_SERVICE:${PN}-sequencer = "${SEQ_MONITOR_SVC} ${SEQ_PGOOD_SVC}"
SYSTEMD_SERVICE:${PN}-monitor = "${@bb.utils.contains('PACKAGECONFIG', 'monitor', '${PSU_MONITOR_TMPL}', '', d)}"
SYSTEMD_SERVICE:${PN}-psu-monitor = "${PSU_MONITOR_SVC}"
SYSTEMD_SERVICE:${PN}-regulators = "${REGS_SVC} ${REGS_CONF_SVC} ${REGS_MON_ENA_SVC} ${REGS_MON_DIS_SVC}"


# TODO: cold-redundancy is not installed in the repo yet
# FILES:${PN}-cold-redundancy = "${bindir}/cold-redundancy"

FILES:${PN}-monitor = "${bindir}/psu-monitor"
FILES:${PN}-psu-monitor = "${bindir}/phosphor-psu-monitor ${datadir}/phosphor-psu-monitor"
FILES:${PN}-regulators = "${bindir}/phosphor-regulators ${datadir}/phosphor-regulators"
FILES:${PN}-regulators += "${bindir}/regsctl"
FILES:${PN}-sequencer = "${bindir}/pseq-monitor"
FILES:${PN}-utils = "${bindir}/psutils"
