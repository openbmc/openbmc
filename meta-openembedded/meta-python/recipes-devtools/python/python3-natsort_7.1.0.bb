SUMMARY = "Simple yet flexible natural sorting in Python."
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6a96e5ad780a0eea866ecccec4463517"

PYPI_PACKAGE = "natsort"
SRC_URI[sha256sum] = "33f3f1003e2af4b4df20908fe62aa029999d136b966463746942efbfc821add3"

inherit pypi setuptools3

RDEPENDS_${PN} = "python3-fastnumbers python3-icu"
