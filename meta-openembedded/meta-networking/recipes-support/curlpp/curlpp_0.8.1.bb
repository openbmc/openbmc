SUMMARY = "C++ library for client-side URL transfers"
HOMEPAGE = "http://www.curlpp.org/"
SECTION = "libdevel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://doc/LICENSE;md5=fd0c9adf285a69aa3b4faf34384e1029"

DEPENDS = "curl"
DEPENDS_class-native = "curl-native"

SRC_URI = "git://github.com/jpbarrette/curlpp.git;branch=master;protocol=https"

SRCREV = "592552a165cc569dac7674cb7fc9de3dc829906f"

S = "${WORKDIR}/git"

inherit cmake pkgconfig binconfig

BBCLASSEXTEND = "native nativesdk"
