SUMMARY = "LGI is gobject-introspection based dynamic Lua binding to GObject based libraries."
HOMEPAGE = "https://https://github.com/pavouk/lgi"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a77b7838f84aa753d37f88fd9c9ccf7d"

SRC_URI = "git://github.com/pavouk/lgi.git;protocol=https;branch=master"

DEPENDS = " \
	luajit \
	luajit-native \
	cairo \
	gobject-introspection \
	gobject-introspection-native \
"

S = "${WORKDIR}/git"
SRCREV = "d7666f77e7ee33907c84f5efdef32aef2e1cc196"
SRCPV = "${PV}+${SRCREV}"

inherit meson pkgconfig

EXTRA_OEMESON += "--buildtype release -Dtests=false"

FILES:${PN} = "${libdir} ${datadir}"

# ppc64/riscv64/riscv32 is not supported on luajit
COMPATIBLE_HOST:riscv32 = "null"
COMPATIBLE_HOST:riscv64 = "null"
COMPATIBLE_HOST:powerpc64 = "null"
COMPATIBLE_HOST:powerpc64le = "null"
