SUMMARY = "Tool for writing very fast and very flexible scanners"
HOMEPAGE = "http://re2c.sourceforge.net/"
AUTHOR = "Marcus Börger <helly@users.sourceforge.net>"
SECTION = "devel"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://README;beginline=180;md5=822830a2204aef353f2c489f62e02089"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz \
           file://configure.patch"
SRC_URI[md5sum] = "4a97d8f77ed6d2c76c8bd840a43f5633"
SRC_URI[sha256sum] = "f3a995139af475e80a30207d02728b1e0065b0caade7375e974cb1b14861668c"

BBCLASSEXTEND = "native"

inherit autotools
