SUMMARY = "Resolve JSON Pointers in Python"
HOMEPAGE = "https://github.com/stefankoegl/python-json-pointer"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=32b15c843b7a329130f4e266a281ebb3"

inherit pypi setuptools

SRC_URI[md5sum] = "eaaad79e983d58ecbee5b4a326723777"
SRC_URI[sha256sum] = "819b6dd4fd0a18ac219e02a0117f24b2d31296b0c475c33862cfa9a1616d62c3"

RDEPENDS_${PN} += "python-re python-json"
