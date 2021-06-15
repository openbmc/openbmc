SUMMARY = "Simple yet flexible natural sorting in Python."
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6a96e5ad780a0eea866ecccec4463517"

PYPI_PACKAGE = "natsort"
SRC_URI[sha256sum] = "00c603a42365830c4722a2eb7663a25919551217ec09a243d3399fa8dd4ac403"

inherit pypi setuptools3

RDEPENDS_${PN} = "python3-fastnumbers python3-icu"
