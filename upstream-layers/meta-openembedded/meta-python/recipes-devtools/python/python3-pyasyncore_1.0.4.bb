SUMMARY = "Make asyncore available for Python 3.12 onwards"
HOMEPAGE = "https://github.com/simonrob/pyasyncore"
LICENSE = "Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d5605fc335ce1bab614032468d0a1e00"

inherit pypi setuptools3 ptest-python-pytest

SRC_URI[sha256sum] = "2c7a8b9b750ba6260f1e5a061456d61320a80579c6a43d42183417da89c7d5d6"

RDEPENDS:${PN} += "python3-core python3-io"
RDEPENDS:${PN}-ptest += "python3-tests"

BBCLASSEXTEND = "native nativesdk"
