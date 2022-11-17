SUMMARY = "jsonref is a library for automatic dereferencing of JSON Reference objects for Python"
HOMEPAGE = "https://github.com/gazpachoking/jsonref"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a34264f25338d41744dca1abfe4eb18f"

SRC_URI[sha256sum] = "51d3e18b83ca7170ff51286a0e1a6719d8b7fcc7abdb16b189395a8536996b97"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-core \
	${PYTHON_PN}-json \
	${PYTHON_PN}-netclient \
"
