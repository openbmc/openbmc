SUMMARY = "RTMP Dump"
DESCRIPTION = "rtmpdump is a toolkit for RTMP streams. All forms of RTMP are \
supported, including rtmp://, rtmpt://, rtmpe://, rtmpte://, and rtmps://."
HOMEPAGE = "http://rtmpdump.mplayerhq.hu/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "gnutls zlib"

PV .= "+git"
SRCREV = "6f6bb1353fc84f4cc37138baa99f586750028a01"
SRC_URI = " \
    git://git.ffmpeg.org/rtmpdump;branch=master \
"

S = "${WORKDIR}/git"

CVE_STATUS_GROUPS += "CVES_2015"
CVES_2015 = "CVE-2015-8270 CVE-2015-8271 CVE-2015-8272"
CVES_2015[status] = "fixed-version: patched in current git hash"

inherit autotools-brokensep

EXTRA_OEMAKE = " \
    CC='${CC}' LD='${LD} ${STAGING_LIBDIR}' XCFLAGS='${CFLAGS}' XLDFLAGS='${LDFLAGS}' \
    SYS=posix INC=-I=/usr/include DESTDIR=${D} CRYPTO=GNUTLS \
    prefix=${prefix} libdir=${libdir} incdir=${includedir}/librtmp bindir=${bindir} mandir=${mandir}"
