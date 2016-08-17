HOMEPAGE = "http://cheeseshop.python.org/pypi/simplejson"
SUMMARY = "Simple, fast, extensible JSON encoder/decoder for Python"
DESCRIPTION = "\
  JSON <http://json.org> encoder and decoder for Python 2.5+ \
  and Python 3.3+.  It is pure Python code with no dependencies, \
  but includes an optional C extension for a serious speed boost \
  "
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c6338d7abd321c0b50a2a547e441c52e"
PR = "r0"

SRCNAME = "simplejson"

SRC_URI = "https://pypi.python.org/packages/source/s/simplejson/${SRCNAME}-${PV}.tar.gz"
SRC_URI[md5sum] = "117346e5ee4ed4434ffe485f8e58f5ed"
SRC_URI[sha256sum] = "63d7f7b14a20f29f74325a69e6db45925eaf6e3a003eab46c0234fd050a8c93f"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

RDEPENDS_${PN} = "\
    python-core \
    python-re \
    python-io \
    python-netserver \
    python-numbers \
"


