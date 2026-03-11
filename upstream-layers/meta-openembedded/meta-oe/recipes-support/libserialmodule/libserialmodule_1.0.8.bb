SUMMARY = "A library for Serial/COM"
DESCRIPTION = "Async C/C++ I/O with COM/Serial Port Library."
HOMEPAGE = "https://github.com/thuanalg/libserialmodule"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=22cdd382a6275cb4c2e75c517952ac7c"
DEPENDS = "libsimplelog"
SRC_URI = "git://git@github.com/thuanalg/libserialmodule.git;branch=main;protocol=https;tag=v${PV}"
SRCREV = "f89f98ff0c9d0aaee2624d40addb0687a74c5d81"
inherit cmake
EXTRA_OECMAKE = "-DUNIX_LINUX=1 -DMETA_OPENEMBEDDED=1"

