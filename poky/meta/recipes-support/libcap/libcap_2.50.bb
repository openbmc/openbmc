SUMMARY = "Library for getting/setting POSIX.1e capabilities"
DESCRIPTION = "A library providing the API to access POSIX capabilities. \
These allow giving various kinds of specific privileges to individual \
users, without giving them full root permissions."
HOMEPAGE = "http://sites.google.com/site/fullycapable/"
# no specific GPL version required
LICENSE = "BSD | GPLv2"
LIC_FILES_CHKSUM = "file://License;md5=e2370ba375efe9e1a095c26d37e483b8"

DEPENDS = "hostperl-runtime-native gperf-native"

SRC_URI = "${KERNELORG_MIRROR}/linux/libs/security/linux-privs/${BPN}2/${BPN}-${PV}.tar.xz \
           file://0001-ensure-the-XATTR_NAME_CAPS-is-defined-when-it-is-use.patch \
           file://0002-tests-do-not-run-target-executables.patch \
           file://0001-tests-do-not-statically-link-a-test.patch \
           "
SRC_URI[sha256sum] = "47a57b8bd238b84c93c921a9b4ff82337551dbcb0cca071316aadf3e23b19261"

UPSTREAM_CHECK_URI = "https://www.kernel.org/pub/linux/libs/security/linux-privs/${BPN}2/"

inherit lib_package

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}"
PACKAGECONFIG_class-native ??= ""

PACKAGECONFIG[pam] = "PAM_CAP=yes,PAM_CAP=no,libpam"

EXTRA_OEMAKE = " \
  INDENT=  \
  lib='${baselib}' \
  RAISE_SETFCAP=no \
  DYNAMIC=yes \
  BUILD_GPERF=yes \
"

EXTRA_OEMAKE_append_class-target = " SYSTEM_HEADERS=${STAGING_INCDIR}"

do_compile() {
	unset CFLAGS BUILD_CFLAGS
	oe_runmake \
		${PACKAGECONFIG_CONFARGS} \
		AR="${AR}" \
		CC="${CC}" \
		RANLIB="${RANLIB}" \
		COPTS="${CFLAGS}" \
		BUILD_COPTS="${BUILD_CFLAGS}"
}

do_install() {
	oe_runmake install \
		${PACKAGECONFIG_CONFARGS} \
		DESTDIR="${D}" \
		prefix="${prefix}" \
		SBINDIR="${sbindir}"
}

do_install_append() {
	# Move the library to base_libdir
	install -d ${D}${base_libdir}
	if [ ! ${D}${libdir} -ef ${D}${base_libdir} ]; then
		mv ${D}${libdir}/libcap* ${D}${base_libdir}
                if [ -d ${D}${libdir}/security ]; then
			mv ${D}${libdir}/security ${D}${base_libdir}
		fi
	fi
}

FILES_${PN}-dev += "${base_libdir}/*.so"

# pam files
FILES_${PN} += "${base_libdir}/security/*.so"

BBCLASSEXTEND = "native nativesdk"
