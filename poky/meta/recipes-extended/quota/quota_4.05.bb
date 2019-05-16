SUMMARY = "Tools for monitoring & limiting user disk usage per filesystem"
SECTION = "base"
HOMEPAGE = "http://sourceforge.net/projects/linuxquota/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=18136&atid=118136"
LICENSE = "BSD & GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://rquota_server.c;beginline=1;endline=20;md5=fe7e0d7e11c6f820f8fa62a5af71230f \
                    file://svc_socket.c;beginline=1;endline=17;md5=24d5a8792da45910786eeac750be8ceb"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/linuxquota/quota-tools/${PV}/quota-${PV}.tar.gz \
           file://fcntl.patch \
          "
SRC_URI[md5sum] = "1c1dbd2cd3d680ccac661239b067e147"
SRC_URI[sha256sum] = "ef3b5b5d1014ed1344b46c1826145e20cbef8db967b522403c9a060761cf7ab9"

CVE_PRODUCT = "linux_diskquota"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/linuxquota/files/quota-tools/"
UPSTREAM_CHECK_REGEX = "/quota-tools/(?P<pver>(\d+[\.\-_]*)+)/"

DEPENDS = "gettext-native e2fsprogs libnl dbus"

inherit autotools-brokensep gettext pkgconfig

CFLAGS += "${@bb.utils.contains('PACKAGECONFIG', 'rpc', '-I${STAGING_INCDIR}/tirpc', '', d)}"
LDFLAGS += "${@bb.utils.contains('PACKAGECONFIG', 'rpc', '-ltirpc', '', d)}"
ASNEEDED = ""

PACKAGECONFIG ??= "tcp-wrappers rpc bsd"
PACKAGECONFIG_libc-musl = "tcp-wrappers rpc"

PACKAGECONFIG[tcp-wrappers] = "--enable-libwrap,--disable-libwrap,tcp-wrappers"
PACKAGECONFIG[rpc] = "--enable-rpc,--disable-rpc,libtirpc"
PACKAGECONFIG[bsd] = "--enable-bsd_behaviour=yes,--enable-bsd_behaviour=no,"
PACKAGECONFIG[ldapmail] = "--enable-ldapmail,--disable-ldapmail,openldap"
