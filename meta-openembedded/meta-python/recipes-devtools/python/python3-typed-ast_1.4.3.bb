SUMMARY = "Modified fork of CPython's ast module that parses `# type:` comments"
HOMEPAGE = "https://github.com/python/typed_ast"
LICENSE = "Apache-2.0 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=97f1494e93daf66a5df47118407a4c4f"

PYPI_PACKAGE = "typed_ast"

inherit pypi setuptools3

SRC_URI[sha256sum] = "fb1bbeac803adea29cedd70781399c99138358c26d05fcbd23c13016b7f5ec65"

BBCLASSEXTEND = "native"
