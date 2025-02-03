SUMMARY = "Convert HTML to Markdown-formatted text"
HOMEPAGE = "https://github.com/Alir3z4/html2text"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI[sha256sum] = "05f8e367d15aaabc96415376776cdd11afd5127a77fce6e36afc60c563ca2c32"

inherit pypi setuptools3 ptest-python-pytest

PTEST_PYTEST_DIR = "test"

RDEPENDS:${PN} += "python3-html"

BBCLASSEXTEND = "native nativesdk"
