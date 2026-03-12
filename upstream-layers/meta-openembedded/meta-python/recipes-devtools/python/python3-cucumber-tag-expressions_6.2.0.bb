SUMMARY = "Cucumber tag expression parser"
DESCRIPTION = "Provides a tag-expression parser and evaluation logic for cucumber/behave"
HOMEPAGE = "https://github.com/cucumber/tag-expressions"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=6;endline=6;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "b60aa2cdbf9ac43e28d9b0e4fd49edf9f09d5d941257d2912f5228f9d166c023"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "cucumber_tag_expressions"

DEPENDS += "\
    python3-setuptools-scm-native \
"
