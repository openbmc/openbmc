SUMMARY = "Python lightweight in-process concurrent programming"
HOMEPAGE = "https://greenlet.readthedocs.io/en/latest/"
LICENSE = "MIT & PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e95668d68e4329085c7ab3535e6a7aee \
                    file://LICENSE.PSF;md5=c106931d9429eda0492617f037b8f69a"

SRC_URI[sha256sum] = "f50a96b64dafd6169e595a5c56c9146ef80333e67d4476a65a9c55f400fc22ff"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
