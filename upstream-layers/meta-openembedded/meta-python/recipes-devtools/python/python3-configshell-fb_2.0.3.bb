SUMMARY = "A Python library for building configuration shells"
DESCRIPTION = "configshell-fb is a Python library that provides a framework for \
building simple but nice CLI-based applications."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=34400b68072d710fecd0a2940a0d1658"

PYPI_PACKAGE = "configshell_fb"

SRC_URI[sha256sum] = "dbb086c16c1603a230f9017ef85baccabfd5d87439a01e963377e50f10220f53"

inherit pypi python_hatchling

DEPENDS += "python3-hatch-vcs-native"

RDEPENDS:${PN} += " \
    python3-fcntl \
    python3-modules \
    python3-pyparsing \
"
