SUMMARY = "Sphinx Theme reader"
HOMEPAGE = "https://github.com/readthedocs/sphinx_rtd_theme"
SECTION = "devel/python"
LICENSE = "MIT & OFL-1.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a1db7d4ef426c2935227264e1d4ae8f9 \
                    file://OFL-License.txt;md5=4534c22e0147eadb6828bd9fe86d4868 \
                    file://Apache-License-2.0.txt;md5=8a75796f0ef19c3f601d69857f5a9a5b"

RDEPENDS:${PN} += " \
    python3-compile \
    python3-sphinx \
    python3-sphinxcontrib-jquery \
"

PYPI_PACKAGE = "sphinx_rtd_theme"

SRC_URI[sha256sum] = "bd5d7b80622406762073a04ef8fadc5f9151261563d47027de09910ce03afe6b"
UPSTREAM_CHECK_REGEX ?= "/sphinx-rtd-theme/(?P<pver>(\d+[\.\-_]*)+)/"

inherit setuptools3 pypi

#Fake out the setup scipt
export CI = "True"
export TOX_ENV_NAME = "True"

BBCLASSEXTEND = "native nativesdk"
