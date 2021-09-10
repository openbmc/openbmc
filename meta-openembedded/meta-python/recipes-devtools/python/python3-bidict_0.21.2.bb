SUMMARY = "The bidirectional mapping library for Python."
DESCRIPTION = "The bidirectional mapping library for Python."
HOMEPAGE = "https://bidict.readthedocs.io/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9741c346eef56131163e13b9db1241b3"

SRC_URI[sha256sum] = "4fa46f7ff96dc244abfc437383d987404ae861df797e2fd5b190e233c302be09"

inherit pypi setuptools3

DEPENDS += "python3-setuptools-scm-native"
