DESCRIPTION = "GYP is a Meta-Build system: a build system that generates other build systems."
HOMEPAGE = "https://gyp.gsrc.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ab828cb8ce4c62ee82945a11247b6bbd"
SECTION = "devel"

SRC_URI = "git://chromium.googlesource.com/external/gyp;protocol=https"
SRCREV = "fcd686f1880fa52a1ee78d3e98af1b88cb334528"

S = "${WORKDIR}/git"
PV = "0.1+git${SRCPV}"

BBCLASSEXTEND = "native nativesdk"
