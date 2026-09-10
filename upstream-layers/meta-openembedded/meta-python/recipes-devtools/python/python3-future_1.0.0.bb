DESCRIPTION = "Clean single-source support for Python 3 and 2"
HOMEPAGE = "https://python-future.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ce268d4c2f911490f47ac5473a24bb89"

SRC_URI[sha256sum] = "bd2968309307861edae1458a4f8a4f3598c03be43b97521076aebf5d94c07b05"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
