SUMMARY = "Kernel analysis utility for live systems, netdump, diskdump, kdump, LKCD or mcore dumpfiles"
DESCRIPTION = "The core analysis suite is a self-contained tool that can be used to\
investigate either live systems, kernel core dumps created from the\
netdump, diskdump and kdump packages from Red Hat Linux, the mcore kernel patch\
offered by Mission Critical Linux, or the LKCD kernel patch."

HOMEPAGE = "http://people.redhat.com/anderson"
SECTION = "devel"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING3;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "zlib readline"

SRC_URI = "https://github.com/crash-utility/${BPN}/archive/${PV}.tar.gz;downloadfilename=${BP}.tar.gz \
           http://ftp.gnu.org/gnu/gdb/gdb-7.6.tar.gz;name=gdb;subdir=${BP} \
           file://7001force_define_architecture.patch \ 
           file://7003cross_ranlib.patch \
           file://0001-cross_add_configure_option.patch \
           file://sim-ppc-drop-LIBS-from-psim-dependency.patch \
           file://sim-common-sim-arange-fix-extern-inline-handling.patch \
           file://donnot-extract-gdb-during-do-compile.patch \
           file://gdb_build_jobs_and_not_write_crash_target.patch \
           file://remove-unrecognized-gcc-option-m32-for-mips.patch \
           "

SRC_URI[md5sum] = "155889a233c5230ef1d387858091d294"
SRC_URI[sha256sum] = "ae98529d42b843f07d795b86b8f8529f64cc607ee3c58affc5a8aa8a506e183d"

SRC_URI[gdb.md5sum] = "a9836707337e5f7bf76a009a8904f470"
SRC_URI[gdb.sha256sum] = "8070389a5dcc104eb0be483d582729f98ed4d761ad19cedd3f17b5d2502faa36"

inherit gettext

BBCLASSEXTEND = "native cross"

# crash 7.1.3 and before don't support mips64
COMPATIBLE_HOST = "^(?!mips64).*"

EXTRA_OEMAKE = 'RPMPKG="${PV}" \
                GDB_TARGET="${TARGET_SYS}" \
                GDB_HOST="${BUILD_SYS}" \
                GDB_MAKE_JOBS="${PARALLEL_MAKE}" \
                '

EXTRA_OEMAKE_class-cross = 'RPMPKG="${PV}" \
                            GDB_TARGET="${BUILD_SYS} \
                                        \${GDB_CONF_FLAGS} \
                                        --target=${TARGET_SYS}" \
                            GDB_HOST="${BUILD_SYS}" \
                            GDB_MAKE_JOBS="${PARALLEL_MAKE}" \
                            '

EXTRA_OEMAKE_append_class-native = " LDFLAGS='${BUILD_LDFLAGS}'"
EXTRA_OEMAKE_append_class-cross = " LDFLAGS='${BUILD_LDFLAGS}'"

REMOVE_M32 = "sed -i -e 's/#define TARGET_CFLAGS_ARM_ON_X86_64.*/#define TARGET_CFLAGS_ARM_ON_X86_64\t\"TARGET_CFLAGS=-D_FILE_OFFSET_BITS=64\"/g' ${S}/configure.c"

REMOVE_M32_class-cross = ""

do_configure() {
    :
}

do_compile_prepend() {
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
    ${REMOVE_M32}
    sed -i 's/&gt;/>/g' ${S}/Makefile
}

do_compile() {
    oe_runmake ${EXTRA_OEMAKE}
}

do_install_prepend () {
    install -d ${D}${bindir}
    install -d ${D}/${mandir}/man8
    install -d ${D}${includedir}/crash

    install -m 0644 ${S}/crash.8 ${D}/${mandir}/man8/
    install -m 0644 ${S}/defs.h ${D}${includedir}/crash
}

do_install_class-target () {
    oe_runmake DESTDIR=${D} install
}

do_install_class-native () {
    oe_runmake DESTDIR=${D}${STAGING_DIR_NATIVE} install
}

do_install_class-cross () {
    install -m 0755 ${S}/crash ${D}/${bindir}
}

RDEPENDS_${PN} += "liblzma"
RDEPENDS_${PN}_class-native = ""
RDEPENDS_${PN}_class-cross = ""

# Causes gcc to get stuck and eat all available memory in qemuarm builds
# jenkins  15161  100 12.5 10389596 10321284 ?   R    11:40  28:17 /home/jenkins/oe/world/shr-core/tmp-glibc/sysroots/x86_64-linux/usr/libexec/arm-oe-linux-gnueabi/gcc/arm-oe-linux-gnueabi/4.9.2/cc1 -quiet -I . -I . -I ./common -I ./config -I ./../include/opcode -I ./../opcodes/.. -I ./../readline/.. -I ../bfd -I ./../bfd -I ./../include -I ../libdecnumber -I ./../libdecnumber -I ./gnulib/import -I build-gnulib/import -isysroot /home/jenkins/oe/world/shr-core/tmp-glibc/sysroots/qemuarm -MMD eval.d -MF .deps/eval.Tpo -MP -MT eval.o -D LOCALEDIR="/usr/local/share/locale" -D CRASH_MERGE -D HAVE_CONFIG_H -D TUI=1 eval.c -quiet -dumpbase eval.c -march=armv5te -mthumb -mthumb-interwork -mtls-dialect=gnu -auxbase-strip eval.o -g -O2 -Wall -Wpointer-arith -Wformat-nonliteral -Wno-pointer-sign -Wno-unused -Wunused-value -Wunused-function -Wno-switch -Wno-char-subscripts -Wmissing-prototypes -Wdeclaration-after-statement -Wempty-body -feliminate-unused-debug-types -o -
ARM_INSTRUCTION_SET = "arm"
