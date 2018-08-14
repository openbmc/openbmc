SUMMARY = "JSON-GLib implements a full JSON parser using GLib and GObject"
DESCRIPTION = "Use JSON-GLib it is possible to parse and generate valid JSON\
 data structures, using a DOM-like API. JSON-GLib also offers GObject \
integration, providing the ability to serialize and deserialize GObject \
instances to and from JSON data types."
HOMEPAGE = "http://live.gnome.org/JsonGlib"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

DEPENDS = "glib-2.0"

SRC_URI[archive.md5sum] = "35107e23a7bbbc70f31c34f7b9adf1c3"
SRC_URI[archive.sha256sum] = "2d7709a44749c7318599a6829322e081915bdc73f5be5045882ed120bb686dc8"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase lib_package gobject-introspection gtk-doc gettext

# This builds both API docs (via gtk-doc) and manpages
GTKDOC_ENABLE_FLAG = "-Ddocs=true"
GTKDOC_DISABLE_FLAG = "-Ddocs=false"

GI_ENABLE_FLAG = "-Dintrospection=true"
GI_DISABLE_FLAG = "-Dintrospection=false"

EXTRA_OEMESON_append_class-target = " ${@bb.utils.contains('GTKDOC_ENABLED', 'True', '${GTKDOC_ENABLE_FLAG}', \
                                                                                    '${GTKDOC_DISABLE_FLAG}', d)} "
EXTRA_OEMESON_append_class-target = " ${@bb.utils.contains('GI_DATA_ENABLED', 'True', '${GI_ENABLE_FLAG}', \
                                                                                    '${GI_DISABLE_FLAG}', d)} "

do_install_append() {
    # FIXME: these need to be provided via ptest
    rm -rf ${D}${datadir}/installed-tests ${D}${libexecdir}
}

BBCLASSEXTEND = "native nativesdk"

# Currently it's not possible to disable gettext in Meson, so we need to force
# this back on.
USE_NLS_class-native = "yes"
