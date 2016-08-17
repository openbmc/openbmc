SUMMARY = "udisks provides dbus interfaces for disks and storage devices"
LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=dd79f6dbbffdbc8e86b086a8f0c0ef43"

DEPENDS = "acl libatasmart polkit libgudev dbus-glib glib-2.0 intltool-native gnome-common-native"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

RDEPENDS_${PN} = "acl"

SRC_URI = "http://udisks.freedesktop.org/releases/udisks-${PV}.tar.bz2"
SRC_URI[md5sum] = "73632501002e6de8244f831e38b2b98e"
SRC_URI[sha256sum] = "2cfcf560447ea44cba2a683342c7062aaaf35e4eb554bed64ac2dd55a70a5fb6"

inherit autotools systemd gtk-doc gobject-introspection

S = "${WORKDIR}/udisks-${PV}"

EXTRA_OECONF = "--disable-man"

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
