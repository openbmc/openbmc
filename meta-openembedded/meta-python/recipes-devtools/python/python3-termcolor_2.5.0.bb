SUMMARY = "ANSII Color formatting for output in terminal"
HOMEPAGE = "https://pypi.python.org/pypi/termcolor"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=e5f5f7c9b280511f124dba5dda3d180e"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "998d8d27da6d48442e8e1f016119076b690d962507531df4890fcd2db2ef8a6f"

DEPENDS += " \
	python3-toml-native \
	python3-hatch-vcs-native \
"

BBCLASSEXTEND = "native"
