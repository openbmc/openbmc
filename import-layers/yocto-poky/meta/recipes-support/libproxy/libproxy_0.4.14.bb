SUMMARY = "Library providing automatic proxy configuration management"
HOMEPAGE = "https://github.com/libproxy/libproxy"
BUGTRACKER = "https://github.com/libproxy/libproxy/issues"
SECTION = "libs"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://utils/proxy.c;beginline=1;endline=18;md5=55152a1006d7dafbef32baf9c30a99c0"

DEPENDS = "glib-2.0"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${PV}/${BP}.tar.xz"
SRC_URI[md5sum] = "e7514f78f5432210e17a01b6b618dde2"
SRC_URI[sha256sum] = "e25d046850346a505c0617646f1e68b660092efadf665548a89c280900262ea6"

UPSTREAM_CHECK_URI = "https://github.com/libproxy/libproxy/releases"
UPSTREAM_CHECK_REGEX = "libproxy-(?P<pver>.*)\.tar"

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
SECURITY_PIE_CFLAGS_remove = "-fPIE -pie"

FILES_${PN} += "${libdir}/${BPN}/${PV}/modules"
FILES_${PN}-dev += "${datadir}/cmake"
