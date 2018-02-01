SUMMARY  = "Yet Another Python Profiler"
HOMEPAGE = "http://yappi.googlecode.com/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=6b131c3041637f6a5175a43112dde05c"

SRC_URI[md5sum] = "dc56240575c99938a924eaeb7c0d8beb"
SRC_URI[sha256sum] = "5f657129e1b9b952379ffbc009357d0dcdb58c50f3bfe88ffbb992e4b27b263c"

inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-threading \
    "
