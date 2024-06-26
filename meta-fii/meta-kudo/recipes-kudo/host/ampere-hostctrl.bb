SUMMARY = "Ampere Computing LLC Host Control Implementation"
DESCRIPTION = "A host control implementation suitable for Ampere Computing LLC's systems"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

SRC_URI = "file://ampere-host-shutdown.service \
          file://ampere_power_util.sh \
          file://ampere-chassis-poweroff.service \
          file://ampere-chassis-poweron.service \
          file://ampere-chassis-powercycle.service \
          file://ampere-host-shutdown-ack.service \
          file://ampere-host-power-cycle.service \
          file://ampere-host-reset.service \
          "

DEPENDS = "systemd"
RDEPENDS:${PN} = "bash"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = " \
        ampere-host-shutdown.service \
        ampere-chassis-poweroff.service \
        ampere-chassis-poweron.service \
        ampere-chassis-powercycle.service \
        ampere-host-shutdown-ack.service \
        ampere-host-power-cycle.service \
        ampere-host-reset.service \
        "
# host power control
# overwrite the host shutdown to graceful shutdown
HOST_SHUTDOWN_TMPL = "ampere-host-shutdown.service"
HOST_SHUTDOWN_TGTFMT = "obmc-host-shutdown@{0}.target"
HOST_SHUTDOWN_FMT = "../${HOST_SHUTDOWN_TMPL}:${HOST_SHUTDOWN_TGTFMT}.requires/${HOST_SHUTDOWN_TMPL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'HOST_SHUTDOWN_FMT', 'OBMC_HOST_INSTANCES')}"

# Force the power cycle target to run the ampere power cycle
HOST_REBOOT_SVC = "ampere-host-power-cycle.service"
HOST_REBOOT_SVC_TGTFMT = "obmc-host-reboot@{0}.target"
HOST_REBOOT_SVC_FMT = "../${HOST_REBOOT_SVC}:${HOST_REBOOT_SVC_TGTFMT}.requires/${HOST_REBOOT_SVC}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'HOST_REBOOT_SVC_FMT', 'OBMC_HOST_INSTANCES')}"

# chassis power control
CHASSIS_POWERON_SVC = "ampere-chassis-poweron.service"
CHASSIS_POWERON_TGTFMT = "obmc-chassis-poweron@{0}.target"
CHASSIS_POWERON_FMT = "../${CHASSIS_POWERON_SVC}:${CHASSIS_POWERON_TGTFMT}.requires/${CHASSIS_POWERON_SVC}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'CHASSIS_POWERON_FMT', 'OBMC_CHASSIS_INSTANCES')}"

CHASSIS_POWEROFF_SVC = "ampere-chassis-poweroff.service"
CHASSIS_POWEROFF_TGTFMT = "obmc-chassis-poweroff@{0}.target"
CHASSIS_POWEROFF_FMT = "../${CHASSIS_POWEROFF_SVC}:${CHASSIS_POWEROFF_TGTFMT}.requires/${CHASSIS_POWEROFF_SVC}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'CHASSIS_POWEROFF_FMT', 'OBMC_CHASSIS_INSTANCES')}"

CHASSIS_POWERCYCLE_SVC = "ampere-chassis-powercycle.service"
CHASSIS_POWERCYCLE_TGTFMT = "obmc-chassis-powercycle@{0}.target"
CHASSIS_POWERCYCLE_FMT = "../${CHASSIS_POWERCYCLE_SVC}:${CHASSIS_POWERCYCLE_TGTFMT}.requires/${CHASSIS_POWERCYCLE_SVC}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'CHASSIS_POWERCYCLE_FMT', 'OBMC_CHASSIS_INSTANCES')}"

TMPL = "phosphor-gpio-monitor@.service"
INSTFMT = "phosphor-gpio-monitor@{0}.service"
TGT = "multi-user.target"
FMT = "../${TMPL}:${TGT}.requires/${INSTFMT}"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'FMT', 'OBMC_HOST_MONITOR_INSTANCES')}"

do_install() {
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/ampere_power_util.sh ${D}${libexecdir}/${PN}/ampere_power_util.sh
}
