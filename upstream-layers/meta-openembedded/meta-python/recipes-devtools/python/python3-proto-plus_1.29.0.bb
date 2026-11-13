SUMMARY = "Beautiful, Pythonic protocol buffers"
DESCRIPTION = "Proto Plus provides a Pythonic wrapper around protocol buffers. \
    Messages behave like native Python types, which makes them easier to \
    construct, read and serialize."
HOMEPAGE = "https://github.com/googleapis/google-cloud-python/tree/main/packages/proto-plus"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "cfb4e62ad7e13dd18f346cabbda00cab39930d36a05791fd81ddb074d6ee884f"

RDEPENDS:${PN} += "python3-protobuf"

BBCLASSEXTEND = "native nativesdk"
