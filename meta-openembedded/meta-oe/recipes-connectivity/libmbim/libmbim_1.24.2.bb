SUMMARY = "libmbim is library for talking to WWAN devices by MBIM protocol"
DESCRIPTION = "libmbim is a glib-based library for talking to WWAN modems and devices which speak the Mobile Interface Broadband Model (MBIM) protocol"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libmbim/"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
"

DEPENDS = "glib-2.0 glib-2.0-native libgudev"

inherit autotools pkgconfig bash-completion gobject-introspection

SRC_URI = "http://www.freedesktop.org/software/${BPN}/${BPN}-${PV}.tar.xz"

SRC_URI[md5sum] = "6c2b490af87773c8446f37536e7411ac"
SRC_URI[sha256sum] = "32198c099987849c5f442d1cbf4b6e43e645cbdbe9cfdc197b19ddd63c6981e4"
