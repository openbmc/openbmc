SUMMARY = "NVMe Drive Manager"
DESCRIPTION = "Daemon to monitor and report the status of NVMe drives"
HOMEPAGE = "https://github.com/openbmc/phosphor-nvme"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit meson pkgconfig
inherit systemd

DEPENDS += "sdbusplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "sdeventplus"
DEPENDS += "phosphor-logging"
DEPENDS += "nlohmann-json"

SRC_URI = "git://github.com/openbmc/phosphor-nvme.git;protocol=https;branch=master"
SRCREV = "8353c5f0c4f20b843dac5d89fb52e07e6f44c395"
S = "${WORKDIR}/git"

SYSTEMD_SERVICE:${PN} = "xyz.openbmc_project.nvme.manager.service"
