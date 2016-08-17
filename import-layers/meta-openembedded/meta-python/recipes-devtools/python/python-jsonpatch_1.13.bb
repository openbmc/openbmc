SUMMARY  = "Appling JSON patches in Python 2.6+ and 3.x"
HOMEPAGE = "https://github.com/stefankoegl/python-json-patch"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=32b15c843b7a329130f4e266a281ebb3"

inherit pypi setuptools

SRC_URI[md5sum] = "4d6650ced683f632e117bafe5d9f093b"
SRC_URI[sha256sum] = "9470656a08002e309632b59772b206ce0564c9a77b44c25f05f49dd2cad248d5"

RDEPENDS_${PN} += "python-re python-json python-jsonpointer"

