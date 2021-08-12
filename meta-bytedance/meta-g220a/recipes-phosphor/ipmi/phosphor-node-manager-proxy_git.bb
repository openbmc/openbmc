SUMMARY = "Node Manager Proxy"
DESCRIPTION = "The Node Manager Proxy provides a simple interface for communicating \
with Management Engine via IPMB"

SRC_URI = "git://github.com/Intel-BMC/node-manager;protocol=ssh"
SRCREV = "23590b428ea26e0ed4b8225015471b962e3b3704"
PV = "0.1+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

SYSTEMD_SERVICE:${PN} = "node-manager-proxy.service"

DEPENDS = "sdbusplus \
           phosphor-logging \
           boost"

S = "${WORKDIR}/git/"
inherit cmake systemd

SRC_URI:append = " \
    file://0001-Remove-Total_Power-sensor.patch \
"
