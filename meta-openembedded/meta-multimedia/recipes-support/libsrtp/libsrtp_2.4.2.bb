SUMMARY = "Library implementing Secure RTP (RFC 3711)"
DESCRIPTION = "This package provides an implementation of the Secure Real-time Transport Protocol (SRTP), \
               the Universal Security Transform (UST), and a supporting cryptographic kernel. The SRTP API \
               is documented in include/srtp.h, and the library is in libsrtp2.a (after compilation)."
HOMEPAGE = "https://github.com/cisco/libsrtp"
BUGTRACKER = "https://github.com/cisco/libsrtp/issues"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2909fcf6f09ffff8430463d91c08c4e1"

SRC_URI = "git://github.com/cisco/libsrtp.git;branch=2_4_x_throttle;protocol=https"
SRCREV = "90d05bf8980d16e4ac3f16c19b77e296c4bc207b"

S = "${WORKDIR}/git"

PACKAGECONFIG ?= ""
PACKAGECONFIG[nss] = "-Dcrypto-library=nss,, nss,,, openssl"
PACKAGECONFIG[openssl] = "-Dcrypto-library=openssl,, openssl,,, nss"

inherit meson pkgconfig
