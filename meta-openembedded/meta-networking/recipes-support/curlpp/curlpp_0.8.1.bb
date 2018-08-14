SUMMARY = "C++ library for client-side URL transfers"
HOMEPAGE = "http://www.curlpp.org/"
SECTION = "libdevel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

DEPENDS = "curl"
DEPENDS_class-native = "curl-native"

SRC_URI = "git://github.com/jpbarrette/curlpp.git"

SRCREV = "592552a165cc569dac7674cb7fc9de3dc829906f"

S = "${WORKDIR}/git"

inherit cmake pkgconfig binconfig

do_install_append () {
    sed -i 's,${STAGING_DIR_TARGET},,g' ${D}${libdir}/pkgconfig/curlpp.pc
}

PACKAGES =+ "libcurlpp libcurlpp-dev libcurlpp-staticdev"

FILES_lib${BPN} = "${libdir}/lib*.so.*"

FILES_lib${BPN}-dev = "${includedir} \
    ${libdir}/pkgconfig \
    ${bindir}/*-config \
"

FILES_lib${BPN}-staticdev = "${libdir}/lib*.a"

BBCLASSEXTEND = "native nativesdk"
