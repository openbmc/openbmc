SUMMARY = "python-distutils extension"
DESCRITION = "python-distutils extension integrating gettext support, themed icons and scrollkeeper based documentation"
HOMEPAGE = "https://launchpad.net/python-distutils-extra"
SECTION = "devel/python"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4325afd396febcb659c36b49533135d4"

PV = "2.39"
SRC_URI = "https://launchpad.net/python-distutils-extra/trunk/${PV}/+download/python-distutils-extra-${PV}.tar.gz"
SRC_URI[md5sum] = "16e06db0ef73a35b4bff4b9eed5699b5"
SRC_URI[sha256sum] = "723f24f4d65fc8d99b33a002fbbb3771d4cc9d664c97085bf37f3997ae8063af"

BBCLASSEXTEND = "native"
