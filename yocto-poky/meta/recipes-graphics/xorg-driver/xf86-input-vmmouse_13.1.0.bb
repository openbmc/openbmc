require xorg-driver-input.inc

SUMMARY = "X.Org X server -- VMWare mouse input driver"
DESCRIPTION = "The vmmouse driver enables support for the special VMMouse \
protocol that is provided by VMware virtual machines to give absolute \
pointer positioning. The vmmouse driver is capable of falling back to the \
standard 'mouse' driver if a VMware virtual machine is not detected."

SRC_URI[md5sum] = "85e2e464b7219c495ad3a16465c226ed"
SRC_URI[sha256sum] = "0af558957ac1be1b2863712c2475de8f4d7f14921fd01ded2e2fde4921b19319"

RDEPENDS_${PN} += "xf86-input-mouse"

LIC_FILES_CHKSUM = "file://COPYING;md5=622841c068a9d7625fbfe7acffb1a8fc"

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

do_install_append () {
	# We don't care about hal
	rm -rf ${D}${datadir}/hal/
	rm -rf ${D}${libdir}/hal/
}

EXTRA_OECONF = "--with-udev-rules-dir=${nonarch_base_libdir}/udev/rules.d"

FILES_${PN} += "${nonarch_base_libdir}/udev/rules.d/ ${datadir}/X11/xorg.conf.d"
