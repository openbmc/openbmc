DESCRIPTION = "A microbenchmark support library"
HOMEPAGE = "https://github.com/google/benchmark"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "git://github.com/google/benchmark.git;protocol=https;branch=main;tag=v${PV}"
SRCREV = "192ef10025eb2c4cdd392bc502f0c852196baa48"

EXTRA_OECMAKE = " \
    -DBUILD_SHARED_LIBS=yes \
    -DBENCHMARK_ENABLE_TESTING=no \
    -DCMAKE_BUILD_TYPE=Release \
"

# Needed with glibc 2.43 which also defines __COUNTER__ now
CXXFLAGS += "-Wno-c2y-extensions"

inherit cmake

FILES:${PN}-dev += "${libdir}/cmake"
