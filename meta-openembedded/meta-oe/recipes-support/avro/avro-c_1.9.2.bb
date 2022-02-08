SUMMARY = "Apache Avro data serialization system."
HOMEPAGE = "http://apr.apache.org/"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6d502b41f76179fc84e536236f359cae"

DEPENDS = "jansson zlib xz"

BRANCH = "branch-1.9"
SRCREV = "bf20128ca6138a830b2ea13e0490f3df6b035639"
SRC_URI = "git://github.com/apache/avro;branch=${BRANCH};protocol=https \
           file://0001-cmake-Use-GNUInstallDirs-instead-of-hard-coded-paths.patch;patchdir=../../ \
          "

S = "${WORKDIR}/git/lang/c"

inherit cmake
