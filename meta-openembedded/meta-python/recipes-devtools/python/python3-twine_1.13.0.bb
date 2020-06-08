DESCRIPTION = "Six is a Python 2 and 3 compatibility library"
HOMEPAGE = "https://github.com/benjaminp/six"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README.rst;md5=3963bdcee7562bedae1effa09e7542b2"

SRC_URI[md5sum] = "6fb4da0c7d81ddfd48f619b8caa1493c"
SRC_URI[sha256sum] = "d6c29c933ecfc74e9b1d9fa13aa1f87c5d5770e119f5a4ce032092f0ff5b14dc"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
