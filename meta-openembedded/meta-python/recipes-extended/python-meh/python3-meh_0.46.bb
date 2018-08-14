SUMMARY = "A python library for handling exceptions"
DESCRIPTION = "The python-meh package is a python library for handling, saving, and reporting \
exceptions."
HOMEPAGE = "http://git.fedorahosted.org/git/?p=python-meh.git"
LICENSE = "GPLv2+"

inherit setuptools3

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/rhinstaller/python-meh.git;protocol=https;branch=master \
"
SRCREV = "bb1156728a4f76e5e3638ab20b9454a1568a99db"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

FILES_${PN} += "${datadir}/*"

