SUMMARY = "A Pure Python Expect like Module for Python"
HOMEPAGE = "http://pexpect.readthedocs.org/"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=66c2378a96b307d56bfb3a9e58edafa8"

SRC_URI = "https://pypi.python.org/packages/source/p/pexpect/pexpect-${PV}.tar.gz"
SRC_URI[md5sum] = "056df81e6ca7081f1015b4b147b977b7"
SRC_URI[sha256sum] = "232795ebcaaf2e120396dbbaa3a129eda51757eeaae1911558f4ef8ee414fc6c"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/pexpect"

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
