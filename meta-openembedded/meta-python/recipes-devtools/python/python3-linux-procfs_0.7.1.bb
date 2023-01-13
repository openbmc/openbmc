DESCRIPTION = "Python classes to extract information from the Linux kernel /proc files."
HOMEPAGE = "https://git.kernel.org/pub/scm/libs/python/python-linux-procfs/python-linux-procfs.git/"
SECTION = "devel/python"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

SRC_URI = "git://git.kernel.org/pub/scm/libs/python/python-linux-procfs/python-linux-procfs.git;branch=main"
SRCREV = "7f43598387e44e2da93ead2e075b7232429e4cc4"

S = "${WORKDIR}/git"

inherit setuptools3
