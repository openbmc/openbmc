SUMMARY = "Fan watchdog services"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

RDEPENDS:${PN} += "phosphor-gpio-monitor-monitor"
RDEPENDS:${PN} += "busybox"

RESET_SERVICE = "reset-fan-watchdog.service"
TGTFMT = "obmc-chassis-poweron@0.target"
RESET_FMT = "../${RESET_SERVICE}:${TGTFMT}.wants/${RESET_SERVICE}"

MONITOR_SERVICE = "fan-watchdog-monitor@.service"

WATCHDOG_SERVICE = "fan-watchdog.service"
WATCHDOG_FMT = "../${WATCHDOG_SERVICE}:${TGTFMT}.wants/${WATCHDOG_SERVICE}"

SYSTEMD_SERVICE:${PN} += "${RESET_SERVICE} ${MONITOR_SERVICE} ${WATCHDOG_SERVICE}"
SYSTEMD_LINK:${PN} += "${RESET_FMT} ${WATCHDOG_FMT}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:df-openpower = " obmc/fan-watchdog/reset-fan-watchdog.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:df-openpower = " obmc/fan-watchdog/fan-watchdog.conf"
