SUMMARY = "Kernel analysis utility for live systems, netdump, diskdump, kdump, LKCD or mcore dumpfiles"
DESCRIPTION = "The core analysis suite is a self-contained tool that can be used to\
investigate either live systems, kernel core dumps created from the\
netdump, diskdump and kdump packages from Red Hat Linux, the mcore kernel patch\
offered by Mission Critical Linux, or the LKCD kernel patch."

HOMEPAGE = "http://people.redhat.com/anderson"
SECTION = "devel"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING3;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "zlib readline coreutils-native ncurses-native"

INC_PR = "r1"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/crash-utility/${BPN}.git;branch=master;protocol=https \
           ${GNU_MIRROR}/gdb/gdb-10.2.tar.gz;name=gdb;subdir=git \
           file://7001force_define_architecture.patch \
           file://7003cross_ranlib.patch \
           file://0001-cross_add_configure_option.patch \
           file://donnot-extract-gdb-during-do-compile.patch \
           file://gdb_build_jobs_and_not_write_crash_target.patch \
           file://0001-symbol-fix-S-cannot-work-with-kaslr-detection.patch \
           "
SRCREV = "ceacceef7d13134d327719a624cfafed99e90f8a"

SRC_URI[gdb.sha256sum] = "b33ad58d687487a821ec8d878daab0f716be60d0936f2e3ac5cf08419ce70350"

UPSTREAM_CHECK_URI = "https://github.com/crash-utility/crash/releases"

inherit gettext

TARGET_CC_ARCH:append = " ${SELECTED_OPTIMIZATION}"

# crash 7.1.3 and before don't support mips64/riscv64
COMPATIBLE_HOST:riscv64 = "null"
COMPATIBLE_HOST:riscv32 = "null"
COMPATIBLE_HOST:mipsarchn64 = "null"
COMPATIBLE_HOST:mipsarchn32 = "null"


EXTRA_OEMAKE = 'RPMPKG="${PV}" \
                GDB_TARGET="${TARGET_SYS}" \
                GDB_HOST="${BUILD_SYS}" \
                GDB_MAKE_JOBS="${PARALLEL_MAKE}" \
                LDFLAGS="${LDFLAGS}" \
                '

do_configure() {
    :
}

do_compile:prepend() {
    case ${TARGET_ARCH} in
        aarch64*)    ARCH=ARM64 ;;
        arm*)        ARCH=ARM ;;
        i*86*)       ARCH=X86 ;;
        x86_64*)     ARCH=X86_64 ;;
        powerpc64*)  ARCH=PPC64 ;;
        powerpc*)    ARCH=PPC ;;
        mips*)       ARCH=MIPS ;;
    esac

    sed -i s/FORCE_DEFINE_ARCH/"${ARCH}"/g ${S}/configure.c
    sed -i -e 's/#define TARGET_CFLAGS_ARM_ON_X86_64.*/#define TARGET_CFLAGS_ARM_ON_X86_64\t\"TARGET_CFLAGS=-D_FILE_OFFSET_BITS=64\"/g' ${S}/configure.c
    sed -i -e 's/#define TARGET_CFLAGS_MIPS_ON_X86_64.*/#define TARGET_CFLAGS_MIPS_ON_X86_64\t\"TARGET_CFLAGS=-D_FILE_OFFSET_BITS=64\"/g' ${S}/configure.c
    sed -i 's/&gt;/>/g' ${S}/Makefile
}

do_compile() {
    oe_runmake ${EXTRA_OEMAKE} RECIPE_SYSROOT=${RECIPE_SYSROOT}
}

do_install:prepend () {
    install -d ${D}${bindir}
    install -d ${D}/${mandir}/man8
    install -d ${D}${includedir}/crash

    install -m 0644 ${S}/crash.8 ${D}/${mandir}/man8/
    install -m 0644 ${S}/defs.h ${D}${includedir}/crash
}

RDEPENDS:${PN} += "liblzma"

# Causes gcc to get stuck and eat all available memory in qemuarm builds
# jenkins  15161  100 12.5 10389596 10321284 ?   R    11:40  28:17 /home/jenkins/oe/world/shr-core/tmp-glibc/sysroots/x86_64-linux/usr/libexec/arm-oe-linux-gnueabi/gcc/arm-oe-linux-gnueabi/4.9.2/cc1 -quiet -I . -I . -I ./common -I ./config -I ./../include/opcode -I ./../opcodes/.. -I ./../readline/.. -I ../bfd -I ./../bfd -I ./../include -I ../libdecnumber -I ./../libdecnumber -I ./gnulib/import -I build-gnulib/import -isysroot /home/jenkins/oe/world/shr-core/tmp-glibc/sysroots/qemuarm -MMD eval.d -MF .deps/eval.Tpo -MP -MT eval.o -D LOCALEDIR="/usr/local/share/locale" -D CRASH_MERGE -D HAVE_CONFIG_H -D TUI=1 eval.c -quiet -dumpbase eval.c -march=armv5te -mthumb -mthumb-interwork -mtls-dialect=gnu -auxbase-strip eval.o -g -O2 -Wall -Wpointer-arith -Wformat-nonliteral -Wno-pointer-sign -Wno-unused -Wunused-value -Wunused-function -Wno-switch -Wno-char-subscripts -Wmissing-prototypes -Wdeclaration-after-statement -Wempty-body -feliminate-unused-debug-types -o -
ARM_INSTRUCTION_SET = "arm"

# http://errors.yoctoproject.org/Errors/Details/186964/
COMPATIBLE_HOST:libc-musl = 'null'
