SUMMARY = "Universal character encoding detector"
HOMEPAGE = "https://github.com/PyYoshi/cChardet"
LICENSE = "MPL-1.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ecda54f6f525388d71d6b3cd92f7474"

SRC_URI[sha256sum] = "c428b6336545053c2589f6caf24ea32276c6664cb86db817e03a94c60afa0eaf"

inherit pypi setuptools3 cython

BBCLASSEXTEND = "native nativesdk"
