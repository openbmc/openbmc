SUMMARY = "Transport-Independent RPC library"
DESCRIPTION = "Libtirpc is a port of Suns Transport-Independent RPC library to Linux"
SECTION = "libs/network"
HOMEPAGE = "http://sourceforge.net/projects/libtirpc/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=183075&atid=903784"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=f835cce8852481e4b2bbbdd23b5e47f3 \
                    file://src/netname.c;beginline=1;endline=27;md5=f8a8cd2cb25ac5aa16767364fb0e3c24"

PROVIDES = "virtual/librpc"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.bz2"
UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/libtirpc/files/libtirpc/"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)/"
SRC_URI[sha256sum] = "6474e98851d9f6f33871957ddee9714fdcd9d8a5ee9abb5a98d63ea2e60e12f3"

# Was fixed in 1.3.3rc1 so not present in 1.3.3
CVE_CHECK_IGNORE += "CVE-2021-46828"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-gssapi"

do_install:append() {
	chown root:root ${D}${sysconfdir}/netconfig
}

BBCLASSEXTEND = "native nativesdk"
