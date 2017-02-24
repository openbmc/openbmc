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
SRC_URI[md5sum] = "bd25d261ca1808c947cb687e2034be81"
SRC_URI[sha256sum] = "2e1509a96bb18d248bf08ead0d74804957304ff7c6f8b2e5965309c632421e39"

inherit autotools-brokensep

S = "${WORKDIR}/unixODBC-${PV}"

EXTRA_OEMAKE += "LIBS=-lltdl"

do_configure_prepend() {
    # old m4 files will cause libtool version don't match
    rm -rf m4/*
    rm -fr libltdl
}
