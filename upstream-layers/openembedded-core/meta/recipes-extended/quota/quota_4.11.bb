SUMMARY = "Tools for monitoring & limiting user disk usage per filesystem"
SECTION = "base"
HOMEPAGE = "http://sourceforge.net/projects/linuxquota/"
DESCRIPTION = "Tools and patches for the Linux Diskquota system as part of the Linux kernel"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=18136&atid=118136"
LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://rquota_server.c;beginline=1;endline=20;md5=fe7e0d7e11c6f820f8fa62a5af71230f \
                    file://svc_socket.c;beginline=1;endline=17;md5=24d5a8792da45910786eeac750be8ceb"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/linuxquota/quota-tools/${PV}/quota-${PV}.tar.gz"
SRC_URI[sha256sum] = "0a51b8f920254d8e83c34a4c3082b7d241f5d6fd65188afadf29859d5223ef78"

CVE_PRODUCT = "linux_diskquota"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/linuxquota/files/quota-tools/"
UPSTREAM_CHECK_REGEX = "/quota-tools/(?P<pver>(\d+[\.\-_]*)+)/"

DEPENDS = "gettext-native e2fsprogs libnl dbus"

inherit autotools-brokensep gettext pkgconfig

CFLAGS += "${@bb.utils.contains('PACKAGECONFIG', 'rpc', '-I${STAGING_INCDIR}/tirpc', '', d)}"
LDFLAGS += "${@bb.utils.contains('PACKAGECONFIG', 'rpc', '-ltirpc', '', d)}"
ASNEEDED = ""

PACKAGECONFIG ??= "rpc bsd"
PACKAGECONFIG:libc-musl = "rpc"

PACKAGECONFIG[rpc] = "--enable-rpc,--disable-rpc,libtirpc"
PACKAGECONFIG[bsd] = "--enable-bsd_behaviour=yes,--enable-bsd_behaviour=no,"
PACKAGECONFIG[ldapmail] = "--enable-ldapmail,--disable-ldapmail,openldap"
