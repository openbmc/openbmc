require xorg-driver-input.inc

SUMMARY = "X.Org X server -- VMWare mouse input driver"
DESCRIPTION = "The vmmouse driver enables support for the special VMMouse \
protocol that is provided by VMware virtual machines to give absolute \
pointer positioning. The vmmouse driver is capable of falling back to the \
standard 'mouse' driver if a VMware virtual machine is not detected."

XORG_DRIVER_COMPRESSOR = ".tar.xz"

SRC_URI[sha256sum] = "56f077580ab8f02e2f40358c5c46b0ae3e1828fc77744526b24adf1ceea339b8"

RDEPENDS:${PN} += "xf86-input-mouse"

LIC_FILES_CHKSUM = "file://COPYING;md5=622841c068a9d7625fbfe7acffb1a8fc"

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

do_install:append () {
	# We don't care about hal
	rm -rf ${D}${datadir}/hal/
	rm -rf ${D}${libdir}/hal/
}

EXTRA_OECONF = "--with-udev-rules-dir=${nonarch_base_libdir}/udev/rules.d"

FILES:${PN} += "${datadir}/X11/xorg.conf.d"
