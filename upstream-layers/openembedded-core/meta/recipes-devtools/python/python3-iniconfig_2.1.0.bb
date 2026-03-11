SUMMARY = "A small and simple INI-file parser module"
HOMEPAGE = "https://pypi.org/project/iniconfig/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4a73af4b5f1f3a8731e5def70e8c1254"

SRC_URI[sha256sum] = "3abbd2e30b36733fee78f9c7f7308f2d0050e88f0087fd25c2645f63c773e1c7"

DEPENDS += "python3-hatch-vcs-native"

inherit pypi python_hatchling

BBCLASSEXTEND = "native nativesdk"
