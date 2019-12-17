SUMMARY = "Memory Efficient Serialization Library"
HOMEPAGE = "https://github.com/google/flatbuffers"
SECTION = "console/tools"
LICENSE = "Apache-2.0"

PACKAGE_BEFORE_PN = "${PN}-compiler"

RDEPENDS_${PN}-compiler = "${PN}"
RDEPENDS_${PN}-dev += "${PN}-compiler"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a873c5645c184d51e0f9b34e1d7cf559"

SRCREV = "9e7e8cbe9f675123dd41b7c62868acad39188cae"

SRC_URI = "git://github.com/google/flatbuffers.git \
           file://0001-Add-detection-of-strtoull_l-function.patch \
          "

# Make sure C++11 is used, required for example for GCC 4.9
CXXFLAGS += "-std=c++11"
BUILD_CXXFLAGS += "-std=c++11"

# BUILD_TYPE=Release is required, otherwise flatc is not installed
EXTRA_OECMAKE += "\
    -DCMAKE_BUILD_TYPE=Release \
    -DFLATBUFFERS_BUILD_TESTS=OFF \    
    -DFLATBUFFERS_BUILD_SHAREDLIB=ON \
"

inherit cmake

S = "${WORKDIR}/git"

FILES_${PN}-compiler = "${bindir}"

BBCLASSEXTEND = "native nativesdk"
