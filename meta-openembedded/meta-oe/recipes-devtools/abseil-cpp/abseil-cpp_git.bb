SUMMARY = "Abseil is a cpp library like STL"
DESCRIPTION = "Abseil provides pieces missing from the C++ standard. Contains \
additional useful libraries like algorithm, container, debugging, hash, memory, \
meta, numeric, strings, synchronization, time, types and utility"
HOMEPAGE = "https://abseil.io/"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=df52c6edb7adc22e533b2bacc3bd3915"

PV = "20230125.2"
SRCREV = "b971ac5250ea8de900eae9f95e06548d14cd95fe"
BRANCH = "lts_2023_01_25"
SRC_URI = "git://github.com/abseil/abseil-cpp;branch=${BRANCH};protocol=https \
           file://0001-absl-always-use-asm-sgidefs.h.patch             \
           file://0002-Remove-maes-option-from-cross-compilation.patch \
           file://abseil-ppc-fixes.patch \
           file://0003-Remove-neon-option-from-cross-compilation.patch \
           file://0001-direct_mmap-Use-off_t-on-linux.patch \
          "

S = "${WORKDIR}/git"

ASNEEDED:class-native = ""
ASNEEDED:class-nativesdk = ""

inherit cmake

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON \
                 -DBUILD_TESTING=OFF    \
                 -DCMAKE_CXX_STANDARD=14 \
                 -DABSL_ENABLE_INSTALL=ON \
                "

BBCLASSEXTEND = "native nativesdk"

FILES:${PN}-dev += "${includedir} ${libdir}/cmake ${libdir}/pkgconfig"
