DESCRIPTION = "Boost.URL is a library for manipulating Uniform Resource Identifiers (URI) and Locators (URL)"
HOMEPAGE = "https://github.com/CPPAlliance/url"
SECTION = "libs"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE_1_0.txt;md5=e4224ccaecb14d942c71d31bef20d78c"

SRC_URI = "git://github.com/CPPAlliance/url.git;branch=develop"

SRCREV = "2c867fbe284ae532f1329b87a86ad3f8cd382867"

S = "${WORKDIR}/git"

inherit cmake

DEPENDS = "boost"

BBCLASSEXTEND = "native nativesdk"
