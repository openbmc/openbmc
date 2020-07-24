SUMMARY = "Simple yet flexible natural sorting in Python."
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6a96e5ad780a0eea866ecccec4463517"

PYPI_PACKAGE = "natsort"
SRC_URI[sha256sum] = "a633464dc3a22b305df0f27abcb3e83515898aa1fd0ed2f9726c3571a27258cf"

inherit pypi setuptools3

RDEPENDS_${PN} = "python3-fastnumbers python3-icu"
