SUMMARY = "Folks is a contact aggregation library."
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = " \
    glib-2.0 \
    libgee \
"

GNOMEBASEBUILDCLASS = "meson"
EXTRA_OEMESON += "-Dtests=false"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
GIR_MESON_OPTION = ""

PACKAGECONFIG[eds] = "-Deds_backend=true,-Deds_backend=false,evolution-data-server"
PACKAGECONFIG[bluez] = "-Dbluez_backend=true,-Dbluez_backend=false,evolution-data-server"
PACKAGECONFIG[ofono] = "-Deds_backend=true -Dofono_backend=true,-Dofono_backend=false,evolution-data-server"
PACKAGECONFIG[telepathy] = "-Dtelepathy_backend=true,-Dtelepathy_backend=false,telepathy-glib dbus-glib"
PACKAGECONFIG[import_tool] = "-Dimport_tool=true,-Dimport_tool=false,libxml2"
PACKAGECONFIG[inspect_tool] = "-Dinspect_tool=true,-Dinspect_tool=false"

PACKAGECONFIG ??= ""

inherit pkgconfig gnomebase gettext gobject-introspection vala features_check

SRC_URI[archive.sha256sum] = "0fff8a896330cd82aee4598324f7e541c884d0337536212723b4beb38c759086"
