SUMMARY = "A portal frontend service for Flatpak and possibly other desktop containment frameworks."
HOMEPAGE = "https://github.com/flatpak/xdg-desktop-portal"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

REQUIRED_DISTRO_FEATURES = "polkit"

DEPENDS = " \
    json-glib \
    glib-2.0 \
    glib-2.0-native \
    flatpak \
    libportal \
    geoclue \
    pipewire \
    fuse3 \
    xmlto-native \
"

PORTAL_BACKENDS ?= " \
	${@bb.utils.contains('DISTRO_FEATURES', 'gtk+3', 'xdg-desktop-portal-gtk', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'gtk4', 'xdg-desktop-portal-gtk', '', d)} \
"

RDEPENDS:${PN} = "bubblewrap rtkit ${PORTAL_BACKENDS} fuse3-utils"

inherit meson pkgconfig python3native features_check

SRC_URI = " \
	git://github.com/flatpak/xdg-desktop-portal.git;protocol=https;branch=xdg-desktop-portal-1.22;name=main;tag=${PV} \
	git://gitlab.gnome.org/GNOME/libglnx.git;protocol=https;branch=master;name=libglnx;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/subprojects/libglnx \
	git://gitlab.gnome.org/GNOME/gvdb.git;protocol=https;branch=main;name=gvdb;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/subprojects/gvdb \
	file://0001-meson.build-add-a-hack-for-crosscompile.patch \
"

SRCREV_main = "1d20fadc304f6601452b5db65ed91197dba77041"

# this revision comes from subprojects/libglnx.wrap file of the main source repo
SRCREV_libglnx = "ff64d52116ae74f0d25e24f089db28921ea171ff"
# this revision comes from subprojects/gvdb.wrap file of the main source repo
SRCREV_gvdb = "c6f2359cc1d00f16e0a0e2527fa0bc1882b8b5ab"

SRCREV_FORMAT = "main"

FILES:${PN} += "${libdir}/systemd ${datadir}/dbus-1"

EXTRA_OEMESON += " \
	--cross-file=${WORKDIR}/meson-${PN}.cross \
	-Ddbus-service-dir=${datadir}/dbus-1/services \
	-Dtests=disabled \
"

do_write_config:append() {
    cat >${WORKDIR}/meson-${PN}.cross <<EOF
[binaries]
bwrap = '${bindir}/bwrap'
EOF
}

CVE_STATUS[CVE-2026-40354] = "fixed-version: fixed in 1.20.4"
