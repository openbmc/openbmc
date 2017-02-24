SUMMARY = "udisks provides dbus interfaces for disks and storage devices"
LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=dd79f6dbbffdbc8e86b086a8f0c0ef43"

DEPENDS = "acl libatasmart polkit libgudev dbus-glib glib-2.0 intltool-native gnome-common-native"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

RDEPENDS_${PN} = "acl"

SRC_URI = "http://udisks.freedesktop.org/releases/udisks-${PV}.tar.bz2 \
           file://non-gnu-libc.patch \
"
SRC_URI[md5sum] = "8bccd36573b75286538bd5bd2c424f45"
SRC_URI[sha256sum] = "abae2bb3bdc691ca13c1e4c244630b8c881c4f3b35c207299f1b39b7bec83785"

inherit autotools systemd gtk-doc gobject-introspection

S = "${WORKDIR}/udisks-${PV}"

EXTRA_OECONF = "--disable-man --disable-gtk-doc"

FILES_${PN} += "${libdir}/polkit-1/extensions/*.so \
                ${datadir}/dbus-1/ \
                ${datadir}/polkit-1 \
                ${nonarch_base_libdir}/udev/* \
                ${exec_prefix}${nonarch_base_libdir}/udisks2/* \
"

PACKAGES =+ "${PN}-libs"

FILES_${PN} += "${datadir}/bash-completion"
FILES_${PN}-libs = "${libdir}/lib*${SOLIBS}"

FILES_${PN}-dbg += "${exec_prefix}${nonarch_base_libdir}/udisks2/.debug"

SYSTEMD_SERVICE_${PN} = "${BPN}.service"
SYSTEMD_AUTO_ENABLE = "disable"
