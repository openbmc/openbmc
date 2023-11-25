SUMMARY = "Hardware accelerated JPEG compression/decompression library"
DESCRIPTION = "libjpeg-turbo is a derivative of libjpeg that uses SIMD instructions (MMX, SSE2, NEON) to accelerate baseline JPEG compression and decompression"
HOMEPAGE = "http://libjpeg-turbo.org/"

LICENSE = "IJG & BSD-3-Clause & Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=2a8e0d8226a102f07ab63ed7fd6ce155"

DEPENDS:append:x86-64:class-target = " nasm-native"
DEPENDS:append:x86:class-target = " nasm-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz"

SRC_URI[sha256sum] = "22429507714ae147b3acacd299e82099fce5d9f456882fc28e252e4579ba2a75"
UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/libjpeg-turbo/files/"
UPSTREAM_CHECK_REGEX = "/libjpeg-turbo/files/(?P<pver>(\d+[\.\-_]*)+)/"

PE = "1"

# Drop-in replacement for jpeg
PROVIDES = "jpeg"
RPROVIDES:${PN} += "jpeg"
RREPLACES:${PN} += "jpeg"
RCONFLICTS:${PN} += "jpeg"

inherit cmake pkgconfig

export NASMENV = "--reproducible --debug-prefix-map=${WORKDIR}=${TARGET_DBGSRC_DIR}"

# Add nasm-native dependency consistently for all build arches is hard
EXTRA_OECMAKE:append:class-native = " -DWITH_SIMD=False"
EXTRA_OECMAKE:append:class-nativesdk = " -DWITH_SIMD=False"

# Work around missing x32 ABI support
EXTRA_OECMAKE:append:class-target = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", "-DWITH_SIMD=False", "", d)}"

# Work around missing non-floating point ABI support in MIPS
EXTRA_OECMAKE:append:class-target = " ${@bb.utils.contains("MIPSPKGSFX_FPU", "-nf", "-DWITH_SIMD=False", "", d)}"

EXTRA_OECMAKE:append:class-target:arm = " ${@bb.utils.contains("TUNE_FEATURES", "neon", "", "-DWITH_SIMD=False", d)}"
EXTRA_OECMAKE:append:class-target:armeb = " ${@bb.utils.contains("TUNE_FEATURES", "neon", "", "-DWITH_SIMD=False", d)}"

# Provide a workaround if Altivec unit is not present in PPC
EXTRA_OECMAKE:append:class-target:powerpc = " ${@bb.utils.contains("TUNE_FEATURES", "altivec", "", "-DWITH_SIMD=False", d)}"
EXTRA_OECMAKE:append:class-target:powerpc64 = " ${@bb.utils.contains("TUNE_FEATURES", "altivec", "", "-DWITH_SIMD=False", d)}"
EXTRA_OECMAKE:append:class-target:powerpc64le = " ${@bb.utils.contains("TUNE_FEATURES", "altivec", "", "-DWITH_SIMD=False", d)}"

DEBUG_OPTIMIZATION:append:armv4 = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"
DEBUG_OPTIMIZATION:append:armv5 = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"

PACKAGES =+ "jpeg-tools libturbojpeg"

DESCRIPTION:jpeg-tools = "The jpeg-tools package includes client programs to access libjpeg functionality.  These tools allow for the compression, decompression, transformation and display of JPEG files and benchmarking of the libjpeg library."
FILES:jpeg-tools = "${bindir}/*"

DESCRIPTION:libturbojpeg = "A SIMD-accelerated JPEG codec which provides only TurboJPEG APIs"
FILES:libturbojpeg = "${libdir}/libturbojpeg.so.*"

BBCLASSEXTEND = "native nativesdk"
