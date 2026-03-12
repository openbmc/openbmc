SUMMARY = "ANSII Color formatting for output in terminal"
HOMEPAGE = "https://pypi.python.org/pypi/termcolor"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=e5f5f7c9b280511f124dba5dda3d180e"

inherit pypi python_hatchling

SRC_URI[sha256sum] = "348871ca648ec6a9a983a13ab626c0acce02f515b9e1983332b17af7979521c5"

DEPENDS += " \
	python3-toml-native \
	python3-hatch-vcs-native \
"

BBCLASSEXTEND = "native"
