SUMMARY = "Use git repo data for building a version number according PEP-440"
HOMEPAGE = "https://setuptools-git-versioning.readthedocs.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=92e79e3a844e66731724600f3ac9c0d8"

SRC_URI[sha256sum] = "c8a599bacf163b5d215552b5701faf5480ffc4d65426a5711a010b802e1590eb"

inherit pypi python_setuptools_build_meta

PACKAGECONFIG ?= ""
PACKAGECONFIG[python-version-smaller-3-dot-11] = ",,,python3-tomli"

RDEPENDS:${PN} += "python3-core python3-datetime python3-logging \
				   python3-packaging python3-pprint python3-setuptools \
				   python3-tomllib"

PYPI_PACKAGE = "setuptools_git_versioning"

BBCLASSEXTEND += "native"
