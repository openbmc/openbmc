SUMMARY = "PhysicsFS is a library to provide abstract access to various archives"
HOMEPAGE = "http://icculus.org/physfs"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=96801882d1120239dcafbf888e821a6e"
DEPENDS = "readline zlib"

inherit cmake

PE = "1"

SRC_URI = "http://icculus.org/${BPN}/downloads/${BP}.tar.bz2"
SRC_URI[md5sum] = "dc751294aaf59d1359bbe34e693d1d87"
SRC_URI[sha256sum] = "304df76206d633df5360e738b138c94e82ccf086e50ba84f456d3f8432f9f863"

EXTRA_OECMAKE = "-DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')}"
