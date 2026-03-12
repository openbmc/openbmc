SUMMARY = "Recipe to embedded the Python PiP Package icontract"
HOMEPAGE = "https://pypi.org/project/icontract"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=1d4a9b1f6b84bedf7a38843931e0dd57"

inherit pypi setuptools3
PYPI_PACKAGE = "icontract"
SRC_URI[sha256sum] = "df37a43d86d532407bc6b84dea29dd9f7ece794b73211769fa8a33a76b8ed145"

RDEPENDS:${PN} += "python3-asttokens"

BBCLASSEXTEND = "native nativesdk"
