SUMMARY = "Unbearably fast runtime type checking in pure Python."
HOMEPAGE = "https://beartype.readthedocs.io"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e71f94261c1b39896cacacfeaf60560e"

SRC_URI[sha256sum] = "e911e1ae7de4bccd15745f7643609d8732f64de5c2fb844e89cbbed1c5a8d495"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
