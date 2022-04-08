DESCRIPTION = "D-Bus interfaces for querying and manipulating user account information"
HOMEPAGE = "https://www.freedesktop.org/wiki/Software/AccountsService/"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = " \
    dbus \
    polkit \
"

inherit meson gobject-introspection gtk-doc features_check systemd vala

REQUIRED_DISTRO_FEATURES = "polkit"

SRC_URI = "https://www.freedesktop.org/software/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI:append:libc-musl = " \
    file://0001-musl-Hack-to-fix-build.patch \
    file://0002-musl-add-missing-fgetspent_r.patch \
"
SRC_URI[sha256sum] = "909997a76919fe7dc138a9a01cea70bd622d5a932dbc9fb13010113023a7a391"

GTKDOC_MESON_OPTION = "gtk_doc"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = ", -Dsystemdsystemunitdir='no', systemd"

SYSTEMD_SERVICE:${PN} = "accounts-daemon.service"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/polkit-1 \
"
