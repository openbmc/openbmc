SUMMARY = "C++ bindings for the glib library"
HOMEPAGE = "http://www.gtkmm.org/"
SECTION = "libs"
LICENSE = "LGPL-2.1-only & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=42dfffebc56fec7527aac53b7a89d1d8 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "mm-common glib-2.0 libsigc++-3 glib-2.0-native"

GNOMEBASEBUILDCLASS = "meson"
GNOMEBN = "glibmm"
inherit gnomebase

SHRT_VER = "${@d.getVar('PV').split('.')[0]}.${@d.getVar('PV').split('.')[1]}"

SRC_URI[archive.sha256sum] = "8008fd8aeddcc867a3f97f113de625f6e96ef98cf7860379813a9c0feffdb520"

S = "${WORKDIR}/${GNOMEBN}-${PV}"

do_install:append() {
    for i in generate_wrap_init.pl gmmproc; do
        sed -i -e '1s,.*,#!${bindir}/env perl,' ${D}${libdir}/glibmm-2.68/proc/$i
    done
}

FILES:${PN} = "${libdir}/lib*.so.*"
FILES:${PN}-dev += "${datadir}/glibmm-* ${libdir}/${BPN}/include/ ${libdir}/${BPN}/proc/ ${libdir}/giomm-2.68/include/"

RDEPENDS:${PN}-dev = "perl"
