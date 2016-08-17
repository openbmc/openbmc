SUMMARY = "Linux control group abstraction library"
DESCRIPTION = "libcgroup is a library that abstracts the control group file system \
in Linux. Control groups allow you to limit, account and isolate resource usage \
(CPU, memory, disk I/O, etc.) of groups of processes."
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

inherit autotools pkgconfig

DEPENDS = "bison-native flex-native ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/libcg/${BPN}/v0.41/${BPN}-${PV}.tar.bz2"
SRC_URI_append_libc-musl = " file://musl-decls-compat.patch"

SRC_URI[md5sum] = "3dea9d50b8a5b73ff0bf1cdcb210f63f"
SRC_URI[sha256sum] = "e4e38bdc7ef70645ce33740ddcca051248d56b53283c0dc6d404e17706f6fb51"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/libcg/files/libcgroup/"

DEPENDS_append_libc-musl = " fts "
EXTRA_OEMAKE_append_libc-musl = "LIBS=-lfts"

EXTRA_OECONF = "${@bb.utils.contains('DISTRO_FEATURES', 'pam', '--enable-pam-module-dir=${base_libdir}/security --enable-pam=yes', '--enable-pam=no', d)}"

PACKAGES =+ "cgroups-pam-plugin"
FILES_cgroups-pam-plugin = "${base_libdir}/security/pam_cgroup.so*"
FILES_${PN}-dev += "${base_libdir}/security/*.la"

do_install_append() {
	# Moving libcgroup to base_libdir
	if [ ! ${D}${libdir} -ef ${D}${base_libdir} ]; then
		mkdir -p ${D}/${base_libdir}/
		mv -f ${D}${libdir}/libcgroup.so.* ${D}${base_libdir}/
		rel_lib_prefix=`echo ${libdir} | sed 's,\(^/\|\)[^/][^/]*,..,g'`
		ln -sf ${rel_lib_prefix}${base_libdir}/libcgroup.so.1 ${D}${libdir}/libcgroup.so
	fi
	# pam modules in ${base_libdir}/security/ should be binary .so files, not symlinks.
	if [ -f ${D}${base_libdir}/security/pam_cgroup.so.0.0.0 ]; then
		mv -f ${D}${base_libdir}/security/pam_cgroup.so.0.0.0 ${D}${base_libdir}/security/pam_cgroup.so
		rm -f ${D}${base_libdir}/security/pam_cgroup.so.*
	fi
}
