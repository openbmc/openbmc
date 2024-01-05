SUMMARY = "Vestigial utilities from IPython"
HOMEPAGE = "http://ipython.org"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.md;md5=f7c3032c3ac398265224533a0a333a35"

PYPI_PACKAGE = "ipython_genutils"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/ipython_genutils"
UPSTREAM_CHECK_REGEX = "/ipython_genutils/(?P<pver>(\d+[\.\-_]*)+)"

SRC_URI[md5sum] = "5a4f9781f78466da0ea1a648f3e1f79f"
SRC_URI[sha256sum] = "eb2e116e75ecef9d4d228fdc66af54269afa26ab4463042e33785b887c628ba8"

inherit setuptools3 pypi
