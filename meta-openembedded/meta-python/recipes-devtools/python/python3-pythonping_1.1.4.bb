SUMMARY = "PythonPing is simple way to ping in Python."
HOMEPAGE = "https://pypi.org/project/pythonping/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://setup.py;beginline=12;endline=12;md5=2d33c00f47720c7e35e1fdb4b9fab027"

SRC_URI[sha256sum] = "acef84640fee6f20b725f2a1d2392771f2845554cfabcef30b1fdea5030161af"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-io"
