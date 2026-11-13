SUMMARY = "C++ bindings for the glib library"
HOMEPAGE = "http://www.gtkmm.org/"
SECTION = "libs"
LICENSE = "GPL-2.0-only AND LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4bf661c1e3793e55c8d1051bc5e0ae21 \
                    file://COPYING.tools;md5=570a9b3749dd0463a1778803b12a6dce"

DEPENDS = "mm-common glib-2.0 libsigc++-3 glib-2.0-native"

GNOMEBN = "glibmm"
inherit gnomebase

SHRT_VER = "${@d.getVar('PV').split('.')[0]}.${@d.getVar('PV').split('.')[1]}"

SRC_URI[archive.sha256sum] = "e2efa45643f16b9fea2d6299f2f403d672eaeacddf0ff7f8094e1af9b0f5980b"

S = "${UNPACKDIR}/${GNOMEBN}-${PV}"

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
