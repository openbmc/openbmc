SUMMARY = "ANSII Color formatting for output in terminal"
HOMEPAGE = "https://pypi.python.org/pypi/termcolor"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=e5f5f7c9b280511f124dba5dda3d180e"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "6b2cf769e93364a2676e1de56a7c0cff2cf5bd07f37e9cc80b0dd6320ebfe388"

DEPENDS += " \
	${PYTHON_PN}-toml-native \
	${PYTHON_PN}-hatch-vcs-native \
"

BBCLASSEXTEND = "native"
