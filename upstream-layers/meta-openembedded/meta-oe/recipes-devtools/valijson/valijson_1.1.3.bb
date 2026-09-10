SUMMARY = "Header-only C++ library for JSON Schema validation"
HOMEPAGE = "https://github.com/tristanpenman/valijson"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5c4583a434195e4f3b418e17c8ca2daf"

SRC_URI = "git://github.com/tristanpenman/valijson.git;branch=master;protocol=https;tag=v${PV}"
SRCREV = "5eac4b66f20b2c189ae40281c29e71cf39a958ec"


inherit cmake

PACKAGECONFIG ?= "boost"

PACKAGECONFIG[boost] = "-Dvalijson_EXCLUDE_BOOST=FALSE,-Dvalijson_EXCLUDE_BOOST=TRUE,boost"
PACKAGECONFIG[examples] = "-Dvalijson_BUILD_EXAMPLES=TRUE,-Dvalijson_BUILD_EXAMPLES=FALSE,curlpp"
PACKAGECONFIG[tests] = "-Dvalijson_BUILD_TESTS=TRUE,-Dvalijson_BUILD_TESTS=FALSE,curlpp"

# valijson is a header only C++ library, so the main package will be empty.
RDEPENDS:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
