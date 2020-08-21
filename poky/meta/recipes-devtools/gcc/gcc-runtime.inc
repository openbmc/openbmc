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
EXTRA_OECONF_append = " --cache-file=${B}/config.cache"
EXTRA_OECONF_append_libc-newlib = " --with-newlib"

# Disable ifuncs for libatomic on arm conflicts -march/-mcpu
EXTRA_OECONF_append_arm = " libat_cv_have_ifunc=no "
EXTRA_OECONF_append_armeb = " libat_cv_have_ifunc=no "

DISABLE_STATIC_class-nativesdk ?= ""

# Newlib does not support symbol versioning on libsdtcc++
SYMVERS_CONF_libc-newlib = ""

# Building with thumb enabled on armv6t fails
ARM_INSTRUCTION_SET_armv6 = "arm"

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
# Only build libstdc++ for newlib
RUNTIMETARGET_libc-newlib = "libstdc++-v3"

# libiberty
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
	if [ -d ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/include ]; then
		install -d ${D}${libdir}/${TARGET_SYS}/${BINV}/include 
		mv ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/include/* ${D}${libdir}/${TARGET_SYS}/${BINV}/include
		rmdir --ignore-fail-on-non-empty -p ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/include
	fi
	rm -rf ${D}${infodir}/libgomp.info ${D}${infodir}/dir
	rm -rf ${D}${infodir}/libitm.info ${D}${infodir}/dir
	rm -rf ${D}${infodir}/libquadmath.info ${D}${infodir}/dir
	if [ -d ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/finclude ]; then
		rmdir --ignore-fail-on-non-empty -p ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/finclude
	fi
	if [ -d ${D}${infodir} ]; then
		rmdir --ignore-fail-on-non-empty -p ${D}${infodir}
	fi
}

do_install_append_class-target () {
	if [ "${TARGET_OS}" = "linux-gnuspe" ]; then
		ln -s ${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-linux
	fi

	if [ "${TARGET_OS}" = "linux-gnun32" ]; then
		if [ "${TARGET_VENDOR_MULTILIB_ORIGINAL}" != "" -a "${TARGET_VENDOR}" != "${TARGET_VENDOR_MULTILIB_ORIGINAL}" ]; then
			mkdir ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR_MULTILIB_ORIGINAL}-linux
			ln -s ../${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR_MULTILIB_ORIGINAL}-linux/32
		elif [ "${MULTILIB_VARIANTS}" != "" ]; then
			mkdir ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-linux
			ln -s ../${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-linux/32
		else
			ln -s ${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-linux
		fi
	elif [ "${TARGET_OS}" = "linux-gnux32" ]; then
		if [ "${TARGET_VENDOR_MULTILIB_ORIGINAL}" != "" -a "${TARGET_VENDOR}" != "${TARGET_VENDOR_MULTILIB_ORIGINAL}" ]; then
			mkdir ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR_MULTILIB_ORIGINAL}-linux
			ln -s ../${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR_MULTILIB_ORIGINAL}-linux/x32
		elif [ "${MULTILIB_VARIANTS}" != "" ]; then
			mkdir ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-linux
			ln -s ../${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-linux/32
		else
			ln -s ${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-linux
		fi
	elif [ "${TARGET_VENDOR_MULTILIB_ORIGINAL}" != "" -a "${TARGET_VENDOR}" != "${TARGET_VENDOR_MULTILIB_ORIGINAL}" ]; then
		mkdir ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR_MULTILIB_ORIGINAL}-${TARGET_OS}
		ln -s ../${TARGET_SYS}/bits ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR_MULTILIB_ORIGINAL}-${TARGET_OS}/bits
		ln -s ../${TARGET_SYS}/ext ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR_MULTILIB_ORIGINAL}-${TARGET_OS}/ext
	fi

	if [ "${TARGET_ARCH}" == "x86_64" -a "${MULTILIB_VARIANTS}" != "" ];then
		ln -sf ../${X86ARCH32}${TARGET_VENDOR}-${TARGET_OS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-${TARGET_OS}/32
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
DEPENDS = "virtual/${TARGET_PREFIX}gcc virtual/${TARGET_PREFIX}g++ libgcc virtual/${MLPREFIX}libc"
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
# The base package doesn't exist, so we clear the recommends.
RRECOMMENDS_${PN}-dbg = ""

# include python debugging scripts
FILES_${PN}-dbg += "\
    ${libdir}/libstdc++.*-gdb.py \
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
    ${libdir}/${TARGET_SYS}/${BINV}/include/ssp \
"
SUMMARY_libssp-dev = "GNU stack smashing protection library - development files"
FILES_libssp-staticdev = "${libdir}/libssp*.a"
SUMMARY_libssp-staticdev = "GNU stack smashing protection library - static development files"

FILES_libquadmath = "${libdir}/libquadmath*.so.*"
SUMMARY_libquadmath = "GNU quad-precision math library"
FILES_libquadmath-dev = "\
    ${libdir}/${TARGET_SYS}/${BINV}/include/quadmath* \
    ${libdir}/libquadmath*.so \
    ${libdir}/libquadmath.la \
"
SUMMARY_libquadmath-dev = "GNU quad-precision math library - development files"
FILES_libquadmath-staticdev = "${libdir}/libquadmath.a"
SUMMARY_libquadmath-staticdev = "GNU quad-precision math library - static development files"

FILES_libgomp = "${libdir}/libgomp*${SOLIBS}"
SUMMARY_libgomp = "GNU OpenMP parallel programming library"
FILES_libgomp-dev = "\
    ${libdir}/libgomp*${SOLIBSDEV} \
    ${libdir}/libgomp*.la \
    ${libdir}/libgomp.spec \
    ${libdir}/${TARGET_SYS}/${BINV}/include/acc_prof.h \
    ${libdir}/${TARGET_SYS}/${BINV}/include/omp.h \
    ${libdir}/${TARGET_SYS}/${BINV}/include/openacc.h \
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

require gcc-testsuite.inc

EXTRA_OEMAKE_prepend_task-check = "${PARALLEL_MAKE} "

MAKE_CHECK_TARGETS ??= "check-gcc ${@" ".join("check-target-" + i for i in d.getVar("RUNTIMETARGET").split())}"
# prettyprinters and xmethods require gdb tooling
MAKE_CHECK_IGNORE ??= "prettyprinters.exp xmethods.exp"
MAKE_CHECK_RUNTESTFLAGS ??= "${MAKE_CHECK_BOARDARGS} --ignore '${MAKE_CHECK_IGNORE}'"

# specific host and target dependencies required for test suite running
do_check[depends] += "dejagnu-native:do_populate_sysroot expect-native:do_populate_sysroot"
do_check[depends] += "virtual/libc:do_populate_sysroot"
# only depend on qemu if targeting linux user execution
do_check[depends] += "${@'qemu-native:do_populate_sysroot' if "user" in d.getVar('TOOLCHAIN_TEST_TARGET') else ''}"
# extend the recipe sysroot to include the built libraries (for qemu usermode)
do_check[prefuncs] += "extend_recipe_sysroot"
do_check[prefuncs] += "check_prepare"
do_check[dirs] = "${WORKDIR}/dejagnu ${B}"
do_check[nostamp] = "1"
do_check() {
    export DEJAGNU="${WORKDIR}/dejagnu/site.exp"

    # HACK: this works around the configure setting CXX with -nostd* args
    sed -i 's/-nostdinc++ -nostdlib++//g' $(find ${B} -name testsuite_flags | head -1)

    if [ "${TOOLCHAIN_TEST_TARGET}" = "user" ]; then
        # qemu user has issues allocating large amounts of memory
        export G_SLICE=always-malloc
        # no test should need more that 10G of memory, this prevents tests like pthread7-rope from leaking memory
        ulimit -m 4194304
        ulimit -v 10485760
    fi

    oe_runmake -i ${MAKE_CHECK_TARGETS} RUNTESTFLAGS="${MAKE_CHECK_RUNTESTFLAGS}"
}
addtask check after do_compile do_populate_sysroot

