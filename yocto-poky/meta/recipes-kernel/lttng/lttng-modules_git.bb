SECTION = "devel"
SUMMARY = "Linux Trace Toolkit KERNEL MODULE"
DESCRIPTION = "The lttng-modules 2.0 package contains the kernel tracer modules"
LICENSE = "LGPLv2.1 & GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1412caf5a1aa90d6a48588a4794c0eac \
                    file://gpl-2.0.txt;md5=751419260aa954499f7abaabaa882bbe \
                    file://lgpl-2.1.txt;md5=243b725d71bb5df4a1e5920b344b86ad"

inherit module

SRCREV = "7a88f8b50696dd71e80c08661159caf8e119bf51"
PV = "2.6.2+git${SRCPV}"

COMPATIBLE_HOST = '(x86_64|i.86|powerpc|aarch64|mips|arm).*-linux'

SRC_URI = "git://git.lttng.org/lttng-modules.git;branch=stable-2.6"

export INSTALL_MOD_DIR="kernel/lttng-modules"

EXTRA_OEMAKE += "KERNELDIR='${STAGING_KERNEL_DIR}'"

S = "${WORKDIR}/git"

do_install_append() {
	# Delete empty directories to avoid QA failures if no modules were built
	find ${D}/lib -depth -type d -empty -exec rmdir {} \;
}

python do_package_prepend() {
    if not os.path.exists(os.path.join(d.getVar('D', True), 'lib/modules')):
        bb.warn("%s: no modules were created; this may be due to CONFIG_TRACEPOINTS not being enabled in your kernel." % d.getVar('PN', True))
}

