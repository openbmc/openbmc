SUMMARY = "Simple, fast, extensible JSON encoder/decoder for Python"
HOMEPAGE = "http://cheeseshop.python.org/pypi/simplejson"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c6338d7abd321c0b50a2a547e441c52e"
PR = "r1"

SRCNAME = "simplejson"

SRC_URI = "http://cheeseshop.python.org/packages/source/s/simplejson/${SRCNAME}-${PV}.tar.gz"
SRC_URI[md5sum] = "53b1371bbf883b129a12d594a97e9a18"
SRC_URI[sha256sum] = "d58439c548433adcda98e695be53e526ba940a4b9c44fb9a05d92cd495cdd47f"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit distutils

RDEPENDS_${PN} = "\
    python-core \
    python-re \
    python-io \
    python-netserver \
    python-numbers \
"


