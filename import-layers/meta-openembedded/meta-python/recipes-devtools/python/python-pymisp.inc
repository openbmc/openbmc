DESCRIPTION = "Python API for MISP"
HOMEPAGE = "https://github.com/MISP/PyMISP"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=200286bb8d148a5a157bf22abf623cbb"

SRC_URI[md5sum] = "03b5def47d8fde29f2303ed42ab1e375"
SRC_URI[sha256sum] = "2bb35c365470ad46f9afbb96408c01894aaa10b369fac0f6b92010190a7087ad"

RDEPENDS_${PN} += " ${PYTHON_PN}-requests"
