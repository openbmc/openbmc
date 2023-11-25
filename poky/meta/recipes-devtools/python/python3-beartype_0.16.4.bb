SUMMARY = "Unbearably fast runtime type checking in pure Python."
HOMEPAGE = "https://beartype.readthedocs.io"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e40b52d8eb5553aa8f705cdd3f979d69"

SRC_URI[sha256sum] = "1ada89cf2d6eb30eb6e156eed2eb5493357782937910d74380918e53c2eae0bf"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
