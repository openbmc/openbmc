SUMMARY = "A pure-Python MySQL client library"
DESCRIPTION = " \
  This package contains a pure-Python MySQL client library, based on PEP 249 \
  Most public APIs are compatible with mysqlclient and MySQLdb. \
  "
SECTION = "devel/python"
HOMEPAGE = "https://pymysql.readthedocs.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=528175c84163bb800d23ad835c7fa0fc"

SRC_URI[md5sum] = "d08058b1592724d5808c43f56050f245"
SRC_URI[sha256sum] = "e14070bc84e050e0f80bf6063e31d276f03a0bb4d46b9eca2854566c4ae19837"

PYPI_PACKAGE = "PyMySQL"
inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-cryptography"
