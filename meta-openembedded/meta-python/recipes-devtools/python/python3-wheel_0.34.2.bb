SUMMARY = "The official binary distribution format for Python "
HOMEPAGE = "https://github.com/pypa/wheel"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9d66b41bc2a080e7174acc5dffecd752"

SRC_URI[md5sum] = "ce2a27f99c130a927237b5da1ff5ceaf"
SRC_URI[sha256sum] = "8788e9155fe14f54164c1b9eb0a319d98ef02c160725587ad60f14ddc57b6f96"

inherit pypi setuptools3

BBCLASSEXTEND = "native"

