SUMMARY = "OpenBMC VNC server and ipKVM daemon"
DESCRIPTION = "obmc-ikvm is a vncserver for JPEG-serving V4L2 devices to allow ipKVM"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=75859989545e37968a99b631ef42722e"

DEPENDS = " libvncserver systemd sdbusplus phosphor-logging phosphor-dbus-interfaces"

SRC_URI = "git://github.com/openbmc/obmc-ikvm;branch=master;protocol=https"
SRCREV = "a4f63b38f1e72a3c34c54e275803d945b949483b"

PV = "1.0+git${SRCPV}"

SYSTEMD_SERVICE:${PN} += "start-ipkvm.service"

S = "${WORKDIR}/git"

inherit pkgconfig meson systemd
