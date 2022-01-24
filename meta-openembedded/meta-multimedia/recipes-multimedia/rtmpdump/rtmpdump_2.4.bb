SUMMARY = "RTMP Dump"
DESCRIPTION = "rtmpdump is a toolkit for RTMP streams. All forms of RTMP are \
supported, including rtmp://, rtmpt://, rtmpe://, rtmpte://, and rtmps://."
HOMEPAGE = "http://rtmpdump.mplayerhq.hu/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "gnutls zlib"

SRCREV = "fa8646daeb19dfd12c181f7d19de708d623704c0"
SRC_URI = " \
    git://git.ffmpeg.org/rtmpdump;branch=master \
    file://fix-racing-build-issue.patch"

S = "${WORKDIR}/git"

inherit autotools-brokensep

EXTRA_OEMAKE = " \
    CC='${CC}' LD='${LD} ${STAGING_LIBDIR}' XCFLAGS='${CFLAGS}' XLDFLAGS='${LDFLAGS}' \
    SYS=posix INC=-I=/usr/include DESTDIR=${D} CRYPTO=GNUTLS \
    prefix=${prefix} libdir=${libdir} incdir=${includedir}/librtmp bindir=${bindir} mandir=${mandir}"
