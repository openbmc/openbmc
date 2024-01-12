SUMMARY = "high level C++ wrapper for rdkafka"
DESCRIPTION = "cppkafka allows C++ applications to consume and produce messages using the Apache Kafka protocol."
HOMEPAGE = "https://github.com/mfontanini/cppkafka"
SECTION = "lib"
LICENSE = "BSD-2-Clause"

LIC_FILES_CHKSUM = " \
file://LICENSE;md5=d8b4ca15d239dc1485ef495c8f1bcc72 \
"

SRC_URI = "git://github.com/mfontanini/cppkafka;protocol=https;branch=master \
"
SRCREV = "91ac543cbd2228588dcf24a6ca357f8be0f4e5ab"

BASEPV = "0.4.0"
PV = "${BASEPV}+git"

DEPENDS = "librdkafka curl boost chrpath-replacement-native"

inherit cmake

EXTRA_OECMAKE = "-DCPPKAFKA_BUILD_SHARED=ON"

S = "${WORKDIR}/git"

do_install:append(){
    chrpath -d ${D}${libdir}/libcppkafka.so.${BASEPV}
    sed -i -e 's|${STAGING_INCDIR}|\$\{includedir\}|g' ${D}${datadir}/pkgconfig/cppkafka.pc
}
