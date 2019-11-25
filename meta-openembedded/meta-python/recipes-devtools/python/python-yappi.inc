SUMMARY  = "Yet Another Python Profiler"
HOMEPAGE = "http://yappi.googlecode.com/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=9a193c13f346884e597acdcac7fe9ac8"

SRC_URI[md5sum] = "a545101aa8a435b0780f06f4723f58c8"
SRC_URI[sha256sum] = "7f814131515d51db62b1a3468bcb84de30499124752806a5a6e11caf0b4344bf"

inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-threading \
    "
