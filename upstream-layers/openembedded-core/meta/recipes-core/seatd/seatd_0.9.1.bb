SUMMARY = "A minimal seat management daemon, and a universal seat management library."
DESCRIPTION = "Seat management takes care of mediating access to shared devices (graphics, input), without requiring the applications needing access to be root."
HOMEPAGE = "https://git.sr.ht/~kennylevinsen/seatd"

LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=715a99d2dd552e6188e74d4ed2914d5a"

SRC_URI = "git://git.sr.ht/~kennylevinsen/seatd;protocol=https;branch=master \
           file://init"
SRCREV = "566ffeb032af42865dc1210e48cec08368059bb9"

inherit meson pkgconfig systemd update-rc.d useradd

# https://www.openwall.com/lists/musl/2020/01/20/3
CFLAGS:append:libc-musl:powerpc64le = " -Wno-error=overflow"
#../git/common/connection.c:154:55: error: comparison of integers of different signs: 'unsigned long' and 'long' [-Werror,-Wsign-compare]
#  154 |         for (cmsg = CMSG_FIRSTHDR(msg); cmsg != NULL; cmsg = CMSG_NXTHDR(msg, cmsg)) {
#      |                                                              ^~~~~~~~~~~~~~~~~~~~~~
#/mnt/b/yoe/master/sources/poky/build/tmp/work/riscv64-poky-linux-musl/seatd/0.9.1/recipe-sysroot/usr/include/sys/socket.h:358:44: note: expanded from macro 'CMSG_NXTHDR'
#  358 |         __CMSG_LEN(cmsg) + sizeof(struct cmsghdr) >= __MHDR_END(mhdr) - (unsigned char *)(cmsg) \
#      |         ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ^  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#1 error generated.
CFLAGS:append:libc-musl:toolchain-clang = " -Wno-error=sign-compare"

PACKAGECONFIG ?= " \
	${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
	libseat-builtin \
"

PACKAGECONFIG[libseat-builtin] = "-Dlibseat-builtin=enabled,-Dlibseat-builtin=disabled"
PACKAGECONFIG[systemd] = "-Dlibseat-logind=systemd,,systemd"

do_install:append() {
        if [ "${VIRTUAL-RUNTIME_init_manager}" != "systemd" ]; then
                install -Dm755 ${UNPACKDIR}/init ${D}/${sysconfdir}/init.d/seatd
        fi
        install -Dm644 ${S}/contrib/systemd/seatd.service ${D}${systemd_unitdir}/system/seatd.service
}

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "-r seat"

INITSCRIPT_NAME = "seatd"
INITSCRIPT_PARAMS = "start 9 5 2 . stop 20 0 1 6 ."
INHIBIT_UPDATERCD_BBCLASS = "${@oe.utils.conditional('VIRTUAL-RUNTIME_init_manager', 'systemd', '1', '', d)}"

SYSTEMD_SERVICE:${PN} = "seatd.service"
