SUMMARY = "NVMe Drive Manager"
DESCRIPTION = "Daemon to monitor and report the status of NVMe drives"
HOMEPAGE = "https://github.com/openbmc/phosphor-nvme"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "sdeventplus"
DEPENDS += "phosphor-logging"
DEPENDS += "nlohmann-json"
SRCREV = "5ef51383d77fc32f5d5d314e70f860126de623e7"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-nvme.git;protocol=https;branch=master"

SYSTEMD_SERVICE:${PN} = "xyz.openbmc_project.nvme.manager.service"

inherit meson pkgconfig
inherit systemd
