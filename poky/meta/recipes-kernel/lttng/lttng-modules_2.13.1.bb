SECTION = "devel"
SUMMARY = "Linux Trace Toolkit KERNEL MODULE"
DESCRIPTION = "The lttng-modules 2.0 package contains the kernel tracer modules"
HOMEPAGE = "https://lttng.org/"
LICENSE = "LGPL-2.1-only & GPL-2.0-only & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0464cff101a009c403cd2ed65d01d4c4"

inherit module

include lttng-platforms.inc

SRC_URI = "https://lttng.org/files/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://0001-fix-block-remove-GENHD_FL_SUPPRESS_PARTITION_INFO-v5.patch \
           file://0002-fix-block-remove-the-rq_disk-field-in-struct-request.patch \
           file://0003-fix-mm-compaction-fix-the-migration-stats-in-trace_m.patch \
           file://0004-fix-btrfs-pass-fs_info-to-trace_btrfs_transaction_co.patch \
           file://0005-fix-random-rather-than-entropy_store-abstraction-use.patch \
           file://0006-fix-net-skb-introduce-kfree_skb_reason-v5.17.patch \
           file://0007-fix-net-socket-rename-SKB_DROP_REASON_SOCKET_FILTER-.patch \
          "
# Use :append here so that the patch is applied also when using devupstream
SRC_URI:append = " file://0001-src-Kbuild-change-missing-CONFIG_TRACEPOINTS-to-warn.patch"

SRC_URI[sha256sum] = "a7c86d91c9bbe66d27f025aa04b8cfc6d7785ed2fc7ef774930800ee44d7f343"

export INSTALL_MOD_DIR="kernel/lttng-modules"

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
LIC_FILES_CHKSUM:class-devupstream = "file://LICENSE;md5=0464cff101a009c403cd2ed65d01d4c4"
DEFAULT_PREFERENCE:class-devupstream = "-1"
SRC_URI:class-devupstream = "git://git.lttng.org/lttng-modules;branch=stable-2.13"

SRCREV:class-devupstream = "7584cfc04914cb0842a986e9808686858b9c8630"
PV:class-devupstream = "2.13.1+git${SRCPV}"
S:class-devupstream = "${WORKDIR}/git"
SRCREV_FORMAT ?= "lttng_git"
