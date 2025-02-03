SUMMARY = "Performance analysis tools for Linux"
DESCRIPTION = "Performance counters for Linux are a new kernel-based \
subsystem that provide a framework for all things \
performance analysis. It covers hardware level \
(CPU/PMU, Performance Monitoring Unit) features \
and software features (software counters, tracepoints) \
as well."
HOMEPAGE = "https://perf.wiki.kernel.org/index.php/Main_Page"

LICENSE = "GPL-2.0-only"


PACKAGECONFIG ??= "python tui libunwind libtraceevent"
PACKAGECONFIG[dwarf] = ",NO_DWARF=1"
PACKAGECONFIG[perl] = ",NO_LIBPERL=1,perl"
PACKAGECONFIG[python] = ",NO_LIBPYTHON=1,python3 python3-setuptools-native"
PACKAGECONFIG[tui] = ",NO_SLANG=1,slang"
PACKAGECONFIG[libunwind] = ",NO_LIBUNWIND=1 NO_LIBDW_DWARF_UNWIND=1,libunwind"
PACKAGECONFIG[libnuma] = ",NO_LIBNUMA=1"
PACKAGECONFIG[bfd] = ",NO_LIBBFD=1"
PACKAGECONFIG[systemtap] = ",NO_SDT=1,systemtap"
PACKAGECONFIG[jvmti] = ",NO_JVMTI=1"
# libaudit support would need scripting to be enabled
PACKAGECONFIG[audit] = ",NO_LIBAUDIT=1,audit"
PACKAGECONFIG[manpages] = ",,xmlto-native asciidoc-native"
PACKAGECONFIG[cap] = ",,libcap"
PACKAGECONFIG[libtraceevent] = ",NO_LIBTRACEEVENT=1,libtraceevent"
# jevents requires host python for generating a .c file, but is
# unrelated to the python item.
PACKAGECONFIG[jevents] = ",NO_JEVENTS=1,python3-native"
# Arm CoreSight
PACKAGECONFIG[coresight] = "CORESIGHT=1,,opencsd"
PACKAGECONFIG[pfm4] = ",NO_LIBPFM4=1,libpfm4"
PACKAGECONFIG[babeltrace] = ",NO_LIBBABELTRACE=1,babeltrace"
PACKAGECONFIG[zstd] = ",NO_LIBZSTD=1,zstd"

# libunwind is not yet ported for some architectures
PACKAGECONFIG:remove:arc = "libunwind"
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
inherit_defer ${@bb.utils.contains('PACKAGECONFIG', 'python', 'python3targetconfig', '', d)}
inherit python3-dir
export PYTHON_SITEPACKAGES_DIR

#kernel 3.1+ supports WERROR to disable warnings as errors
export WERROR = "0"

do_populate_lic[depends] += "virtual/kernel:do_shared_workdir"

# needed for building the tools/perf Perl binding
include ${@bb.utils.contains('PACKAGECONFIG', 'perl', 'perf-perl.inc', '', d)}

inherit kernelsrc

S = "${WORKDIR}/${BP}"

# The LDFLAGS is required or some old kernels fails due missing
# symbols and this is preferred than requiring patches to every old
# supported kernel.
LDFLAGS = "-ldl -lutil"

# Perf's build system adds its own optimization flags for most TUs,
# overriding the flags included here. But for some, perf does not add
# any -O option, so ensure the distro's chosen optimization gets used
# for those. Also include ${DEBUG_PREFIX_MAP} which ensures perf is
# built with appropriate -f*-prefix-map options,
# avoiding the 'buildpaths' QA warning.
TARGET_CC_ARCH += "${SELECTED_OPTIMIZATION} ${DEBUG_PREFIX_MAP}"

EXTRA_OEMAKE = '\
    V=1 \
    VF=1 \
    -C ${S}/tools/perf \
    O=${B} \
    CROSS_COMPILE=${TARGET_PREFIX} \
    ARCH=${ARCH} \
    CC="${CC}" \
    CCLD="${CC}" \
    LDSHARED="${CC} -shared" \
    AR="${AR}" \
    LD="${LD}" \
    EXTRA_CFLAGS="-ldw -I${S}" \
    YFLAGS='-y --file-prefix-map=${WORKDIR}=${TARGET_DBGSRC_DIR}' \
    EXTRA_LDFLAGS="${PERF_EXTRA_LDFLAGS}" \
    perfexecdir=${libexecdir} \
    NO_GTK2=1 \
    ${PACKAGECONFIG_CONFARGS} \
    PKG_CONFIG=pkg-config \
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
    ${@bb.utils.contains('PACKAGECONFIG', 'python', 'PYTHON=python3 PYTHON_CONFIG=python3-config', '', d)} \
"

# During do_configure, we might run a 'make clean'. That often breaks
# when done in parallel, so disable parallelism for do_configure. Note
# that it has to be done this way rather than by passing -j1, since
# perf's build system by default ignores any -j argument, but does
# honour a JOBS variable.
EXTRA_OEMAKE:append:task-configure = " JOBS=1"

# the architectures that need this file can be found in
#    tools/include/uapi/asm/bpf_perf_event.h
# We are only listing supported arches at the moment
PERF_BPF_EVENT_SRC ?= '${@bb.utils.contains_any("ARCH", [ "riscv", "arm64" ], "arch/${ARCH}/include/uapi/asm/bpf_perf_event.h", "", d)}'
PERF_SRC ?= "Makefile \
             tools/arch \
             tools/build \
             tools/include \
             tools/lib \
             tools/Makefile \
             tools/perf \
             tools/scripts \
             scripts/ \
             arch/arm64/tools \
             ${PERF_BPF_EVENT_SRC} \
             arch/${ARCH}/Makefile \
"

PERF_EXTRA_LDFLAGS = ""

# MIPS N32/N64
PERF_EXTRA_LDFLAGS:mipsarchn32eb = "-m elf32btsmipn32"
PERF_EXTRA_LDFLAGS:mipsarchn32el = "-m elf32ltsmipn32"
PERF_EXTRA_LDFLAGS:mipsarchn64eb = "-m elf64btsmip"
PERF_EXTRA_LDFLAGS:mipsarchn64el = "-m elf64ltsmip"

do_compile() {
	# Linux kernel build system is expected to do the right thing
	unset CFLAGS
        test -e ${S}/tools/lib/traceevent/plugins/Makefile && \
            sed -i -e 's|\$(libdir)/traceevent/plugins|\$(libdir)/traceevent_${KERNEL_VERSION}/plugins|g' ${S}/tools/lib/traceevent/plugins/Makefile
	test -e ${S}/tools/perf/Makefile.config && \
            sed -i -e 's|\$(libdir)/traceevent/plugins|\$(libdir)/traceevent_${KERNEL_VERSION}/plugins|g' ${S}/tools/perf/Makefile.config
	oe_runmake all
}

do_install() {
	# Linux kernel build system is expected to do the right thing
	unset CFLAGS
	oe_runmake install
	# we are checking for this make target to be compatible with older perf versions
	if ${@bb.utils.contains('PACKAGECONFIG', 'python', 'true', 'false', d)} && grep -q install-python_ext ${S}/tools/perf/Makefile*; then
	    oe_runmake DESTDIR=${D} install-python_ext
	    if [ -e ${D}${libdir}/python*/site-packages/perf-*/SOURCES.txt ]; then
		sed -i -e 's#${WORKDIR}##g' ${D}${libdir}/python*/site-packages/perf-*/SOURCES.txt
	    fi
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
            bb.warn("Path does not exist: %s. Maybe PERF_SRC lists more files than what your kernel version provides and needs." % src)
            continue
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
        # Variant with linux-yocto-specific patch
        sed -i -e "s,prefix='\$(DESTDIR_SQ)/usr'$,prefix='\$(DESTDIR_SQ)/usr' --install-lib='\$(PYTHON_SITEPACKAGES_DIR)' --root='\$(DESTDIR)',g" \
            ${S}/tools/perf/Makefile.perf
        # Variant for mainline Linux
        sed -i -e "s,root='/\$(DESTDIR_SQ)',prefix='\$(DESTDIR_SQ)/usr' --install-lib='\$(PYTHON_SITEPACKAGES_DIR)' --root='/\$(DESTDIR_SQ)',g" \
            ${S}/tools/perf/Makefile.perf
        # backport https://github.com/torvalds/linux/commit/e4ffd066ff440a57097e9140fa9e16ceef905de8
        sed -i -e 's,\($(Q)$(SHELL) .$(arch_errno_tbl).\) $(CC) $(arch_errno_hdr_dir),\1 $(firstword $(CC)) $(arch_errno_hdr_dir),g' \
            ${S}/tools/perf/Makefile.perf
    fi
    sed -i -e "s,--root='/\$(DESTDIR_SQ)',--prefix='\$(DESTDIR_SQ)/usr' --install-lib='\$(DESTDIR)\$(PYTHON_SITEPACKAGES_DIR)',g" \
        ${S}/tools/perf/Makefile

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
        # The same line is in older releases, but looking explicitly for Python 2
        sed -i -e 's,$(call get-executable-or-default\,PYTHON\,$(PYTHON2)),$(notdir $(call get-executable-or-default\,PYTHON\,$(PYTHON2))),g' \
            ${S}/tools/perf/Makefile.config

	# likewise with this substitution. Kernels with commit 18f2967418d031a39
	# [perf tools: Use Python devtools for version autodetection rather than runtime]
	# need this substitution for reproducibility.
	sed -i -e 's,$(call get-executable-or-default\,PYTHON\,$(subst -config\,\,$(PYTHON_AUTO))),$(notdir $(call get-executable-or-default\,PYTHON\,$(subst -config\,\,$(PYTHON_AUTO)))),g' \
	    ${S}/tools/perf/Makefile.config

        # The following line:
        #     srcdir_SQ = $(patsubst %tools/perf,tools/perf,$(subst ','\'',$(srcdir))),
        # Captures the full src path of perf, which of course makes it not
        # reproducible. We really only need the relative location 'tools/perf', so we
        # change the Makefile line to remove everything before 'tools/perf'
        sed -i -e "s%srcdir_SQ = \$(subst ','\\\'',\$(srcdir))%srcdir_SQ = \$(patsubst \%tools/perf,tools/perf,\$(subst ','\\\'',\$(srcdir)))%g" \
            ${S}/tools/perf/Makefile.config
        # Avoid hardcoded path to python-native
        sed -i -e 's#\(PYTHON_WORD := \)$(call shell-wordify,$(PYTHON))#\1 python3#g' \
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
    if [ -e "${S}/tools/perf/pmu-events/jevents.py" ]; then
        sed -i -e "s#os.scandir(path)#sorted(os.scandir(path), key=lambda e: e.name)#g" \
                       "${S}/tools/perf/pmu-events/jevents.py"
    fi
    if [ -e "${S}/tools/perf/arch/arm64/Makefile" ]; then
	sed -i 's,sysdef := $(srctree)/,sysdef := ,' ${S}/tools/perf/arch/arm64/Makefile
	sed -i 's,$(incpath) $(sysdef),$(incpath) $(srctree)/$(sysdef) $(sysdef),' ${S}/tools/perf/arch/arm64/Makefile
    fi
    if [ -e "${S}/tools/perf/arch/arm64/entry/syscalls/mksyscalltbl" ]; then
	if ! grep -q input_rel ${S}/tools/perf/arch/arm64/entry/syscalls/mksyscalltbl; then
	    sed -i 's,input=$4,input=$4\ninput_rel=$5,' ${S}/tools/perf/arch/arm64/entry/syscalls/mksyscalltbl
	fi
	sed -i 's,#include \\"\$input\\",#include \\"\$input_rel\\",'  ${S}/tools/perf/arch/arm64/entry/syscalls/mksyscalltbl
    fi
    # end reproducibility substitutions

    # We need to ensure the --sysroot option in CC is preserved
    if [ -e "${S}/tools/perf/Makefile.perf" ]; then
        sed -i 's,CC = $(CROSS_COMPILE)gcc,#CC,' ${S}/tools/perf/Makefile.perf
        sed -i 's,AR = $(CROSS_COMPILE)ar,#AR,' ${S}/tools/perf/Makefile.perf
        sed -i 's,LD = $(CROSS_COMPILE)ld,#LD,' ${S}/tools/perf/Makefile.perf
        sed -i 's,PKG_CONFIG = $(CROSS_COMPILE)pkg-config,#PKG_CONFIG,' ${S}/tools/perf/Makefile.perf
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
    # The libperl feature check produces fatal warnings due to -Werror being
    # used, silence enough errors that the check passes.
    sed -i 's/\(FLAGS_PERL_EMBED=.*\)/\1 -Wno-error=unused-function -Wno-error=attributes/' ${S}/tools/build/feature/Makefile

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
RDEPENDS:${PN}-tests =+ "python3 bash perl"

RSUGGESTS:${PN} += "${PN}-archive ${PN}-tests \
                    ${@bb.utils.contains('PACKAGECONFIG', 'perl', '${PN}-perl', '', d)} \
                    ${@bb.utils.contains('PACKAGECONFIG', 'python', '${PN}-python', '', d)} \
                    "
FILES_SOLIBSDEV = ""
FILES:${PN} += "${libexecdir}/perf-core ${exec_prefix}/libexec/perf-core ${libdir}/traceevent* ${libdir}/libperf-jvmti.so"
FILES:${PN}-archive = "${libdir}/perf/perf-core/perf-archive"
FILES:${PN}-tests = "${libdir}/perf/perf-core/tests ${libexecdir}/perf-core/tests"
FILES:${PN}-python = " \
                       ${PYTHON_SITEPACKAGES_DIR} \
                       ${libexecdir}/perf-core/scripts/python \
                       "
FILES:${PN}-perl = "${libexecdir}/perf-core/scripts/perl"

DEBUG_OPTIMIZATION:append = " -Wno-error=maybe-uninitialized"

PACKAGESPLITFUNCS =+ "perf_fix_sources"

perf_fix_sources () {
	for f in util/parse-events-flex.h util/parse-events-flex.c util/pmu-flex.c \
			util/pmu-flex.h util/expr-flex.h util/expr-flex.c; do
		f=${PKGD}${TARGET_DBGSRC_DIR}/$f
		if [ -e $f ]; then
			sed -i -e 's#${S}/##g' $f
		fi
	done
}
