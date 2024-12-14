DESCRIPTION = "To make access to the Linux kernel cpufreq subsystem easier for users and cpufreq userspace tools, a cpufrequtils package was created"

inherit gettext

DEPENDS = "libtool-cross"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRCREV = "a2f0c39d5f21596bb9f5223e895c0ff210b265d0"
# SRC_URI = "git://git.kernel.org/pub/scm/utils/kernel/cpufreq/cpufrequtils.git

SRC_URI = "git://github.com/emagii/cpufrequtils.git;branch=master;protocol=https \
           file://0001-dont-unset-cflags.patch \
"

# Upstream repo does not tag
UPSTREAM_CHECK_COMMITS = "1"

EXTRA_OEMAKE:append = " ${@['', 'NLS=false']['${USE_NLS}' == 'no']} "


S = "${WORKDIR}/git"

TARGET_CC_ARCH += "${LDFLAGS}"

EXTRA_OEMAKE = "V=1 CROSS=${TARGET_PREFIX} STRIPCMD=echo 'CP=cp'"

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake -e install DESTDIR=${D}
    rm -f ${D}${libdir}/libcpufreq.so.0 ${D}${libdir}/libcpufreq.so
    ln -s libcpufreq.so.0.0.0 ${D}${libdir}/libcpufreq.so.0
    ln -s libcpufreq.so.0.0.0 ${D}${libdir}/libcpufreq.so
}

