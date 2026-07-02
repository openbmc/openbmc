SECTION = "devel"
SUMMARY = "Linux Trace Toolkit KERNEL MODULE"
DESCRIPTION = "The lttng-modules 2.0 package contains the kernel tracer modules"
HOMEPAGE = "https://lttng.org/"
LICENSE = "LGPL-2.1-only & GPL-2.0-only & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=018e002dbdda3306682e394ddd65fa32"

inherit module

include lttng-platforms.inc

SRC_URI = "https://lttng.org/files/${BPN}/${BPN}-${PV}.tar.bz2 \
           "

# Use :append here so that the patch is applied also when using devupstream
SRC_URI:append = " file://0001-src-Kbuild-change-missing-CONFIG_TRACEPOINTS-to-warn.patch \
                   file://0001-fix-hrtimer-Reduce-trace-noise-in-hrtimer_start-v7.1.patch \
                   file://0001-fix-hrtimer-Drop-unnecessary-pointer-indirection-in.patch \
                   file://0001-fix-mm-vmscan-Convert-pageout-to-take-a-folio-v5.18.patch \
                   file://0001-fix-mm-vmscan-add-cgroup-IDs-to-vmscan-tracepoints-v7.1.patch \
                   file://0001-fix-vfs-widen-trace-event-i_ino-fields-to-u64-v7.1.patch \
                   file://0001-fix-treewide-change-inode-i_ino-from-unsigned-long-to-u64-v7.1.patch \
                   file://0001-fix-ext4-enhance-tracepoints-during-the-folios-writeback-v6.17.patch \
                   file://0001-fix-ext4-widen-trace-event-i_ino-fields-to-u64-v7.1.patch \
                "
SRC_URI[sha256sum] = "4eab35edeaa84ddefa243f2f842af1482325062ee008fb511a3ff191b9aa09ac"

export INSTALL_MOD_DIR = "kernel/lttng-modules"

EXTRA_OEMAKE += "KERNELDIR='${STAGING_KERNEL_DIR}'"

MODULES_MODULE_SYMVERS_LOCATION = "src"

do_install:append() {
	# Delete empty directories to avoid QA failures if no modules were built
	if [ -d ${D}/${nonarch_base_libdir} ]; then
		find ${D}/${nonarch_base_libdir} -depth -type d -empty -exec rmdir {} \;
	fi
}

python do_package:prepend() {
    if not os.path.exists(os.path.join(d.getVar('D'), d.getVar('nonarch_base_libdir')[1:], 'modules')):
        bb.warn("%s: no modules were created; this may be due to CONFIG_TRACEPOINTS not being enabled in your kernel." % d.getVar('PN'))
}

BBCLASSEXTEND = "devupstream:target"
SRC_URI:class-devupstream = "git://git.lttng.org/lttng-modules;branch=stable-2.13;protocol=https"
SRCREV:class-devupstream = "7584cfc04914cb0842a986e9808686858b9c8630"
SRCREV_FORMAT ?= "lttng_git"
