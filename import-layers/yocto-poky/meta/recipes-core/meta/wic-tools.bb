SUMMARY = "A meta recipe to build native tools used by wic."

LICENSE = "MIT"

DEPENDS = "\
           parted-native syslinux-native gptfdisk-native dosfstools-native \
           mtools-native bmap-tools-native grub-efi-native cdrtools-native \
           btrfs-tools-native squashfs-tools-native \
           "
DEPENDS_append_x86 = " syslinux grub-efi systemd-boot"
DEPENDS_append_x86-64 = " syslinux grub-efi systemd-boot"

INHIBIT_DEFAULT_DEPS = "1"
inherit nopackages

# The sysroot of wic-tools is needed for wic, but if rm_work is enabled, it will
# be removed before wic has a chance to use it, hence the exclusion below.
RM_WORK_EXCLUDE += "${PN}"

python do_build_sysroot () {
    bb.build.exec_func("extend_recipe_sysroot", d)

    # Write environment variables used by wic
    # to tmp/sysroots/<machine>/imgdata/wictools.env
    outdir = os.path.join(d.getVar('STAGING_DIR'), d.getVar('MACHINE'), 'imgdata')
    bb.utils.mkdirhier(outdir)
    with open(os.path.join(outdir, "wic-tools.env"), 'w') as envf:
        for var in ('RECIPE_SYSROOT_NATIVE', 'STAGING_DATADIR', 'STAGING_LIBDIR'):
            envf.write('%s="%s"\n' % (var, d.getVar(var).strip()))

}
addtask do_build_sysroot after do_prepare_recipe_sysroot before do_build
