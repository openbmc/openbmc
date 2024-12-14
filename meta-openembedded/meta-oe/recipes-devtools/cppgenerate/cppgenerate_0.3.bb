SUMMARY = "C++ Code Generator library"
BUGTRACKER = "https://github.com/rm5248/libcppgenerate/issues"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

SRC_URI = "git://github.com/rm5248/libcppgenerate.git;branch=master;protocol=https"
SRCREV = "930c5503f76c877b72b9ff8546353d6f422bd010"

S = "${WORKDIR}/git"

inherit cmake

BBCLASSEXTEND="native nativesdk"
