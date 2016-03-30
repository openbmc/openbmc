require libgcc-common.inc

DEPENDS = "virtual/${TARGET_PREFIX}gcc virtual/${TARGET_PREFIX}g++"

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
"

FILES_${PN}-dbg += "${base_libdir}/.debug/"

LIBGCCBUILDTREENAME = "gcc-build-internal-"

do_package[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_ipk[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_deb[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_rpm[depends] += "virtual/${MLPREFIX}libc:do_packagedata"

INSANE_SKIP_${PN}-dev = "staticdev"

