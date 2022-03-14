SUMMARY = "Apache Avro data serialization system."
HOMEPAGE = "http://apr.apache.org/"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6d502b41f76179fc84e536236f359cae"

DEPENDS = "jansson zlib xz"

BRANCH = "branch-1.11"
SRCREV = "4e1fefca493029ace961b7ef8889a3722458565a"
SRC_URI = "git://github.com/apache/avro;branch=${BRANCH};protocol=https \
          "

S = "${WORKDIR}/git/lang/c"

inherit cmake pkgconfig
