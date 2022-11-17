SUMMARY = "ANSII Color formatting for output in terminal"
HOMEPAGE = "https://pypi.python.org/pypi/termcolor"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=e5f5f7c9b280511f124dba5dda3d180e"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "b80df54667ce4f48c03fe35df194f052dc27a541ebbf2544e4d6b47b5d6949c4"

DEPENDS += " \
	${PYTHON_PN}-toml-native \
	${PYTHON_PN}-hatch-vcs-native \
"

BBCLASSEXTEND = "native"
