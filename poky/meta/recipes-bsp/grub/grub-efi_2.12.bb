require grub2.inc

require conf/image-uefi.conf

GRUBPLATFORM = "efi"

DEPENDS:append = " grub-native"
RDEPENDS:${PN} = "grub-common virtual-grub-bootconf"

SRC_URI += " \
           file://cfg \
          "

S = "${WORKDIR}/grub-${PV}"

# Determine the target arch for the grub modules
python __anonymous () {
    import re
    target = d.getVar('TARGET_ARCH')
    prefix = "" if d.getVar('EFI_PROVIDER') == "grub-efi" else "grub-efi-"
    if target == "x86_64":
        grubtarget = 'x86_64'
    elif re.match('i.86', target):
        grubtarget = 'i386'
    elif re.match('aarch64', target):
        grubtarget = 'arm64'
    elif re.match('arm', target):
        grubtarget = 'arm'
    elif re.match('riscv64', target):
        grubtarget = 'riscv64'
    elif re.match('riscv32', target):
        grubtarget = 'riscv32'
    else:
        raise bb.parse.SkipRecipe("grub-efi is incompatible with target %s" % target)
    grubimage = prefix + d.getVar("EFI_BOOT_IMAGE")
    d.setVar("GRUB_TARGET", grubtarget)
    d.setVar("GRUB_IMAGE", grubimage)
    prefix = "grub-efi-" if prefix == "" else ""
    d.setVar("GRUB_IMAGE_PREFIX", prefix)
}

inherit deploy

CACHED_CONFIGUREVARS += "ac_cv_path_HELP2MAN="
EXTRA_OECONF += "--enable-efiemu=no"

do_mkimage() {
	cd ${B}

	GRUB_MKIMAGE_MODULES="${GRUB_BUILDIN}"

	# If 'all' is included in GRUB_BUILDIN we will include all available grub2 modules
	if [ "${@ bb.utils.contains('GRUB_BUILDIN', 'all', 'True', 'False', d)}" = "True" ]; then
		bbdebug 1 "Including all available modules"
		# Get the list of all .mod files in grub-core build directory
		GRUB_MKIMAGE_MODULES=$(find ${B}/grub-core/ -type f -name "*.mod" -exec basename {} .mod \;)
	fi

	# Search for the grub.cfg on the local boot media by using the
	# built in cfg file provided via this recipe
	grub-mkimage -v -c ../cfg -p ${EFIDIR} -d ./grub-core/ \
	               -O ${GRUB_TARGET}-efi -o ./${GRUB_IMAGE_PREFIX}${GRUB_IMAGE} \
	               ${GRUB_MKIMAGE_MODULES}
}

addtask mkimage before do_install after do_compile

do_install() {
    oe_runmake 'DESTDIR=${D}' -C grub-core install

    # Remove build host references...
    find "${D}" -name modinfo.sh -type f -exec \
        sed -i \
        -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
        -e 's|${DEBUG_PREFIX_MAP}||g' \
        -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
        {} +

    install -d ${D}${EFI_FILES_PATH}
    install -m 644 ${B}/${GRUB_IMAGE_PREFIX}${GRUB_IMAGE} ${D}${EFI_FILES_PATH}/${GRUB_IMAGE}
}

# To include all available modules, add 'all' to GRUB_BUILDIN
GRUB_BUILDIN ?= "boot linux ext2 fat serial part_msdos part_gpt normal \
                 efi_gop iso9660 configfile search loadenv test"

# 'xen_boot' is a module valid only for aarch64
GRUB_BUILDIN:append:aarch64 = "${@bb.utils.contains('DISTRO_FEATURES', 'xen', ' xen_boot', '', d)}"

do_deploy() {
	install -m 644 ${B}/${GRUB_IMAGE_PREFIX}${GRUB_IMAGE} ${DEPLOYDIR}
}

addtask deploy after do_install before do_build

FILES:${PN} = "${libdir}/grub/${GRUB_TARGET}-efi \
               ${datadir}/grub \
               ${EFI_FILES_PATH}/${GRUB_IMAGE} \
               "

# 64-bit binaries are expected for the bootloader with an x32 userland
INSANE_SKIP:${PN}:append:linux-gnux32 = " arch"
INSANE_SKIP:${PN}-dbg:append:linux-gnux32 = " arch"
INSANE_SKIP:${PN}:append:linux-muslx32 = " arch"
INSANE_SKIP:${PN}-dbg:append:linux-muslx32 = " arch"
