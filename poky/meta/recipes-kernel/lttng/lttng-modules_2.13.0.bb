SECTION = "devel"
SUMMARY = "Linux Trace Toolkit KERNEL MODULE"
DESCRIPTION = "The lttng-modules 2.0 package contains the kernel tracer modules"
HOMEPAGE = "https://lttng.org/"
LICENSE = "LGPLv2.1 & GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0464cff101a009c403cd2ed65d01d4c4"

inherit module

include lttng-platforms.inc

SRC_URI = "https://lttng.org/files/${BPN}/${BPN}-${PV}.tar.bz2"
# Use :append here so that the patch is applied also when using devupstream
SRC_URI:append = " file://0001-src-Kbuild-change-missing-CONFIG_TRACEPOINTS-to-warn.patch"

SRC_URI[sha256sum] = "5ebf2b3cd128b3a1c8afaea1e98d5a6f7f0676fd524fcf72361c34d9dc603356"

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

SRCREV:class-devupstream = "f982b51a98a29cb4aaf607cb9bbf2b509d8e6933"
PV:class-devupstream = "2.13.0-rc2+git${SRCPV}"
S:class-devupstream = "${WORKDIR}/git"
SRCREV_FORMAT ?= "lttng_git"
