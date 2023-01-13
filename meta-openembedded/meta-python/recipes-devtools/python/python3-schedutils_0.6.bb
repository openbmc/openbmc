DESCRIPTION = "Python interface for the Linux scheduler functions etc."
HOMEPAGE = "https://git.kernel.org/pub/scm/libs/python/python-schedutils/python-schedutils.git/"
SECTION = "devel/python"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

SRC_URI = "git://git.kernel.org/pub/scm/libs/python/python-schedutils/python-schedutils.git;branch=main"
SRCREV = "46469f425f9844f355f6496785ee1ce993b58747"

S = "${WORKDIR}/git"

inherit setuptools3
