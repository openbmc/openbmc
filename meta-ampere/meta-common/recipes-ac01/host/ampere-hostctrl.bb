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

SRC_URI = " \
          file://ampere_power_util.sh \
          file://ampere-host-force-reset@.service \
          file://ampere-host-shutdown-ack@.service \
          "

DEPENDS = "systemd virtual/obmc-gpio-monitor"
RDEPENDS_${PN} = "bash virtual/obmc-gpio-monitor"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = " \
        ampere-host-force-reset@.service \
        ampere-host-shutdown-ack@.service \
        "

# overwrite force reboot
HOST_WARM_REBOOT_FORCE_TGT = "ampere-host-force-reset@.service"
HOST_WARM_REBOOT_FORCE_TGTFMT = "obmc-host-force-warm-reboot@{0}.target"
HOST_WARM_REBOOT_FORCE_TARGET_FMT = "../${HOST_WARM_REBOOT_FORCE_TGT}:${HOST_WARM_REBOOT_FORCE_TGTFMT}.requires/${HOST_WARM_REBOOT_FORCE_TGT}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'HOST_WARM_REBOOT_FORCE_TARGET_FMT', 'OBMC_HOST_INSTANCES')}"

TMPL = "phosphor-gpio-monitor@.service"
INSTFMT = "phosphor-gpio-monitor@{0}.service"
TGT = "multi-user.target"
FMT = "../${TMPL}:${TGT}.requires/${INSTFMT}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_HOST_MONITOR_INSTANCES')}"

do_install() {
    install -d ${D}/usr/sbin
    install -m 0755 ${WORKDIR}/ampere_power_util.sh ${D}/${sbindir}/ampere_power_util.sh
}

