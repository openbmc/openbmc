SUMMARY = "OpenBMC VNC server and ipKVM daemon"
DESCRIPTION = "obmc-ikvm is a vncserver for JPEG-serving V4L2 devices to allow ipKVM"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=75859989545e37968a99b631ef42722e"

DEPENDS = " libvncserver systemd sdbusplus phosphor-logging phosphor-dbus-interfaces"

SRC_URI = "git://github.com/openbmc/obmc-ikvm"
SRCREV = "ee09e3033a453565034b2b9bf4f2e0cbc8323ccd"

PV = "1.0+git${SRCPV}"

SYSTEMD_SERVICE:${PN} += "start-ipkvm.service"

S = "${WORKDIR}/git"

inherit pkgconfig meson systemd
