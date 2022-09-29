inherit cross-canadian

require license.inc

PN = "external-arm-sdk-toolchain-${TARGET_ARCH}"
BPN = "external-arm-sdk-toolchain"
PV = "${EAT_VER_MAIN}"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_DEFAULT_DEPS = "1"
EXCLUDE_FROM_SHLIBS = "1"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Skip packaging QA checks for prebuilt binaries
INSANE_SKIP:gcc-cross-canadian-${TRANSLATED_TARGET_ARCH} = "dev-so staticdev file-rdeps libdir"
INSANE_SKIP:gdb-cross-canadian-${TRANSLATED_TARGET_ARCH} = "dev-so file-rdeps"
INSANE_SKIP:binutils-cross-canadian-${TRANSLATED_TARGET_ARCH} = "dev-so file-rdeps"

# Skip file dependencies in RPM for prebuilt binaries
SKIP_FILEDEPS = "1"

PROVIDES = "\
	gcc-cross-canadian-${TRANSLATED_TARGET_ARCH} \
	gdb-cross-canadian-${TRANSLATED_TARGET_ARCH} \
	binutils-cross-canadian-${TRANSLATED_TARGET_ARCH} \
"

PACKAGES = "\
	gcc-cross-canadian-${TRANSLATED_TARGET_ARCH} \
	gdb-cross-canadian-${TRANSLATED_TARGET_ARCH} \
	binutils-cross-canadian-${TRANSLATED_TARGET_ARCH} \
"

# Adjust defaults in line with external toolchain
bindir = "${exec_prefix}/bin"
libdir = "${exec_prefix}/lib"
libexecdir = "${exec_prefix}/libexec"
datadir = "${exec_prefix}/share"
gcclibdir = "${libdir}/gcc"

FILES:gcc-cross-canadian-${TRANSLATED_TARGET_ARCH} = "\
	${prefix}/${EAT_TARGET_SYS}/lib/libstdc++.* \
	${prefix}/${EAT_TARGET_SYS}/lib/libgcc_s.* \
	${prefix}/${EAT_TARGET_SYS}/lib/libsupc++.* \
	${prefix}/${EAT_TARGET_SYS}/include \
	${gcclibdir}/${EAT_TARGET_SYS}/${EAT_VER_GCC}/* \
	${bindir}/${TARGET_PREFIX}gcov \
	${bindir}/${TARGET_PREFIX}gcc* \
	${bindir}/${TARGET_PREFIX}g++ \
	${bindir}/${TARGET_PREFIX}cpp \
	${libexecdir}/* \
"

FILES:gdb-cross-canadian-${TRANSLATED_TARGET_ARCH} = "\
	${bindir}/${TARGET_PREFIX}gdb* \
	${datadir}/gdb/* \
"

FILES:binutils-cross-canadian-${TRANSLATED_TARGET_ARCH} = "\
	${prefix}/${EAT_TARGET_SYS}/bin/ld* \
	${prefix}/${EAT_TARGET_SYS}/bin/objcopy \
	${prefix}/${EAT_TARGET_SYS}/bin/strip \
	${prefix}/${EAT_TARGET_SYS}/bin/nm \
	${prefix}/${EAT_TARGET_SYS}/bin/ranlib \
	${prefix}/${EAT_TARGET_SYS}/bin/as \
	${prefix}/${EAT_TARGET_SYS}/bin/ar \
	${prefix}/${EAT_TARGET_SYS}/bin/objdump \
	${prefix}/${EAT_TARGET_SYS}/lib/ldscripts/* \
	${bindir}/${TARGET_PREFIX}ld* \
	${bindir}/${TARGET_PREFIX}addr2line \
	${bindir}/${TARGET_PREFIX}objcopy \
	${bindir}/${TARGET_PREFIX}readelf \
	${bindir}/${TARGET_PREFIX}strip \
	${bindir}/${TARGET_PREFIX}nm \
	${bindir}/${TARGET_PREFIX}ranlib \
	${bindir}/${TARGET_PREFIX}gprof \
	${bindir}/${TARGET_PREFIX}as \
	${bindir}/${TARGET_PREFIX}c++filt \
	${bindir}/${TARGET_PREFIX}ar \
	${bindir}/${TARGET_PREFIX}strings \
	${bindir}/${TARGET_PREFIX}objdump \
	${bindir}/${TARGET_PREFIX}size \
"

DESCRIPTION:gcc-cross-canadian-${TRANSLATED_TARGET_ARCH} = "The GNU cc and gcc C compilers"
DESCRIPTION:gdb-cross-canadian-${TRANSLATED_TARGET_ARCH} = "gdb - GNU debugger"
DESCRIPTION:binutils-cross-canadian-${TRANSLATED_TARGET_ARCH} = "A GNU collection of binary utilities"

LICENSE:gcc-cross-canadian-${TRANSLATED_TARGET_ARCH} = "${EAT_GCC_LICENSE}"
LICENSE:gdb-cross-canadian-${TRANSLATED_TARGET_ARCH} = "${EAT_GDB_LICENSE}"
LICENSE:binutils-cross-canadian-${TRANSLATED_TARGET_ARCH} = "${EAT_BFD_LICENSE}"

PKGV:gcc-cross-canadian-${TRANSLATED_TARGET_ARCH} = "${EAT_VER_GCC}"
PKGV:gdb-cross-canadian-${TRANSLATED_TARGET_ARCH} = "${EAT_VER_GDB}"
PKGV:binutils-cross-canadian-${TRANSLATED_TARGET_ARCH} = "${EAT_VER_BFD}"

do_install() {
	install -d ${D}${prefix}/${EAT_TARGET_SYS}/bin
	install -d ${D}${prefix}/${EAT_TARGET_SYS}/lib
	install -d ${D}${prefix}/${EAT_TARGET_SYS}/include
	install -d ${D}${bindir}
	install -d ${D}${libdir}
	install -d ${D}${prefix}/${EAT_TARGET_SYS}/lib/ldscripts
	install -d ${D}${libexecdir}
	install -d ${D}${datadir}/gdb
	install -d ${D}${gcclibdir}/${EAT_TARGET_SYS}/${EAT_VER_GCC}/include

	CP_ARGS="-Prf --preserve=mode,timestamps --no-preserve=ownership"

	# gcc
	for i in libstdc++.* libgcc_s.* libsupc++.*; do
		cp ${CP_ARGS} ${EXTERNAL_TOOLCHAIN}/${EAT_TARGET_SYS}/${EAT_LIBDIR}/$i ${D}${prefix}/${EAT_TARGET_SYS}/lib
	done
	cp ${CP_ARGS} ${EXTERNAL_TOOLCHAIN}/${EAT_TARGET_SYS}/include/* ${D}${prefix}/${EAT_TARGET_SYS}/include
	cp ${CP_ARGS} ${EXTERNAL_TOOLCHAIN}/lib/gcc/${EAT_TARGET_SYS}/${EAT_VER_GCC}/* ${D}${gcclibdir}/${EAT_TARGET_SYS}/${EAT_VER_GCC}
	for i in gcov gcc* g++ cpp; do
		cp ${CP_ARGS} ${EXTERNAL_TOOLCHAIN}/bin/${TARGET_PREFIX}$i ${D}${bindir}
	done
	cp ${CP_ARGS} ${EXTERNAL_TOOLCHAIN}/libexec/* ${D}${libexecdir}

	# gdb
	cp ${CP_ARGS} ${EXTERNAL_TOOLCHAIN}/bin/${TARGET_PREFIX}gdb* ${D}${bindir}
	cp ${CP_ARGS} ${EXTERNAL_TOOLCHAIN}/share/gdb/* ${D}${datadir}/gdb/

	# binutils
	for i in ld* objcopy strip nm ranlib as ar objdump; do
		cp ${CP_ARGS} ${EXTERNAL_TOOLCHAIN}/${EAT_TARGET_SYS}/bin/$i ${D}${prefix}/${EAT_TARGET_SYS}/bin
	done
	cp ${CP_ARGS} ${EXTERNAL_TOOLCHAIN}/${EAT_TARGET_SYS}/lib/ldscripts/* ${D}${prefix}/${EAT_TARGET_SYS}/lib/ldscripts
	for i in ld* addr2line objcopy readelf strip nm ranlib gprof as c++filt ar strings objdump size; do
		cp ${CP_ARGS} ${EXTERNAL_TOOLCHAIN}/bin/${TARGET_PREFIX}$i ${D}${bindir}
	done
}

python () {
    if not d.getVar("EAT_VER_MAIN", False):
        raise bb.parse.SkipPackage("External ARM toolchain not configured (EAT_VER_MAIN not set).")
    if d.getVar('TCLIBC', True) != "glibc":
        raise bb.parse.SkipPackage("incompatible with %s" % d.getVar('TCLIBC', True))
}
