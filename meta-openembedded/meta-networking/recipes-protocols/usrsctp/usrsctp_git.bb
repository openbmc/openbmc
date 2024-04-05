DESCRIPTION = "This is a userland SCTP stack supporting FreeBSD, Linux, Mac OS X and Windows."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=ffcf846341f3856d79a483eafa18e2a5"

SRCREV = "848eca82f92273af9a79687a90343a2ebcf3481d"
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

CVE_VERSION = "0.9.5.0"
