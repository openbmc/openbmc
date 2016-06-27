SUMMARY = "Library for getting/setting POSIX.1e capabilities"
HOMEPAGE = "http://sites.google.com/site/fullycapable/"

# no specific GPL version required
LICENSE = "BSD | GPLv2"
LIC_FILES_CHKSUM = "file://License;md5=3f84fd6f29d453a56514cb7e4ead25f1"

DEPENDS = "hostperl-runtime-native"

SRC_URI = "${KERNELORG_MIRROR}/linux/libs/security/linux-privs/${BPN}2/${BPN}-${PV}.tar.xz"

SRC_URI[md5sum] = "d43ab9f680435a7fff35b4ace8d45b80"
SRC_URI[sha256sum] = "cee4568f78dc851d726fc93f25f4ed91cc223b1fe8259daa4a77158d174e6c65"

inherit lib_package

# do NOT pass target cflags to host compilations
#
do_configure() {
	# libcap uses := for compilers, fortunately, it gives us a hint
	# on what should be replaced with ?=
	sed -e 's,:=,?=,g' -i Make.Rules
	sed -e 's,^BUILD_CFLAGS ?= $(.*CFLAGS),BUILD_CFLAGS := $(BUILD_CFLAGS),' -i Make.Rules

	# disable gperf detection
	sed -e '/shell gperf/cifeq (,yes)' -i libcap/Makefile
}

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'xattr', 'attr', '', d)}"
PACKAGECONFIG_class-native ??= ""

PACKAGECONFIG[attr] = "LIBATTR=yes,LIBATTR=no,attr"
PACKAGECONFIG[pam] = "PAM_CAP=yes,PAM_CAP=no,libpam"

EXTRA_OEMAKE = " \
  INDENT=  \
  lib=${@os.path.basename('${libdir}')} \
  RAISE_SETFCAP=no \
  DYNAMIC=yes \
"

EXTRA_OEMAKE_append_class-target = " SYSTEM_HEADERS=${STAGING_INCDIR}"

# these are present in the libcap defaults, so include in our CFLAGS too
CFLAGS += "-D_LARGEFILE64_SOURCE -D_FILE_OFFSET_BITS=64"

do_compile() {
	oe_runmake ${EXTRA_OECONF}
}

do_install() {
	oe_runmake install \
		${EXTRA_OECONF} \
		DESTDIR="${D}" \
		prefix="${prefix}" \
		SBINDIR="${D}${sbindir}"
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
