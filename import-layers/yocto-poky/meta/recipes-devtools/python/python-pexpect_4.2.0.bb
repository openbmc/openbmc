SUMMARY = "A Pure Python Expect like Module for Python"
HOMEPAGE = "http://pexpect.readthedocs.org/"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1c7a725251880af8c6a148181665385b"

SRCNAME = "pexpect"

SRC_URI = "https://files.pythonhosted.org/packages/source/p/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"
SRC_URI[md5sum] = "8071ec5df0f3d515daedafad672d1632"
SRC_URI[sha256sum] = "bf6816b8cc8d301a499e7adf338828b39bc7548eb64dbed4dd410ed93d95f853"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/pexpect"

S = "${WORKDIR}/pexpect-${PV}"

inherit setuptools

RDEPENDS_${PN} = "\
    python-core \
    python-io \
    python-terminal \
    python-resource \
    python-fcntl \
    python-ptyprocess \
"

BBCLASSEXTEND = "nativesdk"
