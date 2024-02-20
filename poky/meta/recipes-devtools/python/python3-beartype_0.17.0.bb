SUMMARY = "Unbearably fast runtime type checking in pure Python."
HOMEPAGE = "https://beartype.readthedocs.io"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e71f94261c1b39896cacacfeaf60560e"

SRC_URI[sha256sum] = "3226fbba8c53b4e698acdb47dcaf3c0640151c4d405618c281e6631f4112947d"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
