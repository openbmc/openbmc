DESCRIPTION = "To make access to the Linux kernel cpufreq subsystem easier for users and cpufreq userspace tools, a cpufrequtils package was created"

inherit_defer ${@bb.utils.contains('PACKAGECONFIG', 'nls', 'gettext', '', d)}

DEPENDS = "libtool-cross"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRCREV = "a2f0c39d5f21596bb9f5223e895c0ff210b265d0"
PV .= "+git"

SRC_URI = "git://git.kernel.org/pub/scm/utils/kernel/cpufreq/cpufrequtils.git;branch=master;protocol=https"

# Upstream repo does not tag
UPSTREAM_CHECK_COMMITS = "1"

TARGET_CC_ARCH += "${LDFLAGS}"

PACKAGECONFIG ??= "${@oe.utils.conditional('USE_NLS', 'no', '', 'nls', d)}"
PACKAGECONFIG[nls] = "NLS=true,NLS=false,gettext-native"

EXTRA_OEMAKE = "V=1 CROSS=${TARGET_PREFIX} CC='${CC}' STRIPCMD=echo 'CP=cp' ${PACKAGECONFIG_CONFARGS}"

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake install DESTDIR=${D}
	rm -f ${D}${libdir}/libcpufreq.so.0 ${D}${libdir}/libcpufreq.so
	ln -s libcpufreq.so.0.0.0 ${D}${libdir}/libcpufreq.so.0
	ln -s libcpufreq.so.0.0.0 ${D}${libdir}/libcpufreq.so
}

