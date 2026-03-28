SUMMARY = "File identification library for Python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bbdc006359f3157660173ec7f133a80e"

PYPI_PACKAGE = "identify"

inherit pypi setuptools3

SRC_URI[sha256sum] = "873ac56a5e3fd63e7438a7ecbc4d91aca692eb3fefa4534db2b7913f3fc352fd"

RDEPENDS:${PN} = " \
	python3-ukkonen \
"
