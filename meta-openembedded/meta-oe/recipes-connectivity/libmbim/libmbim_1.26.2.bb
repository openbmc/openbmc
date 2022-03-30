SUMMARY = "libmbim is library for talking to WWAN devices by MBIM protocol"
DESCRIPTION = "libmbim is a glib-based library for talking to WWAN modems and devices which speak the Mobile Interface Broadband Model (MBIM) protocol"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libmbim/"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
"

DEPENDS = "glib-2.0 glib-2.0-native libgudev"

inherit autotools pkgconfig bash-completion gobject-introspection

SRC_URI = "http://www.freedesktop.org/software/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "10c77bf5b5eb8c92ba80e9b519923ad9b898362bc8e1928e2bc9a17eeba649af"
