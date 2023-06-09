SUMMARY = "Memory Efficient Serialization Library - Python3 Modules"
HOMEPAGE = "https://github.com/google/flatbuffers"
SECTION = "console/tools"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

require flatbuffers.inc

S = "${WORKDIR}/git/python"

RDEPENDS:${PN} = "flatbuffers"

inherit setuptools3

BBCLASSEXTEND = "native"
