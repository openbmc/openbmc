SECTION = "devel"
SUMMARY = "Linux Trace Toolkit KERNEL MODULE"
DESCRIPTION = "The lttng-modules 2.0 package contains the kernel tracer modules"
HOMEPAGE = "https://lttng.org/"
LICENSE = "LGPLv2.1 & GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3f882d431dc0f32f1f44c0707aa41128"

inherit module

include lttng-platforms.inc

SRC_URI = "https://lttng.org/files/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://Makefile-Do-not-fail-if-CONFIG_TRACEPOINTS-is-not-en.patch \
           "

SRC_URI[sha256sum] = "95ac2a2cf92d85d23ffbdaca6a1ec0d7c167211d1e0fb850ab90004a3f475eaa"

export INSTALL_MOD_DIR="kernel/lttng-modules"

EXTRA_OEMAKE += "KERNELDIR='${STAGING_KERNEL_DIR}'"

do_install_append() {
	# Delete empty directories to avoid QA failures if no modules were built
	find ${D}/${nonarch_base_libdir} -depth -type d -empty -exec rmdir {} \;
}

python do_package_prepend() {
    if not os.path.exists(os.path.join(d.getVar('D'), d.getVar('nonarch_base_libdir')[1:], 'modules')):
        bb.warn("%s: no modules were created; this may be due to CONFIG_TRACEPOINTS not being enabled in your kernel." % d.getVar('PN'))
}

BBCLASSEXTEND = "devupstream:target"
LIC_FILES_CHKSUM_class-devupstream = "file://LICENSE;md5=0464cff101a009c403cd2ed65d01d4c4"
DEFAULT_PREFERENCE_class-devupstream = "-1"
SRC_URI_class-devupstream = "git://git.lttng.org/lttng-modules;branch=stable-2.13 \
           file://0001-src-Kbuild-change-missing-CONFIG_TRACEPOINTS-to-warn.patch \
           "

SRCREV_class-devupstream = "f982b51a98a29cb4aaf607cb9bbf2b509d8e6933"
PV_class-devupstream = "2.13.0-rc2+git${SRCPV}"
S_class-devupstream = "${WORKDIR}/git"
SRCREV_FORMAT ?= "lttng_git"
