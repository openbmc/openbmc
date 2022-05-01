SUMMARY = "Linux kernel Development Source"
DESCRIPTION = "Development source linux kernel. When built, this recipe packages the \
source of the preferred virtual/kernel provider and makes it available for full kernel \
development or external module builds"

SECTION = "kernel"

LICENSE = "GPL-2.0-only"

inherit linux-kernel-base

# Whilst not a module, this ensures we don't get multilib extended (which would make no sense)
inherit module-base

# We need the kernel to be staged (unpacked, patched and configured) before
# we can grab the source and make the source package. We also need the bits from
# ${B} not to change while we install, so virtual/kernel must finish do_compile.
do_install[depends] += "virtual/kernel:do_shared_workdir"
# Need the source, not just the output of populate_sysroot
do_install[depends] += "virtual/kernel:do_install"

# There's nothing to do here, except install the source where we can package it
do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
deltask do_populate_sysroot

S = "${STAGING_KERNEL_DIR}"
B = "${STAGING_KERNEL_BUILDDIR}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

KERNEL_BUILD_ROOT="${nonarch_base_libdir}/modules/"

do_install() {
    kerneldir=${D}${KERNEL_BUILD_ROOT}${KERNEL_VERSION}
    install -d $kerneldir

    # create the directory structure
    rm -f $kerneldir/build
    rm -f $kerneldir/source
    mkdir -p $kerneldir/build

    # for compatibility with some older variants of this package, we
    # create  a /usr/src/kernel symlink to /lib/modules/<version>/source
    mkdir -p ${D}/usr/src
    (
	cd ${D}/usr/src
	ln -rs ${D}${KERNEL_BUILD_ROOT}${KERNEL_VERSION}/source kernel
    )

    # for on target purposes, we unify build and source
    (
	cd $kerneldir
	ln -s build source
    )

    # first copy everything
    (
	cd ${S}
	cp --parents $(find  -type f -name "Makefile*" -o -name "Kconfig*") $kerneldir/build
	cp --parents $(find  -type f -name "Build" -o -name "Build.include") $kerneldir/build
    )

    # then drop all but the needed Makefiles/Kconfig files
    rm -rf $kerneldir/build/scripts
    rm -rf $kerneldir/build/include

    # now copy in parts from the build that we'll need later
    (
	cd ${B}

	if [ -s Module.symvers ]; then
	    cp Module.symvers $kerneldir/build
	fi
	cp System.map* $kerneldir/build
	if [ -s Module.markers ]; then
	    cp Module.markers $kerneldir/build
	fi

	cp -a .config $kerneldir/build

	# This scripts copy blow up QA, so for now, we require a more
	# complex 'make scripts' to restore these, versus copying them
	# here. Left as a reference to indicate that we know the scripts must
	# be dealt with.
	# cp -a scripts $kerneldir/build

	# although module.lds can be regenerated on target via 'make modules_prepare'
	# there are several places where 'makes scripts prepare' is done, and that won't
	# regenerate the file. So we copy it onto the target as a migration to using
	# modules_prepare
	cp -a --parents scripts/module.lds $kerneldir/build/ 2>/dev/null || :

        if [ -d arch/${ARCH}/scripts ]; then
	    cp -a arch/${ARCH}/scripts $kerneldir/build/arch/${ARCH}
	fi
	if [ -f arch/${ARCH}/*lds ]; then
	    cp -a arch/${ARCH}/*lds $kerneldir/build/arch/${ARCH}
	fi

	rm -f $kerneldir/build/scripts/*.o
	rm -f $kerneldir/build/scripts/*/*.o

	if [ "${ARCH}" = "powerpc" ]; then
	    if [ -e arch/powerpc/lib/crtsavres.S ] ||
		   [ -e arch/powerpc/lib/crtsavres.o ]; then
		cp -a --parents arch/powerpc/lib/crtsavres.[So] $kerneldir/build/
	    fi
	fi

	if [ "${ARCH}" = "arm64" -o "${ARCH}" = "riscv" ]; then
            if [ -e arch/${ARCH}/kernel/vdso/vdso.lds ]; then
	        cp -a --parents arch/${ARCH}/kernel/vdso/vdso.lds $kerneldir/build/
            fi
	fi
	if [ "${ARCH}" = "powerpc" ]; then
	    cp -a --parents arch/powerpc/kernel/vdso32/vdso32.lds $kerneldir/build 2>/dev/null || :
	    cp -a --parents arch/powerpc/kernel/vdso64/vdso64.lds $kerneldir/build 2>/dev/null || :
	fi

	cp -a include $kerneldir/build/include

	# we don't usually copy generated files, since they can be rebuilt on the target,
	# but without this file, we get a forced syncconfig run in v5.8+, which prompts and
	# breaks workflows.
	cp -a --parents include/generated/autoconf.h $kerneldir/build 2>/dev/null || :

	if [ -e $kerneldir/include/generated/.vdso-offsets.h.cmd ]; then
	    rm $kerneldir/include/generated/.vdso-offsets.h.cmd
	fi
    )

    # now grab the chunks from the source tree that we need
    (
	cd ${S}

	cp -a scripts $kerneldir/build

	# if our build dir had objtool, it will also be rebuilt on target, so
	# we copy what is required for that build
	if [ -f ${B}/tools/objtool/objtool ]; then
	    # these are a few files associated with objtool, since we'll need to
	    # rebuild it
	    cp -a --parents tools/build/Build.include $kerneldir/build/
	    cp -a --parents tools/build/Build $kerneldir/build/
	    cp -a --parents tools/build/fixdep.c $kerneldir/build/
	    cp -a --parents tools/scripts/utilities.mak $kerneldir/build/

	    # extra files, just in case
	    cp -a --parents tools/objtool/* $kerneldir/build/
	    cp -a --parents tools/lib/* $kerneldir/build/
	    cp -a --parents tools/lib/subcmd/* $kerneldir/build/

	    cp -a --parents tools/include/* $kerneldir/build/

	    cp -a --parents $(find tools/arch/${ARCH}/ -type f) $kerneldir/build/
	fi

	if [ "${ARCH}" = "arm64" ]; then
	    # arch/arm64/include/asm/xen references arch/arm
	    cp -a --parents arch/arm/include/asm/xen $kerneldir/build/
	    # arch/arm64/include/asm/opcodes.h references arch/arm
	    cp -a --parents arch/arm/include/asm/opcodes.h $kerneldir/build/

            cp -a --parents arch/arm64/kernel/vdso/*gettimeofday.* $kerneldir/build/
            cp -a --parents arch/arm64/kernel/vdso/sigreturn.S $kerneldir/build/
            cp -a --parents arch/arm64/kernel/vdso/note.S $kerneldir/build/
            cp -a --parents arch/arm64/kernel/vdso/gen_vdso_offsets.sh $kerneldir/build/

            cp -a --parents arch/arm64/kernel/module.lds $kerneldir/build/ 2>/dev/null || :

            # 5.13+ needs these tools
            cp -a --parents arch/arm64/tools/gen-cpucaps.awk $kerneldir/build/ 2>/dev/null || :
            cp -a --parents arch/arm64/tools/cpucaps $kerneldir/build/ 2>/dev/null || :

            if [ -e $kerneldir/build/arch/arm64/tools/gen-cpucaps.awk ]; then
                 sed -i -e "s,#!.*awk.*,#!${USRBINPATH}/env awk," $kerneldir/build/arch/arm64/tools/gen-cpucaps.awk
            fi
	fi

	if [ "${ARCH}" = "powerpc" ]; then
	    # 5.0 needs these files, but don't error if they aren't present in the source
	    cp -a --parents arch/${ARCH}/kernel/syscalls/syscall.tbl $kerneldir/build/ 2>/dev/null || :
	    cp -a --parents arch/${ARCH}/kernel/syscalls/syscalltbl.sh $kerneldir/build/ 2>/dev/null || :
	    cp -a --parents arch/${ARCH}/kernel/syscalls/syscallhdr.sh $kerneldir/build/ 2>/dev/null || :
	    cp -a --parents arch/${ARCH}/kernel/vdso32/* $kerneldir/build/ 2>/dev/null || :
	    cp -a --parents arch/${ARCH}/kernel/vdso64/* $kerneldir/build/ 2>/dev/null || :
	fi
	if [ "${ARCH}" = "riscv" ]; then
            cp -a --parents arch/riscv/kernel/vdso/*gettimeofday.* $kerneldir/build/
            cp -a --parents arch/riscv/kernel/vdso/note.S $kerneldir/build/
            if [ -e arch/riscv/kernel/vdso/gen_vdso_offsets.sh ]; then
                    cp -a --parents arch/riscv/kernel/vdso/gen_vdso_offsets.sh $kerneldir/build/
            fi
	    cp -a --parents arch/riscv/kernel/vdso/* $kerneldir/build/ 2>/dev/null || :
	fi

	# include the machine specific headers for ARM variants, if available.
	if [ "${ARCH}" = "arm" ]; then
	    cp -a --parents arch/${ARCH}/mach-*/include $kerneldir/build/

	    # include a few files for 'make prepare'
	    cp -a --parents arch/arm/tools/gen-mach-types $kerneldir/build/
	    cp -a --parents arch/arm/tools/mach-types $kerneldir/build/

	    # ARM syscall table tools only exist for kernels v4.10 or later
            SYSCALL_TOOLS=$(find arch/arm/tools -name "syscall*")
            if [ -n "$SYSCALL_TOOLS" ] ; then
	        cp -a --parents $SYSCALL_TOOLS $kerneldir/build/
            fi

            cp -a --parents arch/arm/kernel/module.lds $kerneldir/build/ 2>/dev/null || :
	fi

	if [ -d arch/${ARCH}/include ]; then
	    cp -a --parents arch/${ARCH}/include $kerneldir/build/
	fi

	cp -a include $kerneldir/build

	cp -a --parents lib/vdso/* $kerneldir/build/ 2>/dev/null || :

	cp -a --parents tools/include/tools/le_byteshift.h $kerneldir/build/
	cp -a --parents tools/include/tools/be_byteshift.h $kerneldir/build/

	# required for generate missing syscalls prepare phase
	cp -a --parents $(find arch/x86 -type f -name "syscall_32.tbl") $kerneldir/build
	cp -a --parents $(find arch/arm -type f -name "*.tbl") $kerneldir/build 2>/dev/null || :

	if [ "${ARCH}" = "x86" ]; then
	    # files for 'make prepare' to succeed with kernel-devel
	    cp -a --parents $(find arch/x86 -type f -name "syscall_32.tbl") $kerneldir/build/ 2>/dev/null || :
	    cp -a --parents $(find arch/x86 -type f -name "syscalltbl.sh") $kerneldir/build/ 2>/dev/null || :
	    cp -a --parents $(find arch/x86 -type f -name "syscallhdr.sh") $kerneldir/build/ 2>/dev/null || :
	    cp -a --parents $(find arch/x86 -type f -name "syscall_64.tbl") $kerneldir/build/ 2>/dev/null || :
	    cp -a --parents arch/x86/tools/relocs_32.c $kerneldir/build/
	    cp -a --parents arch/x86/tools/relocs_64.c $kerneldir/build/
	    cp -a --parents arch/x86/tools/relocs.c $kerneldir/build/
	    cp -a --parents arch/x86/tools/relocs_common.c $kerneldir/build/
	    cp -a --parents arch/x86/tools/relocs.h $kerneldir/build/
	    cp -a --parents arch/x86/tools/gen-insn-attr-x86.awk $kerneldir/build/ 2>/dev/null || :
	    cp -a --parents arch/x86/purgatory/purgatory.c $kerneldir/build/

	    # 4.18 + have unified the purgatory files, so we ignore any errors if
	    # these files are not present
	    cp -a --parents arch/x86/purgatory/sha256.h $kerneldir/build/ 2>/dev/null || :
	    cp -a --parents arch/x86/purgatory/sha256.c $kerneldir/build/ 2>/dev/null || :

	    cp -a --parents arch/x86/purgatory/stack.S $kerneldir/build/
	    cp -a --parents arch/x86/purgatory/string.c $kerneldir/build/ 2>/dev/null || :
	    cp -a --parents arch/x86/purgatory/setup-x86_64.S $kerneldir/build/
	    cp -a --parents arch/x86/purgatory/entry64.S $kerneldir/build/
	    cp -a --parents arch/x86/boot/string.h $kerneldir/build/
	    cp -a --parents arch/x86/boot/string.c $kerneldir/build/
	    cp -a --parents arch/x86/boot/compressed/string.c $kerneldir/build/ 2>/dev/null || :
	    cp -a --parents arch/x86/boot/ctype.h $kerneldir/build/

	    # objtool requires these files
	    cp -a --parents arch/x86/lib/inat.c $kerneldir/build/ 2>/dev/null || :
	    cp -a --parents arch/x86/lib/insn.c $kerneldir/build/ 2>/dev/null || :
	fi

	if [ "${ARCH}" = "mips" ]; then
	    cp -a --parents arch/mips/Kbuild.platforms $kerneldir/build/
	    cp --parents $(find	 -type f -name "Platform") $kerneldir/build
	    cp --parents arch/mips/boot/tools/relocs* $kerneldir/build
	    cp -a --parents arch/mips/kernel/asm-offsets.c $kerneldir/build
	    cp -a --parents kernel/time/timeconst.bc $kerneldir/build
	    cp -a --parents kernel/bounds.c $kerneldir/build
	    cp -a --parents Kbuild $kerneldir/build
	    cp -a --parents arch/mips/kernel/syscalls/*.sh $kerneldir/build 2>/dev/null || :
	    cp -a --parents arch/mips/kernel/syscalls/*.tbl $kerneldir/build 2>/dev/null || :
	    cp -a --parents arch/mips/tools/elf-entry.c $kerneldir/build 2>/dev/null || :
	fi

        # required to build scripts/selinux/genheaders/genheaders
        cp -a --parents security/selinux/include/* $kerneldir/build/

	# copy any localversion files
	cp -a localversion* $kerneldir/build/ 2>/dev/null || :
    )

    # Make sure the Makefile and version.h have a matching timestamp so that
    # external modules can be built
    touch -r $kerneldir/build/Makefile $kerneldir/build/include/generated/uapi/linux/version.h

    # Copy .config to include/config/auto.conf so "make prepare" is unnecessary.
    cp $kerneldir/build/.config $kerneldir/build/include/config/auto.conf

    # make sure these are at least as old as the .config, or rebuilds will trigger
    touch -r $kerneldir/build/.config $kerneldir/build/include/generated/autoconf.h 2>/dev/null || :
    touch -r $kerneldir/build/.config $kerneldir/build/include/config/auto.conf* 2>/dev/null || :

    if [ -e "$kerneldir/build/include/config/auto.conf.cmd" ]; then
        sed -i 's/ifneq "$(CC)" ".*-linux-.*gcc.*$/ifneq "$(CC)" "gcc"/' "$kerneldir/build/include/config/auto.conf.cmd"
        sed -i 's/ifneq "$(LD)" ".*-linux-.*ld.bfd.*$/ifneq "$(LD)" "ld"/' "$kerneldir/build/include/config/auto.conf.cmd"
        sed -i 's/ifneq "$(AR)" ".*-linux-.*ar.*$/ifneq "$(AR)" "ar"/' "$kerneldir/build/include/config/auto.conf.cmd"
        sed -i 's/ifneq "$(OBJCOPY)" ".*-linux-.*objcopy.*$/ifneq "$(OBJCOPY)" "objcopy"/' "$kerneldir/build/include/config/auto.conf.cmd"
        if [ "${ARCH}" = "powerpc" ]; then
            sed -i 's/ifneq "$(NM)" ".*-linux-.*nm.*$/ifneq "$(NM)" "nm --synthetic"/' "$kerneldir/build/include/config/auto.conf.cmd"
        else
            sed -i 's/ifneq "$(NM)" ".*-linux-.*nm.*$/ifneq "$(NM)" "nm"/' "$kerneldir/build/include/config/auto.conf.cmd"
        fi
        sed -i 's/ifneq "$(HOSTCXX)" ".*$/ifneq "$(HOSTCXX)" "g++"/' "$kerneldir/build/include/config/auto.conf.cmd"
        sed -i 's/ifneq "$(HOSTCC)" ".*$/ifneq "$(HOSTCC)" "gcc"/' "$kerneldir/build/include/config/auto.conf.cmd"
        sed -i 's/ifneq "$(CC_VERSION_TEXT)".*\(gcc.*\)"/ifneq "$(CC_VERSION_TEXT)" "\1"/' "$kerneldir/build/include/config/auto.conf.cmd"
        sed -i 's/ifneq "$(srctree)" ".*"/ifneq "$(srctree)" "."/' "$kerneldir/build/include/config/auto.conf.cmd"
        # we don't build against the defconfig, so make sure it isn't the trigger for syncconfig
        sed -i 's/ifneq "$(KBUILD_DEFCONFIG)".*"\(.*\)"/ifneq "\1" "\1"/' "$kerneldir/build/include/config/auto.conf.cmd"
    fi

    # make the scripts python3 safe. We won't be running these, and if they are
    # left as /usr/bin/python rootfs assembly will fail, since we only have python3
    # in the RDEPENDS (and the python3 package does not include /usr/bin/python)
    for ss in $(find $kerneldir/build/scripts -type f -name '*'); do
	sed -i 's,/usr/bin/python2,/usr/bin/env python3,' "$ss"
	sed -i 's,/usr/bin/env python2,/usr/bin/env python3,' "$ss"
	sed -i 's,/usr/bin/python,/usr/bin/env python3,' "$ss"
    done

    chown -R root:root ${D}
}

# Ensure we don't race against "make scripts" during cpio
do_install[lockfiles] = "${TMPDIR}/kernel-scripts.lock"

FILES:${PN} = "${KERNEL_BUILD_ROOT} ${KERNEL_SRC_PATH}"
FILES:${PN}-dbg += "${KERNEL_BUILD_ROOT}*/build/scripts/*/.debug/*"

RDEPENDS:${PN} = "bc python3 flex bison ${TCLIBC}-utils"
# 4.15+ needs these next two RDEPENDS
RDEPENDS:${PN} += "openssl-dev util-linux"
# and x86 needs a bit more for 4.15+
RDEPENDS:${PN} += "${@bb.utils.contains('ARCH', 'x86', 'elfutils-dev', '', d)}"
# 5.8+ needs gcc-plugins libmpc-dev
RDEPENDS:${PN} += "gcc-plugins libmpc-dev"
# 5.13+ needs awk for arm64
RDEPENDS:${PN}:append:aarch64 = " gawk"
# 5.13+ needs grep for powerpc
RDEPENDS:${PN}:append:powerpc = " grep"
