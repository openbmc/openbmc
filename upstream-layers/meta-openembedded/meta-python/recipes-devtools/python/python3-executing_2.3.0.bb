SUMMARY = "Get the currently executing AST node of a frame, and other information"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a3d6c15f7859ae235a78f2758e5a48cf"

DEPENDS = "python3-setuptools-scm-native"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

SRC_URI[sha256sum] = "15919cb5d667e5cb4e099511971d00d659573fff2dd5c4e6cd8b71636c7858d2"
