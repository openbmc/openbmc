SUMMARY = "Folks is a contact aggregation library."
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = " \
    glib-2.0 \
    libgee \
"

EXTRA_OEMESON += "-Dtests=false -Db_lto=false "

CFLAGS:append:toolchain-clang = " -Wno-error=implicit-function-declaration"
# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
GIR_MESON_OPTION = ""

PACKAGECONFIG[eds] = "-Deds_backend=true,-Deds_backend=false,evolution-data-server"
PACKAGECONFIG[bluez] = "-Dbluez_backend=true,-Dbluez_backend=false,evolution-data-server"
PACKAGECONFIG[ofono] = "-Deds_backend=true -Dofono_backend=true,-Dofono_backend=false,evolution-data-server"
PACKAGECONFIG[telepathy] = "-Dtelepathy_backend=true,-Dtelepathy_backend=false,telepathy-glib dbus-glib"
PACKAGECONFIG[import_tool] = "-Dimport_tool=true,-Dimport_tool=false,libxml2"
PACKAGECONFIG[inspect_tool] = "-Dinspect_tool=true,-Dinspect_tool=false,readline"

PACKAGECONFIG ??= ""

inherit pkgconfig gnomebase gettext gobject-introspection vala features_check

SRC_URI[archive.sha256sum] = "21f44e2bdabb1ee7f8e41bb996d10ac7daf35c78c498177db0c00f580a20a914"
