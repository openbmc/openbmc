DESCRIPTION = "GYP is a Meta-Build system: a build system that generates other build systems."
HOMEPAGE = "https://gyp.gsrc.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ab828cb8ce4c62ee82945a11247b6bbd"
SECTION = "devel"

SRC_URI = "git://chromium.googlesource.com/external/gyp;protocol=https"
SRCREV = "caa60026e223fc501e8b337fd5086ece4028b1c6"

S = "${WORKDIR}/git"
PV = "0.1+git${SRCPV}"

inherit setuptools3

BBCLASSEXTEND = "native nativesdk"
