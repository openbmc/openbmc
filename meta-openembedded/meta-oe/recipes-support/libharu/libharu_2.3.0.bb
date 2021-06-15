SUMMARY = "free PDF library"
HOMEPAGE = "http://libharu.org"
DESCRIPTION = "libHaru is a library for generating PDF files. \
               It is free, open source, written in ANSI C and cross platform. "

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://README;md5=3ee6bc1f64d9cc7907f44840c8e50cb1"

SRC_URI = "git://github.com/libharu/libharu.git;branch=2_3 \
           file://libharu-RELEASE_2_3_0_cmake.patch \
           file://0001-Install-static-lib-into-var-libdir-rather-than-hardc.patch \
	   "

SRCREV = "4ae1d5f4c84459f130bf1b1ef4c5c330af8eca5d"

S = "${WORKDIR}/git"

inherit cmake
DEPENDS += "libpng zlib"

do_install_append() {
     mkdir -p ${D}/${datadir}/libharu
     mv ${D}/libharu ${D}/${datadir}
}
