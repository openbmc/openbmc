DESCRIPTION = "Clean single-source support for Python 3 and 2"
HOMEPAGE = "https://python-future.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3f457538b297bd0ef86564b8ac216ad7"

SRC_URI[md5sum] = "3e8e88a2bda48d54b1da7634d04760d7"
SRC_URI[sha256sum] = "e39ced1ab767b5936646cedba8bcce582398233d6a627067d4c6a454c90cfedb"

PYPI_PACKAGE_HASH = "99abde815842bc6e97d5a7806ad51236630da14ca2f3b1fce94c0bb94d3d"

inherit pypi

BBCLASSEXTEND = "native"
