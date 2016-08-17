SUMMARY = "Library providing automatic proxy configuration management"
HOMEPAGE = "http://code.google.com/p/libproxy/"
BUGTRACKER = "http://code.google.com/p/libproxy/issues/list"
SECTION = "libs"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://utils/proxy.c;beginline=1;endline=18;md5=55152a1006d7dafbef32baf9c30a99c0"

DEPENDS = "glib-2.0"

SRC_URI = "${DEBIAN_MIRROR}/main/libp/${BPN}/${BPN}_${PV}.orig.tar.gz \
           file://0001-test-Include-sys-select.h-for-select.patch \
          "

SRC_URI[md5sum] = "3cd1ae2a4abecf44b3f24d6639d2cd84"
SRC_URI[sha256sum] = "dc3f33de54163718f82b3e7c496a7de97f8862578414b8ecaad3cbfe4821864b"

inherit cmake pkgconfig

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'gnome', '', d)} gnome3"
PACKAGECONFIG[gnome] = "-DWITH_GNOME=yes,-DWITH_GNOME=no,gconf"
PACKAGECONFIG[gnome3] = "-DWITH_GNOME3=yes,-DWITH_GNOME3=no"

EXTRA_OECMAKE += " \
    -DWITH_KDE4=no \
    -DWITH_MOZJS=no \
    -DWITH_NM=no \
    -DWITH_PERL=no \
    -DWITH_PYTHON=no \
    -DWITH_WEBKIT=no \
    -DLIB_INSTALL_DIR=${libdir} \
    -DLIBEXEC_INSTALL_DIR=${libexecdir} \
"

FILES_${PN} += "${libdir}/${BPN}/${PV}/modules"
FILES_${PN}-dev += "${datadir}/cmake"
