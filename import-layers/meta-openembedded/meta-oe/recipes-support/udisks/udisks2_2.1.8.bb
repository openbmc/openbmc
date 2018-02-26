SUMMARY = "udisks provides dbus interfaces for disks and storage devices"
LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=dd79f6dbbffdbc8e86b086a8f0c0ef43"

DEPENDS = "acl libatasmart polkit libgudev dbus-glib glib-2.0 intltool-native gnome-common-native libxslt-native"
DEPENDS += "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

RDEPENDS_${PN} = "acl"

SRC_URI = "http://udisks.freedesktop.org/releases/udisks-${PV}.tar.bz2 \
           file://non-gnu-libc.patch \
"
SRC_URI[md5sum] = "501d11c243bd8c6c00650474cd2afaab"
SRC_URI[sha256sum] = "da416914812a77e5f4d82b81deb8c25799fd3228d27d52f7bf89a501b1857dda"

CVE_PRODUCT = "udisks"

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
