SUMMARY = "A minimal seat management daemon, and a universal seat management library."
DESCRIPTION = "Seat management takes care of mediating access to shared devices (graphics, input), without requiring the applications needing access to be root."
HOMEPAGE = "https://git.sr.ht/~kennylevinsen/seatd"

LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=715a99d2dd552e6188e74d4ed2914d5a"

SRC_URI = "git://git.sr.ht/~kennylevinsen/seatd;protocol=https;branch=master \
           file://init"
SRCREV = "566ffeb032af42865dc1210e48cec08368059bb9"
S = "${WORKDIR}/git"

inherit meson pkgconfig systemd update-rc.d useradd

# https://www.openwall.com/lists/musl/2020/01/20/3
CFLAGS:append:libc-musl:powerpc64le = " -Wno-error=overflow"

PACKAGECONFIG ?= " \
	${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
	libseat-builtin \
"

PACKAGECONFIG[libseat-builtin] = "-Dlibseat-builtin=enabled,-Dlibseat-builtin=disabled"
PACKAGECONFIG[systemd] = "-Dlibseat-logind=systemd,,systemd"

do_install:append() {
        if [ "${VIRTUAL-RUNTIME_init_manager}" != "systemd" ]; then
                install -Dm755 ${UNPACKDIR}/init ${D}/${sysconfdir}/init.d/seatd
        else
                install -Dm644 ${S}/contrib/systemd/seatd.service ${D}${systemd_unitdir}/system/seatd.service
        fi
}

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "-r seat"

INITSCRIPT_NAME = "seatd"
INITSCRIPT_PARAMS = "start 9 5 2 . stop 20 0 1 6 ."
INHIBIT_UPDATERCD_BBCLASS = "${@oe.utils.conditional('VIRTUAL-RUNTIME_init_manager', 'systemd', '1', '', d)}"

SYSTEMD_SERVICE:${PN} = "seatd.service"
