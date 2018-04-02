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
           file://export_key_secretkey_is_set.patch \
           file://0001-replace-__bzero-with-memset-API.patch \
           file://0001-include-stdint.h-for-uintptr_t.patch \
           "

SRC_URI_append_libc-musl = " \
                             file://Use-netbsd-queue.h.patch \
                           "

SRC_URI[md5sum] = "d5a37f1dccec484f9cabe2b97e54e9a6"
SRC_URI[sha256sum] = "723c5ce92706cbb601a8db09110df1b4b69391643158f20ff587e20e7c5f90f5"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-gssapi"

do_install_append() {
        chown root:root ${D}${sysconfdir}/netconfig
}

BBCLASSEXTEND = "native nativesdk"
