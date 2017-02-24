SUMMARY = "the blessed package to manage your versions by scm tags"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=838c366f69b72c5df05c96dff79b35f2"

inherit pypi setuptools

SRCNAME = "setuptools_scm"

SRC_URI = "https://pypi.python.org/packages/84/aa/c693b5d41da513fed3f0ee27f1bf02a303caa75bbdfa5c8cc233a1d778c4/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "4d19b2bc9580016d991f665ac20e2e8f"
SRC_URI[sha256sum] = "8c45f738a23410c5276b0ed9294af607f491e4260589f1eb90df8312e23819bf"

S = "${WORKDIR}/${SRCNAME}-${PV}"

RDEPENDS_${PN} = "python-py python-setuptools python-argparse python-debugger python-json"


