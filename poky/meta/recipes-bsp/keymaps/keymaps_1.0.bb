SUMMARY = "Keyboard maps"
DESCRIPTION = "Keymaps and initscript to set the keymap on bootup."
SECTION = "base"

RDEPENDS:${PN} = "kbd"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://keymap.sh;beginline=5;endline=5;md5=829e563511c9a1d6d41f17a7a4989d6a"
PACKAGE_ARCH = "${MACHINE_ARCH}"
PR = "r31"

INHIBIT_DEFAULT_DEPS = "1"

# As the recipe doesn't inherit systemd.bbclass, we need to set this variable
# manually to avoid unnecessary postinst/preinst generated.
python __anonymous() {
    if not bb.utils.contains('DISTRO_FEATURES', 'sysvinit', True, False, d):
        d.setVar("INHIBIT_UPDATERCD_BBCLASS", "1")
}

inherit update-rc.d

SRC_URI = "file://keymap.sh"

INITSCRIPT_NAME = "keymap.sh"
INITSCRIPT_PARAMS = "start 01 S ."

S = "${WORKDIR}"

do_install () {
    # Only install the script if 'sysvinit' is in DISTRO_FEATURES
    # THe ulitity this script provides could be achieved by systemd-vconsole-setup.service
    if ${@bb.utils.contains('DISTRO_FEATURES','sysvinit','true','false',d)}; then
	install -d ${D}${sysconfdir}/init.d/
	install -m 0755 ${WORKDIR}/keymap.sh ${D}${sysconfdir}/init.d/
    fi
}

PACKAGE_WRITE_DEPS:append = " ${@bb.utils.contains('DISTRO_FEATURES','systemd sysvinit','systemd-systemctl-native','',d)}"
pkg_postinst:${PN} () {
	if ${@bb.utils.contains('DISTRO_FEATURES','systemd sysvinit','true','false',d)}; then
		if [ -n "$D" ]; then
			OPTS="--root=$D"
		fi
		systemctl $OPTS mask keymap.service
	fi
}

ALLOW_EMPTY:${PN} = "1"
