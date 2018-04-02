SUMMARY = "Userspace NFS server v3 protocol"
SECTION = "console/network"
LICENSE = "unfs3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9475885294e17c0cc0067820d042792e"

# SVN support for upstream version check isn't implemented yet
RECIPE_UPSTREAM_VERSION = "0.9.22.r497"
RECIPE_UPSTREAM_DATE = "Oct 08, 2015"
CHECK_DATE = "Dec 10, 2015"

DEPENDS = "flex-native bison-native flex"
DEPENDS += "libtirpc"
DEPENDS_append_class-nativesdk = " flex-nativesdk"

ASNEEDED = ""

MOD_PV = "497"
S = "${WORKDIR}/trunk"
# Only subversion url left in OE-Core, use a mirror tarball instead since
# this rarely changes.
# svn://svn.code.sf.net/p/unfs3/code;module=trunk;rev=${MOD_PV};protocol=http
# rename the tarball in mirror to avoid clash with user local svn tarball
SRC_URI = "http://downloads.yoctoproject.org/mirror/sources/unfs3-0.9.22.r497.tar.gz \
           file://unfs3_parallel_build.patch \
           file://alternate_rpc_ports.patch \
           file://fix_pid_race_parent_writes_child_pid.patch \
           file://fix_compile_warning.patch \
           file://rename_fh_cache.patch \
           file://relative_max_socket_path_len.patch \
           file://tcp_no_delay.patch \
           file://0001-daemon.c-Libtirpc-porting-fixes.patch \
           "
SRC_URI[md5sum] = "2e43e471c77ade0331901c40b8f8e9a3"
SRC_URI[sha256sum] = "21009468a9ba07b72ea93780d025a63ab4e55bf8fc3127803c296f0900fe1bac"

BBCLASSEXTEND = "native nativesdk"

inherit autotools
EXTRA_OECONF_append_class-native = " --sbindir=${bindir}"
CFLAGS_append = " -I${STAGING_INCDIR}/tirpc"
LDFLAGS_append = " -ltirpc"

# Turn off these header detects else the inode search
# will walk entire file systems and this is a real problem
# if you have 2 TB of files to walk in your file system
CACHED_CONFIGUREVARS = "ac_cv_header_mntent_h=no ac_cv_header_sys_mnttab_h=no"
