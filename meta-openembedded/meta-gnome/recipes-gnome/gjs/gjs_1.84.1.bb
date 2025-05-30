SUMMARY = "Javascript bindings for GNOME"
LICENSE = "MIT & LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=8dcea832f6acf45d856abfeb2d51ec48"


DEPENDS = "mozjs-128 cairo"

inherit gnomebase gobject-introspection gettext features_check upstream-version-is-even pkgconfig multilib_script

SRC_URI[archive.sha256sum] = "44796b91318dbbe221a13909f00fd872ef92f38c68603e0e3574e46bc6bac32c"
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
    -Dskip_gtk_tests=true \
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

MULTILIB_SCRIPTS:append = "${PN}:${libexecdir}/installed-tests/gjs/GIMarshallingTests-1.0.typelib \
                    ${PN}:${libexecdir}/installed-tests/gjs/Regress-1.0.typelib \
                    ${PN}:${libexecdir}/installed-tests/gjs/Utility-1.0.typelib \
                    ${PN}:${libexecdir}/installed-tests/gjs/WarnLib-1.0.typelib \
                    "
