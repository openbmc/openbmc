SUMMARY = "A tiny CSS parser"
DESCRIPTION = "Tinycss2 is a low-level CSS parser and generator written in \
Python: it can parse strings, return objects representing tokens and blocks, \
and generate CSS strings corresponding to these objects."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1d072d7e30e34f33f8ae956ada04fa2c"

SRC_URI[sha256sum] = "d339d2b616ba90ccce58da8495a78f46e55d4d25f9fd71dfd526f07e7d53f957"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
