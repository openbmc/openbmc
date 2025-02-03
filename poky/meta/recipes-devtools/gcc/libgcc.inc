require libgcc-common.inc

DEPENDS = "virtual/cross-cc virtual/cross-c++ virtual/${MLPREFIX}libc"

do_install:append:class-target () {
	if [ "${TCLIBC}" != "glibc" ]; then
		case "${TARGET_OS}" in
			"linux-musl" | "linux-*spe") extra_target_os="linux";;
			"linux-musleabi") extra_target_os="linux-gnueabi";;
			*) extra_target_os="linux";;
		esac
		if [ ! -e ${D}${libdir}/${TARGET_ARCH}${TARGET_VENDOR}-$extra_target_os ]; then
			ln -s ${TARGET_SYS} ${D}${libdir}/${TARGET_ARCH}${TARGET_VENDOR}-$extra_target_os
		fi
	fi
	if [ -n "${@ bb.utils.contains('TUNE_CCARGS_MFLOAT', 'hard', 'hf', '', d)}" ]; then
		case "${TARGET_OS}" in
			"linux-musleabi") extra_target_os="linux-musleabihf";;
			"linux-gnueabi") extra_target_os="linux-gnueabihf";;
		esac
		if [ ! -e ${D}${libdir}/${TARGET_ARCH}${TARGET_VENDOR}-$extra_target_os ]; then
			ln -s ${TARGET_SYS} ${D}${libdir}/${TARGET_ARCH}${TARGET_VENDOR}-$extra_target_os
		fi
	fi
}

PACKAGES = "\
    ${PN} \
    ${PN}-dev \
    ${PN}-dbg \
"

# All libgcc source is marked with the exception.
#
LICENSE:${PN} = "GPL-3.0-with-GCC-exception"
LICENSE:${PN}-dev = "GPL-3.0-with-GCC-exception"
LICENSE:${PN}-dbg = "GPL-3.0-with-GCC-exception"


FILES:${PN}-dev = "\
    ${base_libdir}/libgcc*.so \
    ${@oe.utils.conditional('BASETARGET_SYS', '${TARGET_SYS}', '', '${libdir}/${BASETARGET_SYS}', d)} \
    ${libdir}/${TARGET_SYS}/${BINV}* \
    ${libdir}/${TARGET_ARCH}${TARGET_VENDOR}* \
"

do_package[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_ipk[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_deb[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_rpm[depends] += "virtual/${MLPREFIX}libc:do_packagedata"

INSANE_SKIP:${PN}-dev = "staticdev"

