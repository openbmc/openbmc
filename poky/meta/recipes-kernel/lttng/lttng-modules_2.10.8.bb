SECTION = "devel"
SUMMARY = "Linux Trace Toolkit KERNEL MODULE"
DESCRIPTION = "The lttng-modules 2.0 package contains the kernel tracer modules"
LICENSE = "LGPLv2.1 & GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c4613d1f8a9587bd7b366191830364b3 \
                    file://gpl-2.0.txt;md5=751419260aa954499f7abaabaa882bbe \
                    file://lgpl-2.1.txt;md5=243b725d71bb5df4a1e5920b344b86ad \
                    "

inherit module

COMPATIBLE_HOST = '(x86_64|i.86|powerpc|aarch64|mips|nios2|arm|riscv).*-linux'

#https://lttng.org/files/lttng-modules/lttng-modules-2.10.7.tar.bz2
SRC_URI = "https://lttng.org/files/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://Makefile-Do-not-fail-if-CONFIG_TRACEPOINTS-is-not-en.patch \
           file://BUILD_RUNTIME_BUG_ON-vs-gcc7.patch \
           file://0001-Fix-signal-Distinguish-between-kernel_siginfo-and-si.patch \
           file://0002-Fix-signal-Remove-SEND_SIG_FORCED-v4.20.patch \
           file://0003-Fix-ext4-adjust-reserved-cluster-count-when-removing.patch \
           file://0004-Fix-timer-instrumentation-for-RHEL-7.6.patch \
           file://0005-Fix-Remove-type-argument-from-access_ok-function-v5..patch \
           file://0006-Fix-Replace-pointer-values-with-task-tk_pid-and-rpc_.patch \
           file://0007-Fix-SUNRPC-Simplify-defining-common-RPC-trace-events.patch \
           file://0008-Fix-btrfs-Remove-fsid-metadata_fsid-fields-from-btrf.patch \
           file://0009-Cleanup-tp-mempool-Remove-logically-dead-code.patch \
           "

SRC_URI[md5sum] = "54bd9fca61487bbec1b3fca2f2213c98"
SRC_URI[sha256sum] = "fe1d269bca723e8948af871c322c37d3900e647cdc5eb3efbe821e434beee44c"

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

