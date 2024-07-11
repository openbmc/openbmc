SUMMARY = "library for CBOR"
DESCRIPTION = "C library for parsing and generating CBOR, the general-purpose schema-less binary data format."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=6f3b3881df62ca763a02d359a6e94071"

SRC_URI = "git://github.com/PJK/libcbor.git;protocol=https;branch=master"
SRCREV = "170bee2b82cdb7b2ed25af301f62cb6efdd40ec1"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release -DBUILD_SHARED_LIBS:BOOL=ON"
