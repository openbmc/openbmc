SUMMARY = "Simple DNS resolver for asyncio"
DESCRIPTION = "aiodns provides a simple way for doing asynchronous DNS resolutions using pycares."
HOMEPAGE = "https://github.com/saghul/aiodns"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d574ab425d1fcb37c9f1ad3961f18527"

SRC_URI[sha256sum] = "17be26a936ba788c849ba5fd20e0ba69d8c46e6273e846eb5430eae2630ce5b1"

PYPI_PACKAGE = "aiodns"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-pycares \
"

BBCLASSEXTEND = "native nativesdk"
