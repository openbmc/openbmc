SUMMARY = "Linux kernel userland utilities for the PCMCIA subsystem"
HOMEPAGE = "https://www.kernel.org/pub/linux/utils/kernel/pcmcia/"
SECTION = "kernel/userland"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "udev sysfsutils flex-native"
RDEPENDS_${PN} = "udev module-init-tools"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/kernel/pcmcia/${BP}.tar.xz"

S = "${WORKDIR}/pcmciautils-${PV}"

inherit pkgconfig

export HOSTCC = "${BUILD_CC}"
export etcdir = "${sysconfdir}"
export sbindir = "${base_sbindir}"
export pcmciaconfdir = "${sysconfdir}/pcmcia"
export udevdir = "`pkg-config --variable=udevdir udev`"
export udevrulesdir = "`pkg-config --variable=udevdir udev`/rules.d"
export UDEV = "1"
LD = "${CC}"
CFLAGS =+ "-I${S}/src"
CFLAGS =+ "-DPCMCIAUTILS_VERSION=\\"${PV}\\""

EXTRA_OEMAKE = "-e 'STRIP=echo' 'LIB_OBJS=-lc -lsysfs' 'LEX=flex'"

do_install () {
	oe_runmake 'DESTDIR=${D}' install
}

CONFFILES_${PN} += "${sysconfdir}/pcmcia/config.opts"
