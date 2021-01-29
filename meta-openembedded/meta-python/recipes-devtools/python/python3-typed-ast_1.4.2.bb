SUMMARY = "Modified fork of CPython's ast module that parses `# type:` comments"
HOMEPAGE = "https://github.com/python/typed_ast"
LICENSE = "Apache-2.0 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=97f1494e93daf66a5df47118407a4c4f"

PYPI_PACKAGE = "typed_ast"

inherit pypi setuptools3

SRC_URI[sha256sum] = "9fc0b3cb5d1720e7141d103cf4819aea239f7d136acf9ee4a69b047b7986175a"

BBCLASSEXTEND = "native"
