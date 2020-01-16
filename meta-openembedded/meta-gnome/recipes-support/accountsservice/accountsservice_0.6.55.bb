DESCRIPTION = "D-Bus interfaces for querying and manipulating user account information"
HOMEPAGE = "https://www.freedesktop.org/wiki/Software/AccountsService/"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = " \
    dbus \
    polkit \
"

inherit meson gobject-introspection gtk-doc features_check systemd

REQUIRED_DISTRO_FEATURES = "polkit"

SRC_URI = "https://www.freedesktop.org/software/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI_append_libc-musl = " \
    file://0001-musl-Hack-to-fix-build.patch \
    file://0002-musl-add-missing-fgetspent_r.patch \
"
SRC_URI[md5sum] = "6e4c6fbd490260cfe17de2e76f5d803a"
SRC_URI[sha256sum] = "ff2b2419a7e06bd9cb335ffe391c7409b49a0f0130b890bd54692a3986699c9b"

GTKDOC_MESON_OPTION = "gtk_doc"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "-Dsystemd=true, -Dsystemd=false -Dsystemdsystemunitdir='no', systemd"

SYSTEMD_SERVICE_${PN} = "accounts-daemon.service"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/polkit-1 \
"
