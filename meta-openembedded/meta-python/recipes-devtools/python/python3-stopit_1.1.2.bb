SUMMARY = "Raise asynchronous exceptions in other threads, control the timeout of blocks or callables with two context managers and two decorators."
HOMEPAGE = "https://pypi.org/project/stopit/"
SECTION = "devel/python"

SRC_URI += " file://LICENSE "
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=497c556f42b1355b64190da2f3d88f93"

SRC_URI[sha256sum] = "f7f39c583fd92027bd9d06127b259aee7a5b7945c1f1fa56263811e1e766996d"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-setuptools \
    "

BBCLASSEXTEND = "native nativesdk"