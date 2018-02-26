DESCRIPTION = "The concurrent.futures module provides a high-level interface for asynchronously executing callables."
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=3d78c5bb15ac641d34f2ddc3bd7f51fa"
HOMEPAGE = "http://code.google.com/p/pythonfutures"
DEPENDS = "python"

SRC_URI = "https://pypi.python.org/packages/source/f/futures/futures-${PV}.tar.gz"
SRC_URI[md5sum] = "ced2c365e518242512d7a398b515ff95"
SRC_URI[sha256sum] = "0542525145d5afc984c88f914a0c85c77527f65946617edb5274f72406f981df"

S = "${WORKDIR}/futures-${PV}"

inherit setuptools

BBCLASSEXTEND = "native"
