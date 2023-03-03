SUMMARY = "Pure-Python implementation of HSS/LMS Digital Signatures (RFC 8554)"
HOMEPAGE ="https://pypi.org/project/pyhsslms"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=58f6f7065b99f9d01d56e759256a6f1b"

inherit pypi python_setuptools_build_meta
PYPI_PACKAGE = "pyhsslms"
SRC_URI[sha256sum] = "58bf03e34c6f9d5a3cfd77875d0a1356d4f23d7ad6ffd129b1e60de1208db753"

BBCLASSEXTEND = "native nativesdk"
