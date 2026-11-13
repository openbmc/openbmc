SUMMARY = "File identification library for Python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bbdc006359f3157660173ec7f133a80e"


inherit pypi setuptools3

SRC_URI[sha256sum] = "ad729860a923858d26917c2f4fb0a1d83d27a75b1e090c06440c573f048f3285"

RDEPENDS:${PN} = " \
	python3-ukkonen \
"
