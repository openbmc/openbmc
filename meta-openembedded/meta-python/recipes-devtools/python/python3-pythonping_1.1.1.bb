SUMMARY = "PythonPing is simple way to ping in Python."
HOMEPAGE = "https://pypi.org/project/pythonping/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://setup.py;beginline=12;endline=12;md5=2d33c00f47720c7e35e1fdb4b9fab027"

SRC_URI[sha256sum] = "0022f6cbe52762e9d596316e3bccb8a3b794355a49c0d788f7228d90f9461cfc"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-io"
