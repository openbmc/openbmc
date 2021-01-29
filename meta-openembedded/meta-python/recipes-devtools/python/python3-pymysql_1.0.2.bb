SUMMARY = "A pure-Python MySQL client library"
DESCRIPTION = " \
  This package contains a pure-Python MySQL client library, based on PEP 249 \
  Most public APIs are compatible with mysqlclient and MySQLdb. \
  "
SECTION = "devel/python"
HOMEPAGE = "https://pymysql.readthedocs.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=528175c84163bb800d23ad835c7fa0fc"

SRC_URI[sha256sum] = "816927a350f38d56072aeca5dfb10221fe1dc653745853d30a216637f5d7ad36"

PYPI_PACKAGE = "PyMySQL"
inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-cryptography"
