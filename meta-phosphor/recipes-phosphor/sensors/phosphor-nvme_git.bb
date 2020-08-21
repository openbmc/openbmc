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

SRC_URI = "git://github.com/openbmc/phosphor-nvme.git;protocol=git"
SRCREV = "69b9618d6875d93976db536b2fa0df176e50cbdd"
S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} = "xyz.openbmc_project.nvme.manager.service"
