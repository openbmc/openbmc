SUMMARY = "Memory Efficient Serialization Library"
HOMEPAGE = "https://github.com/google/flatbuffers"
SECTION = "console/tools"
LICENSE = "Apache-2.0"

inherit python3-dir

PACKAGE_BEFORE_PN = "${PN}-compiler ${PN}-${PYTHON_PN}"

RDEPENDS:${PN}-compiler = "${PN}"
RDEPENDS:${PN}-${PYTHON_PN} = "${PN}"
RDEPENDS:${PN}-dev += "${PN}-compiler"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "a9a295fecf3fbd5a4f571f53b01f63202a3e2113"
SRC_URI = "git://github.com/google/flatbuffers.git;branch=master;protocol=https"
S = "${WORKDIR}/git"

CVE_CHECK_IGNORE += "CVE-2020-35864"

CXXFLAGS += "-fPIC"
BUILD_CXXFLAGS += "-fPIC"

# BUILD_TYPE=Release is required, otherwise flatc is not installed
EXTRA_OECMAKE += "\
    -DCMAKE_BUILD_TYPE=Release \
    -DFLATBUFFERS_BUILD_TESTS=OFF \    
    -DFLATBUFFERS_BUILD_SHAREDLIB=ON \
"

inherit cmake

do_install:append() {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    cp -rf ${S}/python/flatbuffers ${D}${PYTHON_SITEPACKAGES_DIR}
}

FILES:${PN}-compiler = "${bindir}"

FILES:${PN}-${PYTHON_PN} = "${PYTHON_SITEPACKAGES_DIR}"

BBCLASSEXTEND = "native nativesdk"
