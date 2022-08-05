SUMMARY = "Simple DNS resolver for asyncio"
DESCRIPTION = "aiodns provides a simple way for doing asynchronous DNS resolutions using pycares."
HOMEPAGE = "https://github.com/saghul/aiodns"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a565d8b5d06b9620968a135a2657b093"

SRC_URI[md5sum] = "181e11935c78965de2b2b7b0e5efba8d"
SRC_URI[sha256sum] = "946bdfabe743fceeeb093c8a010f5d1645f708a241be849e17edfb0e49e08cd6"

PYPI_PACKAGE = "aiodns"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-asyncio \
    ${PYTHON_PN}-pycares \
"
