SUMMARY = "A pure-Python MySQL client library"
DESCRIPTION = " \
  This package contains a pure-Python MySQL client library, based on PEP 249 \
  Most public APIs are compatible with mysqlclient and MySQLdb. \
  "
SECTION = "devel/python"
HOMEPAGE = "https://pymysql.readthedocs.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=528175c84163bb800d23ad835c7fa0fc"

SRC_URI[md5sum] = "4a1fe973afbbdd78928650118ce8ff68"
SRC_URI[sha256sum] = "263040d2779a3b84930f7ac9da5132be0fefcd6f453a885756656103f8ee1fdd"

PYPI_PACKAGE = "PyMySQL"
inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-cryptography"
