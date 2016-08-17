SUMMARY = "Google Data APIs Python Client Library"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.txt;md5=473bd4dff0ddca1f958244b2dc7a162c"
HOMEPAGE = "http://code.google.com/p/gdata-python-client/"

inherit distutils

SRC_URI = "http://gdata-python-client.googlecode.com/files/gdata.py-${PV}.tar.gz"
SRC_URI[md5sum] = "521f33a377d64f8a6505ba119415b787"
SRC_URI[sha256sum] = "fc5ddb8f76b17abd728721a0e0177ea35f55a70106f44dc9010b22eceb06abde"

S = "${WORKDIR}/gdata.py-${PV}"

FILES_${PN} += "${datadir}"

RDEPENDS_${PN} = "python-xml"
