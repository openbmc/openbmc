SUMMARY = "log4cplus provides a simple C++ logging API for log management"
SECTION = "libs"
HOMEPAGE = "http://sourceforge.net/projects/log4cplus/"
BUGTRACKER = "http://sourceforge.net/p/log4cplus/bugs/"

LICENSE = "Apache-2.0 & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cedaa287ececcb94f9f2880d9c4ef085"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}-stable/${PV}/${BP}.tar.gz"
SRC_URI[md5sum] = "e250f0f431c0723f8b625323e7b6465d"
SRC_URI[sha256sum] = "ad5ec3b62f2f0bdc7f06fa398bde61091fd2493e91059e17315b5197420dcf04"

inherit autotools pkgconfig

BBCLASSEXTEND += "native"
