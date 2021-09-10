SUMMARY = "Abseil is a cpp library like STL"
DESCRIPTION = "Abseil provides pieces missing from the C++ standard. Contains \
additional useful libraries like algorithm, container, debugging, hash, memory, \
meta, numeric, strings, synchronization, time, types and utility"
HOMEPAGE = "https://abseil.io/"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=df52c6edb7adc22e533b2bacc3bd3915"

PV = "20210324.2+git${SRCPV}"
SRCREV = "278e0a071885a22dcd2fd1b5576cc44757299343"
BRANCH = "lts_2021_03_24"
SRC_URI = "git://github.com/abseil/abseil-cpp;branch=${BRANCH}         \
           file://0001-absl-always-use-asm-sgidefs.h.patch             \
           file://0002-Remove-maes-option-from-cross-compilation.patch \
           file://abseil-ppc-fixes.patch \
           file://0001-Export-of-internal-Abseil-changes.patch \
          "

S = "${WORKDIR}/git"

ASNEEDED:class-native = ""
ASNEEDED:class-nativesdk = ""

inherit cmake

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON \
                 -DBUILD_TESTING=OFF    \
                 -DCMAKE_CXX_STANDARD=14 \
                "

BBCLASSEXTEND = "native nativesdk"

FILES:${PN}-dev += "${includedir} ${libdir}/cmake ${libdir}/pkgconfig"
