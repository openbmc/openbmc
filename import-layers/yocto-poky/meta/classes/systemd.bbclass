# The list of packages that should have systemd packaging scripts added.  For
# each entry, optionally have a SYSTEMD_SERVICE_[package] that lists the service
# files in this package.  If this variable isn't set, [package].service is used.
SYSTEMD_PACKAGES ?= "${PN}"
SYSTEMD_PACKAGES_class-native ?= ""
SYSTEMD_PACKAGES_class-nativesdk ?= ""

# Whether to enable or disable the services on installation.
SYSTEMD_AUTO_ENABLE ??= "enable"

# This class will be included in any recipe that supports systemd init scripts,
# even if systemd is not in DISTRO_FEATURES.  As such don't make any changes
# directly but check the DISTRO_FEATURES first.
python __anonymous() {
    # If the distro features have systemd but not sysvinit, inhibit update-rcd
    # from doing any work so that pure-systemd images don't have redundant init
    # files.
    if bb.utils.contains('DISTRO_FEATURES', 'systemd', True, False, d):
        d.appendVar("DEPENDS", " systemd-systemctl-native")
        if not bb.utils.contains('DISTRO_FEATURES', 'sysvinit', True, False, d):
            d.setVar("INHIBIT_UPDATERCD_BBCLASS", "1")
}

systemd_postinst() {
OPTS=""

if [ -n "$D" ]; then
    OPTS="--root=$D"
fi

if type systemctl >/dev/null 2>/dev/null; then
	systemctl $OPTS ${SYSTEMD_AUTO_ENABLE} ${SYSTEMD_SERVICE}

	if [ -z "$D" -a "${SYSTEMD_AUTO_ENABLE}" = "enable" ]; then
		systemctl restart ${SYSTEMD_SERVICE}
	fi
fi
}

systemd_prerm() {
OPTS=""

if [ -n "$D" ]; then
    OPTS="--root=$D"
fi

if type systemctl >/dev/null 2>/dev/null; then
	if [ -z "$D" ]; then
		systemctl stop ${SYSTEMD_SERVICE}
	fi

	systemctl $OPTS disable ${SYSTEMD_SERVICE}
fi
}


systemd_populate_packages[vardeps] += "systemd_prerm systemd_postinst"
systemd_populate_packages[vardepsexclude] += "OVERRIDES"


python systemd_populate_packages() {
    import re

    if not bb.utils.contains('DISTRO_FEATURES', 'systemd', True, False, d):
        return

    def get_package_var(d, var, pkg):
        val = (d.getVar('%s_%s' % (var, pkg), True) or "").strip()
        if val == "":
            val = (d.getVar(var, True) or "").strip()
        return val

    # Check if systemd-packages already included in PACKAGES
    def systemd_check_package(pkg_systemd):
        packages = d.getVar('PACKAGES', True)
        if not pkg_systemd in packages.split():
            bb.error('%s does not appear in package list, please add it' % pkg_systemd)


    def systemd_generate_package_scripts(pkg):
        bb.debug(1, 'adding systemd calls to postinst/postrm for %s' % pkg)

        # Add pkg to the overrides so that it finds the SYSTEMD_SERVICE_pkg
        # variable.
        localdata = d.createCopy()
        localdata.prependVar("OVERRIDES", pkg + ":")
        bb.data.update_data(localdata)

        postinst = d.getVar('pkg_postinst_%s' % pkg, True)
        if not postinst:
            postinst = '#!/bin/sh\n'
        postinst += localdata.getVar('systemd_postinst', True)
        d.setVar('pkg_postinst_%s' % pkg, postinst)

        prerm = d.getVar('pkg_prerm_%s' % pkg, True)
        if not prerm:
            prerm = '#!/bin/sh\n'
        prerm += localdata.getVar('systemd_prerm', True)
        d.setVar('pkg_prerm_%s' % pkg, prerm)


    # Add files to FILES_*-systemd if existent and not already done
    def systemd_append_file(pkg_systemd, file_append):
        appended = False
        if os.path.exists(oe.path.join(d.getVar("D", True), file_append)):
            var_name = "FILES_" + pkg_systemd
            files = d.getVar(var_name, False) or ""
            if file_append not in files.split():
                d.appendVar(var_name, " " + file_append)
                appended = True
        return appended

    # Add systemd files to FILES_*-systemd, parse for Also= and follow recursive
    def systemd_add_files_and_parse(pkg_systemd, path, service, keys):
        # avoid infinite recursion
        if systemd_append_file(pkg_systemd, oe.path.join(path, service)):
            fullpath = oe.path.join(d.getVar("D", True), path, service)
            if service.find('.service') != -1:
                # for *.service add *@.service
                service_base = service.replace('.service', '')
                systemd_add_files_and_parse(pkg_systemd, path, service_base + '@.service', keys)
            if service.find('.socket') != -1:
                # for *.socket add *.service and *@.service
                service_base = service.replace('.socket', '')
                systemd_add_files_and_parse(pkg_systemd, path, service_base + '.service', keys)
                systemd_add_files_and_parse(pkg_systemd, path, service_base + '@.service', keys)
            for key in keys.split():
                # recurse all dependencies found in keys ('Also';'Conflicts';..) and add to files
                cmd = "grep %s %s | sed 's,%s=,,g' | tr ',' '\\n'" % (key, fullpath, key)
                pipe = os.popen(cmd, 'r')
                line = pipe.readline()
                while line:
                    line = line.replace('\n', '')
                    systemd_add_files_and_parse(pkg_systemd, path, line, keys)
                    line = pipe.readline()
                pipe.close()

    # Check service-files and call systemd_add_files_and_parse for each entry
    def systemd_check_services():
        searchpaths = [oe.path.join(d.getVar("sysconfdir", True), "systemd", "system"),]
        searchpaths.append(d.getVar("systemd_system_unitdir", True))
        systemd_packages = d.getVar('SYSTEMD_PACKAGES', True)

        keys = 'Also'
        # scan for all in SYSTEMD_SERVICE[]
        for pkg_systemd in systemd_packages.split():
            for service in get_package_var(d, 'SYSTEMD_SERVICE', pkg_systemd).split():
                path_found = ''

                # Deal with adding, for example, 'ifplugd@eth0.service' from
                # 'ifplugd@.service'
                base = None
                if service.find('@') != -1:
                    base = re.sub('@[^.]+.', '@.', service)

                for path in searchpaths:
                    if os.path.exists(oe.path.join(d.getVar("D", True), path, service)):
                        path_found = path
                        break
                    elif base is not None:
                        if os.path.exists(oe.path.join(d.getVar("D", True), path, base)):
                            path_found = path
                            break

                if path_found != '':
                    systemd_add_files_and_parse(pkg_systemd, path_found, service, keys)
                else:
                    raise bb.build.FuncFailed("SYSTEMD_SERVICE_%s value %s does not exist" % \
                        (pkg_systemd, service))

    # Run all modifications once when creating package
    if os.path.exists(d.getVar("D", True)):
        for pkg in d.getVar('SYSTEMD_PACKAGES', True).split():
            systemd_check_package(pkg)
            if d.getVar('SYSTEMD_SERVICE_' + pkg, True):
                systemd_generate_package_scripts(pkg)
        systemd_check_services()
}

PACKAGESPLITFUNCS_prepend = "systemd_populate_packages "

python rm_systemd_unitdir (){
    import shutil
    if not bb.utils.contains('DISTRO_FEATURES', 'systemd', True, False, d):
        systemd_unitdir = oe.path.join(d.getVar("D", True), d.getVar('systemd_unitdir', True))
        if os.path.exists(systemd_unitdir):
            shutil.rmtree(systemd_unitdir)
        systemd_libdir = os.path.dirname(systemd_unitdir)
        if (os.path.exists(systemd_libdir) and not os.listdir(systemd_libdir)):
            os.rmdir(systemd_libdir)
}
do_install[postfuncs] += "rm_systemd_unitdir "

python rm_sysvinit_initddir (){
    import shutil
    sysv_initddir = oe.path.join(d.getVar("D", True), (d.getVar('INIT_D_DIR', True) or "/etc/init.d"))

    if bb.utils.contains('DISTRO_FEATURES', 'systemd', True, False, d) and \
        not bb.utils.contains('DISTRO_FEATURES', 'sysvinit', True, False, d) and \
        os.path.exists(sysv_initddir):
        systemd_system_unitdir = oe.path.join(d.getVar("D", True), d.getVar('systemd_system_unitdir', True))

        # If systemd_system_unitdir contains anything, delete sysv_initddir
        if (os.path.exists(systemd_system_unitdir) and os.listdir(systemd_system_unitdir)):
            shutil.rmtree(sysv_initddir)
}
do_install[postfuncs] += "rm_sysvinit_initddir "
