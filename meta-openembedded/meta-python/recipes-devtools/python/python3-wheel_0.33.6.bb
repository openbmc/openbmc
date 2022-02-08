SUMMARY = "The official binary distribution format for Python "
HOMEPAGE = "https://github.com/pypa/wheel"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9d66b41bc2a080e7174acc5dffecd752"

SRC_URI = "git://github.com/pypa/wheel.git;branch=master;protocol=https"
SRCREV ?= "b227ddd5beaba49294017d061d501f6d433393b0"


inherit setuptools3

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"

