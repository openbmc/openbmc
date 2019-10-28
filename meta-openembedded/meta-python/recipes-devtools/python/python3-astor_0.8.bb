SUMMARY = "Easy manipulation of Python source via the AST."
HOMEPAGE = "https://github.com/berkerpeksag/astor"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=561205fdabc3ec52cae2d30815b8ade7"

SRC_URI = "git://github.com/berkerpeksag/astor.git \
           file://f820f3ff7ad8818475b6e107e63aa9a54252d2a9.patch \
          "
SRCREV ?= "3a7607e31f0c17e747ded5cfe0b582d99f7caecf"

inherit setuptools3

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
