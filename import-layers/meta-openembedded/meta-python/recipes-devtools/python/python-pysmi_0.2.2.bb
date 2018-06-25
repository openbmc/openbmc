SUMMARY = "SNMP SMI/MIB Parser"
DESCRIPTION = "A pure-Python implementation of SNMP/SMI MIB \
  parsing and conversion library. Can produce PySNMP MIB modules. \
"
HOMEPAGE = "https://pypi.python.org/pypi/pysmi"
SECTION = "devel/python"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=d8b1bbadd635f187dee74d999a8c67b8"

SRC_URI[md5sum] = "3b0f71e4e9f730d211f09d8ef5371140"
SRC_URI[sha256sum] = "2a315cc3e556b3428372b69da663a24d2f0df9e9ab075b022cb133855f25aef7"

inherit pypi setuptools

RDEPENDS_${PN} = "python-ply"
