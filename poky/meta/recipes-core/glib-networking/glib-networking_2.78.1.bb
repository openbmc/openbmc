SUMMARY = "GLib networking extensions"
DESCRIPTION = "glib-networking contains the implementations of certain GLib networking features that cannot be implemented directly in GLib itself because of their dependencies."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/glib-networking/"
BUGTRACKER = "http://bugzilla.gnome.org"

LICENSE = "LGPL-2.1-or-later"
LICENSE:append = "${@bb.utils.contains('PACKAGECONFIG', 'openssl', ' & Glib-Networking-OpenSSL-Exception', '', d)}"
NO_GENERIC_LICENSE[Glib-Networking-OpenSSL-Exception] = "LICENSE_EXCEPTION"

LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://LICENSE_EXCEPTION;md5=0f5be697951b5e71aff00f4a4ce66be8 \
                    file://tls/base/gtlsconnection-base.c;beginline=7;endline=22;md5=ab641ac307f3337811008ea9afe7059f"

SECTION = "libs"
DEPENDS = "glib-2.0-native glib-2.0"

SRC_URI[archive.sha256sum] = "e48f2ddbb049832cbb09230529c5e45daca9f0df0eda325f832f7379859bf09f"

# Upstream note that for the openssl backend, half the tests where this backend don't return
# the expected error code or don't work as expected so default to gnutls
PACKAGECONFIG ??= "gnutls environment ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)}"

PACKAGECONFIG[gnutls] = "-Dgnutls=enabled,-Dgnutls=disabled,gnutls"
PACKAGECONFIG[openssl] = "-Dopenssl=enabled,-Dopenssl=disabled,openssl"
PACKAGECONFIG[environment] = "-Denvironment_proxy=enabled,-Denvironment_proxy=disabled"
PACKAGECONFIG[libproxy] = "-Dlibproxy=enabled,-Dlibproxy=disabled,libproxy"
PACKAGECONFIG[tests] = "-Dinstalled_tests=true,-Dinstalled_tests=false"
PACKAGECONFIG[gnomeproxy] = "-Dgnome_proxy=enabled,-Dgnome_proxy=disabled,gsettings-desktop-schemas"

inherit gnomebase gettext upstream-version-is-even gio-module-cache ptest-gnome

SRC_URI += "file://run-ptest"
SRC_URI += "file://eagain.patch"

FILES:${PN} += "\
                ${libdir}/gio/modules/libgio*.so \
                ${datadir}/dbus-1/services/ \
                ${systemd_user_unitdir} \
                "
FILES:${PN}-dev += "${libdir}/gio/modules/libgio*.la"
FILES:${PN}-staticdev += "${libdir}/gio/modules/libgio*.a"

RDEPENDS:${PN}-ptest += "bash"

BBCLASSEXTEND = "native nativesdk"
