SUMMARY = "Linux Test Project"
DESCRIPTION = "The Linux Test Project is a joint project with SGI, IBM, OSDL, and Bull with a goal to deliver test suites to the open source community that validate the reliability, robustness, and stability of Linux. The Linux Test Project is a collection of tools for testing the Linux kernel and related features."
HOMEPAGE = "https://linux-test-project.github.io/"
SECTION = "console/utils"
LICENSE = "GPLv2 & GPLv2+ & LGPLv2+ & LGPLv2.1+ & BSD-2-Clause"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://testcases/kernel/controllers/freezer/COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
    file://testcases/kernel/controllers/freezer/run_freezer.sh;beginline=5;endline=17;md5=86a61d2c042d59836ffb353a21456498 \
    file://testcases/kernel/hotplug/memory_hotplug/COPYING;md5=e04a2e542b2b8629bf9cd2ba29b0fe41 \
    file://testcases/kernel/hotplug/cpu_hotplug/COPYING;md5=e04a2e542b2b8629bf9cd2ba29b0fe41 \
    file://testcases/open_posix_testsuite/COPYING;md5=48b1c5ec633e3e30ec2cf884ae699947 \
    file://testcases/realtime/COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e \
    file://utils/benchmark/kernbench-0.42/COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
"

DEPENDS = "attr libaio libcap acl openssl zip-native"
DEPENDS:append:libc-musl = " fts "
EXTRA_OEMAKE:append:libc-musl = " LIBC=musl "
EXTRA_OECONF:append:libc-musl = " LIBS=-lfts "

# since ltp contains x86-64 assembler which uses the frame-pointer register,
# set -fomit-frame-pointer x86-64 to handle cases where optimisation
# is set to -O0 or frame pointers have been enabled by -fno-omit-frame-pointer
# earlier in CFLAGS, etc.
CFLAGS:append:x86-64 = " -fomit-frame-pointer"

CFLAGS:append:powerpc64 = " -D__SANE_USERSPACE_TYPES__"
CFLAGS:append:mipsarchn64 = " -D__SANE_USERSPACE_TYPES__"
SRCREV = "0fb171f2beddaf64bd27597577c206c0f892b3cd"

SRC_URI = "git://github.com/linux-test-project/ltp.git \
           file://0001-Remove-OOM-tests-from-runtest-mm.patch \
           file://0001-syscalls-ioctl_ns05.c-ioctl_ns06.c-Fix-too-small-buf.patch \
           "

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig

TARGET_CC_ARCH += "${LDFLAGS}"

export prefix = "/opt/${PN}"
export exec_prefix = "/opt/${PN}"

PACKAGECONFIG[numa] = "--with-numa, --without-numa, numactl,"
EXTRA_AUTORECONF += "-I ${S}/testcases/realtime/m4"
EXTRA_OECONF = " --with-realtime-testsuite --with-open-posix-testsuite "
# ltp network/rpc test cases ftbfs when libtirpc is found
EXTRA_OECONF += " --without-tirpc "

do_install(){
    install -d ${D}${prefix}/
    oe_runmake DESTDIR=${D} SKIP_IDCHECK=1 install include-install

    # fixup not deploy STPfailure_report.pl to avoid confusing about it fails to run
    # as it lacks dependency on some perl moudle such as LWP::Simple
    # And this script previously works as a tool for analyzing failures from LTP
    # runs on the OSDL's Scaleable Test Platform (STP) and it mainly accesses
    # http://khack.osdl.org to retrieve ltp test results run on
    # OSDL's Scaleable Test Platform, but now http://khack.osdl.org unaccessible
    rm -rf ${D}${prefix}/bin/STPfailure_report.pl

    # Copy POSIX test suite into ${D}${prefix}/testcases by manual
    cp -r testcases/open_posix_testsuite ${D}${prefix}/testcases

    # Makefile were configured in the build system
    find ${D}${prefix} -name Makefile | xargs -n 1 sed -i \
         -e 's@[^ ]*-fdebug-prefix-map=[^ "]*@@g' \
         -e 's@[^ ]*-fmacro-prefix-map=[^ "]*@@g' \
         -e 's@[^ ]*-ffile-prefix-map=[^ "]*@@g' \
         -e 's@[^ ]*--sysroot=[^ "]*@@g'

    # The controllers memcg_stree test seems to cause us hangs and takes 900s
    # (maybe we expect more regular output?), anyhow, skip it
    sed -e '/^memcg_stress/d' -i ${D}${prefix}/runtest/controllers
}

RDEPENDS:${PN} = "\
    attr \
    bash \
    bc \
    coreutils \
    cpio \
    cronie \
    curl \
    e2fsprogs \
    e2fsprogs-mke2fs \
    expect \
    file \
    gawk \
    gdb \
    gzip \
    iproute2 \
    ldd \
    libaio \
    logrotate \
    net-tools \
    perl \
    python3-core \
    procps \
    quota \
    unzip \
    util-linux \
    which \
    tar \
"

FILES:${PN} += "${prefix}/* ${prefix}/runtest/* ${prefix}/scenario_groups/* ${prefix}/testcases/bin/* ${prefix}/testcases/bin/*/bin/* ${prefix}/testscripts/* ${prefix}/testcases/open_posix_testsuite/* ${prefix}/testcases/open_posix_testsuite/conformance/* ${prefix}/testcases/open_posix_testsuite/Documentation/* ${prefix}/testcases/open_posix_testsuite/functional/* ${prefix}/testcases/open_posix_testsuite/include/* ${prefix}/testcases/open_posix_testsuite/scripts/* ${prefix}/testcases/open_posix_testsuite/stress/* ${prefix}/testcases/open_posix_testsuite/tools/* ${prefix}/testcases/data/nm01/lib.a ${prefix}/lib/libmem.a"

# Avoid stripping some generated binaries otherwise some of the ltp tests such as ldd01 & nm01 fail
INHIBIT_PACKAGE_STRIP_FILES = "${prefix}/testcases/bin/nm01 ${prefix}/testcases/bin/ldd01"
INSANE_SKIP:${PN} += "already-stripped staticdev"

remove_broken_musl_sources() {
	[ "${TCLIBC}" = "musl" ] || return 0

	cd ${S}
	echo "WARNING: remove unsupported tests (until they're fixed)"

	# sync with upstream
	# https://github.com/linux-test-project/ltp/blob/master/travis/alpine.sh#L33
	rm -rfv \
		testcases/kernel/sched/process_stress/process.c \
		testcases/kernel/syscalls/confstr/confstr01.c \
		testcases/kernel/syscalls/fmtmsg/fmtmsg01.c \
		testcases/kernel/syscalls/getcontext/getcontext01.c \
		testcases/kernel/syscalls/rt_tgsigqueueinfo/rt_tgsigqueueinfo01.c \
		testcases/kernel/syscalls/timer_create/timer_create01.c \
		testcases/kernel/syscalls/timer_create/timer_create03.c \
		utils/benchmark/ebizzy-0.3
}
do_patch[postfuncs] += "remove_broken_musl_sources"

# Avoid file dependency scans, as LTP checks for things that may or may not
# exist on the running system.  For instance it has specific checks for
# csh and ksh which are not typically part of OpenEmbedded systems (but
# can be added via additional layers.)
SKIP_FILEDEPS:${PN} = '1'
