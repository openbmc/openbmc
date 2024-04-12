SUMMARY = "Apache Avro data serialization system."
HOMEPAGE = "http://apr.apache.org/"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=43abf34d8b9908494f83c55d213a7f89"

DEPENDS = "boost zlib xz"

BRANCH = "branch-1.11"
SRCREV = "35ff8b997738e4d983871902d47bfb67b3250734"
SRC_URI = "git://github.com/apache/avro;branch=${BRANCH};protocol=https \
           file://0001-Remove-cpp-unittest-compilation.patch \
           file://0002-Add-package-configuration-files.patch \
           file://0003-Update-CXX-standard-to-CXX14.patch \
          "

S = "${WORKDIR}/git/lang/c++"

inherit cmake pkgconfig
