SECTION = "devel"
SUMMARY = "Linux Trace Toolkit KERNEL MODULE"
DESCRIPTION = "The lttng-modules 2.0 package contains the kernel tracer modules"
HOMEPAGE = "https://lttng.org/"
LICENSE = "LGPL-2.1-only & GPL-2.0-only & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8d0d9f08888046474772a5d745d89d6a"

inherit module

include lttng-platforms.inc

SRC_URI = "https://lttng.org/files/${BPN}/${BPN}-${PV}.tar.bz2 \
           "

# Use :append here so that the patch is applied also when using devupstream
SRC_URI:append = " file://0001-src-Kbuild-change-missing-CONFIG_TRACEPOINTS-to-warn.patch \
                   file://0001-Fix-sched_stat_runtime-changed-in-Linux-6.6.66.patch \
                "

SRC_URI[sha256sum] = "6694414a3701fcd6e05b3cb20cfecae3b46b3dfb9e5361b344851c1f8052e854"

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
