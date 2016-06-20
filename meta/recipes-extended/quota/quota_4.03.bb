SUMMARY = "Tools for monitoring & limiting user disk usage per filesystem"
SECTION = "base"
HOMEPAGE = "http://sourceforge.net/projects/linuxquota/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=18136&atid=118136"
LICENSE = "BSD & GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://quota.c;beginline=1;endline=33;md5=331c7d77744bfe0ad24027f0651028ec \
                    file://rquota_server.c;beginline=1;endline=20;md5=fe7e0d7e11c6f820f8fa62a5af71230f \
                    file://svc_socket.c;beginline=1;endline=17;md5=24d5a8792da45910786eeac750be8ceb"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/linuxquota/quota-tools/${PV}/quota-${PV}.tar.gz \
           file://0001-Fix-build-with-disabled-ldap.patch \
           file://0001-Do-not-accidentaly-override-commandline-passed-CFLAG.patch \
           file://fcntl.patch \
           file://remove_non_posix_types.patch \
          "
SRC_URI_append_libc-musl = " file://replace_getrpcbynumber_r.patch"

SRC_URI[md5sum] = "6b09f9c93515c25a528be5754cdfb6f5"
SRC_URI[sha256sum] = "9c6c4d9ae7bf30506dd2aa3d8056c4ff2f8d087930d7c721616f5c093bdc674b"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/linuxquota/files/quota-tools/"
UPSTREAM_CHECK_REGEX = "/quota-tools/(?P<pver>(\d+[\.\-_]*)+)/"

DEPENDS = "gettext-native e2fsprogs libnl dbus"

inherit autotools-brokensep gettext pkgconfig

CFLAGS += "-I${STAGING_INCDIR}/tirpc"
LDFLAGS += "-ltirpc"
ASNEEDED = ""

PACKAGECONFIG ??= "tcp-wrappers rpc bsd"
PACKAGECONFIG_libc-musl = "tcp-wrappers rpc"

PACKAGECONFIG[tcp-wrappers] = "--enable-libwrap,--disable-libwrap,tcp-wrappers"
PACKAGECONFIG[rpc] = "--enable-rpc,--disable-rpc,libtirpc"
PACKAGECONFIG[bsd] = "--enable-bsd_behaviour=yes,--enable-bsd_behaviour=no,"
PACKAGECONFIG[ldapmail] = "--enable-ldapmail,--disable-ldapmail,openldap"
