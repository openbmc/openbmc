SUMMARY = "JSON-GLib implements a full JSON parser using GLib and GObject"
DESCRIPTION = "Use JSON-GLib it is possible to parse and generate valid JSON\
 data structures, using a DOM-like API. JSON-GLib also offers GObject \
integration, providing the ability to serialize and deserialize GObject \
instances to and from JSON data types."
HOMEPAGE = "https://wiki.gnome.org/Projects/JsonGlib"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/json-glib/issues"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

DEPENDS = "glib-2.0"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase lib_package gobject-introspection gi-docgen gettext ptest-gnome manpages upstream-version-is-even
GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GIDOCGEN_MESON_ENABLE_FLAG = 'enabled'
GIDOCGEN_MESON_DISABLE_FLAG = 'disabled'

SRC_URI += " file://run-ptest"
SRC_URI[archive.sha256sum] = "96ec98be7a91f6dde33636720e3da2ff6ecbb90e76ccaa49497f31a6855a490e"

PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)}"
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false,libxslt-native xmlto-native"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false"

BBCLASSEXTEND = "native nativesdk"

# Currently it's not possible to disable gettext in Meson, so we need to force
# this back on.
USE_NLS:class-native = "yes"
