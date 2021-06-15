SUMMARY = "the Apache Kafka C/C++ client library"
DESCRIPTION = "librdkafka is a C library implementation of the Apache Kafka protocol, providing Producer, Consumer and Admin clients."
HOMEPAGE = "https://github.com/edenhill/librdkafka"
SECTION = "libs"
LICENSE = "BSD-2-Clause"

LIC_FILES_CHKSUM = " \
file://LICENSE;md5=2be8675acbfdac48935e73897af5f646 \
"

SRC_URI = "git://github.com/edenhill/librdkafka;protocol=https \
            file://0001_fix_absolute_path_usage.patch"
SRCREV = "1a722553638bba85dbda5050455f7b9a5ef302de"

DEPENDS = "zlib openssl zstd"

inherit cmake

S = "${WORKDIR}/git"

FILES_${PN} += "${datadir}"

EXTRA_OECMAKE += "-DRDKAFKA_BUILD_EXAMPLES=OFF -DRDKAFKA_BUILD_TESTS=OFF"