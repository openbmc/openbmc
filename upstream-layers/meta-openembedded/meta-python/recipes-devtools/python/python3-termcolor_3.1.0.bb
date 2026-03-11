SUMMARY = "ANSII Color formatting for output in terminal"
HOMEPAGE = "https://pypi.python.org/pypi/termcolor"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=e5f5f7c9b280511f124dba5dda3d180e"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "6a6dd7fbee581909eeec6a756cff1d7f7c376063b14e4a298dc4980309e55970"

DEPENDS += " \
	python3-toml-native \
	python3-hatch-vcs-native \
"

BBCLASSEXTEND = "native"
