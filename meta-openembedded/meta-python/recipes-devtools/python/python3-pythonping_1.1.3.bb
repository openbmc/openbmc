SUMMARY = "PythonPing is simple way to ping in Python."
HOMEPAGE = "https://pypi.org/project/pythonping/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://setup.py;beginline=12;endline=12;md5=2d33c00f47720c7e35e1fdb4b9fab027"

SRC_URI[sha256sum] = "3555a03439eb48d5e0e8c201f7c334c1e13b997d744f93453d4d601c0fc8330f"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-io"
