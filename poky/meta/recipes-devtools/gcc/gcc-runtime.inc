require gcc-configure-common.inc

SUMMARY = "Runtime libraries from GCC"

# Over-ride the LICENSE set by gcc-${PV}.inc to remove "& GPLv3"
# All gcc-runtime packages are now covered by the runtime exception.
LICENSE = "GPL-3.0-with-GCC-exception"

CXXFLAGS_remove = "-fvisibility-inlines-hidden"

EXTRA_OECONF_PATHS = "\
    --with-gxx-include-dir=${includedir}/c++/${BINV} \
    --with-sysroot=/not/exist \
    --with-build-sysroot=${STAGING_DIR_TARGET} \
"

EXTRA_OECONF_append_linuxstdbase = " --enable-clocale=gnu"

RUNTIMELIBITM = "libitm"
RUNTIMELIBITM_arc = ""
RUNTIMELIBITM_mipsarch = ""
RUNTIMELIBITM_nios2 = ""
RUNTIMELIBITM_microblaze = ""
RUNTIMELIBITM_riscv32 = ""
RUNTIMELIBITM_riscv64 = ""
RUNTIMELIBSSP ?= ""
RUNTIMELIBSSP_mingw32 ?= "libssp"

RUNTIMETARGET = "${RUNTIMELIBSSP} libstdc++-v3 libgomp libatomic ${RUNTIMELIBITM} \
    ${@bb.utils.contains_any('FORTRAN', [',fortran',',f77'], 'libquadmath', '', d)} \
"
RUNTIMETARGET_append_x86 = " libmpx"
RUNTIMETARGET_append_x86-64 = " libmpx"
RUNTIMETARGET_remove_libc-musl = "libmpx"

# libiberty
# libmudflap
# libgfortran needs separate recipe due to libquadmath dependency

SLIB = "${TMPDIR}/work-shared/gcc-${PV}-${PR}/gcc-${PV}"
SLIB_NEW = "/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}"

DEBUG_PREFIX_MAP_class-target = " \
   -fdebug-prefix-map=${WORKDIR}/recipe-sysroot= \
   -fdebug-prefix-map=${WORKDIR}/recipe-sysroot-native= \
   -fdebug-prefix-map=${SLIB}=${SLIB_NEW} \
   -fdebug-prefix-map=${SLIB}/include=${SLIB_NEW}/libstdc++-v3/../include \
   -fdebug-prefix-map=${SLIB}/libiberty=${SLIB_NEW}/libstdc++-v3/../libiberty \
   -fdebug-prefix-map=${B}=${SLIB_NEW} \
   "

do_configure () {
	export CXX="${CXX} -nostdinc++ -nostdlib++"
	for d in libgcc ${RUNTIMETARGET}; do
		echo "Configuring $d"
		rm -rf ${B}/${TARGET_SYS}/$d/
		mkdir -p ${B}/${TARGET_SYS}/$d/
		cd ${B}/${TARGET_SYS}/$d/
		chmod a+x ${S}/$d/configure
		relpath=${@os.path.relpath("${S}/$d", "${B}/${TARGET_SYS}/$d")}
		$relpath/configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
		if [ "$d" = "libgcc" ]; then
			(cd ${B}/${TARGET_SYS}/libgcc; oe_runmake enable-execute-stack.c unwind.h md-unwind-support.h sfp-machine.h gthr-default.h)
		fi
	done
}
EXTRACONFFUNCS += "extract_stashed_builddir"
do_configure[depends] += "${COMPILERDEP}"

do_compile () {
	for d in libgcc ${RUNTIMETARGET}; do
		cd ${B}/${TARGET_SYS}/$d/
		oe_runmake MULTIBUILDTOP=${B}/${TARGET_SYS}/$d/
	done
}

do_install () {
	for d in ${RUNTIMETARGET}; do
		cd ${B}/${TARGET_SYS}/$d/
		oe_runmake 'DESTDIR=${D}' MULTIBUILDTOP=${B}/${TARGET_SYS}/$d/ install
	done
	rm -rf ${D}${infodir}/libgomp.info ${D}${infodir}/dir
	rm -rf ${D}${infodir}/libitm.info ${D}${infodir}/dir
	rm -rf ${D}${infodir}/libquadmath.info ${D}${infodir}/dir
	if [ -d ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/finclude ]; then
		rmdir --ignore-fail-on-non-empty -p ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/finclude
	fi
	if [ -d ${D}${infodir} ]; then
		rmdir --ignore-fail-on-non-empty -p ${D}${infodir}
	fi
	if [ "${TARGET_VENDOR_MULTILIB_ORIGINAL}" != "" -a "${TARGET_VENDOR}" != "${TARGET_VENDOR_MULTILIB_ORIGINAL}" ]; then
		ln -s ${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR_MULTILIB_ORIGINAL}-${TARGET_OS}
	fi

}

do_install_append_class-target () {
	if [ "${TARGET_OS}" = "linux-gnuspe" ]; then
		ln -s ${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-linux
	fi

	if [ "${TARGET_OS}" = "linux-gnun32" ]; then
		if [ "${MULTILIBS}" != "" ]; then
			mkdir ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}-pokymllib64-linux
			ln -s ../${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}-pokymllib64-linux/32
		else
			ln -s ${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-linux
		fi
	fi
	if [ "${TARGET_OS}" = "linux-gnux32" ]; then
		if [ "${MULTILIBS}" != "" ]; then
			mkdir ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}-poky-linux
			ln -s ../${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}-poky-linux/x32
		else
			ln -s ${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-linux
		fi
	fi

	if [ "${TCLIBC}" != "glibc" ]; then
		case "${TARGET_OS}" in
			"linux-musl" | "linux-*spe") extra_target_os="linux";;
			"linux-musleabi") extra_target_os="linux-gnueabi";;
			*) extra_target_os="linux";;
		esac
		ln -s ${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-$extra_target_os
	fi
	chown -R root:root ${D}
}

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "virtual/${TARGET_PREFIX}gcc virtual/${TARGET_PREFIX}g++ libgcc"
PROVIDES = "virtual/${TARGET_PREFIX}compilerlibs"

BBCLASSEXTEND = "nativesdk"

PACKAGES = "\
    ${PN}-dbg \
    libstdc++ \
    libstdc++-precompile-dev \
    libstdc++-dev \
    libstdc++-staticdev \
    libg2c \
    libg2c-dev \
    libssp \
    libssp-dev \
    libssp-staticdev \
    libmudflap \
    libmudflap-dev \
    libmudflap-staticdev \
    libquadmath \
    libquadmath-dev \
    libquadmath-staticdev \
    libgomp \
    libgomp-dev \
    libgomp-staticdev \
    libatomic \
    libatomic-dev \
    libatomic-staticdev \
    libitm \
    libitm-dev \
    libitm-staticdev \
"
PACKAGES_append_x86 = "\
    libmpx \
    libmpx-dev \
    libmpx-staticdev \
"

PACKAGES_append_x86-64 = "\
    libmpx \
    libmpx-dev \
    libmpx-staticdev \
"

# The base package doesn't exist, so we clear the recommends.
RRECOMMENDS_${PN}-dbg = ""

# include python debugging scripts
FILES_${PN}-dbg += "\
    ${libdir}/libstdc++.so.*-gdb.py \
    ${datadir}/gcc-${BINV}/python/libstdcxx \
"

FILES_libg2c = "${target_libdir}/libg2c.so.*"
SUMMARY_libg2c = "Companion runtime library for g77"
FILES_libg2c-dev = "\
    ${libdir}/libg2c.so \
    ${libdir}/libg2c.a \
    ${libdir}/libfrtbegin.a \
"
SUMMARY_libg2c-dev = "Companion runtime library for g77 - development files"

FILES_libstdc++ = "${libdir}/libstdc++.so.*"
SUMMARY_libstdc++ = "GNU standard C++ library"
FILES_libstdc++-dev = "\
    ${includedir}/c++/ \
    ${libdir}/libstdc++.so \
    ${libdir}/libstdc++*.la \
    ${libdir}/libsupc++.la \
"
SUMMARY_libstdc++-dev = "GNU standard C++ library - development files"
FILES_libstdc++-staticdev = "\
    ${libdir}/libstdc++*.a \
    ${libdir}/libsupc++.a \
"
SUMMARY_libstdc++-staticdev = "GNU standard C++ library - static development files"

FILES_libstdc++-precompile-dev = "${includedir}/c++/${TARGET_SYS}/bits/*.gch"
SUMMARY_libstdc++-precompile-dev = "GNU standard C++ library - precompiled header files"

FILES_libssp = "${libdir}/libssp.so.*"
SUMMARY_libssp = "GNU stack smashing protection library"
FILES_libssp-dev = "\
    ${libdir}/libssp*.so \
    ${libdir}/libssp*_nonshared.a \
    ${libdir}/libssp*.la \
    ${libdir}/gcc/${TARGET_SYS}/${BINV}/include/ssp \
"
SUMMARY_libssp-dev = "GNU stack smashing protection library - development files"
FILES_libssp-staticdev = "${libdir}/libssp*.a"
SUMMARY_libssp-staticdev = "GNU stack smashing protection library - static development files"

FILES_libquadmath = "${libdir}/libquadmath*.so.*"
SUMMARY_libquadmath = "GNU quad-precision math library"
FILES_libquadmath-dev = "\
    ${libdir}/gcc/${TARGET_SYS}/${BINV}/include/quadmath* \
    ${libdir}/libquadmath*.so \
    ${libdir}/libquadmath.la \
"
SUMMARY_libquadmath-dev = "GNU quad-precision math library - development files"
FILES_libquadmath-staticdev = "${libdir}/libquadmath.a"
SUMMARY_libquadmath-staticdev = "GNU quad-precision math library - static development files"

# NOTE: mudflap has been removed as of gcc 4.9 and has been superseded by the address sanitiser
FILES_libmudflap = "${libdir}/libmudflap*.so.*"
SUMMARY_libmudflap = "Pointer debugging library for gcc"
FILES_libmudflap-dev = "\
    ${libdir}/libmudflap*.so \
    ${libdir}/libmudflap.la \
"
SUMMARY_libmudflap-dev = "Pointer debugging library for gcc - development files"
FILES_libmudflap-staticdev = "${libdir}/libmudflap.a"
SUMMARY_libmudflap-staticdev = "Pointer debugging library for gcc - static development files"

FILES_libgomp = "${libdir}/libgomp*${SOLIBS}"
SUMMARY_libgomp = "GNU OpenMP parallel programming library"
FILES_libgomp-dev = "\
    ${libdir}/libgomp*${SOLIBSDEV} \
    ${libdir}/libgomp*.la \
    ${libdir}/libgomp.spec \
    ${libdir}/gcc/${TARGET_SYS}/${BINV}/include/omp.h \
"
SUMMARY_libgomp-dev = "GNU OpenMP parallel programming library - development files"
FILES_libgomp-staticdev = "${libdir}/libgomp*.a"
SUMMARY_libgomp-staticdev = "GNU OpenMP parallel programming library - static development files"

FILES_libatomic = "${libdir}/libatomic.so.*"
SUMMARY_libatomic = "GNU C++11 atomics support library"
FILES_libatomic-dev = "\
    ${libdir}/libatomic.so \
    ${libdir}/libatomic.la \
"
SUMMARY_libatomic-dev = "GNU C++11 atomics support library - development files"
FILES_libatomic-staticdev = "${libdir}/libatomic.a"
SUMMARY_libatomic-staticdev = "GNU C++11 atomics support library - static development files"

FILES_libitm = "${libdir}/libitm.so.*"
SUMMARY_libitm = "GNU transactional memory support library"
FILES_libitm-dev = "\
    ${libdir}/libitm.so \
    ${libdir}/libitm.la \
    ${libdir}/libitm.spec \
"
SUMMARY_libitm-dev = "GNU transactional memory support library - development files"
FILES_libitm-staticdev = "${libdir}/libitm.a"
SUMMARY_libitm-staticdev = "GNU transactional memory support library - static development files"


FILES_libmpx = "${libdir}/libmpx.so.* ${libdir}/libmpxwrappers.so.*"
SUMMARY_libmpx = "Intel Memory Protection Extension library"
FILES_libmpx-dev = "\
    ${libdir}/libmpxwrappers.so \
    ${libdir}/libmpxwrappers.la \
    ${libdir}/libmpx.so \
    ${libdir}/libmpx.la \
    ${libdir}/libmpx.spec \
"
SUMMARY_libmpx-dev = "Intel Memory Protection Extension library - development files"
FILES_libmpx-staticdev = "${libdir}/libmpx.a ${libdir}/libmpxwrappers.a"
SUMMARY_libmpx-staticdev = "Intel Memory Protection Extension library - static development files"

do_package_write_ipk[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_deb[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_rpm[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
