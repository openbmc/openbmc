SUMMARY = "SNMP SMI/MIB Parser"
DESCRIPTION = "A pure-Python implementation of SNMP/SMI MIB \
  parsing and conversion library. Can produce PySNMP MIB modules. \
"
HOMEPAGE = "https://pypi.python.org/pypi/pysmi"
SECTION = "devel/python"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=a088b5c72b59d51a5368ad3b18e219bf"

SRC_URI[md5sum] = "10a9dd140ad512eed9f37344df83ce9d"
SRC_URI[sha256sum] = "bd15a15020aee8376cab5be264c26330824a8b8164ed0195bd402dd59e4e8f7c"

inherit pypi setuptools

RDEPENDS_${PN} = "python-ply"
