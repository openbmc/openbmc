SUMMARY = "Python library used to interact with Git repositories"
DESCRIPTION = "GitPython provides object model read and write access to \
a git repository. Access repository information conveniently, alter the \
index directly, handle remotes, or go down to low-level object database \
access with big-files support."
HOMEPAGE = "http://github.com/gitpython-developers/GitPython"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5279a7ab369ba336989dcf2a107e5c8e"

PYPI_PACKAGE = "gitpython"

CVE_PRODUCT = "gitpython_project:gitpython"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "1791de66309bc0c7cfca40bf8d2e3de7ca091cbf94e6051be1ad0722c61062af"

DEPENDS += " python3-gitdb"

RDEPENDS:${PN} += " \
                   python3-datetime \
                   python3-gitdb \
                   python3-io \
                   python3-logging \
                   python3-math \
                   python3-netclient \
                   python3-stringold \
                   python3-unittest \
                   python3-unixadmin \
                   git \
"

BBCLASSEXTEND = "native nativesdk"
