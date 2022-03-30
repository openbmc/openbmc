SUMMARY = "Modified fork of CPython's ast module that parses `# type:` comments"
HOMEPAGE = "https://github.com/python/typed_ast"
LICENSE = "Apache-2.0 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=97f1494e93daf66a5df47118407a4c4f"

PYPI_PACKAGE = "typed_ast"

inherit pypi setuptools3

SRC_URI[sha256sum] = "525a2d4088e70a9f75b08b3f87a51acc9cde640e19cc523c7e41aa355564ae27"

BBCLASSEXTEND = "native"
