inherit linux-kernel-base kernel-module-split

PROVIDES += "virtual/kernel"
DEPENDS += "virtual/${TARGET_PREFIX}binutils virtual/${TARGET_PREFIX}gcc kmod-native depmodwrapper-cross bc-native"

S = "${STAGING_KERNEL_DIR}"
B = "${WORKDIR}/build"
KBUILD_OUTPUT = "${B}"
OE_TERMINAL_EXPORTS += "KBUILD_OUTPUT"

# we include gcc above, we dont need virtual/libc
INHIBIT_DEFAULT_DEPS = "1"

KERNEL_IMAGETYPE ?= "zImage"
INITRAMFS_IMAGE ?= ""
INITRAMFS_TASK ?= ""
INITRAMFS_IMAGE_BUNDLE ?= ""

# KERNEL_VERSION is extracted from source code. It is evaluated as
# None for the first parsing, since the code has not been fetched.
# After the code is fetched, it will be evaluated as real version
# number and cause kernel to be rebuilt. To avoid this, make
# KERNEL_VERSION_NAME and KERNEL_VERSION_PKG_NAME depend on
# LINUX_VERSION which is a constant.
KERNEL_VERSION_NAME = "${@d.getVar('KERNEL_VERSION', True) or ""}"
KERNEL_VERSION_NAME[vardepvalue] = "${LINUX_VERSION}"
KERNEL_VERSION_PKG_NAME = "${@legitimize_package_name(d.getVar('KERNEL_VERSION', True))}"
KERNEL_VERSION_PKG_NAME[vardepvalue] = "${LINUX_VERSION}"

python __anonymous () {
    import re

    # Merge KERNEL_IMAGETYPE and KERNEL_ALT_IMAGETYPE into KERNEL_IMAGETYPES
    type = d.getVar('KERNEL_IMAGETYPE', True) or ""
    alttype = d.getVar('KERNEL_ALT_IMAGETYPE', True) or ""
    types = d.getVar('KERNEL_IMAGETYPES', True) or ""
    if type not in types.split():
        types = (type + ' ' + types).strip()
    if alttype not in types.split():
        types = (alttype + ' ' + types).strip()
    d.setVar('KERNEL_IMAGETYPES', types)

    typeformake = re.sub(r'\.gz', '', types)
    d.setVar('KERNEL_IMAGETYPE_FOR_MAKE', typeformake)

    for type in typeformake.split():
        typelower = type.lower()

        d.appendVar('PACKAGES', ' ' + 'kernel-image-' + typelower)

        d.setVar('FILES_kernel-image-' + typelower, '/boot/' + type + '*')

        d.appendVar('RDEPENDS_kernel-image', ' ' + 'kernel-image-' + typelower)

        d.setVar('PKG_kernel-image-' + typelower, 'kernel-image-' + typelower + '-${KERNEL_VERSION_PKG_NAME}')

        d.setVar('ALLOW_EMPTY_kernel-image-' + typelower, '1')

        imagedest = d.getVar('KERNEL_IMAGEDEST', True)
        priority = d.getVar('KERNEL_PRIORITY', True)
        postinst = '#!/bin/sh\n' + 'update-alternatives --install /' + imagedest + '/' + type + ' ' + type + ' ' + '/' + imagedest + '/' + type + '-${KERNEL_VERSION_NAME} ' + priority + ' || true' + '\n'
        d.setVar('pkg_postinst_kernel-image-' + typelower, postinst)

        postrm = '#!/bin/sh\n' + 'update-alternatives --remove' + ' ' + type + ' ' + type + '-${KERNEL_VERSION_NAME} || true' + '\n'
        d.setVar('pkg_postrm_kernel-image-' + typelower, postrm)

    image = d.getVar('INITRAMFS_IMAGE', True)
    if image:
        d.appendVarFlag('do_bundle_initramfs', 'depends', ' ${INITRAMFS_IMAGE}:do_image_complete')

    # NOTE: setting INITRAMFS_TASK is for backward compatibility
    #       The preferred method is to set INITRAMFS_IMAGE, because
    #       this INITRAMFS_TASK has circular dependency problems
    #       if the initramfs requires kernel modules
    image_task = d.getVar('INITRAMFS_TASK', True)
    if image_task:
        d.appendVarFlag('do_configure', 'depends', ' ${INITRAMFS_TASK}')
}

# Here we pull in all various kernel image types which we support.
#
# In case you're wondering why kernel.bbclass inherits the other image
# types instead of the other way around, the reason for that is to
# maintain compatibility with various currently existing meta-layers.
# By pulling in the various kernel image types here, we retain the
# original behavior of kernel.bbclass, so no meta-layers should get
# broken.
#
# KERNEL_CLASSES by default pulls in kernel-uimage.bbclass, since this
# used to be the default behavior when only uImage was supported. This
# variable can be appended by users who implement support for new kernel
# image types.

KERNEL_CLASSES ?= " kernel-uimage "
inherit ${KERNEL_CLASSES}

# Old style kernels may set ${S} = ${WORKDIR}/git for example
# We need to move these over to STAGING_KERNEL_DIR. We can't just
# create the symlink in advance as the git fetcher can't cope with
# the symlink.
do_unpack[cleandirs] += " ${S} ${STAGING_KERNEL_DIR} ${B} ${STAGING_KERNEL_BUILDDIR}"
do_clean[cleandirs] += " ${S} ${STAGING_KERNEL_DIR} ${B} ${STAGING_KERNEL_BUILDDIR}"
base_do_unpack_append () {
    s = d.getVar("S", True)
    if s[-1] == '/':
        # drop trailing slash, so that os.symlink(kernsrc, s) doesn't use s as directory name and fail
        s=s[:-1]
    kernsrc = d.getVar("STAGING_KERNEL_DIR", True)
    if s != kernsrc:
        bb.utils.mkdirhier(kernsrc)
        bb.utils.remove(kernsrc, recurse=True)
        if d.getVar("EXTERNALSRC", True):
            # With EXTERNALSRC S will not be wiped so we can symlink to it
            os.symlink(s, kernsrc)
        else:
            import shutil
            shutil.move(s, kernsrc)
            os.symlink(kernsrc, s)
}

inherit kernel-arch deploy

PACKAGES_DYNAMIC += "^kernel-module-.*"
PACKAGES_DYNAMIC += "^kernel-image-.*"
PACKAGES_DYNAMIC += "^kernel-firmware-.*"

export OS = "${TARGET_OS}"
export CROSS_COMPILE = "${TARGET_PREFIX}"

KERNEL_PRIORITY ?= "${@int(d.getVar('PV',1).split('-')[0].split('+')[0].split('.')[0]) * 10000 + \
                       int(d.getVar('PV',1).split('-')[0].split('+')[0].split('.')[1]) * 100 + \
                       int(d.getVar('PV',1).split('-')[0].split('+')[0].split('.')[-1])}"

KERNEL_RELEASE ?= "${KERNEL_VERSION}"

# The directory where built kernel lies in the kernel tree
KERNEL_OUTPUT_DIR ?= "arch/${ARCH}/boot"
KERNEL_IMAGEDEST = "boot"

#
# configuration
#
export CMDLINE_CONSOLE = "console=${@d.getVar("KERNEL_CONSOLE",1) or "ttyS0"}"

KERNEL_VERSION = "${@get_kernelversion_headers('${B}')}"

KERNEL_LOCALVERSION ?= ""

# kernels are generally machine specific
PACKAGE_ARCH = "${MACHINE_ARCH}"

# U-Boot support
UBOOT_ENTRYPOINT ?= "20008000"
UBOOT_LOADADDRESS ?= "${UBOOT_ENTRYPOINT}"

# Some Linux kernel configurations need additional parameters on the command line
KERNEL_EXTRA_ARGS ?= ""

# For the kernel, we don't want the '-e MAKEFLAGS=' in EXTRA_OEMAKE.
# We don't want to override kernel Makefile variables from the environment
EXTRA_OEMAKE = ""

KERNEL_ALT_IMAGETYPE ??= ""

copy_initramfs() {
	echo "Copying initramfs into ./usr ..."
	# In case the directory is not created yet from the first pass compile:
	mkdir -p ${B}/usr
	# Find and use the first initramfs image archive type we find
	rm -f ${B}/usr/${INITRAMFS_IMAGE}-${MACHINE}.cpio
	for img in cpio.gz cpio.lz4 cpio.lzo cpio.lzma cpio.xz; do
		if [ -e "${DEPLOY_DIR_IMAGE}/${INITRAMFS_IMAGE}-${MACHINE}.$img" ]; then
			cp ${DEPLOY_DIR_IMAGE}/${INITRAMFS_IMAGE}-${MACHINE}.$img ${B}/usr/.
			case $img in
			*gz)
				echo "gzip decompressing image"
				gunzip -f ${B}/usr/${INITRAMFS_IMAGE}-${MACHINE}.$img
				break
				;;
			*lz4)
				echo "lz4 decompressing image"
				lz4 -df ${B}/usr/${INITRAMFS_IMAGE}-${MACHINE}.$img
				break
				;;
			*lzo)
				echo "lzo decompressing image"
				lzop -df ${B}/usr/${INITRAMFS_IMAGE}-${MACHINE}.$img
				break
				;;
			*lzma)
				echo "lzma decompressing image"
				lzma -df ${B}/usr/${INITRAMFS_IMAGE}-${MACHINE}.$img
				break
				;;
			*xz)
				echo "xz decompressing image"
				xz -df ${B}/usr/${INITRAMFS_IMAGE}-${MACHINE}.$img
				break
				;;
			esac
		fi
	done
	echo "Finished copy of initramfs into ./usr"
}

INITRAMFS_BASE_NAME = "initramfs-${PV}-${PR}-${MACHINE}-${DATETIME}"
INITRAMFS_BASE_NAME[vardepsexclude] = "DATETIME"
do_bundle_initramfs () {
	if [ ! -z "${INITRAMFS_IMAGE}" -a x"${INITRAMFS_IMAGE_BUNDLE}" = x1 ]; then
		echo "Creating a kernel image with a bundled initramfs..."
		copy_initramfs
		# Backing up kernel image relies on its type(regular file or symbolic link)
		tmp_path=""
		for type in ${KERNEL_IMAGETYPES} ; do
			if [ -h ${KERNEL_OUTPUT_DIR}/$type ] ; then
				linkpath=`readlink -n ${KERNEL_OUTPUT_DIR}/$type`
				realpath=`readlink -fn ${KERNEL_OUTPUT_DIR}/$type`
				mv -f $realpath $realpath.bak
				tmp_path=$tmp_path" "$type"#"$linkpath"#"$realpath
			elif [ -f ${KERNEL_OUTPUT_DIR}/$type ]; then
				mv -f ${KERNEL_OUTPUT_DIR}/$type ${KERNEL_OUTPUT_DIR}/$type.bak
				tmp_path=$tmp_path" "$type"##"
			fi
		done
		use_alternate_initrd=CONFIG_INITRAMFS_SOURCE=${B}/usr/${INITRAMFS_IMAGE}-${MACHINE}.cpio
		kernel_do_compile
		# Restoring kernel image
		for tp in $tmp_path ; do
			type=`echo $tp|cut -d "#" -f 1`
			linkpath=`echo $tp|cut -d "#" -f 2`
			realpath=`echo $tp|cut -d "#" -f 3`
			if [ -n "$realpath" ]; then
				mv -f $realpath $realpath.initramfs
				mv -f $realpath.bak $realpath
				cd ${B}/${KERNEL_OUTPUT_DIR}
				ln -sf $linkpath.initramfs
			else
				mv -f ${KERNEL_OUTPUT_DIR}/$type ${KERNEL_OUTPUT_DIR}/$type.initramfs
				mv -f ${KERNEL_OUTPUT_DIR}/$type.bak ${KERNEL_OUTPUT_DIR}/$type
			fi
		done
		# Update install area
		for type in ${KERNEL_IMAGETYPES} ; do
			echo "There is kernel image bundled with initramfs: ${B}/${KERNEL_OUTPUT_DIR}/$type.initramfs"
			install -m 0644 ${B}/${KERNEL_OUTPUT_DIR}/$type.initramfs ${D}/boot/$type-initramfs-${MACHINE}.bin
			echo "${B}/${KERNEL_OUTPUT_DIR}/$type.initramfs"
		done
	fi
}

python do_devshell_prepend () {
    os.environ["LDFLAGS"] = ''
}

addtask bundle_initramfs after do_install before do_deploy

kernel_do_compile() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS MACHINE
	# The $use_alternate_initrd is only set from
	# do_bundle_initramfs() This variable is specifically for the
	# case where we are making a second pass at the kernel
	# compilation and we want to force the kernel build to use a
	# different initramfs image.  The way to do that in the kernel
	# is to specify:
	# make ...args... CONFIG_INITRAMFS_SOURCE=some_other_initramfs.cpio
	if [ "$use_alternate_initrd" = "" ] && [ "${INITRAMFS_TASK}" != "" ] ; then
		# The old style way of copying an prebuilt image and building it
		# is turned on via INTIRAMFS_TASK != ""
		copy_initramfs
		use_alternate_initrd=CONFIG_INITRAMFS_SOURCE=${B}/usr/${INITRAMFS_IMAGE}-${MACHINE}.cpio
	fi
	for typeformake in ${KERNEL_IMAGETYPE_FOR_MAKE} ; do
		oe_runmake ${typeformake} CC="${KERNEL_CC}" LD="${KERNEL_LD}" ${KERNEL_EXTRA_ARGS} $use_alternate_initrd
		for type in ${KERNEL_IMAGETYPES} ; do
			if test "${typeformake}.gz" = "${type}"; then
				gzip -9c < "${typeformake}" > "${KERNEL_OUTPUT_DIR}/${type}"
				break;
			fi
		done
	done
}

do_compile_kernelmodules() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS MACHINE
	if (grep -q -i -e '^CONFIG_MODULES=y$' ${B}/.config); then
		oe_runmake -C ${B} ${PARALLEL_MAKE} modules CC="${KERNEL_CC}" LD="${KERNEL_LD}" ${KERNEL_EXTRA_ARGS}

		# Module.symvers gets updated during the 
		# building of the kernel modules. We need to
		# update this in the shared workdir since some
		# external kernel modules has a dependency on
		# other kernel modules and will look at this
		# file to do symbol lookups
		cp Module.symvers ${STAGING_KERNEL_BUILDDIR}/
	else
		bbnote "no modules to compile"
	fi
}
addtask compile_kernelmodules after do_compile before do_strip

kernel_do_install() {
	#
	# First install the modules
	#
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS MACHINE
	if (grep -q -i -e '^CONFIG_MODULES=y$' .config); then
		oe_runmake DEPMOD=echo INSTALL_MOD_PATH="${D}" modules_install
		rm "${D}/lib/modules/${KERNEL_VERSION}/build"
		rm "${D}/lib/modules/${KERNEL_VERSION}/source"
		# If the kernel/ directory is empty remove it to prevent QA issues
		rmdir --ignore-fail-on-non-empty "${D}/lib/modules/${KERNEL_VERSION}/kernel"
	else
		bbnote "no modules to install"
	fi

	#
	# Install various kernel output (zImage, map file, config, module support files)
	#
	install -d ${D}/${KERNEL_IMAGEDEST}
	install -d ${D}/boot
	for type in ${KERNEL_IMAGETYPES} ; do
		install -m 0644 ${KERNEL_OUTPUT_DIR}/${type} ${D}/${KERNEL_IMAGEDEST}/${type}-${KERNEL_VERSION}
	done
	install -m 0644 System.map ${D}/boot/System.map-${KERNEL_VERSION}
	install -m 0644 .config ${D}/boot/config-${KERNEL_VERSION}
	install -m 0644 vmlinux ${D}/boot/vmlinux-${KERNEL_VERSION}
	[ -e Module.symvers ] && install -m 0644 Module.symvers ${D}/boot/Module.symvers-${KERNEL_VERSION}
	install -d ${D}${sysconfdir}/modules-load.d
	install -d ${D}${sysconfdir}/modprobe.d
}
do_install[prefuncs] += "package_get_auto_pr"

addtask shared_workdir after do_compile before do_compile_kernelmodules
addtask shared_workdir_setscene

do_shared_workdir_setscene () {
	exit 1
}

emit_depmod_pkgdata() {
	# Stash data for depmod
	install -d ${PKGDESTWORK}/kernel-depmod/
	echo "${KERNEL_VERSION}" > ${PKGDESTWORK}/kernel-depmod/kernel-abiversion
	cp ${B}/System.map ${PKGDESTWORK}/kernel-depmod/System.map-${KERNEL_VERSION}
}

PACKAGEFUNCS += "emit_depmod_pkgdata"

do_shared_workdir () {
	cd ${B}

	kerneldir=${STAGING_KERNEL_BUILDDIR}
	install -d $kerneldir

	#
	# Store the kernel version in sysroots for module-base.bbclass
	#

	echo "${KERNEL_VERSION}" > $kerneldir/kernel-abiversion

	# Copy files required for module builds
	cp System.map $kerneldir/System.map-${KERNEL_VERSION}
	cp Module.symvers $kerneldir/
	cp .config $kerneldir/
	mkdir -p $kerneldir/include/config
	cp include/config/kernel.release $kerneldir/include/config/kernel.release

	# We can also copy over all the generated files and avoid special cases
	# like version.h, but we've opted to keep this small until file creep starts
	# to happen
	if [ -e include/linux/version.h ]; then
		mkdir -p $kerneldir/include/linux
		cp include/linux/version.h $kerneldir/include/linux/version.h
	fi

	# As of Linux kernel version 3.0.1, the clean target removes
	# arch/powerpc/lib/crtsavres.o which is present in
	# KBUILD_LDFLAGS_MODULE, making it required to build external modules.
	if [ ${ARCH} = "powerpc" ]; then
		mkdir -p $kerneldir/arch/powerpc/lib/
		cp arch/powerpc/lib/crtsavres.o $kerneldir/arch/powerpc/lib/crtsavres.o
	fi

	if [ -d include/generated ]; then
		mkdir -p $kerneldir/include/generated/
		cp -fR include/generated/* $kerneldir/include/generated/
	fi

	if [ -d arch/${ARCH}/include/generated ]; then
		mkdir -p $kerneldir/arch/${ARCH}/include/generated/
		cp -fR arch/${ARCH}/include/generated/* $kerneldir/arch/${ARCH}/include/generated/
	fi
}

# We don't need to stage anything, not the modules/firmware since those would clash with linux-firmware
sysroot_stage_all () {
	:
}

KERNEL_CONFIG_COMMAND ?= "oe_runmake_call -C ${S} O=${B} oldnoconfig || yes '' | oe_runmake -C ${S} O=${B} oldconfig"

python check_oldest_kernel() {
    oldest_kernel = d.getVar('OLDEST_KERNEL', True)
    kernel_version = d.getVar('KERNEL_VERSION', True)
    tclibc = d.getVar('TCLIBC', True)
    if tclibc == 'glibc':
        kernel_version = kernel_version.split('-', 1)[0]
        if oldest_kernel and kernel_version:
            if bb.utils.vercmp_string(kernel_version, oldest_kernel) < 0:
                bb.warn('%s: OLDEST_KERNEL is "%s" but the version of the kernel you are building is "%s" - therefore %s as built may not be compatible with this kernel. Either set OLDEST_KERNEL to an older version, or build a newer kernel.' % (d.getVar('PN', True), oldest_kernel, kernel_version, tclibc))
}

check_oldest_kernel[vardepsexclude] += "OLDEST_KERNEL KERNEL_VERSION"
do_configure[prefuncs] += "check_oldest_kernel"

kernel_do_configure() {
	# fixes extra + in /lib/modules/2.6.37+
	# $ scripts/setlocalversion . => +
	# $ make kernelversion => 2.6.37
	# $ make kernelrelease => 2.6.37+
	touch ${B}/.scmversion ${S}/.scmversion

	if [ "${S}" != "${B}" ] && [ -f "${S}/.config" ] && [ ! -f "${B}/.config" ]; then
		mv "${S}/.config" "${B}/.config"
	fi

	# Copy defconfig to .config if .config does not exist. This allows
	# recipes to manage the .config themselves in do_configure_prepend().
	if [ -f "${WORKDIR}/defconfig" ] && [ ! -f "${B}/.config" ]; then
		cp "${WORKDIR}/defconfig" "${B}/.config"
	fi

	${KERNEL_CONFIG_COMMAND}
}

do_savedefconfig() {
	oe_runmake -C ${B} savedefconfig
}
do_savedefconfig[nostamp] = "1"
addtask savedefconfig after do_configure

inherit cml1

EXPORT_FUNCTIONS do_compile do_install do_configure

# kernel-base becomes kernel-${KERNEL_VERSION}
# kernel-image becomes kernel-image-${KERNEL_VERSION}
PACKAGES = "kernel kernel-base kernel-vmlinux kernel-image kernel-dev kernel-modules"
FILES_${PN} = ""
FILES_kernel-base = "/lib/modules/${KERNEL_VERSION}/modules.order /lib/modules/${KERNEL_VERSION}/modules.builtin"
FILES_kernel-image = ""
FILES_kernel-dev = "/boot/System.map* /boot/Module.symvers* /boot/config* ${KERNEL_SRC_PATH} /lib/modules/${KERNEL_VERSION}/build"
FILES_kernel-vmlinux = "/boot/vmlinux*"
FILES_kernel-modules = ""
RDEPENDS_kernel = "kernel-base"
# Allow machines to override this dependency if kernel image files are
# not wanted in images as standard
RDEPENDS_kernel-base ?= "kernel-image"
PKG_kernel-image = "kernel-image-${@legitimize_package_name('${KERNEL_VERSION}')}"
RDEPENDS_kernel-image += "${@base_conditional('KERNEL_IMAGETYPE', 'vmlinux', 'kernel-vmlinux', '', d)}"
PKG_kernel-base = "kernel-${@legitimize_package_name('${KERNEL_VERSION}')}"
RPROVIDES_kernel-base += "kernel-${KERNEL_VERSION}"
ALLOW_EMPTY_kernel = "1"
ALLOW_EMPTY_kernel-base = "1"
ALLOW_EMPTY_kernel-image = "1"
ALLOW_EMPTY_kernel-modules = "1"
DESCRIPTION_kernel-modules = "Kernel modules meta package"

pkg_postinst_kernel-base () {
	if [ ! -e "$D/lib/modules/${KERNEL_VERSION}" ]; then
		mkdir -p $D/lib/modules/${KERNEL_VERSION}
	fi
	if [ -n "$D" ]; then
		depmodwrapper -a -b $D ${KERNEL_VERSION}
	else
		depmod -a ${KERNEL_VERSION}
	fi
}

PACKAGESPLITFUNCS_prepend = "split_kernel_packages "

python split_kernel_packages () {
    do_split_packages(d, root='/lib/firmware', file_regex='^(.*)\.(bin|fw|cis|csp|dsp)$', output_pattern='kernel-firmware-%s', description='Firmware for %s', recursive=True, extra_depends='')
}

# Many scripts want to look in arch/$arch/boot for the bootable
# image. This poses a problem for vmlinux based booting. This 
# task arranges to have vmlinux appear in the normalized directory
# location.
do_kernel_link_vmlinux() {
	if [ ! -d "${B}/arch/${ARCH}/boot" ]; then
		mkdir ${B}/arch/${ARCH}/boot
	fi
	cd ${B}/arch/${ARCH}/boot
	ln -sf ../../../vmlinux
}

do_strip() {
	if [ -n "${KERNEL_IMAGE_STRIP_EXTRA_SECTIONS}" ]; then
		if ! (echo "${KERNEL_IMAGETYPES}" | grep -wq "vmlinux"); then
			bbwarn "image type(s) will not be stripped (not supported): ${KERNEL_IMAGETYPES}"
			return
		fi

		cd ${B}
		headers=`"$CROSS_COMPILE"readelf -S ${KERNEL_OUTPUT_DIR}/vmlinux | \
			  grep "^ \{1,\}\[[0-9 ]\{1,\}\] [^ ]" | \
			  sed "s/^ \{1,\}\[[0-9 ]\{1,\}\] //" | \
			  gawk '{print $1}'`

		for str in ${KERNEL_IMAGE_STRIP_EXTRA_SECTIONS}; do {
			if ! (echo "$headers" | grep -q "^$str$"); then
				bbwarn "Section not found: $str";
			fi

			"$CROSS_COMPILE"strip -s -R $str ${KERNEL_OUTPUT_DIR}/vmlinux
		}; done

		bbnote "KERNEL_IMAGE_STRIP_EXTRA_SECTIONS is set, stripping sections:" \
			"${KERNEL_IMAGE_STRIP_EXTRA_SECTIONS}"
	fi;
}
do_strip[dirs] = "${B}"

addtask do_strip before do_sizecheck after do_kernel_link_vmlinux

# Support checking the kernel size since some kernels need to reside in partitions
# with a fixed length or there is a limit in transferring the kernel to memory
do_sizecheck() {
	if [ ! -z "${KERNEL_IMAGE_MAXSIZE}" ]; then
		invalid=`echo ${KERNEL_IMAGE_MAXSIZE} | sed 's/[0-9]//g'`
		if [ -n "$invalid" ]; then
			die "Invalid KERNEL_IMAGE_MAXSIZE: ${KERNEL_IMAGE_MAXSIZE}, should be an integerx (The unit is Kbytes)"
		fi
		for type in ${KERNEL_IMAGETYPES} ; do
			size=`du -ks ${B}/${KERNEL_OUTPUT_DIR}/$type | awk '{print $1}'`
			if [ $size -ge ${KERNEL_IMAGE_MAXSIZE} ]; then
				warn "This kernel $type (size=$size(K) > ${KERNEL_IMAGE_MAXSIZE}(K)) is too big for your device. Please reduce the size of the kernel by making more of it modular."
			fi
		done
	fi
}
do_sizecheck[dirs] = "${B}"

addtask sizecheck before do_install after do_strip

KERNEL_IMAGE_BASE_NAME ?= "${PKGE}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
# Don't include the DATETIME variable in the sstate package signatures
KERNEL_IMAGE_BASE_NAME[vardepsexclude] = "DATETIME"
KERNEL_IMAGE_SYMLINK_NAME ?= "${MACHINE}"
MODULE_IMAGE_BASE_NAME ?= "modules-${PKGE}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
MODULE_IMAGE_BASE_NAME[vardepsexclude] = "DATETIME"
MODULE_TARBALL_BASE_NAME ?= "${MODULE_IMAGE_BASE_NAME}.tgz"
# Don't include the DATETIME variable in the sstate package signatures
MODULE_TARBALL_SYMLINK_NAME ?= "modules-${MACHINE}.tgz"
MODULE_TARBALL_DEPLOY ?= "1"

kernel_do_deploy() {
	for type in ${KERNEL_IMAGETYPES} ; do
		base_name=${type}-${KERNEL_IMAGE_BASE_NAME}
		install -m 0644 ${KERNEL_OUTPUT_DIR}/${type} ${DEPLOYDIR}/${base_name}.bin
	done
	if [ ${MODULE_TARBALL_DEPLOY} = "1" ] && (grep -q -i -e '^CONFIG_MODULES=y$' .config); then
		mkdir -p ${D}/lib
		tar -cvzf ${DEPLOYDIR}/${MODULE_TARBALL_BASE_NAME} -C ${D} lib
		ln -sf ${MODULE_TARBALL_BASE_NAME} ${DEPLOYDIR}/${MODULE_TARBALL_SYMLINK_NAME}
	fi

	for type in ${KERNEL_IMAGETYPES} ; do
		base_name=${type}-${KERNEL_IMAGE_BASE_NAME}
		symlink_name=${type}-${KERNEL_IMAGE_SYMLINK_NAME}
		ln -sf ${base_name}.bin ${DEPLOYDIR}/${symlink_name}.bin
		ln -sf ${base_name}.bin ${DEPLOYDIR}/${type}
	done

	cp ${COREBASE}/meta/files/deploydir_readme.txt ${DEPLOYDIR}/README_-_DO_NOT_DELETE_FILES_IN_THIS_DIRECTORY.txt

	cd ${B}
	# Update deploy directory
	for type in ${KERNEL_IMAGETYPES} ; do
		if [ -e "${KERNEL_OUTPUT_DIR}/${type}.initramfs" ]; then
			echo "Copying deploy ${type} kernel-initramfs image and setting up links..."
			initramfs_base_name=${type}-${INITRAMFS_BASE_NAME}
			initramfs_symlink_name=${type}-initramfs-${MACHINE}
			install -m 0644 ${KERNEL_OUTPUT_DIR}/${type}.initramfs ${DEPLOYDIR}/${initramfs_base_name}.bin
			cd ${DEPLOYDIR}
			ln -sf ${initramfs_base_name}.bin ${initramfs_symlink_name}.bin
		fi
	done
}
do_deploy[cleandirs] = "${DEPLOYDIR}"
do_deploy[dirs] = "${DEPLOYDIR} ${B}"
do_deploy[prefuncs] += "package_get_auto_pr"

addtask deploy after do_populate_sysroot

EXPORT_FUNCTIONS do_deploy

