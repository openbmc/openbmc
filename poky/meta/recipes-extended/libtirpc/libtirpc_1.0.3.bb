SUMMARY = "Transport-Independent RPC library"
DESCRIPTION = "Libtirpc is a port of Suns Transport-Independent RPC library to Linux"
SECTION = "libs/network"
HOMEPAGE = "http://sourceforge.net/projects/libtirpc/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=183075&atid=903784"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=f835cce8852481e4b2bbbdd23b5e47f3 \
                    file://src/netname.c;beginline=1;endline=27;md5=f8a8cd2cb25ac5aa16767364fb0e3c24"

PROVIDES = "virtual/librpc"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.bz2 \
           file://libtirpc-1.0.4-rc1.patch \
           file://musl.patch \
           "

SRC_URI[md5sum] = "f8403a10695348854e71d525c4db5931"
SRC_URI[sha256sum] = "86c3a78fc1bddefa96111dd233124c703b22a78884203c55c3e06b3be6a0fd5e"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-gssapi"

do_install_append() {
        chown root:root ${D}${sysconfdir}/netconfig
}

BBCLASSEXTEND = "native nativesdk"
