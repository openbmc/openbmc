SUMMARY = "Pixman: Pixel Manipulation library"

DESCRIPTION = "Pixman provides a library for manipulating pixel regions \
-- a set of Y-X banded rectangles, image compositing using the \
Porter/Duff model and implicit mask generation for geometric primitives \
including trapezoids, triangles, and rectangles."

require xorg-lib-common.inc

# see http://cairographics.org/releases/ - only even minor versions are stable
UPSTREAM_CHECK_REGEX = "pixman-(?P<pver>\d+\.(\d*[02468])+(\.\d+)+)"

LICENSE = "MIT & MIT-style & PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=14096c769ae0cbb5fcb94ec468be11b3 \
                    file://pixman/pixman-matrix.c;endline=21;md5=4a018dff3e4e25302724c88ff95c2456 \
                    file://pixman/pixman-arm-neon-asm.h;endline=24;md5=9a9cc1e51abbf1da58f4d9528ec9d49b \
                   "
DEPENDS += "zlib libpng"
BBCLASSEXTEND = "native nativesdk"

PE = "1"

IWMMXT = "--disable-arm-iwmmxt"
LOONGSON_MMI = "--disable-loongson-mmi"
# If target supports neon then disable the 'simd' (ie VFPv2) fallback, otherwise disable neon.
NEON = "${@bb.utils.contains("TUNE_FEATURES", "neon", "--disable-arm-simd", "--disable-arm-neon" ,d)}"

EXTRA_OECONF = "--disable-gtk ${IWMMXT} ${LOONGSON_MMI} ${NEON}"
EXTRA_OECONF_class-native = "--disable-gtk"
EXTRA_OECONF_class-nativesdk = "--disable-gtk"

SRC_URI += "\
            file://0001-ARM-qemu-related-workarounds-in-cpu-features-detecti.patch \
	    file://asm_include.patch \
	    file://0001-test-utils-Check-for-FE_INVALID-definition-before-us.patch \
"

SRC_URI[md5sum] = "002a4fcb644ddfcb4b0e4191576a0d59"
SRC_URI[sha256sum] = "39ba3438f3d17c464b0cb8be006dacbca0ab5aee97ebde69fec7ecdbf85794a0"

REQUIRED_DISTRO_FEATURES = ""
