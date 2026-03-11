DESCRIPTION = "obex-data-server is a D-Bus service providing high-level OBEX client and server side functionality"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "gtk+ dbus-glib dbus-glib-native imagemagick openobex bluez5"

SRC_URI = "https://ftp.osuosl.org/pub/blfs/conglomeration/${BPN}/obex-data-server-${PV}.tar.gz \
           file://obex-data-server-0.4.6-build-fixes-1.patch \
"
SRC_URI[sha256sum] = "b399465ddbd6d0217abedd9411d9d74a820effa0a6a142adc448268d3920094f"

inherit features_check autotools-brokensep pkgconfig

REQUIRED_DISTRO_FEATURES = "x11"

FILES:${PN} += "${datadir}/dbus-1/"
