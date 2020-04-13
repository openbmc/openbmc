SUMMARY = "An HTTP library implementation in C"
HOMEPAGE = "https://wiki.gnome.org/Projects/libsoup"
BUGTRACKER = "https://bugzilla.gnome.org/"
SECTION = "x11/gnome/libs"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

DEPENDS = "glib-2.0 glib-2.0-native libxml2 sqlite3 intltool-native libpsl"

SHRT_VER = "${@d.getVar('PV').split('.')[0]}.${@d.getVar('PV').split('.')[1]}"

SRC_URI = "${GNOME_MIRROR}/libsoup/${SHRT_VER}/libsoup-${PV}.tar.xz"
SRC_URI[md5sum] = "603f3a945cd6ecc1fda644d7853b3b81"
SRC_URI[sha256sum] = "2d50b12922cc516ab6a7c35844d42f9c8a331668bbdf139232743d82582b3294"

CVE_PRODUCT = "libsoup"

S = "${WORKDIR}/libsoup-${PV}"

inherit meson gettext pkgconfig upstream-version-is-even gobject-introspection gtk-doc

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'

# libsoup-gnome is entirely deprecated and just stubs in 2.42 onwards. Disable by default.
PACKAGECONFIG ??= ""
PACKAGECONFIG[gnome] = "-Dgnome=true,-Dgnome=false"
PACKAGECONFIG[gssapi] = "-Dgssapi=enabled,-Dgssapi=disabled,krb5"

EXTRA_OEMESON_append = " -Dvapi=disabled -Dtls_check=false"

GTKDOC_MESON_OPTION = "gtk_doc"

# When built without gnome support, libsoup-2.4 will contain only one shared lib
# and will therefore become subject to renaming by debian.bbclass. Prevent
# renaming in order to keep the package name consistent regardless of whether
# gnome support is enabled or disabled.
DEBIAN_NOAUTONAME_${PN} = "1"

# glib-networking is needed for SSL, proxies, etc.
RRECOMMENDS_${PN} = "glib-networking"

BBCLASSEXTEND = "native"
