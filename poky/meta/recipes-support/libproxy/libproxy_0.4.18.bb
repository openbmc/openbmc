SUMMARY = "Library providing automatic proxy configuration management"
DESCRIPTION = "libproxy  provides  interfaces  to  get  the proxy that will be \
used to access network resources. It uses various plugins to get proxy \
configuration  via different mechanisms (e.g. environment variables or \
desktop settings)."
HOMEPAGE = "https://github.com/libproxy/libproxy"
BUGTRACKER = "https://github.com/libproxy/libproxy/issues"
SECTION = "libs"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://utils/proxy.c;beginline=1;endline=18;md5=55152a1006d7dafbef32baf9c30a99c0"

DEPENDS = "glib-2.0"

SRC_URI = "git://github.com/libproxy/libproxy;protocol=https;branch=main"
SRCREV = "caccaf28e3df6ea612d2d4b39f781c4324019fdb"
S = "${WORKDIR}/git"

inherit cmake pkgconfig

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'gnome', '', d)} gnome3"
PACKAGECONFIG[gnome] = "-DWITH_GNOME=yes,-DWITH_GNOME=no,gconf"
PACKAGECONFIG[gnome3] = "-DWITH_GNOME3=yes,-DWITH_GNOME3=no"

EXTRA_OECMAKE += " \
    -DWITH_KDE=no \
    -DWITH_MOZJS=no \
    -DWITH_NM=no \
    -DWITH_PERL=no \
    -DWITH_PYTHON2=no \
    -DWITH_PYTHON3=no \
    -DWITH_WEBKIT=no \
    -DWITH_SYSCONFIG=no \
    -DLIB_INSTALL_DIR=${libdir} \
    -DLIBEXEC_INSTALL_DIR=${libexecdir} \
"
SECURITY_PIE_CFLAGS:remove = "-fPIE -pie"

FILES:${PN} += "${libdir}/${BPN}/${PV}/modules"
