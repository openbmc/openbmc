SUMMARY = "Python Remote Objects"
HOMEPAGE = "https://pypi.python.org/pypi/Pyro4/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b48b62dd270c4691fabaa85be3236030"

SRCNAME = "Pyro4"

SRC_URI = "https://pypi.python.org/packages/source/P/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "75ed5cd142803b0a8d587dc8b6bb51ed"
SRC_URI[sha256sum] = "39c6ca7f86b0f0bebfeada687a5a8b99f66470a52b0f815195ae63c683266f24"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools
