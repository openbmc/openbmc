SUMMARY = "Keyboard maps"
DESCRIPTION = "Keymaps and initscript to set the keymap on bootup."
HOMEPAGE = "https://www.yoctoproject.org/"
SECTION = "base"

RDEPENDS:${PN} = "kbd"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://keymap.sh;beginline=5;endline=5;md5=829e563511c9a1d6d41f17a7a4989d6a"
PACKAGE_ARCH = "${MACHINE_ARCH}"

INHIBIT_DEFAULT_DEPS = "1"

inherit update-rc.d

SRC_URI = "file://keymap.sh"

INITSCRIPT_NAME = "keymap.sh"
INITSCRIPT_PARAMS = "start 01 S ."

S = "${UNPACKDIR}"

do_install () {
    # Only install the script if 'sysvinit' is in DISTRO_FEATURES
    # THe ulitity this script provides could be achieved by systemd-vconsole-setup.service
    if ${@bb.utils.contains('DISTRO_FEATURES','sysvinit','true','false',d)}; then
	install -d ${D}${sysconfdir}/init.d/
	install -m 0755 ${S}/keymap.sh ${D}${sysconfdir}/init.d/
    fi
}

ALLOW_EMPTY:${PN} = "1"
