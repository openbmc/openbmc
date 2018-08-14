SUMMARY = "Memory Efficient Serialization Library"
HOMEPAGE = "https://github.com/google/flatbuffers"
SECTION = "console/tools"
LICENSE = "Apache-2.0"

PACKAGE_BEFORE_PN = "${PN}-compiler"

RDEPENDS_${PN}-compiler = "${PN}"
RDEPENDS_${PN}-dev += "${PN}-compiler"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a873c5645c184d51e0f9b34e1d7cf559"

SRCREV = "25a15950f5a24d7217689739ed8f6dac64912d62"

SRC_URI = "git://github.com/google/flatbuffers.git \
           file://0001-correct-version-for-so-lib.patch \
           file://0001-flatbuffers-Move-EndianSwap-template-to-flatbuffers-.patch \
           file://0002-use-__builtin_bswap16-when-building-with-clang.patch \
           "

# Make sure C++11 is used, required for example for GCC 4.9
CXXFLAGS += "-std=c++11"
BUILD_CXXFLAGS += "-std=c++11"

EXTRA_OECMAKE += "\
    -DFLATBUFFERS_BUILD_TESTS=OFF \
    -DFLATBUFFERS_BUILD_SHAREDLIB=ON \
    -DPV=${PV} \
"

inherit cmake

S = "${WORKDIR}/git"

FILES_${PN}-compiler = "${bindir}"

BBCLASSEXTEND = "native nativesdk"
