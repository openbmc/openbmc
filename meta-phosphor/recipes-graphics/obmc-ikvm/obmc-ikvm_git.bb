SUMMARY = "OpenBMC VNC server and ipKVM daemon"
DESCRIPTION = "obmc-ikvm is a vncserver for JPEG-serving V4L2 devices to allow ipKVM"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=75859989545e37968a99b631ef42722e"

DEPENDS = " libvncserver systemd sdbusplus phosphor-logging phosphor-dbus-interfaces"

SRC_URI = "git://github.com/openbmc/obmc-ikvm"
SRCREV = "4749f934f199ae257dba48891a47eade5307c828"

PV = "1.0+git${SRCPV}"

SYSTEMD_SERVICE_${PN} += "start-ipkvm.service"

S = "${WORKDIR}/git"

inherit meson systemd
