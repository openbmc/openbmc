SUMMARY = "Fast, multi-threaded malloc() and nifty performance analysis tools"
HOMEPAGE = "http://code.google.com/p/gperftools/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=762732742c73dc6c7fbe8632f06c059a"
DEPENDS = "libunwind"

SRC_URI = "http://pkgs.fedoraproject.org/repo/pkgs/gperftools/gperftools-2.4.tar.gz/2171cea3bbe053036fb5d5d25176a160/gperftools-2.4.tar.gz"

SRC_URI[md5sum] = "2171cea3bbe053036fb5d5d25176a160"
SRC_URI[sha256sum] = "982a37226eb42f40714e26b8076815d5ea677a422fb52ff8bfca3704d9c30a2d"

inherit autotools
