SUMMARY = "Implementation of bounded Levenshtein distance (Ukkonen)"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7e49a187324d5a1a6c4ba2b9c3fd4033"

PYPI_PACKAGE = "ukkonen"

inherit setuptools3

SRC_URI += "git://github.com/asottile/ukkonen;protocol=https;branch=main;tag=v${PV}"
SRCREV = "5fa9858f0997927d9f9c1794e1741fe8dbd4c1e8"
DEPENDS += " \
	python3-pip-native \
	python3-cffi-native \
"

RDEPENDS:${PN} = " \
	python3-cffi \
"
