SUMMARY = "Library for getting/setting POSIX.1e capabilities"
DESCRIPTION = "A library providing the API to access POSIX capabilities. \
These allow giving various kinds of specific privileges to individual \
users, without giving them full root permissions."
HOMEPAGE = "http://sites.google.com/site/fullycapable/"
# no specific GPL version required
LICENSE = "BSD-3-Clause | GPLv2"
LIC_FILES_CHKSUM = "file://License;md5=e2370ba375efe9e1a095c26d37e483b8"

DEPENDS = "hostperl-runtime-native gperf-native"

SRC_URI = "${KERNELORG_MIRROR}/linux/libs/security/linux-privs/${BPN}2/${BPN}-${PV}.tar.xz \
           file://0001-ensure-the-XATTR_NAME_CAPS-is-defined-when-it-is-use.patch \
           file://0002-tests-do-not-run-target-executables.patch \
           "
SRC_URI:append:class-nativesdk = " \
           file://0001-nativesdk-libcap-Raise-the-size-of-arrays-containing.patch \
           "
SRC_URI[sha256sum] = "190c5baac9bee06a129eae20d3e827de62f664fe3507f0bf6c50a9a59fbd83a2"

UPSTREAM_CHECK_URI = "https://www.kernel.org/pub/linux/libs/security/linux-privs/${BPN}2/"

inherit lib_package

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}"
PACKAGECONFIG:class-native ??= ""

PACKAGECONFIG[pam] = "PAM_CAP=yes,PAM_CAP=no,libpam"

EXTRA_OEMAKE = " \
  INDENT=  \
  lib='${baselib}' \
  RAISE_SETFCAP=no \
  DYNAMIC=yes \
  USE_GPERF=yes \
"

EXTRA_OEMAKE:append:class-target = " SYSTEM_HEADERS=${STAGING_INCDIR}"

do_compile() {
	unset CFLAGS BUILD_CFLAGS
	oe_runmake \
		${PACKAGECONFIG_CONFARGS} \
		AR="${AR}" \
		CC="${CC}" \
		RANLIB="${RANLIB}" \
                OBJCOPY="${OBJCOPY}" \
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

do_install:append() {
	# Move the library to base_libdir
	install -d ${D}${base_libdir}
	if [ ! ${D}${libdir} -ef ${D}${base_libdir} ]; then
		mv ${D}${libdir}/libcap* ${D}${base_libdir}
                if [ -d ${D}${libdir}/security ]; then
			mv ${D}${libdir}/security ${D}${base_libdir}
		fi
	fi
}

FILES:${PN}-dev += "${base_libdir}/*.so"

# pam files
FILES:${PN} += "${base_libdir}/security/*.so"

BBCLASSEXTEND = "native nativesdk"
