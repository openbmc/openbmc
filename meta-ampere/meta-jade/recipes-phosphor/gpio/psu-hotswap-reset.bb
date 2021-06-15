SUMMARY = "Ampere Computing LLC PSU Hot Swap Reseting application"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd
inherit allarch

DEPENDS += "virtual/obmc-gpio-monitor"
RDEPENDS_${PN} += "virtual/obmc-gpio-monitor"

OBMC_PSU_MONITOR_INSTANCES = "PSU1_B25 PSU2_B25"

# Copies config file having arguments for psu
# via GPIO assertion
SYSTEMD_ENVIRONMENT_FILE_${PN} +="obmc/gpio/PSU1_B25 \
                                  obmc/gpio/PSU2_B25 \
                                 "
SYSTEMD_SERVICE_${PN} ?= "psu1_hotswap_reset.service psu2_hotswap_reset.service"

SRC_URI += "file://ampere_psu_reset_hotswap.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/ampere_psu_reset_hotswap.sh \
        ${D}${bindir}/ampere_psu_reset_hotswap.sh
}

GPIO_MONITOR_TMPL = "phosphor-gpio-monitor@.service"
GPIO_MONITOR_TGTFMT = "phosphor-gpio-monitor@{0}.service"
TGT = "multi-user.target"
PSU_MONITOR_FMT = "../${GPIO_MONITOR_TMPL}:${TGT}.requires/${GPIO_MONITOR_TGTFMT}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'PSU_MONITOR_FMT', 'OBMC_PSU_MONITOR_INSTANCES', 'OBMC_HOST_INSTANCES')}"
