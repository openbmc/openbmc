SUMMARY = "Powerful Python library for atomic file writes"
HOMEPAGE = "https://github.com/untitaker/python-atomicwrites"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=91cc36cfafeefb7863673bcfcb1d4da4"

SRC_URI[sha256sum] = "81b2c9071a49367a7f770170e5eec8cb66567cfbbc8c73d20ce5ca4a8d71cf11"

inherit pypi setuptools3 ptest-python-pytest

RDEPENDS:${PN}-ptest += " \
	python3-unixadmin \
"

RDEPENDS:${PN} = "python3-misc"

BBCLASSEXTEND = "native nativesdk"
