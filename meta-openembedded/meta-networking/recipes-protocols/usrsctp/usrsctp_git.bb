DESCRIPTION = "This is a userland SCTP stack supporting FreeBSD, Linux, Mac OS X and Windows."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=ffcf846341f3856d79a483eafa18e2a5"

SRCREV = "a10cd498d964508c0e6ec6bd2be9dd4afcbb4d86"
SRC_URI = "git://github.com/sctplab/usrsctp;protocol=https;branch=master \
          "

S = "${WORKDIR}/git"

UPSTREAM_CHECK_COMMITS = "1"

inherit autotools pkgconfig

DEPENDS += "openssl"

CFLAGS  += "-DSCTP_USE_OPENSSL_SHA1 -fPIC"
LDFLAGS += "-lssl -lcrypto"

PACKAGECONFIG ?= "disablewarnings inet inet6"
PACKAGECONFIG[disablewarnings] = "--disable-warnings-as-errors,,"
PACKAGECONFIG[inet] = "--enable-inet,--disable-inet,"
PACKAGECONFIG[inet6] = "--enable-inet6,--disable-inet6,"

EXTRA_OECONF += "--disable-debug"

CVE_VERSION = "0.9.3.0"
