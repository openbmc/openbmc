DESCRIPTION = "Boost.URL is a library for manipulating Uniform Resource Identifiers (URI) and Locators (URL)"
HOMEPAGE = "https://github.com/CPPAlliance/url"
SECTION = "libs"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE_1_0.txt;md5=e4224ccaecb14d942c71d31bef20d78c"

SRC_URI = "git://github.com/CPPAlliance/url.git;branch=develop;protocol=https"

SRCREV = "d740a92d38e3a8f4d5b2153f53b82f1c98e312ab"

S = "${WORKDIR}/git"

DEPENDS = "boost"

BBCLASSEXTEND = "native nativesdk"

do_install() {
    mkdir -p ${D}/${includedir}
    cp -r ${S}/include/** ${D}/${includedir}/
}
