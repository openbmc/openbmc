SUMMARY = "RFC 3986 compliant URI parsing library"
HOMEPAGE = "https://uriparser.github.io"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=72b0f9c74ae96eeab8cf1bf3efe08da2"

SRC_URI := "${SOURCEFORGE_MIRROR}/project/uriparser/Sources/${PV}/uriparser-${PV}.tar.bz2"

inherit autotools

EXTRA_OECONF = "--disable-test --disable-doc"

SRC_URI[md5sum] = "9aabdc3611546f553f4af372167de6d6"
SRC_URI[sha256sum] = "ce7ccda4136974889231e8426a785e7578e66a6283009cfd13f1b24a5e657b23"

BBCLASSEXTEND += "native"
