SUMMARY = "Linux control group abstraction library"
HOMEPAGE = "http://libcg.sourceforge.net/"
DESCRIPTION = "libcgroup is a library that abstracts the control group file system \
in Linux. Control groups allow you to limit, account and isolate resource usage \
(CPU, memory, disk I/O, etc.) of groups of processes."
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

inherit autotools pkgconfig

DEPENDS = "bison-native flex-native"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/v${PV}/${BP}.tar.bz2 \
           file://musl-decls-compat.patch \
           file://module.patch"

SRC_URI[sha256sum] = "11a2fbf0e42f46089f406b8b0dca7fef04aec2f21600b70e402c5db3661305d7"
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
