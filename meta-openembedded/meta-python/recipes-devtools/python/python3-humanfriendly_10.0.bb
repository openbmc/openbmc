DESCRIPTION = "Human friendly output for text interfaces using Python"
HOMEPAGE = "https://humanfriendly.readthedocs.io/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5d178009f806c2bdd498a19be0013a7a"

PYPI_PACKAGE = "humanfriendly"

SRC_URI[sha256sum] = "6b0b831ce8f15f7300721aa49829fc4e83921a9a301cc7f606be6686a2288ddc"

inherit pypi setuptools3

RDEPENDS:${PN}:class-target += " \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-fcntl \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-math \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-stringold \
"

BBCLASSEXTEND = "native nativesdk"
