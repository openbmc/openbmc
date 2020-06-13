SUMMARY = "Grilo is a framework forsearching media content from various sources"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

DEPENDS = " \
    libxml2 \
    glib-2.0 \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gobject-introspection gtk-doc gettext vala

SRC_URI[archive.md5sum] = "b464ce84db6f9354996a1d47b3c613da"
SRC_URI[archive.sha256sum] = "dbfbd6082103288592af97568180b9cc81a336a274ed5160412f87675ec11a71"

GIR_MESON_OPTION = "enable-introspection"
GTKDOC_MESON_OPTION = "enable-gtk-doc"

# Note: removing 'net' from PACKAGECONFIG causes
# | bindings/vala/meson.build:15:0: ERROR: Unknown variable "grlnet_gir".
PACKAGECONFIG ??= "net"

PACKAGECONFIG[net] = "-Denable-grl-net=true, -Denable-grl-net=false, libsoup-2.4"
PACKAGECONFIG[test-ui] = "-Denable-test-ui=true, -Denable-test-ui=false, gtk+3 liboauth"

# Once we have a recipe for 'totem-plparser' this can turn into a PACKAGECONFIG
EXTRA_OEMESON = "-Denable-grl-pls=false"

