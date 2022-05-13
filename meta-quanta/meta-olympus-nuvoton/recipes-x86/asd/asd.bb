DESCRIPTION = "Intel at-scale-debug"

SRC_URI = "git://github.com/Intel-BMC/asd.git;protocol=https;branch=master"
SRCREV = "1.4.4"
PV = "0.1+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0d1c657b2ba1e8877940a8d1614ec560"

S = "${WORKDIR}/git"
inherit cmake pkgconfig
DEPENDS = "sdbusplus openssl libpam libgpiod safec"

EXTRA_OECMAKE = "-DBUILD_UT=OFF -DAPB_FREQ=10000000"
TARGET_CFLAGS += "-DHAVE_C99"

