require gcc-configure-common.inc

SUMMARY = "Runtime libraries from GCC"

# Over-ride the LICENSE set by gcc-${PV}.inc to remove "& GPLv3"
# All gcc-runtime packages are now covered by the runtime exception.
LICENSE = "GPL-3.0-with-GCC-exception"

CXXFLAGS:remove = "-fvisibility-inlines-hidden"

EXTRA_OECONF_PATHS = "\
    --with-gxx-include-dir=${includedir}/c++/${BINV} \
    --with-sysroot=/not/exist \
    --with-build-sysroot=${STAGING_DIR_TARGET} \
"

EXTRA_OECONF:append:linuxstdbase = " --enable-clocale=gnu"
EXTRA_OECONF:append = " --cache-file=${B}/config.cache"
EXTRA_OECONF:append:libc-newlib = " --with-newlib --with-target-subdir"
EXTRA_OECONF:append:libc-picolibc = " --with-newlib --with-target-subdir"
EXTRA_OECONF:append:libc-baremetal = " --with-target-subdir"

# Disable ifuncs for libatomic on arm conflicts -march/-mcpu
EXTRA_OECONF:append:arm = " libat_cv_have_ifunc=no "
EXTRA_OECONF:append:armeb = " libat_cv_have_ifunc=no "

DISABLE_STATIC:class-nativesdk ?= ""

# Newlib does not support symbol versioning on libsdtcc++
SYMVERS_CONF:libc-newlib = ""
SYMVERS_CONF:libc-picolibc = ""

# Building with thumb enabled on armv6t fails
ARM_INSTRUCTION_SET:armv6 = "arm"

RUNTIMELIBITM = "libitm"
RUNTIMELIBITM:arc = ""
RUNTIMELIBITM:mipsarch = ""
RUNTIMELIBITM:nios2 = ""
RUNTIMELIBITM:microblaze = ""
RUNTIMELIBITM:riscv32 = ""
RUNTIMELIBITM:riscv64 = ""
RUNTIMELIBITM:loongarch64 = ""
RUNTIMELIBSSP ?= ""
RUNTIMELIBSSP:mingw32 ?= "libssp"

RUNTIMETARGET = "${RUNTIMELIBSSP} libstdc++-v3 libgomp libatomic ${RUNTIMELIBITM} \
    ${@bb.utils.contains('FORTRAN', ',fortran', 'libquadmath', '', d)} \
"
# Only build libstdc++ for newlib
RUNTIMETARGET:libc-newlib = "libstdc++-v3"
RUNTIMETARGET:libc-picolibc = "libstdc++-v3"

# libiberty
# libgfortran needs separate recipe due to libquadmath dependency

do_configure () {
	export CXX="${CXX} -nostdinc++ -L${WORKDIR}/dummylib"
	# libstdc++ isn't built yet so CXX would error not able to find it which breaks stdc++'s configure
	# tests. Create a dummy empty lib for the purposes of configure.
	mkdir -p ${WORKDIR}/dummylib
	${CC} -x c /dev/null -c -o ${WORKDIR}/dummylib/dummylib.o
	${AR} rcs ${WORKDIR}/dummylib/libstdc++.a ${WORKDIR}/dummylib/dummylib.o
	for d in libgcc ${RUNTIMETARGET}; do
		echo "Configuring $d"
		rm -rf ${B}/${TARGET_SYS}/$d/
		mkdir -p ${B}/${TARGET_SYS}/$d/
		cd ${B}/${TARGET_SYS}/$d/
		chmod a+x ${S}/$d/configure
		${S}/$d/configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
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
	install -d ${D}${datadir}/gdb/auto-load/${libdir}
	mv ${D}${libdir}/libstdc++*-gdb.py ${D}${datadir}/gdb/auto-load/${libdir}
	if [ -d ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/include ]; then
		install -d ${D}${libdir}/${TARGET_SYS}/${BINV}/include 
		mv ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/include/* ${D}${libdir}/${TARGET_SYS}/${BINV}/include
		rmdir --ignore-fail-on-non-empty -p ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/include
	fi
	rm -rf ${D}${infodir}/libgomp.info* ${D}${infodir}/dir
	rm -rf ${D}${infodir}/libitm.info ${D}${infodir}/dir
	rm -rf ${D}${infodir}/libquadmath.info ${D}${infodir}/dir
	if [ -d ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/finclude ]; then
		rmdir --ignore-fail-on-non-empty -p ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/finclude
	fi
	if [ -d ${D}${infodir} ]; then
		rmdir --ignore-fail-on-non-empty -p ${D}${infodir}
	fi
	if [ -d ${D}${libdir} ]; then
		rmdir --ignore-fail-on-non-empty -p ${D}${libdir}
	fi
}

do_install:append:class-target () {
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

	if [ "${TARGET_ARCH}" = "x86_64" -a "${MULTILIB_VARIANTS}" != "" ];then
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
DEPENDS = "virtual/cross-cc virtual/cross-c++ ${MLPREFIX}libgcc virtual/${MLPREFIX}libc"
DEPENDS:class-nativesdk = "virtual/nativesdk-cross-cc virtual/nativesdk-cross-c++ ${MLPREFIX}libgcc virtual/${MLPREFIX}libc"
PROVIDES = "virtual/${MLPREFIX}compilerlibs"

BBCLASSEXTEND = "nativesdk"

PACKAGES = "\
    ${PN}-dbg \
    libstdc++ \
    libstdc++-precompile-dev \
    libstdc++-dev \
    libstdc++-staticdev \
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
RRECOMMENDS:${PN}-dbg = ""

# include python debugging scripts
FILES:${PN}-dbg += "\
    ${datadir}/gcc-${BINV}/python/libstdcxx \
    ${datadir}/gdb/auto-load \
"
# Needed by libstdcxx pretty printer, however it is disabled intentionally
# as it adds build time dependency on bash and some cases e.g. no GPL3 cases
# bash is not availbale and builds fails
# So it needs to be added manually to images sadly.
# RDEPENDS:${PN}-dbg += "python3-datetime"

FILES:libstdc++ = "${libdir}/libstdc++.so.*"
SUMMARY:libstdc++ = "GNU standard C++ library"
FILES:libstdc++-dev = "\
    ${includedir}/c++/ \
    ${libdir}/libstdc++.so \
    ${libdir}/libstdc++*.la \
    ${libdir}/libsupc++.la \
"
SUMMARY:libstdc++-dev = "GNU standard C++ library - development files"
FILES:libstdc++-staticdev = "\
    ${libdir}/libstdc++*.a \
    ${libdir}/libsupc++.a \
"
SUMMARY:libstdc++-staticdev = "GNU standard C++ library - static development files"

FILES:libstdc++-precompile-dev = "${includedir}/c++/${TARGET_SYS}/bits/*.gch"
SUMMARY:libstdc++-precompile-dev = "GNU standard C++ library - precompiled header files"

FILES:libssp = "${libdir}/libssp.so.*"
SUMMARY:libssp = "GNU stack smashing protection library"
FILES:libssp-dev = "\
    ${libdir}/libssp*.so \
    ${libdir}/libssp*_nonshared.a \
    ${libdir}/libssp*.la \
    ${libdir}/${TARGET_SYS}/${BINV}/include/ssp \
"
SUMMARY:libssp-dev = "GNU stack smashing protection library - development files"
FILES:libssp-staticdev = "${libdir}/libssp*.a"
SUMMARY:libssp-staticdev = "GNU stack smashing protection library - static development files"

FILES:libquadmath = "${libdir}/libquadmath*.so.*"
SUMMARY:libquadmath = "GNU quad-precision math library"
FILES:libquadmath-dev = "\
    ${libdir}/${TARGET_SYS}/${BINV}/include/quadmath* \
    ${libdir}/libquadmath*.so \
    ${libdir}/libquadmath.la \
"
SUMMARY:libquadmath-dev = "GNU quad-precision math library - development files"
FILES:libquadmath-staticdev = "${libdir}/libquadmath.a"
SUMMARY:libquadmath-staticdev = "GNU quad-precision math library - static development files"

FILES:libgomp = "${libdir}/libgomp*${SOLIBS}"
SUMMARY:libgomp = "GNU OpenMP parallel programming library"
FILES:libgomp-dev = "\
    ${libdir}/libgomp*${SOLIBSDEV} \
    ${libdir}/libgomp*.la \
    ${libdir}/libgomp.spec \
    ${libdir}/${TARGET_SYS}/${BINV}/include/acc_prof.h \
    ${libdir}/${TARGET_SYS}/${BINV}/include/omp.h \
    ${libdir}/${TARGET_SYS}/${BINV}/include/openacc.h \
"
SUMMARY:libgomp-dev = "GNU OpenMP parallel programming library - development files"
FILES:libgomp-staticdev = "${libdir}/libgomp*.a"
SUMMARY:libgomp-staticdev = "GNU OpenMP parallel programming library - static development files"

FILES:libatomic = "${libdir}/libatomic.so.*"
SUMMARY:libatomic = "GNU C++11 atomics support library"
FILES:libatomic-dev = "\
    ${libdir}/libatomic.so \
    ${libdir}/libatomic.la \
"
SUMMARY:libatomic-dev = "GNU C++11 atomics support library - development files"
FILES:libatomic-staticdev = "${libdir}/libatomic.a"
SUMMARY:libatomic-staticdev = "GNU C++11 atomics support library - static development files"

FILES:libitm = "${libdir}/libitm.so.*"
SUMMARY:libitm = "GNU transactional memory support library"
FILES:libitm-dev = "\
    ${libdir}/libitm.so \
    ${libdir}/libitm.la \
    ${libdir}/libitm.spec \
"
SUMMARY:libitm-dev = "GNU transactional memory support library - development files"
FILES:libitm-staticdev = "${libdir}/libitm.a"
SUMMARY:libitm-staticdev = "GNU transactional memory support library - static development files"

require gcc-testsuite.inc

EXTRA_OEMAKE:prepend:task-check = "${PARALLEL_MAKE} "

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
do_check[network] = "1"
do_check() {
    export DEJAGNU="${WORKDIR}/dejagnu/site.exp"

    # HACK: this works around the configure setting CXX with -nostd* args
    sed -i 's#-nostdinc++ -L${WORKDIR}/dummylib##g' $(find ${B} -name testsuite_flags | head -1)

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

