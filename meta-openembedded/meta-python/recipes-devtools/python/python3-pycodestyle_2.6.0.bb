SUMMARY = "Python style guide checker (formly called pep8)"
HOMEPAGE = "https://pypi.org/project/pycodestyle"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8546d0e77f416fb05a26acd89c8b3bd"

SRC_URI[md5sum] = "e2fcdace728267d5fa3aa98e08ee40db"
SRC_URI[sha256sum] = "c58a7d2815e0e8d7972bf1803331fb0152f867bd89adf8a01dfd55085434192e"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
