SUMMARY = "Transport-Independent RPC library"
DESCRIPTION = "Libtirpc is a port of Suns Transport-Independent RPC library to Linux"
SECTION = "libs/network"
HOMEPAGE = "http://sourceforge.net/projects/libtirpc/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=183075&atid=903784"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=f835cce8852481e4b2bbbdd23b5e47f3 \
                    file://src/netname.c;beginline=1;endline=27;md5=f8a8cd2cb25ac5aa16767364fb0e3c24"

PROVIDES = "virtual/librpc"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.bz2 \
	   file://CVE-2021-46828.patch \
	  "
UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/libtirpc/files/libtirpc/"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)/"
SRC_URI[sha256sum] = "e24eb88b8ce7db3b7ca6eb80115dd1284abc5ec32a8deccfed2224fc2532b9fd"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-gssapi"

do_install:append() {
	test -e ${D}${sysconfdir}/netconfig && chown root:root ${D}${sysconfdir}/netconfig
}

BBCLASSEXTEND = "native nativesdk"
