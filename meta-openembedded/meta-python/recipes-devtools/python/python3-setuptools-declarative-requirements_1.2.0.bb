SUMMARY = "File support for setuptools declarative setup.cfg"
HOMEPAGE = "https://pypi.org/project/setuptools-declarative-requirements/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "d11fdb5ef818c65b20bc241e0f5ef44905a5640b681dae21ba1ac1742dab1fd1"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"
