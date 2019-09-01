SUMMARY = "C++ bindings for the glib library"
HOMEPAGE = "http://www.gtkmm.org/"
SECTION = "libs"
LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=42dfffebc56fec7527aac53b7a89d1d8 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "mm-common glib-2.0 libsigc++-2.0 glib-2.0-native"
inherit gnomebase

SHRT_VER = "${@d.getVar('PV').split('.')[0]}.${@d.getVar('PV').split('.')[1]}"

SRC_URI += " \
    file://remove-examples.patch \
"
SRC_URI[archive.md5sum] = "0f6180d185c067bdb2aa30d035f9b867"
SRC_URI[archive.sha256sum] = "a3a1b1c9805479a16c0018acd84b3bfff23a122aee9e3c5013bb81231aeef2bc"

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
