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
           file://musl.patch \
           "
UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/libtirpc/files/libtirpc/"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)/"
SRC_URI[md5sum] = "f5d2a623e9dfbd818d2f3f3a4a878e3a"
SRC_URI[sha256sum] = "2ca529f02292e10c158562295a1ffd95d2ce8af97820e3534fe1b0e3aec7561d"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-gssapi"

do_install_append() {
        chown root:root ${D}${sysconfdir}/netconfig
        install -d ${D}${includedir}/rpc
        install -d ${D}${includedir}/rpcsvc
        for link_header in ${D}${includedir}/tirpc/rpc/*; do
            if [ -f $link_header -a ! -e ${D}/${includedir}/rpc/$(basename $link_header) ]; then
                ln -sf ../tirpc/rpc/$(basename $link_header) ${D}${includedir}/rpc/$(basename $link_header)
            fi
        done
        for link_header in ${D}${includedir}/tirpc/rpcsvc/*; do
            if [ -f $link_header -a ! -e ${D}/${includedir}/rpcsvc/$(basename $link_header) ]; then
                ln -sf ../tirpc/rpc/$(basename $link_header) ${D}${includedir}/rpcsvc/$(basename $link_header)
            fi
        done
        ln -sf  tirpc/netconfig.h ${D}/${includedir}/netconfig.h

}

BBCLASSEXTEND = "native nativesdk"
