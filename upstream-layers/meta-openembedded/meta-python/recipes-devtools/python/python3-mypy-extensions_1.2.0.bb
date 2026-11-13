SUMMARY = "Experimental type system extensions for programs checked with the mypy typechecker"
HOMEPAGE = "https://github.com/python/mypy_extensions"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0fe3219e2470a78c0d1837019b8b426e"

inherit pypi python_flit_core

SRC_URI[sha256sum] = "c6e275a07f1c2fc096b62940452d0eaa3f3af7de45d704787866240a7c6d5a51"

BBCLASSEXTEND = "native nativesdk"
