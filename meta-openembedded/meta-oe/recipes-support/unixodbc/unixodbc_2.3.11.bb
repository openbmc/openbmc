SUMMARY = "An Open Source ODBC sub-system"
DESCRIPTION = "unixODBC is an Open Source ODBC sub-system and an ODBC SDK \
for Linux, Mac OSX, and UNIX."

HOMEPAGE = "http://www.unixodbc.org/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7b37bf80a3df5a65b355433ae36d206"

DEPENDS = "libtool readline"

SRC_URI = "https://www.unixodbc.org/unixODBC-${PV}.tar.gz \
           file://do-not-use-libltdl-source-directory.patch \
"
SRC_URI[sha256sum] = "d9e55c8e7118347e3c66c87338856dad1516b490fb7c756c1562a2c267c73b5c"

UPSTREAM_CHECK_REGEX = "unixODBC-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools-brokensep multilib_header

S = "${WORKDIR}/unixODBC-${PV}"

EXTRA_OEMAKE += "LIBS=-lltdl"

do_configure:prepend() {
    # old m4 files will cause libtool version don't match
    rm -rf m4/*
    rm -fr libltdl
}

do_install:append() {
    oe_multilib_header unixodbc.h unixODBC/config.h unixODBC/unixodbc_conf.h
}
