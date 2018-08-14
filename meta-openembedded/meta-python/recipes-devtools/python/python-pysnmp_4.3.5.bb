SUMMARY = "A pure-Python SNMPv1/v2c/v3 library"
DESCRIPTION = "SNMP v1/v2c/v3 engine and apps written in pure-Python. \
  Supports Manager/Agent/Proxy roles, scriptable MIBs, asynchronous \
  operation (asyncio, twisted, asyncore) and multiple transports.\
"
HOMEPAGE = "https://pypi.python.org/pypi/pysnmp"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=280606d9c18f200e03e0c247ac61475a"

SRCNAME = "pysnmp"

SRC_URI = "https://files.pythonhosted.org/packages/source/p/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "387aaa23c168ccc0b59775cfdf519fc0"
SRC_URI[sha256sum] = "38253fe95cea33f352fb36e85f3c2874043401724300c4888df74835161169d2"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

RDEPENDS_${PN} += "python-pycrypto \
                   python-pyasn1 \
                   python-pysmi \
"
