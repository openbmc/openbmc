SUMMARY = "Tool for writing very fast and very flexible scanners"
HOMEPAGE = "http://re2c.sourceforge.net/"
AUTHOR = "Marcus Börger <helly@users.sourceforge.net>"
SECTION = "devel"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://README;beginline=146;md5=881056c9add17f8019ccd8c382ba963a"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz \
           file://mkdir.patch"
SRC_URI[md5sum] = "3bf508fabd52ed7334647d0ccb956e8d"
SRC_URI[sha256sum] = "48c12564297641cceb5ff05aead57f28118db6277f31e2262437feba89069e84"

BBCLASSEXTEND = "native"

inherit autotools
