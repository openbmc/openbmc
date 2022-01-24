SUMMARY = "Security oriented static analyser for python code."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI[sha256sum] = "a81b00b5436e6880fa8ad6799bc830e02032047713cbb143a12939ac67eb756c"

DEPENDS = "python3-pbr-native python3-git python3-pbr python3-pyyaml python3-six python3-stevedore"

inherit setuptools3 pypi

RDEPENDS:${PN} += "python3-modules python3-git python3-pbr python3-pyyaml python3-six python3-stevedore"

BBCLASSEXTEND = "native"
