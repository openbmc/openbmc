DESCRIPTION = "pytest plugin for generating html reports from test results"
HOMEPAGE = "https://github.com/pytest-dev/pytest-html"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://../pytest-html.LICENSE;md5=5d425c8f3157dbf212db2ec53d9e5132"

# Per README.rst the license statement is fetched from
# https://raw.githubusercontent.com/davehunt/pytest-html/master/LICENSE
SRC_URI += "https://raw.githubusercontent.com/davehunt/pytest-html/master/LICENSE;name=license;downloadfilename=pytest-html.LICENSE"

PYPI_PACKAGE = "pytest-html"

inherit pypi setuptools3

SRC_URI[md5sum] = "ac956864a9b3392203dacd287ae450f0"
SRC_URI[sha256sum] = "a359de04273239587bd1a15b29b2266daeaf56b7a13f8224bc4fb3ae0ba72c3f"
SRC_URI[license.md5sum] = "5d425c8f3157dbf212db2ec53d9e5132"
SRC_URI[license.sha256sum] = "2bfdca60adf803108d4c7f009000bea76ad00e621e163197881b0eaae91b530e"

RDEPENDS_${PN} = "${PYTHON_PN}-pytest"
