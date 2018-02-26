SUMMARY = "Hardware accelerated JPEG compression/decompression library"
DESCRIPTION = "libjpeg-turbo is a derivative of libjpeg that uses SIMD instructions (MMX, SSE2, NEON) to accelerate baseline JPEG compression and decompression"
HOMEPAGE = "http://libjpeg-turbo.org/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://cdjpeg.h;endline=13;md5=05bab7c7ad899d85bfba60da1a1271f2 \
                    file://jpeglib.h;endline=16;md5=f67d70e547a2662c079781c72f877f72 \
                    file://djpeg.c;endline=11;md5=b90b6d2b4119f9e5807cd273f525d2af \
"
DEPENDS_append_x86-64_class-target = " nasm-native"
DEPENDS_append_x86_class-target    = " nasm-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "6b4923e297a7eaa255f08511017a8818"
SRC_URI[sha256sum] = "9098943b270388727ae61de82adec73cf9f0dbb240b3bc8b172595ebf405b528"
UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/libjpeg-turbo/files/"
UPSTREAM_CHECK_REGEX = "/libjpeg-turbo/files/(?P<pver>(\d+[\.\-_]*)+)/"

PE= "1"

# Drop-in replacement for jpeg
PROVIDES = "jpeg"
RPROVIDES_${PN} += "jpeg"
RREPLACES_${PN} += "jpeg"
RCONFLICTS_${PN} += "jpeg"

inherit autotools pkgconfig

# Add nasm-native dependency consistently for all build arches is hard
EXTRA_OECONF_append_class-native = " --without-simd"

# Work around missing x32 ABI support
EXTRA_OECONF_append_class-target = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", "--without-simd", "", d)}"

# Work around missing non-floating point ABI support in MIPS
EXTRA_OECONF_append_class-target = " ${@bb.utils.contains("MIPSPKGSFX_FPU", "-nf", "--without-simd", "", d)}"

# Provide a workaround if Altivec unit is not present in PPC
EXTRA_OECONF_append_class-target_powerpc = " ${@bb.utils.contains("TUNE_FEATURES", "altivec", "", "--without-simd", d)}"
EXTRA_OECONF_append_class-target_powerpc64 = " ${@bb.utils.contains("TUNE_FEATURES", "altivec", "", "--without-simd", d)}"

PACKAGES =+ "jpeg-tools libturbojpeg"

DESCRIPTION_jpeg-tools = "The jpeg-tools package includes client programs to access libjpeg functionality.  These tools allow for the compression, decompression, transformation and display of JPEG files and benchmarking of the libjpeg library."
FILES_jpeg-tools = "${bindir}/*"

DESCRIPTION_libturbojpeg = "A SIMD-accelerated JPEG codec which provides only TurboJPEG APIs"
FILES_libturbojpeg = "${libdir}/libturbojpeg.so.*"

BBCLASSEXTEND = "native"
