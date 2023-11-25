SUMMARY = "Python module for generating .ninja files."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://setup.py;beginline=38;endline=38;md5=f5441d6119564d4094cf77bee7cf7b0a"

SRC_URI[sha256sum] = "342dc97b9e88a6495bae22953ee6063f91d2f03db6f727b62ba5c3092a18ef1f"

inherit pypi setuptools3

PYPI_PACKAGE = "ninja_syntax"
UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/ninja_syntax/"
UPSTREAM_CHECK_REGEX = "/ninja_syntax/(?P<pver>(\d+[\.\-_]*)+)"

BBCLASSEXTEND = "native nativesdk"
