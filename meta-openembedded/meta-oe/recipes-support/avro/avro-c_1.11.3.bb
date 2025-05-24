SUMMARY = "Apache Avro data serialization system."
HOMEPAGE = "https://avro.apache.org/"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6d502b41f76179fc84e536236f359cae"

DEPENDS = "jansson zlib xz"

BRANCH = "branch-1.11"
SRCREV = "579a36762293fa4c9f2831e27e7af7713a0838a0"
SRC_URI = " \
    git://github.com/apache/avro;branch=${BRANCH};protocol=https \
    file://0001-AVRO-3960-C-Fix-st-ANYARGS-warning-2798.patch;patchdir=../.. \
    file://0001-AVRO-3957-C-Fix-typos-in-docs-and-examples-2795.patch;patchdir=../.. \
"
S = "${WORKDIR}/git/lang/c"

inherit cmake pkgconfig
