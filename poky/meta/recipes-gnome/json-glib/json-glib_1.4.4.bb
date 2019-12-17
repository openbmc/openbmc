SUMMARY = "JSON-GLib implements a full JSON parser using GLib and GObject"
DESCRIPTION = "Use JSON-GLib it is possible to parse and generate valid JSON\
 data structures, using a DOM-like API. JSON-GLib also offers GObject \
integration, providing the ability to serialize and deserialize GObject \
instances to and from JSON data types."
HOMEPAGE = "https://wiki.gnome.org/Projects/JsonGlib"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/json-glib/issues"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

DEPENDS = "glib-2.0"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase lib_package gobject-introspection gtk-doc gettext ptest-gnome manpages

SRC_URI += "file://run-ptest"
SRC_URI[archive.md5sum] = "4d4bb9837f6d31e32d0ce658ae135f68"
SRC_URI[archive.sha256sum] = "720c5f4379513dc11fd97dc75336eb0c0d3338c53128044d9fabec4374f4bc47"

PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false,libxslt-native xmlto-native"

do_install_append() {
	if ! ${@bb.utils.contains('PTEST_ENABLED', '1', 'true', 'false', d)}; then
		rm -rf ${D}${datadir}/installed-tests ${D}${libexecdir}
	fi
}

BBCLASSEXTEND = "native nativesdk"

# Currently it's not possible to disable gettext in Meson, so we need to force
# this back on.
USE_NLS_class-native = "yes"
