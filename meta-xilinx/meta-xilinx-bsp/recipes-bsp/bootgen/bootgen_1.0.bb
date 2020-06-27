SUMMARY = "Building and installing bootgen"
DESCRIPTION = "Building and installing bootgen, a Xilinx tool that lets you stitch binary files together and generate device boot images"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=be5410bcde8eb6481a6e5d3b22e0740b"

S = "${WORKDIR}/git"

DEPENDS += "openssl"
RDEPENDS_${PN} += "openssl"

REPO ?= "git://github.com/Xilinx/bootgen.git;protocol=https"
BRANCH ?= "master"
SRCREV ?= "bb38995468d8c830cbbfc5062e903961444c0a3c"

BRANCHARG = "${@['nobranch=1', 'branch=${BRANCH}'][d.getVar('BRANCH', True) != '']}"
SRC_URI = "${REPO};${BRANCHARG}"

EXTRA_OEMAKE += 'CROSS_COMPILER="${CXX}" -C ${S}'
CXXFLAGS_append = " -std=c++0x"

TARGET_CC_ARCH += "${LDFLAGS}"

do_install() {
    install -d ${D}${bindir}
    install -Dm 0755 ${S}/bootgen ${D}${bindir}
}

FILES_${PN} = "${bindir}/bootgen"

BBCLASSEXTEND = "native nativesdk"
