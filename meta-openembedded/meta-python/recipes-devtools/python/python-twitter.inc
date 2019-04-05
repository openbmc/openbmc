SUMMARY = "Twitter for Python"
DESCRIPTION = "Python module to support twitter API"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=0307fffd8c4d9946c029daa1426d26e7"

SRC_URI[md5sum] = "c6dc2f315ded761aafc96bea3351c7f6"
SRC_URI[sha256sum] = "fe85a79f58a01dd335968523b91c5fce760e7fe78bf25a6e71c72204fe499d0b"

PYPI_PACKAGE = "tweepy"

RDEPENDS_${PN} += "\
        ${PYTHON_PN}-pip \
        ${PYTHON_PN}-pysocks \
        ${PYTHON_PN}-requests \
        ${PYTHON_PN}-six \
        "
