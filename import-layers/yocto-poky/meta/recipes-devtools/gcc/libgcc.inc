require libgcc-common.inc

DEPENDS = "virtual/${TARGET_PREFIX}gcc virtual/${TARGET_PREFIX}g++"

do_install_append_class-target () {
	if [ "${TCLIBC}" != "glibc" ]; then
		case "${TARGET_OS}" in
			"linux-musl" | "linux-uclibc" | "linux-*spe") extra_target_os="linux";;
			"linux-musleabi" | "linux-uclibceabi") extra_target_os="linux-gnueabi";;
			*) extra_target_os="linux";;
		esac
		ln -s ${TARGET_SYS} ${D}${libdir}/${TARGET_ARCH}${TARGET_VENDOR}-$extra_target_os
	fi
}

PACKAGES = "\
    ${PN} \
    ${PN}-dev \
    ${PN}-dbg \
"

# All libgcc source is marked with the exception.
#
LICENSE_${PN} = "GPL-3.0-with-GCC-exception"
LICENSE_${PN}-dev = "GPL-3.0-with-GCC-exception"
LICENSE_${PN}-dbg = "GPL-3.0-with-GCC-exception"


FILES_${PN}-dev = "\
    ${base_libdir}/libgcc*.so \
    ${@base_conditional('BASETARGET_SYS', '${TARGET_SYS}', '', '${libdir}/${BASETARGET_SYS}', d)} \
    ${libdir}/${TARGET_SYS}/${BINV}* \
    ${libdir}/${TARGET_ARCH}${TARGET_VENDOR}* \
"

LIBGCCBUILDTREENAME = "gcc-build-internal-"

do_package[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_ipk[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_deb[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_rpm[depends] += "virtual/${MLPREFIX}libc:do_packagedata"

INSANE_SKIP_${PN}-dev = "staticdev"

