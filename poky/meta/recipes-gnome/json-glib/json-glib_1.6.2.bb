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
inherit gnomebase lib_package gobject-introspection gtk-doc gettext ptest-gnome manpages upstream-version-is-even
GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GTKDOC_MESON_OPTION = "gtk_doc"
GTKDOC_MESON_ENABLE_FLAG = 'enabled'
GTKDOC_MESON_DISABLE_FLAG = 'disabled'

SRC_URI += " file://run-ptest"
SRC_URI[archive.sha256sum] = "a33d66c6d038bda46b910c6c6d59c4e15db014e363dc997a0414c2e07d134f24"

PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)}"
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false,libxslt-native xmlto-native"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false"

BBCLASSEXTEND = "native nativesdk"

# Currently it's not possible to disable gettext in Meson, so we need to force
# this back on.
USE_NLS_class-native = "yes"
