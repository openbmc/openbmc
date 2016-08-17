SUMMARY = "Central control for Telepathy IM connection managers"
HOMEPAGE = "http://telepathy.freedesktop.org/wiki/Mission_Control/"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499 \
                    file://src/request.h;beginline=1;endline=21;md5=f80534d9af1c33291b3b79609f196eb2"
SECTION = "libs"
DEPENDS = "libtelepathy dbus-glib gconf libxslt-native"

SRC_URI = "http://telepathy.freedesktop.org/releases/telepathy-mission-control/${BP}.tar.gz \
           file://tmc-Makefile-fix-race.patch \
          "
SRC_URI[md5sum] = "421115a35b9e427807326877f86e7f43"
SRC_URI[sha256sum] = "14ceb7d53535b43d44b8271ad11319d1d0fe6d193d154636b9e62b42799b9723"

inherit autotools-brokensep pkgconfig pythonnative

PACKAGECONFIG ??= ""
PACKAGECONFIG[upower] = "--enable-upower,--disable-upower,upower"

# to select connman or nm you need to use "connectivity" and "connman" or "nm", default is to disable both
PACKAGECONFIG[connectivity] = ",--with-connectivity=no"
PACKAGECONFIG[connman] = "--with-connectivity=connman,,connman"
PACKAGECONFIG[nm] = "--with-connectivity=nm,,networkmanager"

PACKAGES =+ " \
	libmissioncontrol \
	libmissioncontrol-config \
	libmissioncontrol-server \
	libmissioncontrol-dev \
	libmissioncontrol-config-dev \
	libmissioncontrol-server-dev \
	libmissioncontrol-dbg \
	libmissioncontrol-config-dbg \
	libmissioncontrol-server-dbg \
"

FILES_${PN} += "${datadir}/dbus* ${datadir}/glib-2.0/schemas"

FILES_libmissioncontrol = "${libdir}/libmissioncontrol.so.*"
FILES_libmissioncontrol-config = "${libdir}/libmissioncontrol-config.so.*"
FILES_libmissioncontrol-server = "${libdir}/libmissioncontrol-server.so.*"

FILES_libmissioncontrol-dev = "${libdir}/libmissioncontrol.* \
                               ${includedir}/libmissioncontrol/ \
                               ${libdir}/pkgconfig/libmissioncontrol.pc"
FILES_libmissioncontrol-config-dev = "${libdir}/libmissioncontrol-config.*"
FILES_libmissioncontrol-server-dev = "${libdir}/libmissioncontrol-server.*"

FILES_libmissioncontrol-dbg = "${libdir}/.debug/libmissioncontrol.so.*"
FILES_libmissioncontrol-config-dbg = "${libdir}/.debug/libmissioncontrol-config.so.*"
FILES_libmissioncontrol-server-dbg = "${libdir}/.debug/libmissioncontrol-server.so.*"
