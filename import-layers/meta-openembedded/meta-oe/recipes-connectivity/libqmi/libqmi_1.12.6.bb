SUMMARY = "libqmi is a library for talking to WWAN devices by QMI protocol"
DESCRIPTION = "libqmi is a glib-based library for talking to WWAN modems and devices which speak the Qualcomm MSM Interface (QMI) protocol"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libqmi"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
"

DEPENDS = "glib-2.0"

inherit autotools pkgconfig

SRC_URI = "http://www.freedesktop.org/software/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "24c9eb300662ba6cff0152de89bd9ec0"
SRC_URI[sha256sum] = "0857bffece4e8ddfa7f721dd9ca63b4c78de345ac9ae2faebf04062cacba3780"
