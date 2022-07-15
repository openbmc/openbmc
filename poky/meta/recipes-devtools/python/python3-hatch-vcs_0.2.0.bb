SUMMARY = "Hatch plugin for versioning with your preferred VCS"
HOMEPAGE = "https://pypi.org/project/hatch-vcs/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=26501cfd0bbddf830ee820e95551fa3d"

inherit pypi python_hatchling

PYPI_PACKAGE = "hatch_vcs"

SRC_URI[sha256sum] = "9913d733b34eec9bb0345d0626ca32165a4ad2de15d1ce643c36d09ca908abff"

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = "/hatch-vcs/(?P<pver>(\d+[\.\-_]*)+)/"
