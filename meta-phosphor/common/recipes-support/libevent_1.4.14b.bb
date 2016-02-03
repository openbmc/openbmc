DESCRIPTION = "an asynchronous event notification library"
HOMEPAGE = "http://www.monkey.org/~provos/libevent/"
SECTION = "libs"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://event.c;endline=26;md5=bc20aa63bf60c36c2d8edb77746f6b7c"

PR = "r0"

SRC_URI = "http://www.monkey.org/~provos/${BPN}-${PV}-stable.tar.gz"
S = "${WORKDIR}/${BPN}-${PV}-stable"

inherit autotools

LEAD_SONAME = "libevent-1.4.so"
