SUMMARY = "Library for supporting single instance GTK+ applications"
DESCRIPTION = "Unique is a library for writing single instance GTK+ applications. If you launch a single instance application twice, the second instance will either just quit or will send a message to the running instance."
HOMEPAGE = "https://wiki.gnome.org/Attic/LibUnique"
BUGTRACKER = "https://bugzilla.gnome.org/enter_bug.cgi?product=libunique"

SRC_URI = "${GNOME_MIRROR}/libunique/1.1/libunique-${PV}.tar.bz2 \
           file://fix_for_compile_with_gcc-4.6.0.patch \
           file://noconst.patch \
           file://build.patch \
           file://0001-Makefile.am-use-LIBTOOL-instead-of-hardcoded-libtool.patch \
           file://0001-test-unique-Add-format-qualifier-s-for-string.patch \
           "

SRC_URI[md5sum] = "7955769ef31f1bc4f83446dbb3625e6d"
SRC_URI[sha256sum] = "e5c8041cef8e33c55732f06a292381cb345db946cf792a4ae18aa5c66cdd4fbb"

PR = "r7"

DEPENDS = "dbus-glib-native glib-2.0 gtk+"

PACKAGECONFIG ??= "dbus"
PACKAGECONFIG[dbus] = "--enable-dbus,--disable-dbus,dbus dbus-glib"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"


inherit autotools pkgconfig gobject-introspection features_check gtk-doc

REQUIRED_DISTRO_FEATURES = "x11"

do_install_append () {
    rmdir --ignore-fail-on-non-empty ${D}${datadir}
}
