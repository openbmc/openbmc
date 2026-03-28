SUMMARY = "library for CBOR"
DESCRIPTION = "C library for parsing and generating CBOR, the general-purpose schema-less binary data format."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=6f3b3881df62ca763a02d359a6e94071"

SRC_URI = "git://github.com/PJK/libcbor.git;protocol=https;branch=master;tag=v${PV}"

SRCREV = "9826a4315116a588f7416912db9da6fcecd8da11"

inherit cmake

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release -DBUILD_SHARED_LIBS:BOOL=ON"
