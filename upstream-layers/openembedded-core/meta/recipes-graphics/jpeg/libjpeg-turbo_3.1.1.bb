SUMMARY = "Hardware accelerated JPEG compression/decompression library"
DESCRIPTION = "libjpeg-turbo is a derivative of libjpeg that uses SIMD instructions (MMX, SSE2, NEON) to accelerate baseline JPEG compression and decompression"
HOMEPAGE = "http://libjpeg-turbo.org/"

LICENSE = "IJG & BSD-3-Clause & Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=023c2e8942020502d6ea709e2a5453f7"

DEPENDS:append:x86-64:class-target = " nasm-native"
DEPENDS:append:x86:class-target = " nasm-native"

SRC_URI = "${GITHUB_BASE_URI}/download/${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "aadc97ea91f6ef078b0ae3a62bba69e008d9a7db19b34e4ac973b19b71b4217c"

PE = "1"

# Drop-in replacement for jpeg
PROVIDES = "jpeg"
RPROVIDES:${PN} += "jpeg"
RREPLACES:${PN} += "jpeg"
RCONFLICTS:${PN} += "jpeg"

inherit cmake pkgconfig github-releases

export NASMENV = "--reproducible --debug-prefix-map=${WORKDIR}=${TARGET_DBGSRC_DIR}"

# The binaries have RUNPATH=$libdir, which is redundant
EXTRA_OECMAKE += "-DCMAKE_SKIP_INSTALL_RPATH=ON"

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

# libjpeg-turbo-2.0.2/simd/mips/jsimd_dspr2.S
# <instantiation>:13:5: error: invalid token in expression
# .if $17 != 0
# ^
CFLAGS:append:toolchain-clang:mipsarch = " -fno-integrated-as"

PACKAGES =+ "jpeg-tools libturbojpeg"

DESCRIPTION:jpeg-tools = "The jpeg-tools package includes client programs to access libjpeg functionality.  These tools allow for the compression, decompression, transformation and display of JPEG files and benchmarking of the libjpeg library."
FILES:jpeg-tools = "${bindir}/*"

DESCRIPTION:libturbojpeg = "A SIMD-accelerated JPEG codec which provides only TurboJPEG APIs"
FILES:libturbojpeg = "${libdir}/libturbojpeg.so.*"

BBCLASSEXTEND = "native nativesdk"
