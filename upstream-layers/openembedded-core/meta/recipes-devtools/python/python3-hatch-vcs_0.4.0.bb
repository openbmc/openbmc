SUMMARY = "Hatch plugin for versioning with your preferred VCS"
HOMEPAGE = "https://pypi.org/project/hatch-vcs/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=26501cfd0bbddf830ee820e95551fa3d"

inherit pypi python_hatchling

PYPI_PACKAGE = "hatch_vcs"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "093810748fe01db0d451fabcf2c1ac2688caefd232d4ede967090b1c1b07d9f7"

BBCLASSEXTEND = "native nativesdk"
