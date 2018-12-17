SUMMARY = "Library for getting/setting POSIX.1e capabilities"
HOMEPAGE = "http://sites.google.com/site/fullycapable/"

# no specific GPL version required
LICENSE = "BSD | GPLv2"
LIC_FILES_CHKSUM = "file://License;md5=3f84fd6f29d453a56514cb7e4ead25f1"

DEPENDS = "hostperl-runtime-native gperf-native"

SRC_URI = "${KERNELORG_MIRROR}/linux/libs/security/linux-privs/${BPN}2/${BPN}-${PV}.tar.xz \
           file://0001-ensure-the-XATTR_NAME_CAPS-is-defined-when-it-is-use.patch \
           file://0001-Fix-build-with-gperf-3.1.patch \
           "
SRC_URI[md5sum] = "6666b839e5d46c2ad33fc8aa2ceb5f77"
SRC_URI[sha256sum] = "693c8ac51e983ee678205571ef272439d83afe62dd8e424ea14ad9790bc35162"

inherit lib_package

# do NOT pass target cflags to host compilations
#
do_configure() {
	# libcap uses := for compilers, fortunately, it gives us a hint
	# on what should be replaced with ?=
	sed -e 's,:=,?=,g' -i Make.Rules
	sed -e 's,^BUILD_CFLAGS ?= $(.*CFLAGS),BUILD_CFLAGS := $(BUILD_CFLAGS),' -i Make.Rules
}

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

# these are present in the libcap defaults, so include in our CFLAGS too
CFLAGS += "-D_LARGEFILE64_SOURCE -D_FILE_OFFSET_BITS=64"

do_compile() {
	oe_runmake ${PACKAGECONFIG_CONFARGS}
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
