SUMMARY = "Python library used to interact with Git repositories"
DESCRIPTION = "GitPython provides object model read and write access to \
a git repository. Access repository information conveniently, alter the \
index directly, handle remotes, or go down to low-level object database \
access with big-files support."
HOMEPAGE = "http://github.com/gitpython-developers/GitPython"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8b8d26c37c1d5a04f9b0186edbebc183"

SRC_URI = "https://files.pythonhosted.org/packages/source/G/GitPython/GitPython-${PV}.tar.gz"

SRC_URI[md5sum] = "aa0ba9df0abe4c8f35dd7bb9be85d56e"
SRC_URI[sha256sum] = "d8e7adaacceedd3d043e6cd2544f57dbe00c53fc26374880b7cea67f3188aa68"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/GitPython/"
UPSTREAM_CHECK_REGEX = "/GitPython/(?P<pver>(\d+[\.\-_]*)+)"

S = "${WORKDIR}/GitPython-${PV}"

BBCLASSEXTEND = "nativesdk"
