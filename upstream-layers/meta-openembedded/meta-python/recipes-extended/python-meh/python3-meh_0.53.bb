SUMMARY = "A python library for handling exceptions"
DESCRIPTION = "The python-meh package is a python library for handling, saving, and reporting \
exceptions."
HOMEPAGE = "https://github.com/rhinstaller/python-meh"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit setuptools3_legacy


SRC_URI = "git://github.com/rhinstaller/python-meh.git;protocol=https;branch=master"

SRCREV = "35376b77be9f3e39a81fb19c6d8d86fe17b54c2a"

FILES:${PN} += "${datadir}/python-meh"
