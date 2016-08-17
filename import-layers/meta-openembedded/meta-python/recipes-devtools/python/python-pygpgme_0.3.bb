SUMMARY = "A Python module for working with OpenPGP messages"
DESCRIPTION = "PyGPGME is a Python module that lets you sign, verify, \
               encrypt and decrypt messages using the OpenPGP format."
HOMEPAGE = "https://launchpad.net/pygpgme"

LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=6517bdc8f2416f27ab725d4702f7aac3"

SRC_URI = "file://run-ptest"

SRC_URI[md5sum] = "d38355af73f0352cde3d410b25f34fd0"
SRC_URI[sha256sum] = "5fd887c407015296a8fd3f4b867fe0fcca3179de97ccde90449853a3dfb802e1"

DEPENDS += "gpgme"
RDEPENDS_${PN} += "gnupg"

inherit pypi setuptools ptest

do_install_ptest(){
    install ${S}/test_all.py ${D}${PTEST_PATH}
    cp -r ${S}/tests ${D}${PTEST_PATH}
}
