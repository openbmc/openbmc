SUMMARY = "OpenPOWER Host checkstop monitor application"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${OPENPOWERBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit obmc-phosphor-systemd
inherit allarch

DEPENDS += "virtual/obmc-gpio-monitor"
RDEPENDS_${PN} += "virtual/obmc-gpio-monitor"

# For now, monitoring checkstop is the only usecase
OBMC_HOST_MONITOR_INSTANCES = "checkstop"

# Copies config file having arguments for monitoring host checkstop
# via GPIO assertion
SYSTEMD_ENVIRONMENT_FILE_${PN} +="obmc/gpio/checkstop"

# This package is not supplying the unit file and also this is not a native
# recipe since state-mgmt needs this package at runtime. Unsetting this below
# variable will let the build go through
SYSTEMD_SERVICE_${PN} ?=""

# Install the override to set up a Conflicts relation
SYSTEMD_OVERRIDE_${PN} +="checkstop.conf:phosphor-gpio-monitor@checkstop.service.d/checkstop.conf"

STATES = "startmin"
GPIO_MONITOR_TMPL = "phosphor-gpio-monitor@.service"
GPIO_MONITOR_TGTFMT = "phosphor-gpio-monitor@{1}.service"
CHECKSTOP_MONITOR_FMT = "../${GPIO_MONITOR_TMPL}:obmc-host-{0}@{2}.target.wants/${GPIO_MONITOR_TGTFMT}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'CHECKSTOP_MONITOR_FMT', 'STATES', 'OBMC_HOST_MONITOR_INSTANCES', 'OBMC_HOST_INSTANCES')}"
