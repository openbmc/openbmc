SUMMARY = "File support for setuptools declarative setup.cfg"
HOMEPAGE = "https://pypi.org/project/setuptools-declarative-requirements/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "57a5b9bb9ad350c278e8aa6be4cdebbcd925b9ba71d6a712a178a618cfb898f7"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"
