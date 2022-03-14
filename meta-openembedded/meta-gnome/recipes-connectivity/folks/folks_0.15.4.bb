SUMMARY = "Folks is a contact aggregation library."
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = " \
    dbus-glib \
    glib-2.0 \
    libgee \
    libxml2 \
    python3-dbusmock-native \
"

GNOMEBASEBUILDCLASS = "meson"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
GIR_MESON_OPTION = ""

PACKAGECONFIG[eds] = "-Deds_backend=true,-Deds_backend=false,evolution-data-server"
PACKAGECONFIG[bluez] = "-Dbluez_backend=true,-Dbluez_backend=false"
PACKAGECONFIG[ofono] = "-Dofono_backend=true,-Dofono_backend=false"
PACKAGECONFIG[telepathy] = "-Dtelepathy_backend=true,-Dtelepathy_backend=false,telepathy-glib"

PACKAGECONFIG ??= ""

inherit pkgconfig gnomebase gettext gobject-introspection vala features_check

SRC_URI[archive.sha256sum] = "e71099afc9e88fad4e757ae134bc3fd63e12b901ad62b0ed5536afb79124af5f"

