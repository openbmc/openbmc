SUMMARY = "Library providing automatic proxy configuration management"
HOMEPAGE = "https://github.com/libproxy/libproxy"
BUGTRACKER = "https://github.com/libproxy/libproxy/issues"
SECTION = "libs"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://utils/proxy.c;beginline=1;endline=18;md5=55152a1006d7dafbef32baf9c30a99c0"

DEPENDS = "glib-2.0"

SRC_URI = "https://github.com/${BPN}/${BPN}/archive/${PV}.tar.gz"

UPSTREAM_CHECK_URI = "https://github.com/libproxy/libproxy/releases"

SRC_URI[md5sum] = "de293bb311f185a2ffa3492700a694c2"
SRC_URI[sha256sum] = "d610bc0ef81a18ba418d759c5f4f87bf7102229a9153fb397d7d490987330ffd"

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
