SUMMARY = "C++ bindings for the glib library"
HOMEPAGE = "http://www.gtkmm.org/"
SECTION = "libs"
LICENSE = "LGPL-2.1-only & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=42dfffebc56fec7527aac53b7a89d1d8 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "mm-common glib-2.0 libsigc++-3 glib-2.0-native"

GNOMEBN = "glibmm"
inherit gnomebase

SHRT_VER = "${@d.getVar('PV').split('.')[0]}.${@d.getVar('PV').split('.')[1]}"

SRC_URI[archive.sha256sum] = "5d2e872564996f02a06d8bbac3677e7c394af8b00dd1526aebd47af842a3ef50"

S = "${WORKDIR}/${GNOMEBN}-${PV}"

FILES:${PN} = "${libdir}/lib*.so.*"
FILES:${PN}-dev += "${datadir}/glibmm-* ${libdir}/${BPN}/include/ ${libdir}/${BPN}/proc/ ${libdir}/giomm-2.68/include/"

RDEPENDS:${PN}-dev = "perl"

EXTRA_OEMESON += "--cross-file=${WORKDIR}/meson-${PN}.cross -Dmaintainer-mode=false"

do_write_config:append() {
    cat >${WORKDIR}/meson-${PN}.cross <<EOF
[binaries]
m4 = '${bindir}/m4'
perl = '${bindir}/perl'
EOF
}
