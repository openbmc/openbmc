HOMEPAGE = "https://pypi.python.org/pypi/Flask-Cors/"
SUMMARY = "A Flask extension adding a decorator for CORS support"
DESCRIPTION = "\
  A Flask extension for handling Cross Origin Resource Sharing (CORS), making cross-origin AJAX possible \
  "
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=6;endline=6;md5=134f1026f0de92fd30e71976590a2868"

PYPI_PACKAGE = "flask_cors"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

CVE_PRODUCT = "flask-cors"

inherit pypi python_setuptools_build_meta
SRC_URI[sha256sum] = "6e118f3698249ae33e429760db98ce032a8bf9913638d085ca0f4c5534ad2423"

RDEPENDS:${PN} += "python3-flask"
