SUMMARY = "Intel(R) Enclosure LED Utilities"

DESCRIPTION = "The utilities are designed primarily to be used on storage servers \
 utilizing MD devices (aka Linux Software RAID) for RAID arrays.\
"
HOMEPAGE = "https://github.com/intel/ledmon"

LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "sg3-utils udev pciutils"

inherit autotools systemd pkgconfig

SYSTEMD_SERVICE:${PN} = "ledmon.service"

SRC_URI = "git://github.com/intel/ledmon;branch=master;protocol=https \
	   file://0002-include-sys-select.h-and-sys-types.h.patch \
	   file://0001-fix-build-with-clang.patch"

SRCREV = "b0edae14e8660b80ffe0384354038a9f62e2978d"

COMPATIBLE_HOST = "(i.86|x86_64).*-linux"
COMPATIBLE_HOST:libc-musl = "null"

S = "${WORKDIR}/git"

EXTRA_OECONF += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--enable-systemd', '', d)}"

EXTRA_OEMAKE = "CC='${CC}' LDFLAGS='${LDFLAGS}' CFLAGS='${CFLAGS}'"

# The ledmon sources include headers in ${S}/config to build but not in CFLAGS. 
# We need to add this include path in CFLAGS.
CFLAGS += "-I${S}/config"
