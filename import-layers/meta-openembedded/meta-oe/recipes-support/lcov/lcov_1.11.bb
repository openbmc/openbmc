SUMMARY = "A graphical front-end for gcov"
HOMEPAGE = "http://ltp.sourceforge.net/coverage/lcov.php"
DESCRIPTION = "LCOV is a graphical front-end for GCC's coverage testing \
tool gcov. It collects gcov data for multiple source files and creates \
HTML pages containing the source code annotated with coverage information. \
It also adds overview pages for easy navigation within the file structure. \
LCOV supports statement, function and branch coverage measurement." 
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

RDEPENDS_${PN} += " \
    gcov \
    perl \
    perl-module-filehandle \
    perl-module-getopt-std \
    perl-module-digest-sha \
"

SRC_URI = "http://downloads.sourceforge.net/ltp/${BP}.tar.gz"

SRC_URI[md5sum] = "e79b799ae3ce149aa924c7520e993024"
SRC_URI[sha256sum] = "c282de8d678ecbfda32ce4b5c85fc02f77c2a39a062f068bd8e774d29ddc9bf8"

do_install() {
    oe_runmake install PREFIX=${D}
    sed -i -e '1s,#!.*perl,#! ${USRBINPATH}/env perl,' ${D}${bindir}/*
}

