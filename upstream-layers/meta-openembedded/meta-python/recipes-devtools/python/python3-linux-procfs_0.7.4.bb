DESCRIPTION = "Python classes to extract information from the Linux kernel /proc files."
HOMEPAGE = "https://git.kernel.org/pub/scm/libs/python/python-linux-procfs/python-linux-procfs.git/"
SECTION = "devel/python"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6a6855782848d572347212f667a2d637"

SRC_URI = "git://git.kernel.org/pub/scm/libs/python/python-linux-procfs/python-linux-procfs.git;branch=main;tag=v${PV}"
SRCREV = "702d6360ce082d8328e0cfd84f6415b0c94ca6dd"


inherit python_setuptools_build_meta

RDEPENDS:${PN} += "python3-six"
