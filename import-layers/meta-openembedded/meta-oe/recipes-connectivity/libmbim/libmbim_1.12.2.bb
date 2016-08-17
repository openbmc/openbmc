SUMMARY = "libmbim is library for talking to WWAN devices by MBIM protocol"
DESCRIPTION = "libmbim is a glib-based library for talking to WWAN modems and devices which speak the Mobile Interface Broadband Model (MBIM) protocol"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libmbim/"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
"

DEPENDS = "glib-2.0 libgudev"

inherit autotools pkgconfig

SRC_URI = "http://www.freedesktop.org/software/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "921fb5ab3f13f1e00833e009d8f3b4f6"
SRC_URI[sha256sum] = "949351d3e3d69b81e40a49f1d187944c26149e0647a415f0227ccdc112047b29"
