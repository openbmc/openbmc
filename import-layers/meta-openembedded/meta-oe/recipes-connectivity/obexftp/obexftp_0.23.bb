DESCRIPTION = "A tool for transfer files to/from any OBEX enabled device"
LICENSE = "GPLv2 & LGPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS += "openobex"

# Depends on openobex
PNBLACKLIST[obexftp] ?= "${@bb.utils.contains('DISTRO_FEATURES', 'bluez5', 'bluez5 conflicts with bluez4 and bluez5 is selected in DISTRO_FEATURES', '', d)}"

SRC_URI = "http://sourceforge.net/projects/openobex/files/obexftp/${PV}/obexftp-${PV}.tar.bz2 \
           file://Remove_some_printf_in_obexftpd.patch "

SRC_URI[md5sum] = "f20762061b68bc921e80be4aebc349eb"
SRC_URI[sha256sum] = "44a74ff288d38c0f75354d6bc2efe7d6dec10112eaff2e7b10e292b0d2105b36"

inherit autotools pkgconfig

EXTRA_OECONF += "--disable-tcl --disable-perl --disable-python --disable-ruby"

