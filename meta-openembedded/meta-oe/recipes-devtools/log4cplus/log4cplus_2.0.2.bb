SUMMARY = "log4cplus provides a simple C++ logging API for log management"
SECTION = "libs"
HOMEPAGE = "http://sourceforge.net/projects/log4cplus/"
BUGTRACKER = "http://sourceforge.net/p/log4cplus/bugs/"

LICENSE = "Apache-2.0 & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=41e8e060c26822886b592ab4765c756b"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}-stable/${PV}/${BP}.tar.gz"
SRC_URI[md5sum] = "b3bbeb2dc3e170768430cf87583016f8"
SRC_URI[sha256sum] = "10539f2315271d370c7bc6a2b4808cbe369279837f4539ce5c789e456489fc62"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/log4cplus/files/log4cplus-stable/"
UPSTREAM_CHECK_REGEX = "log4cplus-stable/(?P<pver>\d+(\.\d+)+)/"

inherit autotools pkgconfig

BBCLASSEXTEND = "native"
