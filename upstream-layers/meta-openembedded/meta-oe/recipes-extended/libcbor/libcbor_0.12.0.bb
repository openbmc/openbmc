SUMMARY = "library for CBOR"
DESCRIPTION = "C library for parsing and generating CBOR, the general-purpose schema-less binary data format."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=6f3b3881df62ca763a02d359a6e94071"

SRC_URI = "git://github.com/PJK/libcbor.git;protocol=https;branch=master \
        file://0001-allow-build-with-cmake-4.patch"

SRCREV = "ae000f44e8d2a69e1f72a738f7c0b6b4b7cc4fbf"

inherit cmake

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release -DBUILD_SHARED_LIBS:BOOL=ON"
