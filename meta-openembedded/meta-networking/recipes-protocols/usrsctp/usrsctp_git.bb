DESCRIPTION = "This is a userland SCTP stack supporting FreeBSD, Linux, Mac OS X and Windows."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=ffcf846341f3856d79a483eafa18e2a5"

SRCREV = "dbfc1b8c4cf1a46a4d8987ba542d5ff06bdaf14c"
SRC_URI = "git://github.com/sctplab/usrsctp;protocol=https;branch=master \
           file://0001-Use-foreign-switch-for-automake.patch \
          "

S = "${WORKDIR}/git"

inherit autotools pkgconfig

DEPENDS += "openssl"

CFLAGS  += "-DSCTP_USE_OPENSSL_SHA1 -fPIC"
LDFLAGS += "-lssl -lcrypto"

PACKAGECONFIG ?= "disablewarnings inet inet6"
PACKAGECONFIG[disablewarnings] = "--disable-warnings-as-errors,,"
PACKAGECONFIG[inet] = "--enable-inet,--disable-inet,"
PACKAGECONFIG[inet6] = "--enable-inet6,--disable-inet6,"

EXTRA_OECONF += "--disable-debug"
