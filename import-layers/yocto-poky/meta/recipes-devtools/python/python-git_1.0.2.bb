SUMMARY = "Python library used to interact with Git repositories"
DESCRIPTION = "GitPython provides object model read and write access to \
a git repository. Access repository information conveniently, alter the \
index directly, handle remotes, or go down to low-level object database \
access with big-files support."
HOMEPAGE = "http://github.com/gitpython-developers/GitPython"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8b8d26c37c1d5a04f9b0186edbebc183"
DEPENDS = "python-gitdb"

SRC_URI = "http://pypi.python.org/packages/source/G/GitPython/GitPython-${PV}.tar.gz"

SRC_URI[md5sum] = "d92d96a8da0fc77cf141d3e16084e094"
SRC_URI[sha256sum] = "85de72556781480a38897a77de5b458ae3838b0fd589593679a1b5f34d181d84"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/GitPython/"
UPSTREAM_CHECK_REGEX = "/GitPython/(?P<pver>(\d+[\.\-_]*)+)"

S = "${WORKDIR}/GitPython-${PV}"

inherit setuptools

RDEPENDS_${PN} += "python-gitdb python-lang python-io python-shell python-math python-re python-subprocess python-stringold python-unixadmin"

BBCLASSEXTEND = "nativesdk"
