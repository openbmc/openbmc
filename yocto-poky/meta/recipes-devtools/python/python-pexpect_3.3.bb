SUMMARY = "A Pure Python Expect like Module for Python"
HOMEPAGE = "http://pexpect.readthedocs.org/"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c25d9a0770ba69a9965acc894e9f3644"

SRC_URI = "https://pypi.python.org/packages/source/p/pexpect/pexpect-${PV}.tar.gz"
SRC_URI[md5sum] = "0de72541d3f1374b795472fed841dce8"
SRC_URI[sha256sum] = "dfea618d43e83cfff21504f18f98019ba520f330e4142e5185ef7c73527de5ba"

S = "${WORKDIR}/pexpect-${PV}"

inherit distutils

RDEPENDS_${PN} = "\
    python-core \
    python-io \
    python-terminal \
    python-resource \
    python-fcntl \
"

BBCLASSEXTEND = "nativesdk"
