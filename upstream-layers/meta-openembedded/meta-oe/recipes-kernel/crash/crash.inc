SUMMARY = "Kernel analysis utility for live systems and core dumpfiles"
DESCRIPTION = "The core analysis suite is a self-contained tool that can be used to \
investigate either live systems, kernel core dumps created from kdump, or mcore dumpfiles."
HOMEPAGE = "https://crash-utility.github.io/"
SECTION = "devel"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://${UNPACKDIR}/${BP}/COPYING3;md5=d32239bcb673463ab874e80d47fae504 \
                    file://${UNPACKDIR}/gdb/gdb-16.2/COPYING3;md5=d32239bcb673463ab874e80d47fae504 \
"

DEPENDS = "zlib readline ncurses gmp mpfr"
# Use append for cross-canadian on native packages
DEPENDS:append = " coreutils-native bison-native flex-native"

SRC_URI = "git://github.com/crash-utility/${BPN}.git;branch=master;protocol=https;destsuffix=${BP} \
           ${GNU_MIRROR}/gdb/gdb-16.2.tar.gz;name=gdb;subdir=gdb \
           file://7003cross_ranlib.patch \
           file://gdb_build_jobs_and_not_write_crash_target.patch \
           file://0001-symbol-fix-S-cannot-work-with-kaslr-detection.patch \
           file://0001-Use-CC-env-var-to-get-compiler-version.patch \
           file://0001-Makefile-support-cross-compiling-for-32-bit-arm.patch \
          "
# 0001-Use-CC-env-var-to-get-compiler-version.patch:
# to get the right compiler info into ./crash --buildinfo

SRCREV = "61fe107ff96a22e7df0029877529b7ce6da36850"
#PV = "9.0.2"
SRC_URI[gdb.sha256sum] = "bdc1da4a033280ac752e7d34b0418efaa45bed093235cb88e62ea961752a37f8"


UPSTREAM_CHECK_URI = "https://github.com/crash-utility/crash/releases"

inherit gettext

TARGET_CC_ARCH:append = " ${SELECTED_OPTIMIZATION}"

# Target machine constraints preserved from your original layout
COMPATIBLE_HOST:riscv64 = "null"
COMPATIBLE_HOST:riscv32 = "null"
COMPATIBLE_HOST:mipsarchn64 = "null"
COMPATIBLE_HOST:mipsarchn32 = "null"
COMPATIBLE_HOST:libc-musl = 'null'

# add gdb autoconf overrides
EXTRA_OEMAKE = ' \
    RPMPKG="${PV}" \
    CROSS_COMPILE="${TARGET_PREFIX}" \
    HOSTCC="gcc" \
    CFLAGS="${CFLAGS} -fcommon --sysroot=${STAGING_DIR_TARGET}" \
    CXXFLAGS="${CXXFLAGS} -fcommon --sysroot=${STAGING_DIR_TARGET}" \
    LDFLAGS="${LDFLAGS} --sysroot=${STAGING_DIR_TARGET}" \
    AR="${AR}" \
    RANLIB="${RANLIB}" \
    GDB_TARGET="${TARGET_SYS}" \
    GDB_HOST="${BUILD_SYS}" \
    GDB_MAKE_JOBS="${PARALLEL_MAKE}" \
    GDB_CONF_FLAGS="--host=${HOST_SYS} \
                    --target=${TARGET_SYS} \
                    --disable-gdbserver \
                    --disable-gprofng \
                    --with-sysroot=/ \
                    ac_cv_type_gregset_t=yes \
                    ac_cv_type_fpregset_t=yes \
                    ac_cv_header_sys_procfs_h=yes" \
'

TARGET_CFLAGS:append = " -fcommon -fpermissive"
TARGET_CXXFLAGS:append = " -fcommon -fpermissive"

do_configure() {
    :
}

do_compile:prepend() {
    # 1. Map Yocto architecture strings to the strict naming convention expected by crash
    case ${TARGET_ARCH} in
        aarch64*)    ARCH_UPPER=ARM64 ;;
        arm*)        ARCH_UPPER=ARM ;;
        i*86*)       ARCH_UPPER=X86 ;;
        x86_64*)     ARCH_UPPER=X86_64 ;;
        powerpc64*)  ARCH_UPPER=PPC64 ;;
        powerpc*)    ARCH_UPPER=PPC ;;
        mips*)       ARCH_UPPER=MIPS ;;
        *)           ARCH_UPPER=$(echo "${TARGET_ARCH}" | tr '[:lower:]' '[:upper:]') ;;
    esac

    bbnote "Staging pre-unpacked GDB 16.2 source tree into active build workspace layout..."
    rm -rf ${B}/gdb-16.2

    # 2. Copy the official clean GNU tarball source footprint into the active workspace mapping
    cp -rf ${UNPACKDIR}/gdb/gdb-16.2 ${B}/gdb-16.2

    bbnote "Writing dynamic target layout architecture context: ${ARCH_UPPER}"
    echo "${ARCH_UPPER}" > ${B}/gdb-16.2/crash.target

    # 4. Apply your original configuration text manipulations natively
    sed -i s/FORCE_DEFINE_ARCH/"${ARCH_UPPER}"/g ${S}/configure.c
    sed -i -e 's/#define TARGET_CFLAGS_ARM_ON_X86_64.*/#define TARGET_CFLAGS_ARM_ON_X86_64\t\"TARGET_CFLAGS=-D_FILE_OFFSET_BITS=64\"/g' ${S}/configure.c
    sed -i -e 's/#define TARGET_CFLAGS_MIPS_ON_X86_64.*/#define TARGET_CFLAGS_MIPS_ON_X86_64\t\"TARGET_CFLAGS=-D_FILE_OFFSET_BITS=64\"/g' ${S}/configure.c
    sed -i 's/&gt;/>/g' ${S}/Makefile

    # The crash gdb merge step re-enters the top-level crash Makefile from gdb's
    # link rule ("make -C ../.. ... library") without passing the compiler, so
    # crash's own objects fall back to the Makefile default $(CROSS_COMPILE)gcc.
    # That only happens to work when a ${cross}-gcc exists; on clang toolchains
    # (and any gcc-less SDK) it fails. Forward CC/CXX through that recursion.
    sed -i 's|GDB_FLAGS=-DGDB_16_2 library|GDB_FLAGS=-DGDB_16_2 CC="$(CC)" CXX="$(CXX)" library|' ${S}/gdb-16.2.patch

    # 5. Return to the active workspace build root to merge downstream additions
    cd ${B}
    bbnote "Applying the native upstream crash integration patch onto the GDB source tree..."
    patch -p0 --fuzz=3 < gdb-16.2.patch
}

do_compile() {
    oe_runmake ${EXTRA_OEMAKE} CC="${CC}" CXX="${CXX}" RECIPE_SYSROOT=${RECIPE_SYSROOT}
}

do_install:prepend () {
    install -d ${D}${bindir}
    install -d ${D}/${mandir}/man8
    install -d ${D}${includedir}/crash

    install -m 0644 ${S}/crash.8 ${D}/${mandir}/man8/
    install -m 0644 ${S}/defs.h ${D}${includedir}/crash
}

RDEPENDS:${PN} += "liblzma"

ARM_INSTRUCTION_SET = "arm"
