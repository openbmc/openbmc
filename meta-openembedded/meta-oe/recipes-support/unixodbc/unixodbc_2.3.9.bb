SUMMARY = "An Open Source ODBC sub-system"
DESCRIPTION = "unixODBC is an Open Source ODBC sub-system and an ODBC SDK \
for Linux, Mac OSX, and UNIX."

HOMEPAGE = "http://www.unixodbc.org/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7b37bf80a3df5a65b355433ae36d206"

DEPENDS = "libtool readline"

SRC_URI = "http://ftp.unixodbc.org/unixODBC-${PV}.tar.gz \
           file://do-not-use-libltdl-source-directory.patch \
"
SRC_URI[sha256sum] = "52833eac3d681c8b0c9a5a65f2ebd745b3a964f208fc748f977e44015a31b207"

UPSTREAM_CHECK_REGEX = "unixODBC-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools-brokensep

S = "${WORKDIR}/unixODBC-${PV}"

EXTRA_OEMAKE += "LIBS=-lltdl"

do_configure:prepend() {
    # old m4 files will cause libtool version don't match
    rm -rf m4/*
    rm -fr libltdl
}
