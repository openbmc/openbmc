SUMMARY = "OpenPOWER Host checkstop monitor application"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit obmc-phosphor-systemd
inherit allarch

DEPENDS += "phosphor-gpio-monitor"
RDEPENDS:${PN} += "phosphor-gpio-monitor-monitor"

# For now, monitoring checkstop is the only usecase
OBMC_HOST_MONITOR_INSTANCES = "checkstop"

# Copies config file having arguments for monitoring host checkstop
# via GPIO assertion
SYSTEMD_ENVIRONMENT_FILE:${PN} +="obmc/gpio/checkstop"

# This package is not supplying the unit file and also this is not a native
# recipe since state-mgmt needs this package at runtime. Unsetting this below
# variable will let the build go through
SYSTEMD_SERVICE:${PN} ?=""

# Install the override to set up a Conflicts relation
SYSTEMD_OVERRIDE:${PN} +="checkstop.conf:phosphor-gpio-monitor@checkstop.service.d/checkstop.conf"

STATES = "startmin"
GPIO_MONITOR_TMPL = "phosphor-gpio-monitor@.service"
GPIO_MONITOR_TGTFMT = "phosphor-gpio-monitor@{1}.service"
CHECKSTOP_MONITOR_FMT = "../${GPIO_MONITOR_TMPL}:obmc-host-{0}@{2}.target.wants/${GPIO_MONITOR_TGTFMT}"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'CHECKSTOP_MONITOR_FMT', 'STATES', 'OBMC_HOST_MONITOR_INSTANCES', 'OBMC_HOST_INSTANCES')}"
