SUMMARY = "Python 2 and 3 compatibility library"
HOMEPAGE = "https://pypi.python.org/pypi/six/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f82eb3a1a7fade60bda5980935084b62"

SRC_URI[md5sum] = "e92c23c882c7d5564ce5773fe31b2771"
SRC_URI[sha256sum] = "30f610279e8b2578cab6db20741130331735c781b56053c59c4076da27f06b66"

inherit pypi

RDEPENDS_${PN} = "${PYTHON_PN}-io"

BBCLASSEXTEND = "native nativesdk"
