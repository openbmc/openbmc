SUMMARY = "python-distutils extension"
DESCRIPTION = "python-distutils extension integrating gettext support, themed icons and scrollkeeper based documentation"
HOMEPAGE = "https://launchpad.net/python-distutils-extra"
SECTION = "devel/python"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4325afd396febcb659c36b49533135d4"

SRC_URI = "https://launchpad.net/python-distutils-extra/trunk/${PV}/+download/python-distutils-extra-${PV}.tar.gz"
SRC_URI[sha256sum] = "723f24f4d65fc8d99b33a002fbbb3771d4cc9d664c97085bf37f3997ae8063af"

inherit setuptools3

S = "${WORKDIR}/python-distutils-extra-${PV}"

BBCLASSEXTEND = "native"
