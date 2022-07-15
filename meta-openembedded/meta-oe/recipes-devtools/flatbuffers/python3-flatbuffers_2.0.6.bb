SUMMARY = "Memory Efficient Serialization Library - Python3 Modules"
HOMEPAGE = "https://github.com/google/flatbuffers"
SECTION = "console/tools"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://../LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "615616cb5549a34bdf288c04bc1b94bd7a65c396"
SRC_URI = "git://github.com/google/flatbuffers.git;branch=master;protocol=https"
S = "${WORKDIR}/git/python"

RDEPENDS:${PN} = "flatbuffers"

inherit setuptools3

BBCLASSEXTEND = "native"
