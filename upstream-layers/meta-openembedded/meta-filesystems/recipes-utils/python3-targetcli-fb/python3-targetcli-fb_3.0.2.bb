SUMMARY = "Command shell for managing Linux LIO kernel target"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=34400b68072d710fecd0a2940a0d1658"
DEPENDS = "python3-hatchling-native python3-hatch-vcs-native"

inherit pypi python_hatchling

PYPI_PACKAGE = "targetcli"

SRC_URI[sha256sum] = "00422cce70f1d4a4d804cb1f3f633169ff4ad13b017b7e52d8f49262afe43b9e"

RDEPENDS:${PN} = "python3-rtslib-fb python3-pygobject"
