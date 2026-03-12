SUMMARY = "A small and simple INI-file parser module"
HOMEPAGE = "https://pypi.org/project/iniconfig/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4a73af4b5f1f3a8731e5def70e8c1254"

SRC_URI[sha256sum] = "c76315c77db068650d49c5b56314774a7804df16fee4402c1f19d6d15d8c4730"

DEPENDS += "python3-hatch-vcs-native"

inherit pypi python_hatchling

BBCLASSEXTEND = "native nativesdk"
