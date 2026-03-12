SUMMARY = "usb device database."
HOMEPAGE = "https://github.com/usbids/usbids"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84dcc94da3adb52b53ae4fa38fe49e5d"

SRC_URI = "git://github.com/usbids/usbids.git;branch=master;protocol=https"

# December 13, 2025
SRCREV = "5de1427442504bc8e28d35cca5397d64ea177456"


do_install() {
	install -d ${D}${datadir}
	install -m0644 ${S}/usb.ids ${D}${datadir}
}

FILES:${PN} = "${datadir}"
