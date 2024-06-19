SUMMARY = "The iniParser library is a simple C library offering INI file parsing services (both reading and writing)."
SECTION = "libs"
HOMEPAGE = "https://gitlab.com/iniparser/iniparser"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8474d3b745f77e203f1fc82fb0bb7678"
SRCBRANCH = "main"
SRCREV = "762715ca6cdba9dcf31a5af9297b444f3b5b8c00"

SRC_URI = "git://gitlab.com/iniparser/iniparser.git;protocol=https;branch=${SRCBRANCH}"
S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = " \
	-DBUILD_DOCS=OFF \
	"

FILES_${PN}-staticdev += "${libdir}/cmake/iniparser/iniparser-staticTargets*.cmake"
