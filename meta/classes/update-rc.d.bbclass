UPDATERCPN ?= "${PN}"

DEPENDS_append_class-target = " update-rc.d-native update-rc.d initscripts"
UPDATERCD = "update-rc.d"
UPDATERCD_class-cross = ""
UPDATERCD_class-native = ""
UPDATERCD_class-nativesdk = ""

INITSCRIPT_PARAMS ?= "defaults"

INIT_D_DIR = "${sysconfdir}/init.d"

updatercd_preinst() {
if [ -z "$D" -a -f "${INIT_D_DIR}/${INITSCRIPT_NAME}" ]; then
	${INIT_D_DIR}/${INITSCRIPT_NAME} stop
fi
if type update-rc.d >/dev/null 2>/dev/null; then
	if [ -n "$D" ]; then
		OPT="-f -r $D"
	else
		OPT="-f"
	fi
	update-rc.d $OPT ${INITSCRIPT_NAME} remove
fi
}

updatercd_postinst() {
if type update-rc.d >/dev/null 2>/dev/null; then
	if [ -n "$D" ]; then
		OPT="-r $D"
	else
		OPT="-s"
	fi
	update-rc.d $OPT ${INITSCRIPT_NAME} ${INITSCRIPT_PARAMS}
fi
}

updatercd_prerm() {
if [ -z "$D" ]; then
	${INIT_D_DIR}/${INITSCRIPT_NAME} stop
fi
}

updatercd_postrm() {
if type update-rc.d >/dev/null 2>/dev/null; then
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
            raise bb.build.FuncFailed("%s inherits update-rc.d but doesn't set INITSCRIPT_NAME" % d.getVar('FILE', False))
        if d.getVar('INITSCRIPT_PARAMS', False) == None:
            raise bb.build.FuncFailed("%s inherits update-rc.d but doesn't set INITSCRIPT_PARAMS" % d.getVar('FILE', False))

python __anonymous() {
    update_rc_after_parse(d)
}

PACKAGESPLITFUNCS_prepend = "populate_packages_updatercd "
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
            mlprefix = d.getVar('MLPREFIX', True) or ""
            d.appendVar('RDEPENDS_' + pkg, ' %sinitscripts-functions' % (mlprefix))

    def update_rcd_package(pkg):
        bb.debug(1, 'adding update-rc.d calls to preinst/postinst/prerm/postrm for %s' % pkg)

        localdata = bb.data.createCopy(d)
        overrides = localdata.getVar("OVERRIDES", True)
        localdata.setVar("OVERRIDES", "%s:%s" % (pkg, overrides))
        bb.data.update_data(localdata)

        update_rcd_auto_depend(pkg)

        preinst = d.getVar('pkg_preinst_%s' % pkg, True)
        if not preinst:
            preinst = '#!/bin/sh\n'
        preinst += localdata.getVar('updatercd_preinst', True)
        d.setVar('pkg_preinst_%s' % pkg, preinst)

        postinst = d.getVar('pkg_postinst_%s' % pkg, True)
        if not postinst:
            postinst = '#!/bin/sh\n'
        postinst += localdata.getVar('updatercd_postinst', True)
        d.setVar('pkg_postinst_%s' % pkg, postinst)

        prerm = d.getVar('pkg_prerm_%s' % pkg, True)
        if not prerm:
            prerm = '#!/bin/sh\n'
        prerm += localdata.getVar('updatercd_prerm', True)
        d.setVar('pkg_prerm_%s' % pkg, prerm)

        postrm = d.getVar('pkg_postrm_%s' % pkg, True)
        if not postrm:
                postrm = '#!/bin/sh\n'
        postrm += localdata.getVar('updatercd_postrm', True)
        d.setVar('pkg_postrm_%s' % pkg, postrm)

        d.appendVar('RRECOMMENDS_' + pkg, " ${MLPREFIX}${UPDATERCD}")

    # Check that this class isn't being inhibited (generally, by
    # systemd.bbclass) before doing any work.
    if bb.utils.contains('DISTRO_FEATURES', 'sysvinit', True, False, d) or \
       not d.getVar("INHIBIT_UPDATERCD_BBCLASS", True):
        pkgs = d.getVar('INITSCRIPT_PACKAGES', True)
        if pkgs == None:
            pkgs = d.getVar('UPDATERCPN', True)
            packages = (d.getVar('PACKAGES', True) or "").split()
            if not pkgs in packages and packages != []:
                pkgs = packages[0]
        for pkg in pkgs.split():
            update_rcd_package(pkg)
}
