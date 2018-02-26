DESCRIPTION = "A python library for manipulating kickstart files"
HOMEPAGE = "http://fedoraproject.org/wiki/pykickstart"
LICENSE = "GPLv2+"

LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

DEPENDS = "python3"
RDEPENDS_${PN} = "python3 \
                  python3-requests \
                  python3-six \
"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/rhinstaller/pykickstart.git;protocol=https;branch=pykickstart-2 \
           file://0001-support-authentication-for-kickstart.patch \
           file://0002-pykickstart-parser.py-add-lock-for-readKickstart-and.patch \
           file://0003-comment-out-sections-shutdown-and-environment-in-gen.patch \
           file://0004-load.py-retry-to-invoke-request-with-timeout.patch \
           file://0005-tweak-shebang-to-python3.patch \
           "
SRCREV = "b2787a818540e678c2f9c5dca0c6bbd65b8b55e5"

inherit setuptools3
