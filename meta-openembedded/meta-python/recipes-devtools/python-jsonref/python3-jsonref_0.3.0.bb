SUMMARY = "jsonref is a library for automatic dereferencing of JSON Reference objects for Python"
HOMEPAGE = "https://github.com/gazpachoking/jsonref"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a34264f25338d41744dca1abfe4eb18f"

SRC_URI[sha256sum] = "68b330c6815dc0d490dbb3d65ccda265ddde9f7856fd2f3322f971d456ea7549"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-core \
	${PYTHON_PN}-json \
	${PYTHON_PN}-netclient \
"
