SUMMARY = "Javascript bindings for GNOME"
LICENSE = "MIT & LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=8dcea832f6acf45d856abfeb2d51ec48"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = "mozjs-78 gtk4"

inherit gnomebase gsettings gobject-introspection vala gettext features_check upstream-version-is-even pkgconfig

SRC_URI[archive.sha256sum] = "bbdc0eec7cf25fbc534769f6a1fb2c7a18e17b871efdb0ca58e9abf08b28003f"
SRC_URI += " \
    file://0001-Support-cross-builds-a-bit-better.patch \
    file://0002-meson.build-Do-not-add-dir-installed-tests-when-inst.patch \
"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
GIR_MESON_OPTION = ""

EXTRA_OEMESON = " \
    -Dinstalled_tests=false \
    -Dskip_dbus_tests=true \
"

LDFLAGS:append:mipsarch = " -latomic"
LDFLAGS:append:powerpc = " -latomic"
LDFLAGS:append:powerpc64 = " -latomic"
LDFLAGS:append:riscv32 = " -latomic"

FILES:${PN} += "${datadir}/gjs-1.0/lsan"

PACKAGES =+ "${PN}-valgrind"
FILES:${PN}-valgrind = "${datadir}/gjs-1.0/valgrind"
RDEPENDS:${PN}-valgrind += "valgrind"

# Valgrind not yet available on rv32/rv64
RDEPENDS:${PN}-valgrind:remove:riscv32 = "valgrind"
RDEPENDS:${PN}-valgrind:remove:riscv64 = "valgrind"
