SUMMARY = "C++ bindings for the glib library"
HOMEPAGE = "http://www.gtkmm.org/"
SECTION = "libs"
LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=42dfffebc56fec7527aac53b7a89d1d8 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "mm-common glib-2.0 libsigc++-2.0 glib-2.0-native"
inherit autotools pkgconfig

SHRT_VER = "${@d.getVar('PV').split('.')[0]}.${@d.getVar('PV').split('.')[1]}"

SRC_URI = " \
    ftp://ftp.gnome.org/pub/GNOME/sources/glibmm/${SHRT_VER}/glibmm-${PV}.tar.xz \
    file://remove-examples.patch \
    file://0001-Glib-Threads-Private-Fix-gobj.patch \
"
SRC_URI[md5sum] = "dee5ebe309f5976c3dacfcf5c43a062b"
SRC_URI[sha256sum] = "7cc28c732b04d70ed34f0c923543129083cfb90580ea4a2b4be5b38802bf6a4a"

do_install_append() {
    install -d ${D}${datadir}/glibmm-2.4
    install -d ${D}${datadir}/aclocal

    install -m 0644 glib/glibmmconfig.h ${D}${datadir}/glibmm-2.4/
    install -m 0644 scripts/glibmm_check_perl.m4 ${D}${datadir}/aclocal/ || true

    for i in generate_wrap_init.pl gmmproc; do
        sed -i -e '1s,.*,#!${bindir}/env perl,' ${D}${libdir}/glibmm-2.4/proc/$i
    done
}

FILES_${PN} = "${libdir}/lib*.so.*"
FILES_${PN}-dev += "${datadir}/glibmm-* ${libdir}/glibmm-2.4/include/ ${libdir}/glibmm-2.4/proc/ ${libdir}/giomm-2.4/include/"

RDEPENDS_${PN}-dev = "perl"
SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"
