SUMMARY = "Hardware accelerated JPEG compression/decompression library"
DESCRIPTION = "libjpeg-turbo is a derivative of libjpeg that uses SIMD instructions (MMX, SSE2, NEON) to accelerate baseline JPEG compression and decompression"
HOMEPAGE = "http://libjpeg-turbo.org/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://cdjpeg.h;endline=13;md5=8a61af33cc1c681cd5cc297150bbb5bd \
                    file://jpeglib.h;endline=16;md5=52b5eaade8d5b6a452a7693dfe52c084 \
                    file://djpeg.c;endline=11;md5=510b386442ab6a27ee241fc5669bc5ea \
                    "
DEPENDS:append:x86-64:class-target = " nasm-native"
DEPENDS:append:x86:class-target = " nasm-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz \
           file://0001-libjpeg-turbo-fix-package_qa-error.patch \
           file://CVE-2023-2804-1.patch \
           file://CVE-2023-2804-2.patch \
           "

SRC_URI[sha256sum] = "2fdc3feb6e9deb17adec9bafa3321419aa19f8f4e5dea7bf8486844ca22207bf"
UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/libjpeg-turbo/files/"
UPSTREAM_CHECK_REGEX = "/libjpeg-turbo/files/(?P<pver>(\d+[\.\-_]*)+)/"

PE = "1"

# Drop-in replacement for jpeg
PROVIDES = "jpeg"
RPROVIDES:${PN} += "jpeg"
RREPLACES:${PN} += "jpeg"
RCONFLICTS:${PN} += "jpeg"

inherit cmake pkgconfig

export NASMENV = "--reproducible --debug-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}"

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
