SUMMARY = "Security oriented static analyser for python code."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI[md5sum] = "24965f102dc62129d3fafe4fe596a3b8"
SRC_URI[sha256sum] = "8a4c7415254d75df8ff3c3b15cfe9042ecee628a1e40b44c15a98890fbfc2608"

DEPENDS = "python3-pbr-native python3-git python3-pbr python3-pyyaml python3-six python3-stevedore"

inherit setuptools3 pypi

RDEPENDS_${PN} += "python3-modules python3-git python3-pbr python3-pyyaml python3-six python3-stevedore"

BBCLASSEXTEND = "native"
