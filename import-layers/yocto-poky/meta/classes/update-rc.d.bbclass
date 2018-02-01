UPDATERCPN ?= "${PN}"

DEPENDS_append_class-target = "${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', ' update-rc.d initscripts', '', d)}"

UPDATERCD = "update-rc.d"
UPDATERCD_class-cross = ""
UPDATERCD_class-native = ""
UPDATERCD_class-nativesdk = ""

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

updatercd_preinst() {
if ${@use_updatercd(d)} && [ -z "$D" -a -f "${INIT_D_DIR}/${INITSCRIPT_NAME}" ]; then
	${INIT_D_DIR}/${INITSCRIPT_NAME} stop || :
fi
if ${@use_updatercd(d)} && type update-rc.d >/dev/null 2>/dev/null; then
	if [ -n "$D" ]; then
		OPT="-f -r $D"
	else
		OPT="-f"
	fi
	update-rc.d $OPT ${INITSCRIPT_NAME} remove
fi
}

PACKAGE_WRITE_DEPS += "update-rc.d-native"

updatercd_postinst() {
# Begin section update-rc.d
if ${@use_updatercd(d)} && type update-rc.d >/dev/null 2>/dev/null; then
	if [ -n "$D" ]; then
		OPT="-r $D"
	else
		OPT="-s"
	fi
	update-rc.d $OPT ${INITSCRIPT_NAME} ${INITSCRIPT_PARAMS}
fi
# End section update-rc.d
}

updatercd_prerm() {
# Begin section update-rc.d
if ${@use_updatercd(d)} && [ -z "$D" -a -x "${INIT_D_DIR}/${INITSCRIPT_NAME}" ]; then
	${INIT_D_DIR}/${INITSCRIPT_NAME} stop || :
fi
# End section update-rc.d
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

PACKAGESPLITFUNCS_prepend = "${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'populate_packages_updatercd ', '', d)}"
PACKAGESPLITFUNCS_remove_class-nativesdk = "populate_packages_updatercd "

populate_packages_updatercd[vardeps] += "updatercd_prerm updatercd_postrm updatercd_preinst updatercd_postinst"
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
            d.appendVar('RDEPENDS_' + pkg, ' %sinitscripts-functions' % (mlprefix))

    def update_rcd_package(pkg):
        bb.debug(1, 'adding update-rc.d calls to preinst/postinst/prerm/postrm for %s' % pkg)

        localdata = bb.data.createCopy(d)
        overrides = localdata.getVar("OVERRIDES")
        localdata.setVar("OVERRIDES", "%s:%s" % (pkg, overrides))

        update_rcd_auto_depend(pkg)

        preinst = d.getVar('pkg_preinst_%s' % pkg)
        if not preinst:
            preinst = '#!/bin/sh\n'
        preinst += localdata.getVar('updatercd_preinst')
        d.setVar('pkg_preinst_%s' % pkg, preinst)

        postinst = d.getVar('pkg_postinst_%s' % pkg)
        if not postinst:
            postinst = '#!/bin/sh\n'
        postinst = postinst.splitlines(True)
        try:
            index = postinst.index('# End section update-alternatives\n')
            postinst.insert(index + 1, localdata.getVar('updatercd_postinst'))
        except ValueError:
            postinst.append(localdata.getVar('updatercd_postinst'))
        postinst = ''.join(postinst)
        d.setVar('pkg_postinst_%s' % pkg, postinst)

        prerm = d.getVar('pkg_prerm_%s' % pkg)
        if not prerm:
            prerm = '#!/bin/sh\n'
        prerm = prerm.splitlines(True)
        try:
            index = prerm.index('# Begin section update-alternatives\n')
            prerm.insert(index, localdata.getVar('updatercd_prerm'))
        except ValueError:
            prerm.append(localdata.getVar('updatercd_prerm'))
        prerm = ''.join(prerm)
        d.setVar('pkg_prerm_%s' % pkg, prerm)

        postrm = d.getVar('pkg_postrm_%s' % pkg)
        if not postrm:
                postrm = '#!/bin/sh\n'
        postrm += localdata.getVar('updatercd_postrm')
        d.setVar('pkg_postrm_%s' % pkg, postrm)

        d.appendVar('RRECOMMENDS_' + pkg, " ${MLPREFIX}${UPDATERCD}")

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
