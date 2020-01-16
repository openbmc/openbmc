SUMMARY = "Apache Avro data serialization system."
HOMEPAGE = "http://apr.apache.org/"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6d502b41f76179fc84e536236f359cae"

DEPENDS = "jansson zlib xz"

BRANCH = "branch-1.9"
SRCREV = "89218262cde62e98fcb3778b86cd3f03056c54f3"
SRC_URI = "git://github.com/apache/avro;branch=${BRANCH} \
           file://0001-Allow-avro-C-to-be-built-on-musl-based-systems.patch;patchdir=../../ \
           file://0001-cmake-Use-GNUInstallDirs-instead-of-hard-coded-paths.patch;patchdir=../../ \
          "

S = "${WORKDIR}/git/lang/c"

inherit cmake
