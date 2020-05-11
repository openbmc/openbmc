DESCRIPTION = "Open Asset Import Library is a portable Open Source library to import \
               various well-known 3D model formats in a uniform manner."
HOMEPAGE = "http://www.assimp.org/"
SECTION = "devel"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2119edef0916b0bd511cb3c731076271"

DEPENDS = "zlib"

SRC_URI = "git://github.com/assimp/assimp.git;branch=assimp_5.0_release \
           file://0001-closes-https-github.com-assimp-assimp-issues-2733-up.patch \
           file://0001-Use-ASSIMP_LIB_INSTALL_DIR-to-search-library.patch \
           "
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>(\d+(\.\d+)+))"

SRCREV = "8f0c6b04b2257a520aaab38421b2e090204b69df"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DASSIMP_BUILD_ASSIMP_TOOLS=OFF -DASSIMP_BUILD_TESTS=OFF -DASSIMP_LIB_INSTALL_DIR=${baselib}"
