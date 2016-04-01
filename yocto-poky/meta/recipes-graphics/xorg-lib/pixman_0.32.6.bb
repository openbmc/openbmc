SUMMARY = "Pixman: Pixel Manipulation library"

DESCRIPTION = "Pixman provides a library for manipulating pixel regions \
-- a set of Y-X banded rectangles, image compositing using the \
Porter/Duff model and implicit mask generation for geometric primitives \
including trapezoids, triangles, and rectangles."

require xorg-lib-common.inc

LICENSE = "MIT & MIT-style & PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=14096c769ae0cbb5fcb94ec468be11b3 \
                    file://pixman/pixman-matrix.c;endline=25;md5=ba6e8769bfaaee2c41698755af04c4be \
                    file://pixman/pixman-arm-neon-asm.h;endline=24;md5=9a9cc1e51abbf1da58f4d9528ec9d49b \
                   "
DEPENDS += "zlib libpng"
BBCLASSEXTEND = "native nativesdk"

PE = "1"

IWMMXT = "--disable-arm-iwmmxt"
LOONGSON_MMI = "--disable-loongson-mmi"
NEON = " --disable-arm-neon "
NEON_class-nativesdk = " --disable-arm-neon "
NEON_armv7a = " "
NEON_armv7a-vfp-neon = " "

EXTRA_OECONF = "--disable-gtk ${IWMMXT} ${LOONGSON_MMI} ${NEON}"
EXTRA_OECONF_class-native = "--disable-gtk"

SRC_URI += "\
            file://0001-ARM-qemu-related-workarounds-in-cpu-features-detecti.patch \
            file://mips-export-revert.patch \
	    file://asm_include.patch \
	    file://0001-v3-test-add-a-check-for-FE_DIVBYZERO.patch \
"

SRC_URI[md5sum] = "8a9e8f14743a39cf303803f369c1f344"
SRC_URI[sha256sum] = "201fc0d7d6bc0017496f2bd27b3ca14224aea0df6b624c5ee2dc0307a4ff14a4"

REQUIRED_DISTRO_FEATURES = ""
