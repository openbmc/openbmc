SUMMARY = "SNMP SMI/MIB Parser"
DESCRIPTION = "A pure-Python implementation of SNMP/SMI MIB \
  parsing and conversion library. Can produce PySNMP MIB modules. \
"
HOMEPAGE = "https://pypi.python.org/pypi/pysmi"
SECTION = "devel/python"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=d8b1bbadd635f187dee74d999a8c67b8"

SRCNAME = "pysmi"

SRC_URI = "https://files.pythonhosted.org/packages/source/p/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "f8754e9ed75698cc16b40f125df85b12"
SRC_URI[sha256sum] = "e0912180fc6faa9c264df78f97e7c451f77f84f5bd840098d2ce7b1bf70082bc"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

RDEPENDS_${PN} = "python-ply"
