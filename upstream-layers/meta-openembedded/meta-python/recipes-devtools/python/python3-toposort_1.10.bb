SUMMARY = "Implements a topological sort algorithm"
HOMEPAGE = "https://gitlab.com/ericvsmith/toposort"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "bfbb479c53d0a696ea7402601f4e693c97b0367837c8898bc6471adfca37a6bd"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native"
