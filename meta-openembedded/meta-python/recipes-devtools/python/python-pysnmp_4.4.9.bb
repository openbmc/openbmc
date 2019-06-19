SUMMARY = "A pure-Python SNMPv1/v2c/v3 library"
DESCRIPTION = "SNMP v1/v2c/v3 engine and apps written in pure-Python. \
  Supports Manager/Agent/Proxy roles, scriptable MIBs, asynchronous \
  operation (asyncio, twisted, asyncore) and multiple transports.\
"
HOMEPAGE = "https://pypi.python.org/pypi/pysnmp"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=b15d29f500f748d1c2a15709769090a8"

SRCNAME = "pysnmp"

SRC_URI = "https://files.pythonhosted.org/packages/source/p/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "6d1b514997326bed18f1ae1510f6b1c9"
SRC_URI[sha256sum] = "d5d1e59780126e963dd92e25993b783295734e71bef181f602e51f7393260441"


S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

RDEPENDS_${PN} += "python-pycrypto \
                   python-pyasn1 \
                   python-pysmi \
"
