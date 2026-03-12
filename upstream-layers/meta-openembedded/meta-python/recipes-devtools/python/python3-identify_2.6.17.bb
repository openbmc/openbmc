SUMMARY = "File identification library for Python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bbdc006359f3157660173ec7f133a80e"

PYPI_PACKAGE = "identify"

inherit pypi setuptools3

SRC_URI[sha256sum] = "f816b0b596b204c9fdf076ded172322f2723cf958d02f9c3587504834c8ff04d"

RDEPENDS:${PN} = " \
	python3-ukkonen \
"
