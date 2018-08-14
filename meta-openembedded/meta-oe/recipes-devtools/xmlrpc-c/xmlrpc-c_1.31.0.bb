DESCRIPTION = "XML-RPC for C/C++ is programming libraries and related tools to help you \
write an XML-RPC server or client in C or C++."

HOMEPAGE = "http://xmlrpc-c.sourceforge.net/"
LICENSE = "BSD & MIT"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=aefbf81ba0750f02176b6f86752ea951"

SRC_URI = "git://github.com/ensc/xmlrpc-c.git;branch=master \
           file://0001-fix-compile-failure-against-musl-C-library.patch \
           file://0002-fix-formatting-issues.patch \
"
SRCREV = "81443a9dc234cc275449dbc17867ad77ae189124"
S = "${WORKDIR}/git"

DEPENDS = "curl libxml2"
RDEPENDS_${PN} = "curl perl"

inherit cmake

EXTRA_OECMAKE = "-D_lib:STRING=${baselib}"

BBCLASSEXTEND = "native"

TARGET_CFLAGS += "-Wno-narrowing"
