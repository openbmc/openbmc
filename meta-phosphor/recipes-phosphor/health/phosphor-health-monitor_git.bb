SUMMARY = "BMC Health Monitoring"
DESCRIPTION = "Daemon to collect and monitor bmc health statistics"
HOMEPAGE = "https://github.com/openbmc/phosphor-health-monitor"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9e69ba356fa59848ffd865152a3ccc13"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "sdeventplus"
DEPENDS += "phosphor-logging"
DEPENDS += "nlohmann-json"
SRCREV = "ce8b5ae4e7fc1c88cdd85dab8c007cd14408d93f"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-health-monitor.git;protocol=https;branch=master"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} = "phosphor-health-monitor.service"

inherit meson pkgconfig
inherit systemd

do_install:append() {
  if [ -e "${WORKDIR}/bmc_health_config.json" ]; then
    install -d ${D}${sysconfdir}/healthMon
    install -m 0644 ${WORKDIR}/bmc_health_config.json ${D}${sysconfdir}/healthMon
  fi
}
