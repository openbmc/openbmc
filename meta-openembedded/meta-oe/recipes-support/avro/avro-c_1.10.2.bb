SUMMARY = "Apache Avro data serialization system."
HOMEPAGE = "http://apr.apache.org/"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6d502b41f76179fc84e536236f359cae"

DEPENDS = "jansson zlib xz"

BRANCH = "branch-1.10"
SRCREV = "8111cdc35430ff68dcb644306362859de40999d9"
SRC_URI = "git://github.com/apache/avro;branch=${BRANCH} \
          "

S = "${WORKDIR}/git/lang/c"

inherit cmake
