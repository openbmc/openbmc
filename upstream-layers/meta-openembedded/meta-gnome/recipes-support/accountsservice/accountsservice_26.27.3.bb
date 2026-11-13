DESCRIPTION = "D-Bus interfaces for querying and manipulating user account information"
HOMEPAGE = "https://www.freedesktop.org/wiki/Software/AccountsService/"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = " \
    dbus \
    glib-2.0 \
    json-c \
    polkit \
    virtual/crypt \
"

inherit meson gobject-introspection gtk-doc features_check systemd vala pkgconfig

REQUIRED_DISTRO_FEATURES = "polkit"

SRC_URI = "git://gitlab.freedesktop.org/${BPN}/${BPN}.git;protocol=https;branch=main;tag=${PV}"
SRCREV = "4e254d9624cc48224c9920b9509df0b144f7be0c"

EXTRA_OEMESON:append:libc-musl = " -Dwtmpfile=/var/log/wtmp -Dtests=false"

UPSTREAM_CHECK_URI = "https://gitlab.freedesktop.org/accountsservice/accountsservice/-/tags"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

GTKDOC_MESON_OPTION = "gtk_doc"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} admin_group"
PACKAGECONFIG[systemd] = ",,systemd"
PACKAGECONFIG[elogind] = "-Delogind=true,-Delogind=false,elogind"
PACKAGECONFIG[admin_group] = "-Dadmin_group=wheel"

SYSTEMD_SERVICE:${PN} = "accounts-daemon.service"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/polkit-1 \
"

CVE_STATUS[CVE-2023-3297] = "not-applicable-platform: The vulnerability is Ubuntu specific"
