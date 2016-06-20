DESCRIPTION = "libjpeg-turbo is a derivative of libjpeg that uses SIMD instructions (MMX, SSE2, NEON) to accelerate baseline JPEG compression and decompression"
HOMEPAGE = "http://libjpeg-turbo.org/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://cdjpeg.h;endline=12;md5=cad955d15145c3fdceec6855e078e953 \
                    file://jpeglib.h;endline=14;md5=dfc803dc51ae21178d1376ec73c4454d \
                    file://djpeg.c;endline=9;md5=e93a8f2061e8a0ac71c7a485c10489e2 \
"

DEPENDS = "nasm-native"

BASEPV = "${@d.getVar('PV',True).split('+')[1]}"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${BASEPV}.tar.gz"
SRC_URI[md5sum] = "86b0d5f7507c2e6c21c00219162c3c44"
SRC_URI[sha256sum] = "521bb5d3043e7ac063ce3026d9a59cc2ab2e9636c655a2515af5f4706122233e"
UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/libjpeg-turbo/files/"
UPSTREAM_CHECK_REGEX = "/libjpeg-turbo/files/(?P<pver>(\d+[\.\-_]*)+)/"

S = "${WORKDIR}/${BPN}-${BASEPV}"

# Drop-in replacement for jpeg
PROVIDES = "jpeg"
RPROVIDES_${PN} += "jpeg"
RREPLACES_${PN} += "jpeg"
RCONFLICTS_${PN} += "jpeg"

inherit autotools pkgconfig

# Work around missing x32 ABI support
EXTRA_OECONF_append_class-target = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", "--without-simd", "", d)}"

PACKAGES =+ "jpeg-tools libturbojpeg"

DESCRIPTION_jpeg-tools = "The jpeg-tools package includes client programs to access libjpeg functionality.  These tools allow for the compression, decompression, transformation and display of JPEG files and benchmarking of the libjpeg library."
FILES_jpeg-tools = "${bindir}/*"

DESCRIPTION_libturbojpeg = "A SIMD-accelerated JPEG codec which provides only TurboJPEG APIs"
FILES_libturbojpeg = "${libdir}/libturbojpeg.so.*"

BBCLASSEXTEND = "native"
