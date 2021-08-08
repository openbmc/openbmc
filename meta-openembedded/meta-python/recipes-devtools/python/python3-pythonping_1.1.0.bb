SUMMARY = "PythonPing is simple way to ping in Python."
HOMEPAGE = "https://pypi.org/project/pythonping/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://setup.py;beginline=12;endline=12;md5=2d33c00f47720c7e35e1fdb4b9fab027"

SRC_URI[sha256sum] = "71199bdeee942ba1258b65f88ca5624278e63b31e7643ee8ca7292d2f5f77e99"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-io"
