DESCRIPTION = "A text parser written in the Python language."
SUMMARY = "A text parser."
HOMEPAGE = "https://github.com/eerimoq/textparser"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fe9942a8bba5458a9dbd11277bc347ad"

SRC_URI[sha256sum] = "859825876a9c38f7c313ee1cf991a59d6b56232a9f67be6dcc0a758d84654fba"


DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN}-ptest += "\
   python3-mypy \
   python3-ruff \
   python3-coverage \
"

inherit pypi python_setuptools_build_meta ptest-python-pytest

CLEANBROKEN = "1"

