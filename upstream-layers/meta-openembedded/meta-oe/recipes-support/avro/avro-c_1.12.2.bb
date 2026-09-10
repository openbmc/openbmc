SUMMARY = "Apache Avro data serialization system."
HOMEPAGE = "https://avro.apache.org/"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6d502b41f76179fc84e536236f359cae"

DEPENDS = "jansson zlib xz"

BRANCH = "branch-1.12"
SRCREV = "8fa2067f70e3012cb3fd9a8839cd97e8c7cc1772"
SRC_URI = " \
    git://github.com/apache/avro;branch=${BRANCH};protocol=https;tag=release-${PV} \
"
S = "${UNPACKDIR}/${BP}/lang/c"

inherit cmake pkgconfig
