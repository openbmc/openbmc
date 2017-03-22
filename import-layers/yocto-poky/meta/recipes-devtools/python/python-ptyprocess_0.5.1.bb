SUMMARY = "Run a subprocess in a pseudo terminal"
HOMEPAGE = "http://ptyprocess.readthedocs.io/en/latest/"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cfdcd51fa7d5808da4e74346ee394490"

SRCNAME = "ptyprocess"

SRC_URI = "https://files.pythonhosted.org/packages/source/p/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"
SRC_URI[md5sum] = "94e537122914cc9ec9c1eadcd36e73a1"
SRC_URI[sha256sum] = "0530ce63a9295bfae7bd06edc02b6aa935619f486f0f1dc0972f516265ee81a6"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/ptyprocess"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

RDEPENDS_${PN} = "\
    python-core \
"

BBCLASSEXTEND = "nativesdk"
