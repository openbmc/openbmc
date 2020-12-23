SUMMARY = "GLib networking extensions"
DESCRIPTION = "glib-networking contains the implementations of certain GLib networking features that cannot be implemented directly in GLib itself because of their dependencies."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/glib-networking/"
BUGTRACKER = "http://bugzilla.gnome.org"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SECTION = "libs"
DEPENDS = "glib-2.0"

SRC_URI[archive.sha256sum] = "c5d7be2437fdd196eebfb70c4517b96d3ba7ec13bd496318b8f02dea383e0099"

PACKAGECONFIG ??= "gnutls ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)}"

PACKAGECONFIG[gnutls] = "-Dgnutls=enabled,-Dgnutls=disabled,gnutls"
PACKAGECONFIG[openssl] = "-Dopenssl=enabled,-Dopenssl=disabled,openssl"
PACKAGECONFIG[libproxy] = "-Dlibproxy=enabled,-Dlibproxy=disabled,libproxy"
PACKAGECONFIG[tests] = "-Dinstalled_tests=true,-Dinstalled_tests=false"

EXTRA_OEMESON = "-Dgnome_proxy=disabled"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase gettext upstream-version-is-even gio-module-cache ptest-gnome

SRC_URI += "file://run-ptest"

FILES_${PN} += "\
                ${libdir}/gio/modules/libgio*.so \
                ${datadir}/dbus-1/services/ \
                ${systemd_user_unitdir} \
                "
FILES_${PN}-dev += "${libdir}/gio/modules/libgio*.la"
FILES_${PN}-staticdev += "${libdir}/gio/modules/libgio*.a"

BBCLASSEXTEND = "native nativesdk"
