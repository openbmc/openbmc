SUMMARY = "Simple DNS resolver for asyncio"
DESCRIPTION = "aiodns provides a simple way for doing asynchronous DNS resolutions using pycares."
HOMEPAGE = "https://github.com/saghul/aiodns"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d574ab425d1fcb37c9f1ad3961f18527"

SRC_URI[sha256sum] = "11264edbab51896ecf546c18eb0dd56dff0428c6aa6d2cd87e643e07300eb310"

PYPI_PACKAGE = "aiodns"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-pycares \
"

BBCLASSEXTEND = "native nativesdk"
