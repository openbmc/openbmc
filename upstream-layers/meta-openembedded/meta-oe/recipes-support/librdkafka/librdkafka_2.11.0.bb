SUMMARY = "the Apache Kafka C/C++ client library"
DESCRIPTION = "librdkafka is a C library implementation of the Apache Kafka protocol, \
               providing Producer, Consumer and Admin clients."
HOMEPAGE = "https://github.com/edenhill/librdkafka"
SECTION = "libs"
LICENSE = "BSD-2-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=40b04809b5d6f648f20f45143cbcb1ad"

SRC_URI = "git://github.com/edenhill/librdkafka;protocol=https;branch=master"
SRCREV = "c56a3e68483ae33622901988ab9c4085f0785c3c"

DEPENDS = "zlib openssl zstd curl"

inherit cmake

FILES:${PN} += "${datadir}"

EXTRA_OECMAKE += "-DRDKAFKA_BUILD_EXAMPLES=OFF -DRDKAFKA_BUILD_TESTS=OFF"
