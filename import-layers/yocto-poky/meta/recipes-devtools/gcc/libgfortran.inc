require gcc-configure-common.inc

EXTRA_OECONF_PATHS = "\
    --with-sysroot=/not/exist \
    --with-build-sysroot=${STAGING_DIR_TARGET} \
"

do_configure () {
	mtarget=`echo ${TARGET_SYS} | sed -e s#-${SDKPKGSUFFIX}##`
	target=`echo ${TARGET_SYS} | sed -e s#-${SDKPKGSUFFIX}##`
	hardlinkdir ${STAGING_INCDIR_NATIVE}/gcc-build-internal-$mtarget ${B}

	echo "Configuring libgfortran"
	rm -rf ${B}/$target/libgfortran/
	mkdir -p ${B}/$target/libgfortran/
	cd ${B}/$target/libgfortran/
	chmod a+x ${S}/libgfortran/configure
	relpath=${@os.path.relpath("${S}/libgfortran", "${B}/$target/libgfortran")}
	$relpath/configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
	# Easiest way to stop bad RPATHs getting into the library since we have a
	# broken libtool here
	sed -i -e 's/hardcode_into_libs=yes/hardcode_into_libs=no/' ${B}/$target/libgfortran/libtool
}

do_compile () {
	target=`echo ${TARGET_SYS} | sed -e s#-${SDKPKGSUFFIX}##`
	cd ${B}/$target/libgfortran/
	oe_runmake MULTIBUILDTOP=${B}/$target/libgfortran/
}

do_install () {
	target=`echo ${TARGET_SYS} | sed -e s#-${SDKPKGSUFFIX}##`
	cd ${B}/$target/libgfortran/
	oe_runmake 'DESTDIR=${D}' MULTIBUILDTOP=${B}/$target/libgfortran/ install
	if [ -d ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/finclude ]; then
		rmdir --ignore-fail-on-non-empty -p ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/finclude
	fi
	if [ -d ${D}${infodir} ]; then
		rmdir --ignore-fail-on-non-empty -p ${D}${infodir}
	fi
	chown -R root:root ${D}
}

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "gcc-runtime"

BBCLASSEXTEND = "nativesdk"

PACKAGES = "\
    ${PN}-dbg \
    libgfortran \
    libgfortran-dev \
    libgfortran-staticdev \
"
FILES_${PN} = "${libdir}/libgfortran.so.*"
FILES_${PN}-dev = "\
    ${libdir}/libgfortran*.so \
    ${libdir}/libgfortran.spec \
    ${libdir}/libgfortran.la \
    ${libdir}/gcc/${TARGET_SYS}/${BINV}/libgfortranbegin.* \
    ${libdir}/gcc/${TARGET_SYS}/${BINV}/libcaf_single* \
"
FILES_${PN}-staticdev = "${libdir}/libgfortran.a"

INSANE_SKIP_${MLPREFIX}libgfortran-dev = "staticdev"

do_package_write_ipk[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_deb[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_rpm[depends] += "virtual/${MLPREFIX}libc:do_packagedata"

python __anonymous () {
    f = d.getVar("FORTRAN", True)
    if "fortran" not in f:
        raise bb.parse.SkipPackage("libgfortran needs fortran support to be enabled in the compiler")
}
