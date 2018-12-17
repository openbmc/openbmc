SUMMARY = "Tool for writing very fast and very flexible scanners"
HOMEPAGE = "http://re2c.sourceforge.net/"
AUTHOR = "Marcus Börger <helly@users.sourceforge.net>"
SECTION = "devel"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://README;beginline=146;md5=881056c9add17f8019ccd8c382ba963a"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "e2c6cf52fc6a21595f21bc82db5324f8"
SRC_URI[sha256sum] = "605058d18a00e01bfc32aebf83af35ed5b13180b4e9f279c90843afab2c66c7c"

BBCLASSEXTEND = "native"

inherit autotools
