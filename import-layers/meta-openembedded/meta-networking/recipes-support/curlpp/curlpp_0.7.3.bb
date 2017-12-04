SUMMARY = "C++ library for client-side URL transfers"
HOMEPAGE = "http://code.google.com/p/curlpp/"
SECTION = "libdevel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

DEPENDS = "curl boost"
DEPENDS_class-native = "curl-native"

SRC_URI = "http://curlpp.googlecode.com/files/curlpp-${PV}.tar.gz \
    file://example21.cpp-remove-deprecated-code.patch \
"

SRC_URI[md5sum] = "ccc3d30d4b3b5d2cdbed635898c29485"
SRC_URI[sha256sum] = "e3f9427b27c5bddf898d383d45c0d3d5397e2056ff935d9a5cdaef6a9a653bd5"

inherit autotools-brokensep pkgconfig binconfig

EXTRA_OECONF = "--with-boost=${STAGING_DIR_HOST}${prefix}"
# Upstream is currently working on porting the code to use std::unique_ptr instead of the
# deprecated auto_ptr.  For now, ignore the issue.
CXXFLAGS += "-Wno-error=deprecated-declarations"

do_install_append () {
    sed -i 's,${STAGING_DIR_TARGET},,g' ${D}${libdir}/pkgconfig/curlpp.pc
}

PACKAGES =+ "libcurlpp libcurlpp-dev libcurlpp-staticdev"

FILES_lib${BPN} = "${libdir}/lib*.so.*"

FILES_lib${BPN}-dev = "${includedir} \
    ${libdir}/lib*.la \
    ${libdir}/pkgconfig \
    ${bindir}/*-config \
"

FILES_lib${BPN}-staticdev = "${libdir}/lib*.a"

BBCLASSEXTEND = "native nativesdk"
