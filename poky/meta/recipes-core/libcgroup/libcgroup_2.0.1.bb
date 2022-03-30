SUMMARY = "Linux control group abstraction library"
HOMEPAGE = "http://libcg.sourceforge.net/"
DESCRIPTION = "libcgroup is a library that abstracts the control group file system \
in Linux. Control groups allow you to limit, account and isolate resource usage \
(CPU, memory, disk I/O, etc.) of groups of processes."
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

inherit autotools pkgconfig

DEPENDS = "bison-native flex-native"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/v${PV}/${BP}.tar.gz"

SRC_URI[sha256sum] = "2dd9c566a90a053e7a5f47607e225648ba7aa9bb7763514dd4778e2ed530fe90"
UPSTREAM_CHECK_URI = "https://github.com/libcgroup/libcgroup/releases/"

DEPENDS:append:libc-musl = " fts "
EXTRA_OEMAKE:append:libc-musl = " LIBS=-lfts"

PACKAGECONFIG = "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}"
PACKAGECONFIG[pam] = "--enable-pam-module-dir=${base_libdir}/security --enable-pam=yes,--enable-pam=no,libpam"

PACKAGES =+ "cgroups-pam-plugin"
FILES:cgroups-pam-plugin = "${base_libdir}/security/pam_cgroup.so*"
FILES:${PN}-dev += "${base_libdir}/security/*.la"
FILES:${PN}-staticdev += "${base_libdir}/security/pam_cgroup.a"

do_install:append() {
	# Until we ship the test suite, this library isn't useful
	rm -f ${D}${libdir}/libcgroupfortesting.*
}
