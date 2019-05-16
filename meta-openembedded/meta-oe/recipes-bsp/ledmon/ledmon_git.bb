SUMMARY = "Intel(R) Enclosure LED Utilities"

DESCRIPTION = "The utilities are designed primarily to be used on storage servers \
 utilizing MD devices (aka Linux Software RAID) for RAID arrays.\
"
HOMEPAGE = "https://github.com/intel/ledmon"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
"

DEPENDS = "sg3-utils udev"

inherit systemd

SYSTEMD_SERVICE_${PN} = "ledmon.service"

SRC_URI = "git://github.com/intel/ledmon;branch=master \
           file://0002-include-sys-select.h-and-sys-types.h.patch \
          "

SRCREV = "ad1304ca1363d727425a1f23703c523e21feae4f"

COMPATIBLE_HOST = "(i.86|x86_64).*-linux"
COMPATIBLE_HOST_libc-musl = "null"

S = "${WORKDIR}/git"
EXTRA_OEMAKE = "CC='${CC}' LDFLAGS='${LDFLAGS}' CFLAGS='${CFLAGS}'"

do_install_append() {
	install -d ${D}/${systemd_unitdir}/system
	oe_runmake  DESTDIR=${D}  install
	oe_runmake  DESTDIR=${D}${systemd_unitdir}/system  install-systemd
}
