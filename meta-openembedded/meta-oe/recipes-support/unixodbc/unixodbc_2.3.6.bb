SUMMARY = "An Open Source ODBC sub-system"
DESCRIPTION = "unixODBC is an Open Source ODBC sub-system and an ODBC SDK \
for Linux, Mac OSX, and UNIX."

HOMEPAGE = "http://www.unixodbc.org/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7b37bf80a3df5a65b355433ae36d206"

DEPENDS = "libtool mysql5"

SRC_URI = "ftp://ftp.unixodbc.org/pub/unixODBC/unixODBC-${PV}.tar.gz \
           file://do-not-use-libltdl-source-directory.patch \
"
SRC_URI[md5sum] = "a8629fd2953b04b4639d0a9d8a5cf9d1"
SRC_URI[sha256sum] = "88b637f647c052ecc3861a3baa275c3b503b193b6a49ff8c28b2568656d14d69"

UPSTREAM_CHECK_REGEX = "unixODBC-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools-brokensep

S = "${WORKDIR}/unixODBC-${PV}"

EXTRA_OEMAKE += "LIBS=-lltdl"

do_configure_prepend() {
    # old m4 files will cause libtool version don't match
    rm -rf m4/*
    rm -fr libltdl
}
