SUMMARY = "The iniParser library is a simple C library offering INI file parsing services (both reading and writing)."
SECTION = "libs"
HOMEPAGE = "https://github.com/ndevilla/iniparser"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e02baf71c76e0650e667d7da133379ac"

DEPENDS = "doxygen-native"

SRC_URI = "git://github.com/ndevilla/iniparser.git;protocol=https \
	   file://Add-CMake-support.patch"

# tag 4.1
SRCREV= "0a38e85c9cde1e099ca3bf70083bd00f89c3e5b6"

S = "${WORKDIR}/git"

inherit cmake
