#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

UPDATERCPN ?= "${PN}"

DEPENDS:append:class-target = "${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', ' update-rc.d initscripts', '', d)}"

UPDATERCD = "update-rc.d"
UPDATERCD:class-cross = ""
UPDATERCD:class-native = ""
UPDATERCD:class-nativesdk = ""

INITSCRIPT_PARAMS ?= "defaults"

INIT_D_DIR = "${sysconfdir}/init.d"

def use_updatercd(d):
    # If the distro supports both sysvinit and systemd, and the current recipe
    # supports systemd, only call update-rc.d on rootfs creation or if systemd
    # is not running. That's because systemctl enable/disable will already call
    # update-rc.d if it detects initscripts.
    if bb.utils.contains('DISTRO_FEATURES', 'systemd', True, False, d) and bb.data.inherits_class('systemd', d):
        return '[ -n "$D" -o ! -d /run/systemd/system ]'
    return 'true'

PACKAGE_WRITE_DEPS += "update-rc.d-native"

updatercd_postinst() {
if ${@use_updatercd(d)} && type update-rc.d >/dev/null 2>/dev/null; then
	if [ -n "$D" ]; then
		OPT="-r $D"
	else
		OPT="-s"
	fi
	update-rc.d $OPT ${INITSCRIPT_NAME} ${INITSCRIPT_PARAMS}
fi
}

updatercd_prerm() {
if ${@use_updatercd(d)} && [ -z "$D" -a -x "${INIT_D_DIR}/${INITSCRIPT_NAME}" ]; then
	${INIT_D_DIR}/${INITSCRIPT_NAME} stop || :
fi
}

updatercd_postrm() {
if ${@use_updatercd(d)} && type update-rc.d >/dev/null 2>/dev/null; then
	if [ -n "$D" ]; then
		OPT="-f -r $D"
	else
		OPT="-f"
	fi
	update-rc.d $OPT ${INITSCRIPT_NAME} remove
fi
}


def update_rc_after_parse(d):
    if d.getVar('INITSCRIPT_PACKAGES', False) == None:
        if d.getVar('INITSCRIPT_NAME', False) == None:
            bb.fatal("%s inherits update-rc.d but doesn't set INITSCRIPT_NAME" % d.getVar('FILE', False))
        if d.getVar('INITSCRIPT_PARAMS', False) == None:
            bb.fatal("%s inherits update-rc.d but doesn't set INITSCRIPT_PARAMS" % d.getVar('FILE', False))

python __anonymous() {
    update_rc_after_parse(d)
}

PACKAGESPLITFUNCS =+ "${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'populate_packages_updatercd', '', d)}"
PACKAGESPLITFUNCS:remove:class-nativesdk = "populate_packages_updatercd"

populate_packages_updatercd[vardeps] += "updatercd_prerm updatercd_postrm updatercd_postinst"
populate_packages_updatercd[vardepsexclude] += "OVERRIDES"

python populate_packages_updatercd () {
    def update_rcd_auto_depend(pkg):
        import subprocess
        import os
        path = d.expand("${D}${INIT_D_DIR}/${INITSCRIPT_NAME}")
        if not os.path.exists(path):
            return
        statement = "grep -q -w '/etc/init.d/functions' %s" % path
        if subprocess.call(statement, shell=True) == 0:
            mlprefix = d.getVar('MLPREFIX') or ""
            d.appendVar('RDEPENDS:' + pkg, ' %sinitd-functions' % (mlprefix))

    def update_rcd_package(pkg):
        bb.debug(1, 'adding update-rc.d calls to postinst/prerm/postrm for %s' % pkg)

        localdata = bb.data.createCopy(d)
        overrides = localdata.getVar("OVERRIDES")
        localdata.setVar("OVERRIDES", "%s:%s" % (pkg, overrides))

        update_rcd_auto_depend(pkg)

        postinst = d.getVar('pkg_postinst:%s' % pkg)
        if not postinst:
            postinst = '#!/bin/sh\n'
        postinst += localdata.getVar('updatercd_postinst')
        d.setVar('pkg_postinst:%s' % pkg, postinst)

        prerm = d.getVar('pkg_prerm:%s' % pkg)
        if not prerm:
            prerm = '#!/bin/sh\n'
        prerm += localdata.getVar('updatercd_prerm')
        d.setVar('pkg_prerm:%s' % pkg, prerm)

        postrm = d.getVar('pkg_postrm:%s' % pkg)
        if not postrm:
                postrm = '#!/bin/sh\n'
        postrm += localdata.getVar('updatercd_postrm')
        d.setVar('pkg_postrm:%s' % pkg, postrm)

        d.appendVar('RRECOMMENDS:' + pkg, " ${MLPREFIX}${UPDATERCD}")

    # Check that this class isn't being inhibited (generally, by
    # systemd.bbclass) before doing any work.
    if not d.getVar("INHIBIT_UPDATERCD_BBCLASS"):
        pkgs = d.getVar('INITSCRIPT_PACKAGES')
        if pkgs == None:
            pkgs = d.getVar('UPDATERCPN')
            packages = (d.getVar('PACKAGES') or "").split()
            if not pkgs in packages and packages != []:
                pkgs = packages[0]
        for pkg in pkgs.split():
            update_rcd_package(pkg)
}
