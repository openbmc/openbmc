SUMMARY = "A python library for handling exceptions"
DESCRIPTION = "The python-meh package is a python library for handling, saving, and reporting \
exceptions."
HOMEPAGE = "http://git.fedorahosted.org/git/?p=python-meh.git"
LICENSE = "GPLv2+"

inherit setuptools3

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/rhinstaller/python-meh.git;protocol=https;branch=master \
"
SRCREV = "78d868d99104317ce8820f39f61d207566e61a55"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

FILES_${PN} += "${datadir}/*"

