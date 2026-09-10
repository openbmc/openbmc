SUMMARY = "C++ bindings for the glib library"
HOMEPAGE = "http://www.gtkmm.org/"
SECTION = "libs"
LICENSE = "GPL-2.0-only AND LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4bf661c1e3793e55c8d1051bc5e0ae21 \
                    file://COPYING.tools;md5=570a9b3749dd0463a1778803b12a6dce"

DEPENDS = "mm-common glib-2.0 libsigc++-2.0 glib-2.0-native"


inherit gnomebase

SHRT_VER = "${@d.getVar('PV').split('.')[0]}.${@d.getVar('PV').split('.')[1]}"

SRC_URI[archive.sha256sum] = "5a026e5602085307c7dcb72b71b07261c40f80914277bef5f8d7f2ecab739bec"

FILES:${PN} = "${libdir}/lib*.so.*"
FILES:${PN}-dev += "${datadir}/glibmm-* ${libdir}/glibmm-2.4/include/ ${libdir}/glibmm-2.4/proc/ ${libdir}/giomm-2.4/include/"

RDEPENDS:${PN}-dev = "perl"

EXTRA_OEMESON += "--cross-file=${WORKDIR}/meson-${PN}.cross -Dmaintainer-mode=false"

do_write_config:append() {
    cat >${WORKDIR}/meson-${PN}.cross <<EOF
[binaries]
m4 = '${bindir_native}/m4'
perl = '${bindir_native}/perl'
EOF
}
