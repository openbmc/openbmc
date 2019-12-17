SUMMARY = "Intel(R) Enclosure LED Utilities"

DESCRIPTION = "The utilities are designed primarily to be used on storage servers \
 utilizing MD devices (aka Linux Software RAID) for RAID arrays.\
"
HOMEPAGE = "https://github.com/intel/ledmon"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
"

DEPENDS = "sg3-utils udev"

inherit autotools systemd

SYSTEMD_SERVICE_${PN} = "ledmon.service"

# 0.93
SRC_URI = "git://github.com/intel/ledmon;branch=master \
           file://0002-include-sys-select.h-and-sys-types.h.patch \
           file://0001-Don-t-build-with-Werror-to-fix-compile-error.patch \
          "

SRCREV = "1d72f9cb5c9163b2ecdf19709935720e65f5b90e"

COMPATIBLE_HOST = "(i.86|x86_64).*-linux"
COMPATIBLE_HOST_libc-musl = "null"

S = "${WORKDIR}/git"
EXTRA_OEMAKE = "CC='${CC}' LDFLAGS='${LDFLAGS}' CFLAGS='${CFLAGS}'"

# The ledmon sources include headers in ${S}/config to build but not in CFLAGS. 
# We need to add this include path in CFLAGS.
CFLAGS += "-I${S}/config"

do_install_append() {
        if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
	        install -d ${D}${systemd_unitdir}/system
	        install -m 0755 ${S}/systemd/ledmon.service ${D}${systemd_unitdir}/system
        fi
}
