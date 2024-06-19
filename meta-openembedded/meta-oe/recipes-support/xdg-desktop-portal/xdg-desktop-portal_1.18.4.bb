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
	git://github.com/flatpak/xdg-desktop-portal.git;protocol=https;branch=xdg-desktop-portal-1.18 \
	file://0001-meson.build-add-a-hack-for-crosscompile.patch \
"

S = "${WORKDIR}/git"
SRCREV = "11c8a96b147aeae70e3f770313f93b367d53fedd"

FILES:${PN} += "${libdir}/systemd ${datadir}/dbus-1"

EXTRA_OEMESON += " \
	--cross-file=${WORKDIR}/meson-${PN}.cross \
	-Ddbus-service-dir=${datadir}/dbus-1/services \
"

do_write_config:append() {
    cat >${WORKDIR}/meson-${PN}.cross <<EOF
[binaries]
bwrap = '${bindir}/bwrap'
EOF
}
