SUMMARY = "C++ bindings for the glib library"
HOMEPAGE = "http://www.gtkmm.org/"
SECTION = "libs"
LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "mm-common glib-2.0 libsigc++-2.0"
inherit autotools pkgconfig

SHRT_VER = "${@d.getVar('PV',1).split('.')[0]}.${@d.getVar('PV',1).split('.')[1]}"

SRC_URI = " \
    ftp://ftp.gnome.org/pub/GNOME/sources/glibmm/${SHRT_VER}/glibmm-${PV}.tar.xz \
    file://remove-examples.patch \
"
SRC_URI[md5sum] = "32ee4150b436d097fe2506d0b0b13a75"
SRC_URI[sha256sum] = "1b0ac0425d24895507c0e0e8088a464c7ae2d289c47afa1c11f63278fc672ea8"

do_install_append() {
    install -d ${D}${datadir}/glibmm-2.4
    install -d ${D}${datadir}/aclocal

    install -m 0644 glib/glibmmconfig.h ${D}${datadir}/glibmm-2.4/
    install -m 0644 scripts/glibmm_check_perl.m4 ${D}${datadir}/aclocal/ || true
}

FILES_${PN} = "${libdir}/lib*.so.*"
FILES_${PN}-dev += "${datadir}/glibmm-* ${libdir}/glibmm-2.4/include/ ${libdir}/glibmm-2.4/proc/ ${libdir}/giomm-2.4/include/"

RDEPENDS_${PN}-dev = "perl"
