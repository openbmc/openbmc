SUMMARY = "Experimental type system extensions for programs checked with the mypy typechecker"
HOMEPAGE = "https://github.com/python/mypy_extensions"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0fe3219e2470a78c0d1837019b8b426e"

PYPI_PACKAGE = "mypy_extensions"

inherit pypi setuptools3

SRC_URI[sha256sum] = "2d82818f5bb3e369420cb3c4060a7970edba416647068eb4c5343488a6c604a8"

BBCLASSEXTEND = "native"
