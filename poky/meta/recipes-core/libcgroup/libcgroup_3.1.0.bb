SUMMARY = "Linux control group abstraction library"
HOMEPAGE = "http://libcg.sourceforge.net/"
DESCRIPTION = "libcgroup is a library that abstracts the control group file system \
in Linux. Control groups allow you to limit, account and isolate resource usage \
(CPU, memory, disk I/O, etc.) of groups of processes."
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4d794c5d710e5b3547a6cc6a6609a641"

inherit autotools pkgconfig github-releases

DEPENDS = "bison-native flex-native"
DEPENDS:append:libc-musl = " fts"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/${BP}.tar.gz \
           file://0001-include-Makefile-install-systemd.h-by-default.patch \
"
UPSTREAM_CHECK_URI = "https://github.com/libcgroup/libcgroup/tags"

SRC_URI[sha256sum] = "976ec4b1e03c0498308cfd28f1b256b40858f636abc8d1f9db24f0a7ea9e1258"

PACKAGECONFIG = "${@bb.utils.filter('DISTRO_FEATURES', 'pam systemd', d)}"
PACKAGECONFIG[pam] = "--enable-pam-module-dir=${base_libdir}/security --enable-pam=yes,--enable-pam=no,libpam"
PACKAGECONFIG[systemd] = "--enable-systemd,--disable-systemd,systemd"

PACKAGES =+ "cgroups-pam-plugin"
FILES:cgroups-pam-plugin = "${base_libdir}/security/pam_cgroup.so*"
FILES:${PN}-dev += "${base_libdir}/security/*.la"
FILES:${PN}-staticdev += "${base_libdir}/security/pam_cgroup.a"

do_install:append() {
	# Until we ship the test suite, this library isn't useful
	rm -f ${D}${libdir}/libcgroupfortesting.*
}
