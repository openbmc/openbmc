SUMMARY = "Parser library for project Change Log documents."
HOMEPAGE = "https://git.sr.ht/~bignose/changelog-chug"
LICENSE = "AGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=7a9d97a1655c1051f8a9351e15ea3122"

SRC_URI[sha256sum] = "98ee1e8be75b6e9d512c35292c3c293a124541a4ec2014a6ec3cf33a3d265d2d"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "changelog_chug"

DEPENDS += " \
    python3-semver-native \
    python3-docutils-native \
"

RDEPENDS:${PN} += "\
    python3-core \
    python3-semver \
    python3-docutils \
"

BBCLASSEXTEND = "native nativesdk"
