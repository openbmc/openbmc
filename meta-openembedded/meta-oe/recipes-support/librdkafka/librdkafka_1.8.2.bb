SUMMARY = "the Apache Kafka C/C++ client library"
DESCRIPTION = "librdkafka is a C library implementation of the Apache Kafka protocol, \
               providing Producer, Consumer and Admin clients."
HOMEPAGE = "https://github.com/edenhill/librdkafka"
SECTION = "libs"
LICENSE = "BSD-2-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=2be8675acbfdac48935e73897af5f646"

SRC_URI = "git://github.com/edenhill/librdkafka;protocol=https;branch=master \
           file://0001-cmake-Use-CMAKE_INSTALL_LIBDIR.patch \
          "
SRCREV = "063a9ae7a65cebdf1cc128da9815c05f91a2a996"

DEPENDS = "zlib openssl zstd"

inherit cmake

S = "${WORKDIR}/git"

FILES:${PN} += "${datadir}"

EXTRA_OECMAKE += "-DRDKAFKA_BUILD_EXAMPLES=OFF -DRDKAFKA_BUILD_TESTS=OFF"
