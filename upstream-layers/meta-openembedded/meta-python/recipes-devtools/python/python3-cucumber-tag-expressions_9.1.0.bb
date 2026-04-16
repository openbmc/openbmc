SUMMARY = "Cucumber tag expression parser"
DESCRIPTION = "Provides a tag-expression parser and evaluation logic for cucumber/behave"
HOMEPAGE = "https://github.com/cucumber/tag-expressions"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=134f1026f0de92fd30e71976590a2868"

SRC_URI[sha256sum] = "d960383d5885300ebcbcb14e41657946fde2a59d5c0f485eb291bc6a0e228acc"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "cucumber_tag_expressions"

DEPENDS += "\
    python3-setuptools-scm-native \
    python3-uv-build-native \
"
