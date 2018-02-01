SUMMARY = "A Pure Python Expect like Module for Python"
HOMEPAGE = "http://pexpect.readthedocs.org/"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1c7a725251880af8c6a148181665385b"

SRCNAME = "pexpect"

SRC_URI = "https://files.pythonhosted.org/packages/source/p/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"
SRC_URI[md5sum] = "3694410001a99dff83f0b500a1ca1c95"
SRC_URI[sha256sum] = "3d132465a75b57aa818341c6521392a06cc660feb3988d7f1074f39bd23c9a92"

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

BBCLASSEXTEND = "native nativesdk"
