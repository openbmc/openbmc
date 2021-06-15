SUMMARY = "PythonPing is simple way to ping in Python."
HOMEPAGE = "https://pypi.org/project/pythonping/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://setup.py;beginline=12;endline=12;md5=2d33c00f47720c7e35e1fdb4b9fab027"

SRC_URI[md5sum] = "9b505ad8a5b8a6a8e57ccf75098ea364"
SRC_URI[sha256sum] = "d025c8b25952580dea47bc241421e17a5a97f97f50098e1096dd10d845d0f156"

inherit pypi setuptools3

RDEPENDS_${PN} += "python3-io"
