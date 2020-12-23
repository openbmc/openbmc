SUMMARY = "Quanta Monitor HostName Service"
DESCRIPTION = "Quanta Monitor HostName Service"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit cmake systemd

DEPENDS += " \
        boost \
        sdbusplus \
        "

SRC_URI += "git://github.com/quanta-bmc/phosphor-monitor-hostname"
SRCREV := "1172ec20f8dd41d18519c2cb3ae59bbde5acd634"
S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} += "xyz.openbmc_project.MonitorHostname.service"



