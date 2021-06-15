SUMMARY = "NVMe Drives Power Control"
DESCRIPTION = "Daemon to monitor and control the power of NVMe drives"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit meson pkgconfig
inherit systemd

DEPENDS += "sdbusplus"
DEPENDS += "boost"
DEPENDS += "libgpiod"

SRC_URI = "git://github.com/quanta-bmc/nvme-power-control;protocol=git"
SRCREV = "3b39e92a8340d8ba1bd40a03af3f5a9049da3bc7"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} = "xyz.openbmc_project.Control.Nvme.Power.service"
