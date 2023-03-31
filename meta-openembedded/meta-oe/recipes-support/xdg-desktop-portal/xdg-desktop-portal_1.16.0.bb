SUMMARY = "A portal frontend service for Flatpak and possibly other desktop containment frameworks."
HOMEPAGE = "https://github.com/flatpak/xdg-desktop-portal"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

REQUIRED_DISTRO_FEATURES = "polkit"

DEPENDS = " \
    json-glib \
    glib-2.0 \
    libportal \
    geoclue \
    pipewire \
    dbus-native \
    fuse3 \
    xmlto-native \
    flatpak \
    python3-dbus-native \
"

PORTAL_BACKENDS ?= " \
	${@bb.utils.contains('DISTRO_FEATURES', 'gtk+3', 'xdg-desktop-portal-gtk', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'gtk4', 'xdg-desktop-portal-gtk', '', d)} \
"

RDEPENDS:${PN} = "bubblewrap rtkit ${PORTAL_BACKENDS}"

inherit meson pkgconfig python3native features_check

SRC_URI = " \
	git://github.com/flatpak/xdg-desktop-portal.git;protocol=https;branch=main \
	file://0001-xdg-desktop-portal-pc-in-add-pc_sysrootdir-dir.patch \
"

S = "${WORKDIR}/git"
SRCREV = "88af6c8ca4106fcf70925355350a669848e9fd5a"

FILES:${PN} += "${libdir}/systemd ${datadir}/dbus-1"

EXTRA_OEMESON += "--cross-file=${WORKDIR}/meson-${PN}.cross"

do_write_config:append() {
    cat >${WORKDIR}/meson-${PN}.cross <<EOF
[binaries]
bwrap = '${bindir}/bwrap'
EOF
}
