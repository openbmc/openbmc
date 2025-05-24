SUMMARY = "ANSII Color formatting for output in terminal"
HOMEPAGE = "https://pypi.python.org/pypi/termcolor"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=e5f5f7c9b280511f124dba5dda3d180e"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "a6abd5c6e1284cea2934443ba806e70e5ec8fd2449021be55c280f8a3731b611"

DEPENDS += " \
	python3-toml-native \
	python3-hatch-vcs-native \
"

BBCLASSEXTEND = "native"
