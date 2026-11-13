SUMMARY = "A pure-Python MySQL client library"
DESCRIPTION = " \
  This package contains a pure-Python MySQL client library, based on PEP 249 \
  Most public APIs are compatible with mysqlclient and MySQLdb. \
  "
SECTION = "devel/python"
HOMEPAGE = "https://pymysql.readthedocs.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=528175c84163bb800d23ad835c7fa0fc"

SRC_URI[sha256sum] = "d5b288529782e536ae171866df3ca9dc4f6cbfb3cc2f18e6f837fbb90dbc262b"


inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "python3-cryptography"
