SUMMARY = "A small and simple INI-file parser module"
HOMEPAGE = "https://pypi.org/project/iniconfig/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a6bb0320b04a0a503f12f69fea479de9"

SRC_URI[sha256sum] = "2d91e135bf72d31a410b17c16da610a82cb55f6b0477d1a902134b24a455b8b3"

DEPENDS += "python3-hatch-vcs-native"

inherit pypi python_hatchling

BBCLASSEXTEND = "native nativesdk"
