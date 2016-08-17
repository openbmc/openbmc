DESCRIPTION = "obex-data-server is a D-Bus service providing high-level OBEX client and server side functionality"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "gtk+ dbus-glib imagemagick openobex"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES','bluez5','bluez5','bluez4',d)}"

SRC_URI = "http://tadas.dailyda.com/software/obex-data-server-${PV}.tar.gz"
SRC_URI[md5sum] = "961ca5db6fe9c97024e133cc6203cc4d"
SRC_URI[sha256sum] = "b399465ddbd6d0217abedd9411d9d74a820effa0a6a142adc448268d3920094f"

inherit autotools-brokensep pkgconfig

FILES_${PN} += "${datadir}/dbus-1/"

