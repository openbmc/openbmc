DESCRIPTION = "Python classes to extract information from the Linux kernel /proc files."
HOMEPAGE = "https://git.kernel.org/pub/scm/libs/python/python-linux-procfs/python-linux-procfs.git/"
SECTION = "devel/python"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6a6855782848d572347212f667a2d637"

SRC_URI = "git://git.kernel.org/pub/scm/libs/python/python-linux-procfs/python-linux-procfs.git;branch=main"
SRCREV = "59ecd1ba018141a02ffe59c16a9346991dfd0d48"

S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS:${PN} += "python3-six"
