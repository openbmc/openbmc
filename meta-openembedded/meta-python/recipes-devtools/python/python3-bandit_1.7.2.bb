SUMMARY = "Security oriented static analyser for python code."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRC_URI[sha256sum] = "6d11adea0214a43813887bfe71a377b5a9955e4c826c8ffd341b494e3ab25260"

DEPENDS = "python3-pbr-native python3-git python3-pbr python3-pyyaml python3-six python3-stevedore"

inherit setuptools3 pypi

RDEPENDS:${PN} += "python3-modules python3-git python3-pbr python3-pyyaml python3-six python3-stevedore"

BBCLASSEXTEND = "native"
