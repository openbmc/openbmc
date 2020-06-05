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
SRC_URI[md5sum] = "73858c0862dd9896fb5f62ae267084a4"
SRC_URI[sha256sum] = "6d200dec3740d9ec4ec8d1180e25779c00bc749f94278c8b9021f5534db223fc"

# see http://cairographics.org/releases/ - only even minor versions are stable
UPSTREAM_CHECK_REGEX = "pixman-(?P<pver>\d+\.(\d*[02468])+(\.\d+)+)"

PE = "1"

LICENSE = "MIT & MIT-style & PD"
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

BBCLASSEXTEND = "native nativesdk"
