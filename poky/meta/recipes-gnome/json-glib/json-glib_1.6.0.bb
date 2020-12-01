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

SRC_URI += "file://run-ptest \
            file://0001-json-glib-json-enum-types.c.in-fix-build-reproducibi.patch"
SRC_URI[archive.sha256sum] = "0d7c67602c4161ea7070fab6c5823afd9bd7f7bc955f652a50d3753b08494e73"

PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)}"
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false,libxslt-native xmlto-native"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false"

BBCLASSEXTEND = "native nativesdk"

# Currently it's not possible to disable gettext in Meson, so we need to force
# this back on.
USE_NLS_class-native = "yes"
