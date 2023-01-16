SUMMARY = "ANSII Color formatting for output in terminal"
HOMEPAGE = "https://pypi.python.org/pypi/termcolor"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=e5f5f7c9b280511f124dba5dda3d180e"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "dfc8ac3f350788f23b2947b3e6cfa5a53b630b612e6cd8965a015a776020b99a"

DEPENDS += " \
	${PYTHON_PN}-toml-native \
	${PYTHON_PN}-hatch-vcs-native \
"

BBCLASSEXTEND = "native"
