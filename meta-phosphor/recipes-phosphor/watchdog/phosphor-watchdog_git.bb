SUMMARY = "Phosphor Watchdog application"
DESCRIPTION = "Application that implements software watchdog"
HOMEPAGE = "http://github.com/openbmc/phosphor-watchdog"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit meson pkgconfig
inherit obmc-phosphor-dbus-service

RPROVIDES_${PN} += "virtual/obmc-watchdog"
PROVIDES += "virtual/obmc-watchdog"

DEPENDS += "cli11"
DEPENDS += "sdbusplus"
DEPENDS += "sdeventplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "phosphor-logging"
DEPENDS += "systemd"

SRC_URI += "git://github.com/openbmc/phosphor-watchdog"
SRCREV = "086837596cdaffd27b399a863775e68cf8f2f344"
S = "${WORKDIR}/git"

EXTRA_OEMESON = " \
        -Dtests=disabled \
        "

# Copies config file having arguments for host watchdog
SYSTEMD_ENVIRONMENT_FILE_${PN} +="obmc/watchdog/poweron"

# Install the override to set up a Conflicts relation
SYSTEMD_OVERRIDE_${PN} += "poweron.conf:phosphor-watchdog@poweron.service.d/poweron.conf"

# For now, watching PowerOn is the only usecase
OBMC_HOST_WATCHDOG_INSTANCES = "poweron"

# This is really a DBUS service but the service name is
# an argument, so making it this way.
WATCHDOG_TMPL = "phosphor-watchdog@.service"
ENABLE_WATCHDOG_TMPL = "obmc-enable-host-watchdog@.service"
SYSTEMD_SERVICE_${PN} += "${WATCHDOG_TMPL}"

# To Enable Host Watchdog early during poweron
SYSTEMD_SERVICE_${PN} += "${ENABLE_WATCHDOG_TMPL}"

WATCHDOG_TGTFMT = "phosphor-watchdog@{0}.service"
ENABLE_WATCHDOG_TGTFMT = "obmc-enable-host-watchdog@{0}.service"

WATCHDOG_FMT = "../${WATCHDOG_TMPL}:obmc-host-startmin@{1}.target.wants/${WATCHDOG_TGTFMT}"
ENABLE_WATCHDOG_FMT = "../${ENABLE_WATCHDOG_TMPL}:obmc-host-startmin@{0}.target.wants/${ENABLE_WATCHDOG_TGTFMT}"

SYSTEMD_LINK_${PN} += "${@compose_list(d, 'WATCHDOG_FMT', 'OBMC_HOST_WATCHDOG_INSTANCES', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'ENABLE_WATCHDOG_FMT', 'OBMC_HOST_INSTANCES')}"
