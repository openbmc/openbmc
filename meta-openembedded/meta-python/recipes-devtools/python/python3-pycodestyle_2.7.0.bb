SUMMARY = "Python style guide checker (formly called pep8)"
HOMEPAGE = "https://pypi.org/project/pycodestyle"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8546d0e77f416fb05a26acd89c8b3bd"

SRC_URI[sha256sum] = "c389c1d06bf7904078ca03399a4816f974a1d590090fecea0c63ec26ebaf1cef"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
