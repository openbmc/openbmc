SUMMARY = "Hardware accelerated JPEG compression/decompression library"
DESCRIPTION = "libjpeg-turbo is a derivative of libjpeg that uses SIMD instructions (MMX, SSE2, NEON) to accelerate baseline JPEG compression and decompression"
HOMEPAGE = "http://libjpeg-turbo.org/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://cdjpeg.h;endline=13;md5=8184bcc7c4ac7b9edc6a7bc00f231d0b \
                    file://jpeglib.h;endline=16;md5=7ea97dc83b0f59052ee837e61ef0e08f \
                    file://djpeg.c;endline=11;md5=c59e19811c006cb38f82d6477134d314 \
"
DEPENDS_append_x86-64_class-target = " nasm-native"
DEPENDS_append_x86_class-target    = " nasm-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz \
           file://0001-libjpeg-turbo-fix-package_qa-error.patch \
           file://0001-libjpeg-turbo-fix-wrongly-defined-define-HAVE_STDLIB.patch"

SRC_URI[md5sum] = "b12a3fcf1d078db38410f27718a91b83"
SRC_URI[sha256sum] = "778876105d0d316203c928fd2a0374c8c01f755d0a00b12a1c8934aeccff8868"
UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/libjpeg-turbo/files/"
UPSTREAM_CHECK_REGEX = "/libjpeg-turbo/files/(?P<pver>(\d+[\.\-_]*)+)/"

PE= "1"

# Drop-in replacement for jpeg
PROVIDES = "jpeg"
RPROVIDES_${PN} += "jpeg"
RREPLACES_${PN} += "jpeg"
RCONFLICTS_${PN} += "jpeg"

inherit cmake pkgconfig

# Add nasm-native dependency consistently for all build arches is hard
EXTRA_OECMAKE_append_class-native = " -DWITH_SIMD=False"

# Work around missing x32 ABI support
EXTRA_OECMAKE_append_class-target = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", "-DWITH_SIMD=False", "", d)}"

# Work around missing non-floating point ABI support in MIPS
EXTRA_OECMAKE_append_class-target = " ${@bb.utils.contains("MIPSPKGSFX_FPU", "-nf", "-DWITH_SIMD=False", "", d)}"

# Provide a workaround if Altivec unit is not present in PPC
EXTRA_OECMAKE_append_class-target_powerpc = " ${@bb.utils.contains("TUNE_FEATURES", "altivec", "", "-DWITH_SIMD=False", d)}"
EXTRA_OECMAKE_append_class-target_powerpc64 = " ${@bb.utils.contains("TUNE_FEATURES", "altivec", "", "-DWITH_SIMD=False", d)}"

DEBUG_OPTIMIZATION_append_armv4 = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"
DEBUG_OPTIMIZATION_append_armv5 = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"

PACKAGES =+ "jpeg-tools libturbojpeg"

DESCRIPTION_jpeg-tools = "The jpeg-tools package includes client programs to access libjpeg functionality.  These tools allow for the compression, decompression, transformation and display of JPEG files and benchmarking of the libjpeg library."
FILES_jpeg-tools = "${bindir}/*"

DESCRIPTION_libturbojpeg = "A SIMD-accelerated JPEG codec which provides only TurboJPEG APIs"
FILES_libturbojpeg = "${libdir}/libturbojpeg.so.*"

BBCLASSEXTEND = "native"
