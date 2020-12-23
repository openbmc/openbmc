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
SRCREV = "f7d2dbd6b48f3992d4a2fb1c0fe2afd746b8428a"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} = "xyz.openbmc_project.Control.Nvme.Power.service"
