SUMMARY = "Performance analysis tools for Linux"
DESCRIPTION = "Performance counters for Linux are a new kernel-based \
subsystem that provide a framework for all things \
performance analysis. It covers hardware level \
(CPU/PMU, Performance Monitoring Unit) features \
and software features (software counters, tracepoints) \
as well."

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

PR = "r9"

require perf-features.inc

BUILDPERF_libc-uclibc = "no"

# gui support was added with kernel 3.6.35
# since 3.10 libnewt was replaced by slang
# to cover a wide range of kernel we add both dependencies
TUI_DEPENDS = "${@perf_feature_enabled('perf-tui', 'libnewt slang', '',d)}"
SCRIPTING_DEPENDS = "${@perf_feature_enabled('perf-scripting', 'perl python', '',d)}"
LIBUNWIND_DEPENDS = "${@perf_feature_enabled('perf-libunwind', 'libunwind', '',d)}"

DEPENDS = " \
    virtual/${MLPREFIX}libc \
    ${MLPREFIX}elfutils \
    ${MLPREFIX}binutils \
    ${TUI_DEPENDS} \
    ${SCRIPTING_DEPENDS} \
    ${LIBUNWIND_DEPENDS} \
    bison flex xz \
"

do_configure[depends] += "virtual/kernel:do_shared_workdir"

PROVIDES = "virtual/perf"

inherit linux-kernel-base kernel-arch pythonnative

# needed for building the tools/perf Python bindings
inherit python-dir
export STAGING_INCDIR
export STAGING_LIBDIR
export BUILD_SYS
export HOST_SYS
export PYTHON_SITEPACKAGES_DIR

#kernel 3.1+ supports WERROR to disable warnings as errors
export WERROR = "0"

do_populate_lic[depends] += "virtual/kernel:do_patch"

# needed for building the tools/perf Perl binding
inherit perlnative cpan-base
# Env var which tells perl if it should use host (no) or target (yes) settings
export PERLCONFIGTARGET = "${@is_target(d)}"
export PERL_INC = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}/CORE"
export PERL_LIB = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}"
export PERL_ARCHLIB = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}"

inherit kernelsrc

B = "${WORKDIR}/${BPN}-${PV}"
SPDX_S = "${S}/tools/perf"

SCRIPTING_DEFINES = "${@perf_feature_enabled('perf-scripting', '', 'NO_LIBPERL=1 NO_LIBPYTHON=1',d)}"
TUI_DEFINES = "${@perf_feature_enabled('perf-tui', '', 'NO_NEWT=1',d)}"
LIBUNWIND_DEFINES = "${@perf_feature_enabled('perf-libunwind', '', 'NO_LIBUNWIND=1 NO_LIBDW_DWARF_UNWIND=1',d)}"
LIBNUMA_DEFINES = "${@perf_feature_enabled('perf-libnuma', '', 'NO_LIBNUMA=1',d)}"

# The LDFLAGS is required or some old kernels fails due missing
# symbols and this is preferred than requiring patches to every old
# supported kernel.
LDFLAGS="-ldl -lutil"

EXTRA_OEMAKE = '\
    -C ${S}/tools/perf \
    O=${B} \
    CROSS_COMPILE=${TARGET_PREFIX} \
    ARCH=${ARCH} \
    CC="${CC}" \
    AR="${AR}" \
    EXTRA_CFLAGS="-ldw" \
    perfexecdir=${libexecdir} \
    NO_GTK2=1 ${TUI_DEFINES} NO_DWARF=1 ${LIBUNWIND_DEFINES} \
    ${SCRIPTING_DEFINES} ${LIBNUMA_DEFINES} \
'

EXTRA_OEMAKE += "\
    'DESTDIR=${D}' \
    'prefix=${prefix}' \
    'bindir=${bindir}' \
    'sharedir=${datadir}' \
    'sysconfdir=${sysconfdir}' \
    'perfexecdir=${libexecdir}/perf-core' \
    \
    'ETC_PERFCONFIG=${@os.path.relpath(sysconfdir, prefix)}' \
    'sharedir=${@os.path.relpath(datadir, prefix)}' \
    'mandir=${@os.path.relpath(mandir, prefix)}' \
    'infodir=${@os.path.relpath(infodir, prefix)}' \
"


do_compile() {
	# Linux kernel build system is expected to do the right thing
	unset CFLAGS
	oe_runmake all
}

do_install() {
	# Linux kernel build system is expected to do the right thing
	unset CFLAGS
	oe_runmake install
	# we are checking for this make target to be compatible with older perf versions
	if [ "${@perf_feature_enabled('perf-scripting', 1, 0, d)}" = "1" ] && grep -q install-python_ext ${S}/tools/perf/Makefile*; then
		oe_runmake DESTDIR=${D} install-python_ext
	fi
}

do_configure_prepend () {
    # Fix for rebuilding
    rm -rf ${B}/
    mkdir -p ${B}/

    # If building a multlib based perf, the incorrect library path will be
    # detected by perf, since it triggers via: ifeq ($(ARCH),x86_64). In a 32 bit
    # build, with a 64 bit multilib, the arch won't match and the detection of a 
    # 64 bit build (and library) are not exected. To ensure that libraries are
    # installed to the correct location, we can use the weak assignment in the
    # config/Makefile.
    #
    # Also need to relocate .config-detected to $(OUTPUT)/config-detected
    # for kernel sources that do not already do this
    # as two builds (e.g. perf and lib32-perf from mutlilib can conflict
    # with each other if its in the shared source directory
    #
    if [ -e "${S}/tools/perf/config/Makefile" ]; then
        # Match $(prefix)/$(lib) and $(prefix)/lib
        sed -i -e 's,^libdir = \($(prefix)/.*lib\),libdir ?= \1,' \
               -e 's,^perfexecdir = \(.*\),perfexecdir ?= \1,' \
               -e 's,\ .config-detected, $(OUTPUT)/config-detected,g' \
            ${S}/tools/perf/config/Makefile
    fi
    if [ -e "${S}/tools/perf/Makefile.perf" ]; then
        sed -i -e 's,\ .config-detected, $(OUTPUT)/config-detected,g' \
            ${S}/tools/perf/Makefile.perf
        sed -i -e "s,prefix='\$(DESTDIR_SQ)/usr'$,prefix='\$(DESTDIR_SQ)/usr' --install-lib='\$(DESTDIR)\$(PYTHON_SITEPACKAGES_DIR)',g" \
            ${S}/tools/perf/Makefile.perf
    fi
    sed -i -e "s,--root='/\$(DESTDIR_SQ)',--prefix='\$(DESTDIR_SQ)/usr' --install-lib='\$(DESTDIR)\$(PYTHON_SITEPACKAGES_DIR)',g" \
        ${S}/tools/perf/Makefile*

    if [ -e "${S}/tools/build/Makefile.build" ]; then
        sed -i -e 's,\ .config-detected, $(OUTPUT)/config-detected,g' \
            ${S}/tools/build/Makefile.build
    fi

    # We need to ensure the --sysroot option in CC is preserved
    if [ -e "${S}/tools/perf/Makefile.perf" ]; then
        sed -i 's,CC = $(CROSS_COMPILE)gcc,#CC,' ${S}/tools/perf/Makefile.perf
        sed -i 's,AR = $(CROSS_COMPILE)ar,#AR,' ${S}/tools/perf/Makefile.perf
    fi
    if [ -e "${S}/tools/lib/api/Makefile" ]; then
        sed -i 's,CC = $(CROSS_COMPILE)gcc,#CC,' ${S}/tools/lib/api/Makefile
        sed -i 's,AR = $(CROSS_COMPILE)ar,#AR,' ${S}/tools/lib/api/Makefile
    fi
    if [ -e "${S}/tools/lib/subcmd/Makefile" ]; then
        sed -i 's,CC = $(CROSS_COMPILE)gcc,#CC,' ${S}/tools/lib/subcmd/Makefile
        sed -i 's,AR = $(CROSS_COMPILE)ar,#AR,' ${S}/tools/lib/subcmd/Makefile
    fi
    if [ -e "${S}/tools/perf/config/feature-checks/Makefile" ]; then
        sed -i 's,CC := $(CROSS_COMPILE)gcc -MD,CC += -MD,' ${S}/tools/perf/config/feature-checks/Makefile
    fi
    if [ -e "${S}/tools/build/Makefile.feature" ]; then
        sed -i 's,CFLAGS=,CC="\$(CC)" CFLAGS=,' ${S}/tools/build/Makefile.feature
    fi

    # 3.17-rc1+ has a include issue for arm/powerpc. Temporarily sed in the appropriate include
    if [ -e "${S}/tools/perf/arch/$ARCH/util/skip-callchain-idx.c" ]; then
        sed -i 's,#include "util/callchain.h",#include "util/callchain.h"\n#include "util/debug.h",' ${S}/tools/perf/arch/$ARCH/util/skip-callchain-idx.c
    fi
    if [ -e "${S}/tools/perf/arch/arm/util/unwind-libunwind.c" ] && [ -e "${S}/tools/perf/arch/arm/tests/dwarf-unwind.c" ]; then
        sed -i 's,#include "tests/tests.h",#include "tests/tests.h"\n#include "util/debug.h",' ${S}/tools/perf/arch/arm/tests/dwarf-unwind.c
        sed -i 's,#include "perf_regs.h",#include "perf_regs.h"\n#include "util/debug.h",' ${S}/tools/perf/arch/arm/util/unwind-libunwind.c
    fi

    # use /usr/bin/env instead of version specific python
    for s in `find ${S}/tools/perf/scripts/python/ -name '*.py'`; do
        sed -i 's,/usr/bin/python2,/usr/bin/env python,' "${s}"
    done
}

python do_package_prepend() {
    d.setVar('PKGV', d.getVar("KERNEL_VERSION", True).split("-")[0])
}

PACKAGE_ARCH = "${MACHINE_ARCH}"


PACKAGES =+ "${PN}-archive ${PN}-tests ${PN}-perl ${PN}-python"

RDEPENDS_${PN} += "elfutils bash"
RDEPENDS_${PN}-archive =+ "bash"
RDEPENDS_${PN}-python =+ "bash python python-modules"
RDEPENDS_${PN}-perl =+ "bash perl perl-modules"
RDEPENDS_${PN}-tests =+ "python"

RSUGGESTS_SCRIPTING = "${@perf_feature_enabled('perf-scripting', '${PN}-perl ${PN}-python', '',d)}"
RSUGGESTS_${PN} += "${PN}-archive ${PN}-tests ${RSUGGESTS_SCRIPTING}"

FILES_${PN} += "${libexecdir}/perf-core ${exec_prefix}/libexec/perf-core ${libdir}/traceevent"
FILES_${PN}-archive = "${libdir}/perf/perf-core/perf-archive"
FILES_${PN}-tests = "${libdir}/perf/perf-core/tests ${libexecdir}/perf-core/tests"
FILES_${PN}-python = "${libdir}/python*/site-packages ${libdir}/perf/perf-core/scripts/python"
FILES_${PN}-python += "${libexecdir}/perf-core/scripts/python/*"
FILES_${PN}-perl = "${libdir}/perf/perf-core/scripts/perl"


INHIBIT_PACKAGE_DEBUG_SPLIT="1"
