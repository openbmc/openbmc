SUMMARY = "A small and simple INI-file parser module"
HOMEPAGE = "https://pypi.org/project/iniconfig/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a6bb0320b04a0a503f12f69fea479de9"

SRC_URI[md5sum] = "0b7f3be87481211c183eae095bcea6f1"
SRC_URI[sha256sum] = "bc3af051d7d14b2ee5ef9969666def0cd1a000e121eaea580d4a313df4b37f32"

DEPENDS += "python3-setuptools-scm-native"

inherit pypi python_setuptools_build_meta
