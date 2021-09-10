SUMMARY = "Performance analysis tools for Linux"
DESCRIPTION = "Performance counters for Linux are a new kernel-based \
subsystem that provide a framework for all things \
performance analysis. It covers hardware level \
(CPU/PMU, Performance Monitoring Unit) features \
and software features (software counters, tracepoints) \
as well."
HOMEPAGE = "https://perf.wiki.kernel.org/index.php/Main_Page"

LICENSE = "GPLv2"

PR = "r9"

PACKAGECONFIG ??= "scripting tui libunwind"
PACKAGECONFIG[dwarf] = ",NO_DWARF=1"
PACKAGECONFIG[scripting] = ",NO_LIBPERL=1 NO_LIBPYTHON=1,perl python3"
# gui support was added with kernel 3.6.35
# since 3.10 libnewt was replaced by slang
# to cover a wide range of kernel we add both dependencies
PACKAGECONFIG[tui] = ",NO_NEWT=1,libnewt slang"
PACKAGECONFIG[libunwind] = ",NO_LIBUNWIND=1 NO_LIBDW_DWARF_UNWIND=1,libunwind"
PACKAGECONFIG[libnuma] = ",NO_LIBNUMA=1"
PACKAGECONFIG[systemtap] = ",NO_SDT=1,systemtap"
PACKAGECONFIG[jvmti] = ",NO_JVMTI=1"
# libaudit support would need scripting to be enabled
PACKAGECONFIG[audit] = ",NO_LIBAUDIT=1,audit"
PACKAGECONFIG[manpages] = ",,xmlto-native asciidoc-native"
PACKAGECONFIG[cap] = ",,libcap"
# Arm CoreSight
PACKAGECONFIG[coresight] = "CORESIGHT=1,,opencsd"

# libunwind is not yet ported for some architectures
PACKAGECONFIG:remove:arc = "libunwind"
PACKAGECONFIG:remove:riscv64 = "libunwind"
PACKAGECONFIG:remove:riscv32 = "libunwind"

DEPENDS = " \
    virtual/${MLPREFIX}libc \
    ${MLPREFIX}elfutils \
    ${MLPREFIX}binutils \
    bison-native flex-native xz \
"

do_configure[depends] += "virtual/kernel:do_shared_workdir"

PROVIDES = "virtual/perf"

inherit linux-kernel-base kernel-arch manpages

# needed for building the tools/perf Python bindings
inherit ${@bb.utils.contains('PACKAGECONFIG', 'scripting', 'python3targetconfig', '', d)}
inherit python3-dir
export PYTHON_SITEPACKAGES_DIR

#kernel 3.1+ supports WERROR to disable warnings as errors
export WERROR = "0"

do_populate_lic[depends] += "virtual/kernel:do_shared_workdir"

# needed for building the tools/perf Perl binding
include ${@bb.utils.contains('PACKAGECONFIG', 'scripting', 'perf-perl.inc', '', d)}

inherit kernelsrc

S = "${WORKDIR}/${BP}"
SPDX_S = "${S}/tools/perf"

# The LDFLAGS is required or some old kernels fails due missing
# symbols and this is preferred than requiring patches to every old
# supported kernel.
LDFLAGS="-ldl -lutil"

EXTRA_OEMAKE = '\
    V=1 \
    -C ${S}/tools/perf \
    O=${B} \
    CROSS_COMPILE=${TARGET_PREFIX} \
    ARCH=${ARCH} \
    CC="${CC}" \
    CCLD="${CC}" \
    LDSHARED="${CC} -shared" \
    AR="${AR}" \
    LD="${LD}" \
    EXTRA_CFLAGS="-ldw" \
    YFLAGS='-y --file-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}' \
    EXTRA_LDFLAGS="${PERF_EXTRA_LDFLAGS}" \
    perfexecdir=${libexecdir} \
    NO_GTK2=1 \
    ${PACKAGECONFIG_CONFARGS} \
    TMPDIR="${B}" \
    LIBUNWIND_DIR=${STAGING_EXECPREFIXDIR} \
'

EXTRA_OEMAKE += "\
    'DESTDIR=${D}' \
    'prefix=${prefix}' \
    'bindir=${bindir}' \
    'sharedir=${datadir}' \
    'sysconfdir=${sysconfdir}' \
    'perfexecdir=${libexecdir}/perf-core' \
    'ETC_PERFCONFIG=${@os.path.relpath(sysconfdir, prefix)}' \
    'sharedir=${@os.path.relpath(datadir, prefix)}' \
    'mandir=${@os.path.relpath(mandir, prefix)}' \
    'infodir=${@os.path.relpath(infodir, prefix)}' \
    ${@bb.utils.contains('PACKAGECONFIG', 'scripting', 'PYTHON=python3 PYTHON_CONFIG=python3-config', '', d)} \
"

# During do_configure, we might run a 'make clean'. That often breaks
# when done in parallel, so disable parallelism for do_configure. Note
# that it has to be done this way rather than by passing -j1, since
# perf's build system by default ignores any -j argument, but does
# honour a JOBS variable.
EXTRA_OEMAKE:append:task-configure = " JOBS=1"

PERF_SRC ?= "Makefile \
             tools/arch \
             tools/build \
             tools/include \
             tools/lib \
             tools/Makefile \
             tools/perf \
             tools/scripts \
             scripts/ \
             arch/${ARCH}/Makefile \
"

PERF_EXTRA_LDFLAGS = ""

# MIPS N32
PERF_EXTRA_LDFLAGS:mipsarchn32eb = "-m elf32btsmipn32"
PERF_EXTRA_LDFLAGS:mipsarchn32el = "-m elf32ltsmipn32"

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
	if ${@bb.utils.contains('PACKAGECONFIG', 'scripting', 'true', 'false', d)} && grep -q install-python_ext ${S}/tools/perf/Makefile*; then
	    oe_runmake DESTDIR=${D} install-python_ext
	fi
}

do_configure[prefuncs] += "copy_perf_source_from_kernel"
python copy_perf_source_from_kernel() {
    sources = (d.getVar("PERF_SRC") or "").split()
    src_dir = d.getVar("STAGING_KERNEL_DIR")
    dest_dir = d.getVar("S")
    bb.utils.mkdirhier(dest_dir)
    bb.utils.prunedir(dest_dir)
    for s in sources:
        src = oe.path.join(src_dir, s)
        dest = oe.path.join(dest_dir, s)
        if not os.path.exists(src):
            bb.fatal("Path does not exist: %s. Maybe PERF_SRC does not match the kernel version." % src)
        if os.path.isdir(src):
            oe.path.copyhardlinktree(src, dest)
        else:
            src_path = os.path.dirname(s)
            os.makedirs(os.path.join(dest_dir,src_path),exist_ok=True)
            bb.utils.copyfile(src, dest)
}

do_configure:prepend () {
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
        perfconfig="${S}/tools/perf/config/Makefile"
    fi
    if [ -e "${S}/tools/perf/Makefile.config" ]; then
        perfconfig="${S}/tools/perf/Makefile.config"
    fi
    if [ -n "${perfconfig}" ]; then
        # Match $(prefix)/$(lib) and $(prefix)/lib
        sed -i -e 's,^libdir = \($(prefix)/.*lib\),libdir ?= \1,' \
               -e 's,^perfexecdir = \(.*\),perfexecdir ?= \1,' \
               -e 's,\ .config-detected, $(OUTPUT)/config-detected,g' \
            ${perfconfig}
    fi
    # The man pages installation is "$(INSTALL) -d -m 755 $(DESTDIR)$(man1dir)"
    # in ${S}/tools/perf/Documentation/Makefile, if the mandir set to '?=', it
    # will use the relative path 'share/man', in the way it will resulting in
    # incorrect installation for man pages.
    if [ -e "${S}/tools/perf/Documentation/Makefile" ]; then
	sed -i 's,^mandir?=,mandir:=,' ${S}/tools/perf/Documentation/Makefile
    fi
    if [ -e "${S}/tools/perf/Makefile.perf" ]; then
        sed -i -e 's,\ .config-detected, $(OUTPUT)/config-detected,g' \
            ${S}/tools/perf/Makefile.perf
        sed -i -e "s,prefix='\$(DESTDIR_SQ)/usr'$,prefix='\$(DESTDIR_SQ)/usr' --install-lib='\$(DESTDIR)\$(PYTHON_SITEPACKAGES_DIR)',g" \
            ${S}/tools/perf/Makefile.perf
        # backport https://github.com/torvalds/linux/commit/e4ffd066ff440a57097e9140fa9e16ceef905de8
        sed -i -e 's,\($(Q)$(SHELL) .$(arch_errno_tbl).\) $(CC) $(arch_errno_hdr_dir),\1 $(firstword $(CC)) $(arch_errno_hdr_dir),g' \
            ${S}/tools/perf/Makefile.perf
    fi
    sed -i -e "s,--root='/\$(DESTDIR_SQ)',--prefix='\$(DESTDIR_SQ)/usr' --install-lib='\$(DESTDIR)\$(PYTHON_SITEPACKAGES_DIR)',g" \
        ${S}/tools/perf/Makefile*

    if [ -e "${S}/tools/build/Makefile.build" ]; then
        sed -i -e 's,\ .config-detected, $(OUTPUT)/config-detected,g' \
            ${S}/tools/build/Makefile.build
    fi

    # start reproducibility substitutions
    if [ -e "${S}/tools/perf/Makefile.config" ]; then
        # The following line in the Makefle:
        #     override PYTHON := $(call get-executable-or-default,PYTHON,$(PYTHON_AUTO))
        # "PYTHON" / "PYTHON_AUTO" have the full path as part of the variable. We've
        # ensure that the environment is setup and we do not need the full path to be
        # captured, since the symbol gets built into the executable, making it not
        # reproducible.
        sed -i -e 's,$(call get-executable-or-default\,PYTHON\,$(PYTHON_AUTO)),$(notdir $(call get-executable-or-default\,PYTHON\,$(PYTHON_AUTO))),g' \
            ${S}/tools/perf/Makefile.config

        # The following line:
        #     srcdir_SQ = $(patsubst %tools/perf,tools/perf,$(subst ','\'',$(srcdir))),
        # Captures the full src path of perf, which of course makes it not
        # reproducible. We really only need the relative location 'tools/perf', so we
        # change the Makefile line to remove everything before 'tools/perf'
        sed -i -e "s%srcdir_SQ = \$(subst ','\\\'',\$(srcdir))%srcdir_SQ = \$(patsubst \%tools/perf,tools/perf,\$(subst ','\\\'',\$(srcdir)))%g" \
            ${S}/tools/perf/Makefile.config
    fi
    if [ -e "${S}/tools/perf/tests/Build" ]; then
        # OUTPUT is the full path, we have python on the path so we remove it from the
        # definition. This is captured in the perf binary, so breaks reproducibility
        sed -i -e 's,PYTHONPATH="BUILD_STR($(OUTPUT)python)",PYTHONPATH="BUILD_STR(python)",g' \
            ${S}/tools/perf/tests/Build
    fi
    if [ -e "${S}/tools/perf/util/Build" ]; then
        # To avoid bison generating #ifdefs that have captured paths, we make sure
        # all the calls have YFLAGS, which contains prefix mapping information.
        sed -i -e 's,$(BISON),$(BISON) $(YFLAGS),g' ${S}/tools/perf/util/Build
    fi
    if [ -e "${S}/scripts/Makefile.host" ]; then
        # To avoid yacc (bison) generating #ifdefs that have captured paths, we make sure
        # all the calls have YFLAGS, which contains prefix mapping information.
        sed -i -e 's,$(YACC),$(YACC) $(YFLAGS),g' ${S}/scripts/Makefile.host
    fi
    if [ -e "${S}/tools/perf/pmu-events/Build" ]; then
        target='$(OUTPUT)pmu-events/pmu-events.c $(V)'
        replacement1='$(OUTPUT)pmu-events/pmu-events.c $(V)\n'
        replacement2='\t$(srctree)/sort-pmuevents.py $(OUTPUT)pmu-events/pmu-events.c $(OUTPUT)pmu-events/pmu-events.c.new\n'
        replacement3='\tcp $(OUTPUT)pmu-events/pmu-events.c.new $(OUTPUT)pmu-events/pmu-events.c'
        sed -i -e "s,$target,$replacement1$replacement2$replacement3,g" \
                       "${S}/tools/perf/pmu-events/Build"
    fi
    # end reproducibility substitutions

    # We need to ensure the --sysroot option in CC is preserved
    if [ -e "${S}/tools/perf/Makefile.perf" ]; then
        sed -i 's,CC = $(CROSS_COMPILE)gcc,#CC,' ${S}/tools/perf/Makefile.perf
        sed -i 's,AR = $(CROSS_COMPILE)ar,#AR,' ${S}/tools/perf/Makefile.perf
        sed -i 's,LD = $(CROSS_COMPILE)ld,#LD,' ${S}/tools/perf/Makefile.perf
    fi
    if [ -e "${S}/tools/lib/api/Makefile" ]; then
        sed -i 's,CC = $(CROSS_COMPILE)gcc,#CC,' ${S}/tools/lib/api/Makefile
        sed -i 's,AR = $(CROSS_COMPILE)ar,#AR,' ${S}/tools/lib/api/Makefile
        sed -i 's,LD = $(CROSS_COMPILE)ld,#LD,' ${S}/tools/lib/api/Makefile
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
    for s in `find ${S}/tools/perf/ -name '*.py'` `find ${S}/scripts/ -name 'bpf_helpers_doc.py'`; do
        sed -i -e "s,#!.*python.*,#!${USRBINPATH}/env python3," ${s}
    done

    # unistd.h can be out of sync between libc-headers and the captured version in the perf source
    # so we copy it from the sysroot unistd.h to the perf unistd.h
    install -D -m0644 ${STAGING_INCDIR}/asm-generic/unistd.h ${S}/tools/include/uapi/asm-generic/unistd.h
    install -D -m0644 ${STAGING_INCDIR}/asm-generic/unistd.h ${S}/include/uapi/asm-generic/unistd.h

    # the fetcher is inhibited by the 'inherit kernelsrc', so we do a quick check and
    # copy for a helper script we need
    for p in $(echo ${FILESPATH} | tr ':' '\n'); do
	if [ -e $p/sort-pmuevents.py ]; then
	    cp $p/sort-pmuevents.py ${S}
	fi
    done
}

python do_package:prepend() {
    d.setVar('PKGV', d.getVar("KERNEL_VERSION").split("-")[0])
}

PACKAGE_ARCH = "${MACHINE_ARCH}"


PACKAGES =+ "${PN}-archive ${PN}-tests ${PN}-perl ${PN}-python"

RDEPENDS:${PN} += "elfutils bash"
RDEPENDS:${PN}-archive =+ "bash"
RDEPENDS:${PN}-python =+ "bash python3 python3-modules ${@bb.utils.contains('PACKAGECONFIG', 'audit', 'audit-python', '', d)}"
RDEPENDS:${PN}-perl =+ "bash perl perl-modules"
RDEPENDS:${PN}-tests =+ "python3 bash"

RSUGGESTS_SCRIPTING = "${@bb.utils.contains('PACKAGECONFIG', 'scripting', '${PN}-perl ${PN}-python', '',d)}"
RSUGGESTS:${PN} += "${PN}-archive ${PN}-tests ${RSUGGESTS_SCRIPTING}"

FILES_SOLIBSDEV = ""
FILES:${PN} += "${libexecdir}/perf-core ${exec_prefix}/libexec/perf-core ${libdir}/traceevent ${libdir}/libperf-jvmti.so"
FILES:${PN}-archive = "${libdir}/perf/perf-core/perf-archive"
FILES:${PN}-tests = "${libdir}/perf/perf-core/tests ${libexecdir}/perf-core/tests"
FILES:${PN}-python = " \
                       ${PYTHON_SITEPACKAGES_DIR} \
                       ${libexecdir}/perf-core/scripts/python \
                       "
FILES:${PN}-perl = "${libexecdir}/perf-core/scripts/perl"


INHIBIT_PACKAGE_DEBUG_SPLIT="1"
DEBUG_OPTIMIZATION:append = " -Wno-error=maybe-uninitialized"
