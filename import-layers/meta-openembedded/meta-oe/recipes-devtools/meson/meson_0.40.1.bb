HOMEPAGE = "http://mesonbuild.com"
SUMMARY = "A high performance build system"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = " \
    git://github.com/mesonbuild/meson.git \
    file://native_bindir.patch \
"

SRCREV = "b25d3e4d3f2b4d37029a507cc089bdde643c6240"

S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS_${PN} = "ninja python3-core python3-modules"

BBCLASSEXTEND = "native"
