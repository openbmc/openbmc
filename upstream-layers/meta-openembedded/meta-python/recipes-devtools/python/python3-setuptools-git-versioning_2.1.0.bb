SUMMARY = "Use git repo data for building a version number according PEP-440"
HOMEPAGE = "https://setuptools-git-versioning.readthedocs.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=92e79e3a844e66731724600f3ac9c0d8"

SRC_URI[sha256sum] = "6aef5b8bb1cfb953b6b343d27cbfc561d96cf2a2ee23c2e0dd3591042a059921"

inherit pypi setuptools3

PACKAGECONFIG ?= ""
PACKAGECONFIG[python-version-smaller-3-dot-11] = ",,,python3-tomli"

RDEPENDS:${PN} += "python3-core python3-datetime python3-logging \
				   python3-packaging python3-pprint python3-setuptools \
				   python3-tomllib"

PYPI_PACKAGE = "setuptools_git_versioning"

BBCLASSEXTEND += "native"
