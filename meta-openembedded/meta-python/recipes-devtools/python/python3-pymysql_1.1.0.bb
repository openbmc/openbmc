SUMMARY = "A pure-Python MySQL client library"
DESCRIPTION = " \
  This package contains a pure-Python MySQL client library, based on PEP 249 \
  Most public APIs are compatible with mysqlclient and MySQLdb. \
  "
SECTION = "devel/python"
HOMEPAGE = "https://pymysql.readthedocs.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=528175c84163bb800d23ad835c7fa0fc"

SRC_URI[sha256sum] = "4f13a7df8bf36a51e81dd9f3605fede45a4878fe02f9236349fd82a3f0612f96"

PYPI_PACKAGE = "PyMySQL"
inherit pypi python_setuptools_build_meta

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/pymysql/"
UPSTREAM_CHECK_REGEX = "/pymysql/(?P<pver>(\d+[\.\-_]*)+)"

RDEPENDS:${PN} += "python3-cryptography"
