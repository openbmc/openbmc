SUMMARY = "Simple DNS resolver for asyncio"
DESCRIPTION = "aiodns provides a simple way for doing asynchronous DNS resolutions using pycares."
HOMEPAGE = "https://github.com/saghul/aiodns"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a565d8b5d06b9620968a135a2657b093"

SRC_URI[sha256sum] = "62869b23409349c21b072883ec8998316b234c9a9e36675756e8e317e8768f72"

PYPI_PACKAGE = "aiodns"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-pycares \
"

BBCLASSEXTEND = "native nativesdk"
