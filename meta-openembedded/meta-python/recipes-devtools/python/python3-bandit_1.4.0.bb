SUMMARY = "Security oriented static analyser for python code."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI[md5sum] = "f74155cb9921be857693b32d2531e857"
SRC_URI[sha256sum] = "cb977045497f83ec3a02616973ab845c829cdab8144ce2e757fe031104a9abd4"

DEPENDS = "python3-pyyaml python3-six python3-stevedore python3-pbr-native"

inherit setuptools3 pypi

BBCLASSEXTEND = "native"
