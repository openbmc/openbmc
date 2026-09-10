SUMMARY = "A meta recipe to build native tools used by wic."

LICENSE = "MIT"

DEPENDS = "\
           wic-native \
           cdrtools-native grub-native pseudo-native \
           "
DEPENDS:append:x86 = " syslinux-native"
DEPENDS:append:x86-64 = " syslinux-native"
DEPENDS:append:x86-x32 = " syslinux-native"

INHIBIT_DEFAULT_DEPS = "1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit nopackages

# The sysroot of wic-tools is needed for wic, but if rm_work is enabled, it will
# be removed before wic has a chance to use it, hence the exclusion below.
RM_WORK_EXCLUDE += "${PN}"

python do_build_sysroot () {
    bb.build.exec_func("extend_recipe_sysroot", d)
}
addtask do_build_sysroot after do_prepare_recipe_sysroot before do_build
do_build_sysroot[vardepsexclude] += "extend_recipe_sysroot"