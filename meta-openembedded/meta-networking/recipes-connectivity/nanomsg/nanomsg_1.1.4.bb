SUMMARY = "nanomsg socket library"
DESCRIPTION = "nanomsg is a socket library that provides several common \
communication patterns. It aims to make the networking layer fast, scalable, \
and easy to use. Implemented in C, it works on a wide range of operating \
systems with no further dependencies."
HOMEPAGE = "https://nanomsg.org/"
LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://COPYING;md5=587b3fd7fd291e418ff4d2b8f3904755"

SECTION = "libs/networking"

SRC_URI = "git://github.com/nanomsg/nanomsg.git;protocol=https"
SRCREV = "e7f8a751316b942d8962cd0232c2d606c1d9a9db"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

# nanomsg documentation generation requires asciidoctor,
# not asciidoc, and currently there's no asciidoctor-native
# recipe anywhere in openembedded-core or meta-openembedded
EXTRA_OECMAKE = " -DNN_ENABLE_DOC=OFF "

# we don't want nanomsg-tools to be renamed to libnanomsg-tools
DEBIAN_NOAUTONAME_${PN}-tools = "1"

PACKAGES =+ "${PN}-tools"
FILES_${PN}-tools = "${bindir}/*"
