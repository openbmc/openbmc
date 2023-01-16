SUMMARY = "Hatch plugin for versioning with your preferred VCS"
HOMEPAGE = "https://pypi.org/project/hatch-vcs/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=26501cfd0bbddf830ee820e95551fa3d"

inherit pypi python_hatchling

PYPI_PACKAGE = "hatch_vcs"

SRC_URI[sha256sum] = "cec5107cfce482c67f8bc96f18bbc320c9aa0d068180e14ad317bbee5a153fee"

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = "/hatch-vcs/(?P<pver>(\d+[\.\-_]*)+)/"
