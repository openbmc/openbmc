SUMMARY = "Unbearably fast runtime type checking in pure Python."
HOMEPAGE = "https://beartype.readthedocs.io"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e71f94261c1b39896cacacfeaf60560e"

SRC_URI[sha256sum] = "264ddc2f1da9ec94ff639141fbe33d22e12a9f75aa863b83b7046ffff1381927"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
