SUMMARY = "A Python library for building configuration shells"
DESCRIPTION = "configshell-fb is a Python library that provides a framework for \
building simple but nice CLI-based applications. This runs with Python 2 and \
2to3 is run by setup.py to run on Python 3."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI = "git://github.com/open-iscsi/configshell-fb.git;protocol=https;branch=master"
SRCREV = "d74a33b69f688ed9b8b28033835303604639d4eb"

S = "${WORKDIR}/git"

inherit setuptools3
PIP_INSTALL_PACKAGE = "configshell_fb"

RDEPENDS:${PN} += "python3-modules python3-fcntl python3-six"
