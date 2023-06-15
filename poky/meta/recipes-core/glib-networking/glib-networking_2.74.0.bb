SUMMARY = "GLib networking extensions"
DESCRIPTION = "glib-networking contains the implementations of certain GLib networking features that cannot be implemented directly in GLib itself because of their dependencies."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/glib-networking/"
BUGTRACKER = "http://bugzilla.gnome.org"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SECTION = "libs"
DEPENDS = "glib-2.0-native glib-2.0"

SRC_URI[archive.sha256sum] = "1f185aaef094123f8e25d8fa55661b3fd71020163a0174adb35a37685cda613b"

PACKAGECONFIG ??= "openssl ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)}"

PACKAGECONFIG[gnutls] = "-Dgnutls=enabled,-Dgnutls=disabled,gnutls"
PACKAGECONFIG[openssl] = "-Dopenssl=enabled,-Dopenssl=disabled,openssl"
PACKAGECONFIG[libproxy] = "-Dlibproxy=enabled,-Dlibproxy=disabled,libproxy"
PACKAGECONFIG[tests] = "-Dinstalled_tests=true,-Dinstalled_tests=false"

EXTRA_OEMESON = "-Dgnome_proxy=disabled"

GNOMEBASEBUILDCLASS = "meson"
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
