SUMMARY = "Tools for monitoring & limiting user disk usage per filesystem"
SECTION = "base"
HOMEPAGE = "http://sourceforge.net/projects/linuxquota/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=18136&atid=118136"
LICENSE = "BSD & GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://quota.c;beginline=1;endline=33;md5=331c7d77744bfe0ad24027f0651028ec \
                    file://rquota_server.c;beginline=1;endline=20;md5=fe7e0d7e11c6f820f8fa62a5af71230f \
                    file://svc_socket.c;beginline=1;endline=17;md5=24d5a8792da45910786eeac750be8ceb"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/linuxquota/quota-tools/${PV}/quota-${PV}.tar.gz \
           file://config-tcpwrappers.patch \
           file://fcntl.patch \
           file://remove_non_posix_types.patch \
	   "
SRC_URI_append_libc-musl = " file://replace_getrpcbynumber_r.patch"

SRC_URI[md5sum] = "a8a5df262261e659716ccad2a5d6df0d"
SRC_URI[sha256sum] = "f4c2f48abf94bbdc396df33d276f2e9d19af58c232cb85eef9c174a747c33795"

S = "${WORKDIR}/quota-tools"

DEPENDS = "gettext-native e2fsprogs"

inherit autotools-brokensep gettext pkgconfig

CFLAGS += "-I${STAGING_INCDIR}/tirpc"
LDFLAGS += "-ltirpc"
ASNEEDED = ""
EXTRA_OEMAKE += 'STRIP=""'

PACKAGECONFIG ??= "tcp-wrappers rpc bsd"
PACKAGECONFIG_libc-musl = "tcp-wrappers rpc"

PACKAGECONFIG[tcp-wrappers] = "--with-tcpwrappers,--without-tcpwrappers,tcp-wrappers"
PACKAGECONFIG[rpc] = "--enable-rpc=yes,--enable-rpc=no,libtirpc"
PACKAGECONFIG[bsd] = "--enable-bsd_behaviour=yes,--enable-bsd_behaviour=no,"

do_install() {
	oe_runmake ROOTDIR=${D} install
}
