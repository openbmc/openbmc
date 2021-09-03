SUMMARY = "log4cplus provides a simple C++ logging API for log management"
SECTION = "libs"
HOMEPAGE = "http://sourceforge.net/projects/log4cplus/"
DESCRIPTION = "log4cplus is a simple to use C++ logging API providing thread-safe, flexible, and arbitrarily granular control over log management and configuration. It is modelled after the Java log4j API."
BUGTRACKER = "http://sourceforge.net/p/log4cplus/bugs/"

LICENSE = "Apache-2.0 & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=41e8e060c26822886b592ab4765c756b"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}-stable/${PV}/${BP}.tar.gz \
          "
SRC_URI[sha256sum] = "086451c7e7c582862cbd6c60d87bb6d9d63c4b65321dba85fa71766382f7ec6d"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/log4cplus/files/log4cplus-stable/"
UPSTREAM_CHECK_REGEX = "log4cplus-stable/(?P<pver>\d+(\.\d+)+)/"

inherit autotools pkgconfig

BBCLASSEXTEND = "native"
