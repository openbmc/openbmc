SUMMARY = "Library for getting/setting POSIX.1e capabilities"
DESCRIPTION = "A library providing the API to access POSIX capabilities. \
These allow giving various kinds of specific privileges to individual \
users, without giving them full root permissions."
HOMEPAGE = "http://sites.google.com/site/fullycapable/"

# The library is BSD | GPLv2, the PAM module is BSD | LGPLv2+
LICENSE = "(BSD-3-Clause | GPL-2.0-only) & (BSD-3-Clause | LGPL-2.0-or-later)"
LIC_FILES_CHKSUM = "file://License;md5=2965a646645b72ecee859b43c592dcaa \
                    file://pam_cap/License;md5=905326f41d3d1f8df21943f9a4ed6b50 \
                    "

DEPENDS = "hostperl-runtime-native gperf-native"

SRC_URI = "${KERNELORG_MIRROR}/linux/libs/security/linux-privs/${BPN}2/${BPN}-${PV}.tar.xz"
SRC_URI:append:class-nativesdk = " \
           file://0001-nativesdk-libcap-Raise-the-size-of-arrays-containing.patch \
           "
SRC_URI[sha256sum] = "897bc18b44afc26c70e78cead3dbb31e154acc24bee085a5a09079a88dbf6f52"

UPSTREAM_CHECK_URI = "https://www.kernel.org/pub/linux/libs/security/linux-privs/${BPN}2/"

inherit lib_package

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}"
PACKAGECONFIG[pam] = "PAM_CAP=yes,PAM_CAP=no,libpam"

EXTRA_OEMAKE = " \
  ${PACKAGECONFIG_CONFARGS} \
  INDENT=  \
  lib='${baselib}' \
  RAISE_SETFCAP=no \
  DYNAMIC=yes \
  USE_GPERF=yes \
"

do_compile() {
	oe_runmake \
		AR="${AR}" \
		BUILD_CC="${BUILD_CC}" \
		BUILD_LDFLAGS="${BUILD_LDFLAGS}" \
		CC="${CC}" \
		RANLIB="${RANLIB}" \
		OBJCOPY="${OBJCOPY}"
}

do_install() {
	oe_runmake install \
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

# pam files
FILES:${PN} += "${base_libdir}/security/*.so"

# The license of the main package depends on whether PAM is enabled or not
LICENSE:${PN} = "(BSD-3-Clause | GPL-2.0-only)${@bb.utils.contains('PACKAGECONFIG', 'pam', ' & (BSD-3-Clause | LGPL-2.0-or-later)', '', d)}"
LICENSE:${PN}-dev = "(BSD-3-Clause | GPL-2.0-only)"

BBCLASSEXTEND = "native nativesdk"
