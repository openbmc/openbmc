SUMMARY = "A module wrapper for os.path"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a7126e068206290f3fe9f8d6c713ea6"

SRC_URI[sha256sum] = "bea3816e1d54f4e33aac78d2031a0b0ed2f95e69db85b45d51f17df97071da69"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"

BBCLASSEXTEND = "nativesdk native"
