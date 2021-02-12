SUMMARY = "Userspace NFS server v3 protocol"
DESCRIPTION = "UNFS3 is a user-space implementation of the NFSv3 server \
specification. It provides a daemon for the MOUNT and NFS protocols, which \
are used by NFS clients for accessing files on the server."
SECTION = "console/network"
LICENSE = "unfs3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9475885294e17c0cc0067820d042792e"

DEPENDS = "flex-native bison-native flex"
DEPENDS += "libtirpc"
DEPENDS_append_class-nativesdk = " flex-nativesdk"

ASNEEDED = ""

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/unfs3/unfs3.git;protocol=https \
           file://unfs3_parallel_build.patch \
           file://alternate_rpc_ports.patch \
           file://fix_pid_race_parent_writes_child_pid.patch \
           file://fix_compile_warning.patch \
           file://rename_fh_cache.patch \
           file://relative_max_socket_path_len.patch \
           file://tcp_no_delay.patch \
           file://0001-daemon.c-Libtirpc-porting-fixes.patch \
           file://0001-attr-fix-utime-for-symlink.patch \
           file://0001-Add-listen-action-for-a-tcp-socket.patch \
           file://no-yywrap.patch \
          "
SRCREV = "c12a5c69a8d59be6916cbd0e0f41c159f1962425"
UPSTREAM_CHECK_GITTAGREGEX = "unfs3\-(?P<pver>.+)"

PV = "0.9.22+${SRCPV}"

BBCLASSEXTEND = "native nativesdk"

inherit autotools
EXTRA_OECONF_append_class-native = " --sbindir=${bindir}"
CFLAGS_append = " -I${STAGING_INCDIR}/tirpc"
LDFLAGS_append = " -ltirpc"

# Turn off these header detects else the inode search
# will walk entire file systems and this is a real problem
# if you have 2 TB of files to walk in your file system
CACHED_CONFIGUREVARS = "ac_cv_header_mntent_h=no ac_cv_header_sys_mnttab_h=no"
