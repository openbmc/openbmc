SUMMARY = "Linux kernel block layer testing framework"
DESCRIPTION = "blktests is a test framework for the Linux kernel block layer and storage stack. It is inspired by the xfstests filesystem testing framework."
DEPENDS = "gnutls keyutils glib-2.0 libnl"
RDEPENDS:${PN} += " bash coreutils gawk util-linux fio"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSES/GPL-3.0;md5=d32239bcb673463ab874e80d47fae504"

SRCREV = "9aa2023312bfcdd6b31c24b6e4a4a5c2d4f870d2"
SRC_URI = " \
	git://github.com/osandov/blktests.git;nobranch=1;protocol=https \
	"


inherit autotools-brokensep

do_configure[noexec] = "1"

FILES:${PN} = "/usr/blktests/*"
