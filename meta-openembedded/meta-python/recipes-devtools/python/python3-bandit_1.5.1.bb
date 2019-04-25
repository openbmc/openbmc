SUMMARY = "Security oriented static analyser for python code."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI[md5sum] = "81ba3979ded1b421fa8d69e6faa06dcb"
SRC_URI[sha256sum] = "9413facfe9de1e1bd291d525c784e1beb1a55c9916b51dae12979af63a69ba4c"

DEPENDS = "python3-pbr-native python3-git python3-pbr python3-pyyaml python3-six python3-stevedore"

inherit setuptools3 pypi

RDEPENDS_${PN} += "python3-modules python3-git python3-pbr python3-pyyaml python3-six python3-stevedore"

BBCLASSEXTEND = "native"
