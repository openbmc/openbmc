SUMMARY = "Hardware accelerated JPEG compression/decompression library"
DESCRIPTION = "libjpeg-turbo is a derivative of libjpeg that uses SIMD instructions (MMX, SSE2, NEON) to accelerate baseline JPEG compression and decompression"
HOMEPAGE = "http://libjpeg-turbo.org/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://cdjpeg.h;endline=13;md5=8184bcc7c4ac7b9edc6a7bc00f231d0b \
                    file://jpeglib.h;endline=16;md5=f67d70e547a2662c079781c72f877f72 \
                    file://djpeg.c;endline=11;md5=c59e19811c006cb38f82d6477134d314 \
"
DEPENDS_append_x86-64_class-target = " nasm-native"
DEPENDS_append_x86_class-target    = " nasm-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "7c82f0f6a3130ec06b8a4d0b321cbca3"
SRC_URI[sha256sum] = "b24890e2bb46e12e72a79f7e965f409f4e16466d00e1dd15d93d73ee6b592523"
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

def get_build_time(d):
    if d.getVar('SOURCE_DATE_EPOCH') != None:
        import datetime
        return " --with-build-date="+ datetime.datetime.fromtimestamp(float(d.getVar('SOURCE_DATE_EPOCH'))).strftime("%Y%m%d")
    return ""

EXTRA_OECONF_append_class-target = "${@get_build_time(d)}"

PACKAGES =+ "jpeg-tools libturbojpeg"

DESCRIPTION_jpeg-tools = "The jpeg-tools package includes client programs to access libjpeg functionality.  These tools allow for the compression, decompression, transformation and display of JPEG files and benchmarking of the libjpeg library."
FILES_jpeg-tools = "${bindir}/*"

DESCRIPTION_libturbojpeg = "A SIMD-accelerated JPEG codec which provides only TurboJPEG APIs"
FILES_libturbojpeg = "${libdir}/libturbojpeg.so.*"

BBCLASSEXTEND = "native"
