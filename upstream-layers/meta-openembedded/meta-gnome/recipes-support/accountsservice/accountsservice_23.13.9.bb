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

SRC_URI = " \
	https://www.freedesktop.org/software/${BPN}/${BPN}-${PV}.tar.xz \
	file://accountsservice-23.13.9-c99-fixes.patch \
	file://00b6e12ad4044d33cc54c71c75773c5a653dad09.patch \
"
SRC_URI:append:libc-musl = " \
	file://0002-musl-add-missing-fgetspent_r.patch \
"

EXTRA_OEMESON:libc-musl += "-Dwtmpfile=/var/log/wtmp -Dtests=false"

SRC_URI[sha256sum] = "adda4cdeae24fa0992e7df3ffff9effa7090be3ac233a3edfdf69d5a9c9b924f"

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
