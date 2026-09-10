SUMMARY = "Javascript bindings for GNOME"
HOMEPAGE = "https://gitlab.gnome.org/GNOME/gjs"
LICENSE = "LGPL-2.0-or-later AND MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=8dcea832f6acf45d856abfeb2d51ec48"

DEPENDS = "mozjs-140 cairo glib-2.0 glib-2.0-native libffi"

inherit meson pkgconfig gettext gobject-introspection features_check

SRC_URI = "${GNOME_MIRROR}/${BPN}/${@oe.utils.trim_version("${PV}", 2)}/${BP}.tar.xz;name=archive"
SRC_URI += "file://0001-Support-cross-builds-a-bit-better.patch"
SRC_URI += "file://0002-meson.build-Do-not-add-dir-installed-tests-when-inst.patch"
SRC_URI[archive.sha256sum] = "767bab80e665d672cb00563c25f0b392a9ec8c2996ed1d4454c698b4c2f0a3d9"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
GIR_MESON_OPTION = ""

# readline and profiler are meson features with value 'auto' and would otherwise
# be picked up from whatever happens to be in the sysroot.
PACKAGECONFIG ??= "readline"
PACKAGECONFIG[readline] = "-Dreadline=enabled,-Dreadline=disabled,readline"
PACKAGECONFIG[profiler] = "-Dprofiler=enabled,-Dprofiler=disabled,sysprof"

EXTRA_OEMESON = " \
    -Dinstalled_tests=false \
    -Dskip_dbus_tests=true \
    -Dskip_gtk_tests=true \
    -Ddtrace=false \
    -Dsystemtap=false \
    -Dverbose_logs=false \
    -Dbsymbolic_functions=true \
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

# The GIMarshallingTests/Regress/Utility/WarnLib typelibs that the 1.84 recipe
# listed in MULTILIB_SCRIPTS are no longer produced by the build system.

UPSTREAM_CHECK_GITTAGREGEX = "^(?P<pver>\d+\.\d*[02468]\.\d+)$"
