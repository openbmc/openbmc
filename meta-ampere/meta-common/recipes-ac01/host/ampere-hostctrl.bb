SUMMARY = "Ampere Computing LLC Host Control Implementation"
DESCRIPTION = "A host control implementation suitable for Ampere Computing LLC's systems"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

# For now, monitoring shutdown_ack and reboot_ack are the only usecases
OBMC_HOST_MONITOR_INSTANCES = "shutdown_ack reboot_ack"
SYSTEMD_ENVIRONMENT_FILE_${PN} +="obmc/gpio/shutdown_ack obmc/gpio/reboot_ack"

S = "${WORKDIR}"

SRC_URI = "file://ampere-host-shutdown.service \
          file://ampere-host-reset.service \
          file://ampere_power_util.sh \
          file://ampere-chassis-poweroff.service \
          file://ampere-chassis-poweron.service \
          file://ampere-host-reset-ack.service \
          file://ampere-host-force-reset.service \
          file://ampere-host-power-cycle.service \
          "

DEPENDS = "systemd virtual/obmc-gpio-monitor"
RDEPENDS_${PN} = "bash virtual/obmc-gpio-monitor"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = " \
        ampere-host-shutdown.service \
        ampere-host-reset.service \
        ampere-chassis-poweroff.service \
        ampere-chassis-poweron.service \
        ampere-host-reset-ack.service \
        ampere-host-force-reset.service \
        ampere-host-power-cycle.service \
        "
# host power control
# overwrite the host shutdown to graceful shutdown
HOST_SHUTDOWN_TMPL = "ampere-host-shutdown.service"
HOST_SHUTDOWN_TGTFMT = "obmc-host-shutdown@{0}.target"
HOST_SHUTDOWN_FMT = "../${HOST_SHUTDOWN_TMPL}:${HOST_SHUTDOWN_TGTFMT}.requires/${HOST_SHUTDOWN_TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'HOST_SHUTDOWN_FMT', 'OBMC_HOST_INSTANCES')}"

# Force the power cycle target to run the ampere power cycle
HOST_REBOOT_SVC = "ampere-host-power-cycle.service"
HOST_REBOOT_SVC_TGTFMT = "obmc-host-reboot@{0}.target"
HOST_REBOOT_SVC_FMT = "../${HOST_REBOOT_SVC}:${HOST_REBOOT_SVC_TGTFMT}.requires/${HOST_REBOOT_SVC}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'HOST_REBOOT_SVC_FMT', 'OBMC_HOST_INSTANCES')}"

# overwrite the host reset to graceful reset
HOST_WARM_REBOOT_SOFT_SVC = "ampere-host-reset.service"
HOST_WARM_REBOOT_TGTFMT = "obmc-host-warm-reboot@{0}.target"
HOST_WARM_REBOOT_SOFT_SVC_FMT = "../${HOST_WARM_REBOOT_SOFT_SVC}:${HOST_WARM_REBOOT_TGTFMT}.requires/${HOST_WARM_REBOOT_SOFT_SVC}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'HOST_WARM_REBOOT_SOFT_SVC_FMT', 'OBMC_HOST_INSTANCES')}"

# overwrite force reboot
HOST_WARM_REBOOT_FORCE_TGT = "ampere-host-force-reset.service"
HOST_WARM_REBOOT_FORCE_TGTFMT = "obmc-host-force-warm-reboot@{0}.target"
HOST_WARM_REBOOT_FORCE_TARGET_FMT = "../${HOST_WARM_REBOOT_FORCE_TGT}:${HOST_WARM_REBOOT_FORCE_TGTFMT}.requires/${HOST_WARM_REBOOT_FORCE_TGT}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'HOST_WARM_REBOOT_FORCE_TARGET_FMT', 'OBMC_HOST_INSTANCES')}"

# chassis power control
CHASSIS_POWERON_SVC = "ampere-chassis-poweron.service"
CHASSIS_POWERON_TGTFMT = "obmc-chassis-poweron@{0}.target"
CHASSIS_POWERON_FMT = "../${CHASSIS_POWERON_SVC}:${CHASSIS_POWERON_TGTFMT}.requires/${CHASSIS_POWERON_SVC}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'CHASSIS_POWERON_FMT', 'OBMC_CHASSIS_INSTANCES')}"

CHASSIS_POWEROFF_SVC = "ampere-chassis-poweroff.service"
CHASSIS_POWEROFF_TGTFMT = "obmc-chassis-poweroff@{0}.target"
CHASSIS_POWEROFF_FMT = "../${CHASSIS_POWEROFF_SVC}:${CHASSIS_POWEROFF_TGTFMT}.requires/${CHASSIS_POWEROFF_SVC}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'CHASSIS_POWEROFF_FMT', 'OBMC_CHASSIS_INSTANCES')}"

TMPL = "phosphor-gpio-monitor@.service"
INSTFMT = "phosphor-gpio-monitor@{0}.service"
TGT = "multi-user.target"
FMT = "../${TMPL}:${TGT}.requires/${INSTFMT}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_HOST_MONITOR_INSTANCES')}"

do_install() {
    install -d ${D}/usr/sbin
    install -m 0755 ${WORKDIR}/ampere_power_util.sh ${D}/${sbindir}/ampere_power_util.sh
}

