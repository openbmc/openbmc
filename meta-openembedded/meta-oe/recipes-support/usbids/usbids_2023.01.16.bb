SUMMARY = "usb device database."
HOMEPAGE = "https://github.com/usbids/usbids"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84dcc94da3adb52b53ae4fa38fe49e5d"

SRC_URI = "git://github.com/usbids/usbids.git;branch=master;protocol=https"

SRCREV = "7963233faf69eb9c80ffca8dfc1f31940f75999f"

S = "${WORKDIR}/git"

do_install() {
	install -d ${D}${datadir}
	install -m0644 ${S}/usb.ids ${D}${datadir}
}

FILES:${PN} = "${datadir}"
