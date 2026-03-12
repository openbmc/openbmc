SUMMARY = "Hatch plugin for versioning with your preferred VCS"
HOMEPAGE = "https://pypi.org/project/hatch-vcs/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=26501cfd0bbddf830ee820e95551fa3d"

inherit pypi python_hatchling

PYPI_PACKAGE = "hatch_vcs"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "0395fa126940340215090c344a2bf4e2a77bcbe7daab16f41b37b98c95809ff9"

BBCLASSEXTEND = "native nativesdk"
