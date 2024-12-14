SUMMARY = "C++ library for client-side URL transfers"
HOMEPAGE = "http://www.curlpp.org/"
SECTION = "libdevel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://doc/LICENSE;md5=fd0c9adf285a69aa3b4faf34384e1029"

DEPENDS = "curl"
DEPENDS:class-native = "curl-native"

SRC_URI = "git://github.com/jpbarrette/curlpp.git;branch=master;protocol=https \
           file://0001-curlpp-config.in-Remove-references-to-absolute-build.patch \
           file://0002-fix-invalid-conversion-from-int-to-CURLoption.patch"

SRCREV = "592552a165cc569dac7674cb7fc9de3dc829906f"

S = "${WORKDIR}/git"

inherit cmake pkgconfig binconfig

BBCLASSEXTEND = "native nativesdk"

do_install:append() {
    sed -e 's@[^ ]*-ffile-prefix-map=[^ "]*@@g' \
        -e 's@[^ ]*-fdebug-prefix-map=[^ "]*@@g' \
        -e 's@[^ ]*-fmacro-prefix-map=[^ "]*@@g' \
        -i ${D}${libdir}/pkgconfig/*.pc
}
