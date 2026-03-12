SUMMARY = "Implements a XML/HTML/XHTML Markup safe string for Python"
HOMEPAGE = "http://github.com/mitsuhiko/markupsafe"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ffeffa59c90c9c4a033c7574f8f3fb75"

SRC_URI[sha256sum] = "722695808f4b6457b320fdc131280796bdceb04ab50fe1795cd540799ebe1698"

PYPI_PACKAGE = "markupsafe"
inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN} += "python3-html python3-stringold"

BBCLASSEXTEND = "native nativesdk"
