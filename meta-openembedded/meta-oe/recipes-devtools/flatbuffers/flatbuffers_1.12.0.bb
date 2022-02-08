SUMMARY = "Memory Efficient Serialization Library"
HOMEPAGE = "https://github.com/google/flatbuffers"
SECTION = "console/tools"
LICENSE = "Apache-2.0"

PACKAGE_BEFORE_PN = "${PN}-compiler"

RDEPENDS_${PN}-compiler = "${PN}"
RDEPENDS_${PN}-dev += "${PN}-compiler"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "6df40a2471737b27271bdd9b900ab5f3aec746c7"

SRC_URI = "git://github.com/google/flatbuffers.git;branch=master;protocol=https"

# affects only flatbuffers rust crate
CVE_CHECK_WHITELIST += "CVE-2020-35864"

# Make sure C++11 is used, required for example for GCC 4.9
CXXFLAGS += "-std=c++11 -fPIC"
BUILD_CXXFLAGS += "-std=c++11 -fPIC"

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
