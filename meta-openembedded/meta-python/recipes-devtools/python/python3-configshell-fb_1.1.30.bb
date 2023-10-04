SUMMARY = "A Python library for building configuration shells"
DESCRIPTION = "configshell-fb is a Python library that provides a framework for \
building simple but nice CLI-based applications. This runs with Python 2 and \
2to3 is run by setup.py to run on Python 3."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI[sha256sum] = "41f6b7eaa7c9ff422acdc910762fd39ef3333a365918463fef8b398d857170fe"

inherit pypi python_poetry_core

RDEPENDS:${PN} += " \
    python3-fcntl \
    python3-modules \
    python3-pyparsing \
    python3-six \
"
