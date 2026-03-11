DESCRIPTION = "Random Strings in Python"
HOMEPAGE = "https://github.com/leapfrogonline/rstr"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5c15c04f1cdbd42c6152066f0b02102a"

SRC_URI[sha256sum] = "c4a564d4dfb4472d931d145c43d1cf1ad78c24592142e7755b8866179eeac012"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"
