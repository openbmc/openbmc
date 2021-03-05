SUMMARY = "Abseil is a cpp library like STL"
DESCRIPTION = "Abseil provides pieces missing from the C++ standard. Contains \
additional useful libraries like algorithm, container, debugging, hash, memory, \
meta, numeric, strings, synchronization, time, types and utility"
HOMEPAGE = "https://abseil.io/"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=df52c6edb7adc22e533b2bacc3bd3915"

PV = "20200923+git${SRCPV}"
SRCREV = "6f9d96a1f41439ac172ee2ef7ccd8edf0e5d068c"
BRANCH = "lts_2020_09_23"
SRC_URI = "git://github.com/abseil/abseil-cpp;branch=${BRANCH}         \
           file://0001-absl-always-use-asm-sgidefs.h.patch             \
           file://0002-Remove-maes-option-from-cross-compilation.patch \
          "

S = "${WORKDIR}/git"

ASNEEDED_class-native = ""
ASNEEDED_class-nativesdk = ""

inherit cmake

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON \
                 -DBUILD_TESTING=OFF    \
                 -DCMAKE_CXX_STANDARD=14 \
                "

BBCLASSEXTEND = "native nativesdk"
ALLOW_EMPTY_${PN} = "1"

FILES_${PN} = "${libdir}/libabsl_*.so ${libdir}/cmake"
FILES_${PN}-dev = "${includedir}"
