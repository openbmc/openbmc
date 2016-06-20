SUMMARY = "Transport-Independent RPC library"
DESCRIPTION = "Libtirpc is a port of Suns Transport-Independent RPC library to Linux"
SECTION = "libs/network"
HOMEPAGE = "http://sourceforge.net/projects/libtirpc/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=183075&atid=903784"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=f835cce8852481e4b2bbbdd23b5e47f3 \
                    file://src/netname.c;beginline=1;endline=27;md5=f8a8cd2cb25ac5aa16767364fb0e3c24"

PROVIDES = "virtual/librpc"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.bz2;name=libtirpc \
           ${GENTOO_MIRROR}/${BPN}-glibc-nfs.tar.xz;name=glibc-nfs \
           file://libtirpc-0.2.1-fortify.patch \
           file://0001-Add-missing-rwlock_unlocks-in-xprt_register.patch \
          "

SRC_URI_append_libc-uclibc = " file://remove-des-functionality.patch \
                             "

SRC_URI_append_libc-musl = " file://remove-des-functionality.patch \
                             file://Use-netbsd-queue.h.patch \
                           "

SRC_URI[libtirpc.md5sum] = "36ce1c0ff80863bb0839d54aa0b94014"
SRC_URI[libtirpc.sha256sum] = "5156974f31be7ccbc8ab1de37c4739af6d9d42c87b1d5caf4835dda75fcbb89e"
SRC_URI[glibc-nfs.md5sum] = "5ae500b9d0b6b72cb875bc04944b9445"
SRC_URI[glibc-nfs.sha256sum] = "2677cfedf626f3f5a8f6e507aed5bb8f79a7453b589d684dbbc086e755170d83"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-gssapi"

do_configure_prepend () {
        cp -r ${S}/../tirpc ${S}
}

do_install_append() {
        chown root:root ${D}${sysconfdir}/netconfig
}
