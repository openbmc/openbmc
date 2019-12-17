SUMMARY = "log4cplus provides a simple C++ logging API for log management"
SECTION = "libs"
HOMEPAGE = "http://sourceforge.net/projects/log4cplus/"
BUGTRACKER = "http://sourceforge.net/p/log4cplus/bugs/"

LICENSE = "Apache-2.0 & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=41e8e060c26822886b592ab4765c756b"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}-stable/${PV}/${BP}.tar.gz \
           file://0001-Remove-log4cplus-version-entry-from-ELF-.comment-sec.patch \
          "
SRC_URI[md5sum] = "2b5b7bc294698bf30a315a259ef9eec2"
SRC_URI[sha256sum] = "196eedbc7f2dd792dae721b1159c7ac132255bb368226353c24d4d5fa7c7797a"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/log4cplus/files/log4cplus-stable/"
UPSTREAM_CHECK_REGEX = "log4cplus-stable/(?P<pver>\d+(\.\d+)+)/"

inherit autotools pkgconfig

BBCLASSEXTEND = "native"
