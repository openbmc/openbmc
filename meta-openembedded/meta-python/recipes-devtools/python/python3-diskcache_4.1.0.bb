DESCRIPTION = "Disk Cache -- Disk and file backed persistent cache."
HOMEPAGE = "http://www.grantjenks.com/docs/diskcache/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9e046ce8fae71087134866418f646ec3"

SRC_URI[md5sum] = "21c9945e7707799e1c64956f8a54d4c3"
SRC_URI[sha256sum] = "bcee5a59f9c264e2809e58d01be6569a3bbb1e36a1e0fb83f7ef9b2075f95ce0"

PYPI_PACKAGE = "diskcache"

inherit pypi setuptools3

CLEANBROKEN = "1"

