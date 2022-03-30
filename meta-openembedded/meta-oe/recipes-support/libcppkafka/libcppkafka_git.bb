SUMMARY = "high level C++ wrapper for rdkafka"
DESCRIPTION = "cppkafka allows C++ applications to consume and produce messages using the Apache Kafka protocol."
HOMEPAGE = "https://github.com/mfontanini/cppkafka"
SECTION = "lib"
LICENSE = "BSD-2-Clause"

LIC_FILES_CHKSUM = " \
file://LICENSE;md5=d8b4ca15d239dc1485ef495c8f1bcc72 \
"

SRC_URI = "git://github.com/mfontanini/cppkafka;protocol=https;branch=master \
           file://0001-cmake-Use-CMAKE_INSTALL_LIBDIR.patch \
"
SRCREV = "5e4b350806d561473138ce7a982e8f6cf2e77733"

DEPENDS = "librdkafka boost chrpath-replacement-native"

inherit cmake

S = "${WORKDIR}/git"

do_install:append(){
    chrpath -d ${D}${libdir}/libcppkafka.so.0.3.1
}
