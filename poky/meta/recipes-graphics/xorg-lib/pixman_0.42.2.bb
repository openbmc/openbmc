SUMMARY = "Pixman: Pixel Manipulation library"
DESCRIPTION = "Pixman provides a library for manipulating pixel regions \
-- a set of Y-X banded rectangles, image compositing using the \
Porter/Duff model and implicit mask generation for geometric primitives \
including trapezoids, triangles, and rectangles."
HOMEPAGE = "http://www.pixman.org"
SECTION = "x11/libs"
DEPENDS = "zlib"

SRC_URI = "https://www.cairographics.org/releases/${BP}.tar.gz \
           file://0001-ARM-qemu-related-workarounds-in-cpu-features-detecti.patch \
           "
SRC_URI[sha256sum] = "ea1480efada2fd948bc75366f7c349e1c96d3297d09a3fe62626e38e234a625e"

# see http://cairographics.org/releases/ - only even minor versions are stable
UPSTREAM_CHECK_REGEX = "pixman-(?P<pver>\d+\.(\d*[02468])+(\.\d+)+)"

PE = "1"

LICENSE = "MIT & PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=14096c769ae0cbb5fcb94ec468be11b3 \
                    file://pixman/pixman-matrix.c;endline=21;md5=4a018dff3e4e25302724c88ff95c2456 \
                    file://pixman/pixman-arm-neon-asm.h;endline=24;md5=9a9cc1e51abbf1da58f4d9528ec9d49b \
                   "

inherit meson pkgconfig

# These are for the tests and demos, which we don't install
EXTRA_OEMESON = "-Dgtk=disabled -Dlibpng=disabled"
# ld: pixman/libpixman-mmx.a(pixman-mmx.c.o):
# linking mips:loongson_2f module with previous mips:isa64 modules 
EXTRA_OEMESON += "-Dloongson-mmi=disabled"
# disable iwmmxt due to compile fails on most arm platforms.
EXTRA_OEMESON += "-Diwmmxt=disabled"

EXTRA_OEMESON:append:class-target:powerpc = " ${@bb.utils.contains("TUNE_FEATURES", "altivec", "-Dvmx=enabled", "-Dvmx=disabled", d)}"
EXTRA_OEMESON:append:class-target:powerpc64 = " ${@bb.utils.contains("TUNE_FEATURES", "altivec", "-Dvmx=enabled", "-Dvmx=disabled", d)}"
EXTRA_OEMESON:append:class-target:powerpc64le = " ${@bb.utils.contains("TUNE_FEATURES", "altivec", "-Dvmx=enabled", "-Dvmx=disabled", d)}"

EXTRA_OEMESON:append:armv7a = "${@bb.utils.contains("TUNE_FEATURES","neon",""," -Dneon=disabled",d)}"
EXTRA_OEMESON:append:armv7ve = "${@bb.utils.contains("TUNE_FEATURES","neon",""," -Dneon=disabled",d)}"

EXTRA_OEMESON:append:class-native = " -Dopenmp=disabled"

BBCLASSEXTEND = "native nativesdk"

CVE_STATUS[CVE-2023-37769] = "not-applicable-config: stress-test is an uninstalled test"
