SUMMARY = "Security oriented static analyser for python code."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI[md5sum] = "c6a6772d7afa0af8828b3384e73b7085"
SRC_URI[sha256sum] = "41e75315853507aa145d62a78a2a6c5e3240fe14ee7c601459d0df9418196065"

DEPENDS = "python3-pbr-native python3-git python3-pbr python3-pyyaml python3-six python3-stevedore"

inherit setuptools3 pypi

RDEPENDS_${PN} += "python3-modules python3-git python3-pbr python3-pyyaml python3-six python3-stevedore"

BBCLASSEXTEND = "native"
