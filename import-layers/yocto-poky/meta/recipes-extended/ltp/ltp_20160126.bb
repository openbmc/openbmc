SUMMARY = "Linux Test Project"
DESCRIPTION = "The Linux Test Project is a joint project with SGI, IBM, OSDL, and Bull with a goal to deliver test suites to the open source community that validate the reliability, robustness, and stability of Linux. The Linux Test Project is a collection of tools for testing the Linux kernel and related features."
HOMEPAGE = "http://ltp.sourceforge.net"
SECTION = "console/utils"
LICENSE = "GPLv2 & GPLv2+ & LGPLv2+ & LGPLv2.1+ & BSD-2-Clause"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://testcases/kernel/controllers/freezer/COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
    file://testcases/kernel/controllers/freezer/run_freezer.sh;beginline=5;endline=17;md5=86a61d2c042d59836ffb353a21456498 \
    file://testcases/kernel/hotplug/memory_hotplug/COPYING;md5=e04a2e542b2b8629bf9cd2ba29b0fe41 \
    file://testcases/kernel/hotplug/cpu_hotplug/COPYING;md5=e04a2e542b2b8629bf9cd2ba29b0fe41 \
    file://testcases/open_posix_testsuite/COPYING;md5=216e43b72efbe4ed9017cc19c4c68b01 \
    file://testcases/realtime/COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e \
    file://tools/netpipe-2.4/COPYING;md5=9e3781bb5fe787aa80e1f51f5006b6fa \
    file://tools/netpipe-2.4-ipv6/COPYING;md5=9e3781bb5fe787aa80e1f51f5006b6fa \
    file://tools/top-LTP/proc/COPYING;md5=aefc88eb8a41672fbfcfe6b69ab8c49c \
    file://tools/pounder21/COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
    file://utils/benchmark/kernbench-0.42/COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
    file://utils/ffsb-6.0-rc2/COPYING;md5=c46082167a314d785d012a244748d803 \
"

DEPENDS = "attr libaio libcap acl openssl zip-native"
DEPENDS_append_libc-musl = " fts "
EXTRA_OEMAKE_append_libc-musl = " LIBC=musl "
CFLAGS_append_powerpc64 = " -D__SANE_USERSPACE_TYPES__"
CFLAGS_append_mips64 = " -D__SANE_USERSPACE_TYPES__"
SRCREV = "fce797676b14f50406718e7ef640b50da66c9b36"

SRC_URI = "git://github.com/linux-test-project/ltp.git \
           file://0001-ltp-Don-t-link-against-libfl.patch \
           file://0002-Add-knob-to-control-whether-numa-support-should-be-c.patch \
           file://0003-Add-knob-to-control-tirpc-support.patch \
           file://0004-build-Add-option-to-select-libc-implementation.patch \
           file://0005-kernel-controllers-Link-with-libfts-explicitly-on-mu.patch \
           file://0006-sendfile-Use-off64_t-instead-of-__off64_t.patch \
           file://0007-replace-SIGCLD-with-SIGCHLD.patch \
           file://0008-Check-if-__GLIBC_PREREQ-is-defined-before-using-it.patch \
           file://0009-Guard-error.h-with-__GLIBC__.patch \
           file://0010-replace-__BEGIN_DECLS-and-__END_DECLS.patch \
           file://0011-Rename-sigset-variable-to-sigset1.patch \
           file://0012-fsstress.c-Replace-__int64_t-with-int64_t.patch \
           file://0013-include-fcntl.h-for-getting-O_-definitions.patch \
           file://0014-hyperthreading-Include-sys-types.h-for-pid_t-definit.patch \
           file://0015-mincore01-Rename-PAGESIZE-to-pagesize.patch \
           file://0016-ustat-Change-header-from-ustat.h-to-sys-ustat.h.patch \
           file://0017-replace-sigval_t-with-union-sigval.patch \
           file://0018-guard-mallocopt-with-__GLIBC__.patch \
           file://0019-tomoyo-Replace-canonicalize_file_name-with-realpath.patch \
           file://0020-getdents-define-getdents-getdents64-only-for-glibc.patch \
           file://0021-Define-_GNU_SOURCE-for-MREMAP_MAYMOVE-definition.patch \
           file://0022-include-sys-types.h.patch \
           file://0023-ptrace-Use-int-instead-of-enum-__ptrace_request.patch \
           file://0024-rt_sigaction-rt_sigprocmark-Define-_GNU_SOURCE.patch \
           file://0025-mc_gethost-include-sys-types.h.patch \
           file://0026-crash01-Define-_GNU_SOURCE.patch \
           file://0027-sysconf01-Use-_SC_2_C_VERSION-conditionally.patch \
           file://0028-rt_sigaction.h-Use-sighandler_t-instead-of-__sighand.patch \
           file://0029-trace_shed-Fix-build-with-musl.patch \
           file://0030-lib-Use-PTHREAD_MUTEX_RECURSIVE-in-place-of-PTHREAD_.patch \
           file://0031-vma03-fix-page-size-offset-as-per-page-size-alignmen.patch \
           file://0032-regen.sh-Include-asm-unistd.h-explicitly.patch \
           file://0033-shmat1-Cover-GNU-specific-code-under-__USE_GNU.patch \
           file://0034-periodic_output.patch \
           file://0035-fix-test_proc_kill-hang.patch \
           "

S = "${WORKDIR}/git"

inherit autotools-brokensep

TARGET_CC_ARCH += "${LDFLAGS}"

export prefix = "/opt/ltp"
export exec_prefix = "/opt/ltp"

PACKAGECONFIG[numa] = "--with-numa, --without-numa, numactl,"
EXTRA_AUTORECONF += "-I ${S}/testcases/realtime/m4"
EXTRA_OECONF = " --with-power-management-testsuite --with-realtime-testsuite "
# ltp network/rpc test cases ftbfs when libtirpc is found
EXTRA_OECONF += " --without-tirpc "

# The makefiles make excessive use of make -C and several include testcases.mk
# which triggers a build of the syscall header. To reproduce, build ltp,
# then delete the header, then "make -j XX" and watch regen.sh run multiple
# times. Its easier to generate this once here instead.
do_compile_prepend () {
	( make -C ${B}/testcases/kernel include/linux_syscall_numbers.h )
}

do_install(){
    install -d ${D}/opt/ltp/
    oe_runmake DESTDIR=${D} SKIP_IDCHECK=1 install

    # Copy POSIX test suite into ${D}/opt/ltp/testcases by manual
    cp -r testcases/open_posix_testsuite ${D}/opt/ltp/testcases
}

RDEPENDS_${PN} = "perl e2fsprogs-mke2fs python-core libaio bash gawk expect ldd"

FILES_${PN}-staticdev += "/opt/ltp/lib/libmem.a"

FILES_${PN} += "/opt/ltp/* /opt/ltp/runtest/* /opt/ltp/scenario_groups/* /opt/ltp/testcases/bin/* /opt/ltp/testcases/bin/*/bin/* /opt/ltp/testscripts/* /opt/ltp/testcases/open_posix_testsuite/* /opt/ltp/testcases/open_posix_testsuite/conformance/* /opt/ltp/testcases/open_posix_testsuite/Documentation/* /opt/ltp/testcases/open_posix_testsuite/functional/* /opt/ltp/testcases/open_posix_testsuite/include/* /opt/ltp/testcases/open_posix_testsuite/scripts/* /opt/ltp/testcases/open_posix_testsuite/stress/* /opt/ltp/testcases/open_posix_testsuite/tools/*"

# Avoid generated binaries stripping. Otherwise some of the ltp tests such as ldd01 & nm01 fails
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
# However, test_arch_stripped is already stripped, so...
INSANE_SKIP_${PN} += "already-stripped"

