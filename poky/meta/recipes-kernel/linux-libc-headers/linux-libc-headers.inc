SUMMARY = "Sanitized set of kernel headers for the C library's use"
SECTION = "devel"
LICENSE = "GPLv2"

#########################################################################
####                        PLEASE READ 
#########################################################################
#
# You're probably looking here thinking you need to create some new copy
# of linux-libc-headers since you have your own custom kernel. To put 
# this simply, you DO NOT.
#
# Why? These headers are used to build the libc. If you customise the 
# headers you are customising the libc and the libc becomes machine
# specific. Most people do not add custom libc extensions to the kernel
# and have a machine specific libc.
#
# But you have some kernel headers you need for some driver? That is fine
# but get them from STAGING_KERNEL_DIR where the kernel installs itself.
# This will make the package using them machine specific but this is much
# better than having a machine specific C library. This does mean your
# recipe needs a
#    do_configure[depends] += "virtual/kernel:do_shared_workdir"
# but again, that is fine and makes total sense.
#
# There can also be a case where your kernel extremely old and you want
# an older libc ABI for that old kernel. The headers installed by this
# recipe should still be a standard mainline kernel, not your own custom 
# one.
#
# -- RP

LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

python __anonymous () {
    major = d.getVar("PV").split('.')[0]
    if major == "3":
        d.setVar("HEADER_FETCH_VER", "3.0")
    elif major == "4":
        d.setVar("HEADER_FETCH_VER", "4.x")
    else:
        d.setVar("HEADER_FETCH_VER", "2.6")
}

inherit kernel-arch pkgconfig multilib_header

KORG_ARCHIVE_COMPRESSION ?= "xz"

SRC_URI = "${KERNELORG_MIRROR}/linux/kernel/v${HEADER_FETCH_VER}/linux-${PV}.tar.${KORG_ARCHIVE_COMPRESSION}"
UPSTREAM_CHECK_URI = "https://www.kernel.org/"

S = "${WORKDIR}/linux-${PV}"

EXTRA_OEMAKE = " HOSTCC="${BUILD_CC}" HOSTCPP="${BUILD_CPP}""

do_configure() {
	oe_runmake allnoconfig
}

do_compile () {
}

do_install() {
	oe_runmake headers_install INSTALL_HDR_PATH=${D}${exec_prefix}
	# Kernel should not be exporting this header
	rm -f ${D}${exec_prefix}/include/scsi/scsi.h

	# The ..install.cmd conflicts between various configure runs
	find ${D}${includedir} -name ..install.cmd | xargs rm -f
}

do_install_append_aarch64 () {
        do_install_armmultilib
}

do_install_append_arm () {
	do_install_armmultilib
}

do_install_append_armeb () {
	do_install_armmultilib
}

do_install_armmultilib () {
	oe_multilib_header asm/auxvec.h asm/bitsperlong.h asm/byteorder.h asm/fcntl.h asm/hwcap.h asm/ioctls.h asm/kvm.h asm/kvm_para.h asm/mman.h asm/param.h asm/perf_regs.h asm/bpf_perf_event.h
	oe_multilib_header asm/posix_types.h asm/ptrace.h  asm/setup.h  asm/sigcontext.h asm/siginfo.h asm/signal.h asm/stat.h  asm/statfs.h asm/swab.h  asm/types.h asm/unistd.h
}

BBCLASSEXTEND = "nativesdk"

RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS += "unifdef-native"
