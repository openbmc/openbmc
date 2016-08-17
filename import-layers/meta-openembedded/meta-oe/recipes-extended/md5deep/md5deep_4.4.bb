SUMMARY = "md5deep and hashdeep to compute and audit hashsets of amounts of files."
DESCRIPTION = "md5deep is a set of programs to compute MD5, SHA-1, SHA-256, Tiger, or Whirlpool message digests on an arbitrary number of files. This package also includes hashdeep which is also able to audit hashsets."
AUTHOR = "Jesse Kornblum, Simson L. Garfinkel"
HOMEPAGE = "http://md5deep.sourceforge.net"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=9190f660105b9a56cdb272309bfd5491"
# Release 4.4
SRCREV = "cd2ed7416685a5e83eb10bb659d6e9bec01244ae"

SRC_URI = "git://github.com/jessek/hashdeep.git \
        file://wrong-variable-expansion.patch \
        "

S = "${WORKDIR}/git"

inherit autotools
