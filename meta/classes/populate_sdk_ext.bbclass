# Extensible SDK

inherit populate_sdk_base

# NOTE: normally you cannot use task overrides for this kind of thing - this
# only works because of get_sdk_ext_rdepends()

TOOLCHAIN_HOST_TASK_task-populate-sdk-ext = " \
    meta-environment-extsdk-${MACHINE} \
    "

TOOLCHAIN_TARGET_TASK_task-populate-sdk-ext = ""

SDK_RDEPENDS_append_task-populate-sdk-ext = " ${SDK_TARGETS}"

SDK_RELOCATE_AFTER_INSTALL_task-populate-sdk-ext = "0"

SDK_LOCAL_CONF_WHITELIST ?= ""
SDK_LOCAL_CONF_BLACKLIST ?= "CONF_VERSION BB_NUMBER_THREADS PARALLEL_MAKE PRSERV_HOST"
SDK_INHERIT_BLACKLIST ?= "buildhistory icecc"
SDK_UPDATE_URL ?= ""

SDK_TARGETS ?= "${PN}"
OE_INIT_ENV_SCRIPT ?= "oe-init-build-env"

# The files from COREBASE that you want preserved in the COREBASE copied
# into the sdk. This allows someone to have their own setup scripts in
# COREBASE be preserved as well as untracked files.
COREBASE_FILES ?= " \
    oe-init-build-env \
    oe-init-build-env-memres \
    scripts \
    LICENSE \
    .templateconf \
"

SDK_DIR_task-populate-sdk-ext = "${WORKDIR}/sdk-ext"
B_task-populate-sdk-ext = "${SDK_DIR}"
TOOLCHAIN_OUTPUTNAME_task-populate-sdk-ext = "${SDK_NAME}-toolchain-ext-${SDK_VERSION}"

SDK_TITLE_task-populate-sdk-ext = "${@d.getVar('DISTRO_NAME', True) or d.getVar('DISTRO', True)} Extensible SDK"

python copy_buildsystem () {
    import re
    import oe.copy_buildsystem

    oe_init_env_script = d.getVar('OE_INIT_ENV_SCRIPT', True)

    conf_bbpath = ''
    conf_initpath = ''
    core_meta_subdir = ''

    # Copy in all metadata layers + bitbake (as repositories)
    buildsystem = oe.copy_buildsystem.BuildSystem('extensible SDK', d)
    baseoutpath = d.getVar('SDK_OUTPUT', True) + '/' + d.getVar('SDKPATH', True)
    layers_copied = buildsystem.copy_bitbake_and_layers(baseoutpath + '/layers')

    sdkbblayers = []
    corebase = os.path.basename(d.getVar('COREBASE', True))
    for layer in layers_copied:
        if corebase == os.path.basename(layer):
            conf_bbpath = os.path.join('layers', layer, 'bitbake')
        else:
            sdkbblayers.append(layer)

    for path in os.listdir(baseoutpath + '/layers'):
        relpath = os.path.join('layers', path, oe_init_env_script)
        if os.path.exists(os.path.join(baseoutpath, relpath)):
            conf_initpath = relpath

        relpath = os.path.join('layers', path, 'scripts', 'devtool')
        if os.path.exists(os.path.join(baseoutpath, relpath)):
            scriptrelpath = os.path.dirname(relpath)

        relpath = os.path.join('layers', path, 'meta')
        if os.path.exists(os.path.join(baseoutpath, relpath, 'lib', 'oe')):
            core_meta_subdir = relpath

    d.setVar('oe_init_build_env_path', conf_initpath)
    d.setVar('scriptrelpath', scriptrelpath)

    # Write out config file for devtool
    import ConfigParser
    config = ConfigParser.SafeConfigParser()
    config.add_section('General')
    config.set('General', 'bitbake_subdir', conf_bbpath)
    config.set('General', 'init_path', conf_initpath)
    config.set('General', 'core_meta_subdir', core_meta_subdir)
    config.add_section('SDK')
    config.set('SDK', 'sdk_targets', d.getVar('SDK_TARGETS', True))
    updateurl = d.getVar('SDK_UPDATE_URL', True)
    if updateurl:
        config.set('SDK', 'updateserver', updateurl)
    bb.utils.mkdirhier(os.path.join(baseoutpath, 'conf'))
    with open(os.path.join(baseoutpath, 'conf', 'devtool.conf'), 'w') as f:
        config.write(f)

    # Create a layer for new recipes / appends
    bbpath = d.getVar('BBPATH', True)
    bb.process.run(['devtool', '--bbpath', bbpath, '--basepath', baseoutpath, 'create-workspace', '--create-only', os.path.join(baseoutpath, 'workspace')])

    # Create bblayers.conf
    bb.utils.mkdirhier(baseoutpath + '/conf')
    with open(baseoutpath + '/conf/bblayers.conf', 'w') as f:
        f.write('# WARNING: this configuration has been automatically generated and in\n')
        f.write('# most cases should not be edited. If you need more flexibility than\n')
        f.write('# this configuration provides, it is strongly suggested that you set\n')
        f.write('# up a proper instance of the full build system and use that instead.\n\n')

        f.write('LCONF_VERSION = "%s"\n\n' % d.getVar('LCONF_VERSION', False))
        f.write('BBPATH = "$' + '{TOPDIR}"\n')
        f.write('SDKBASEMETAPATH = "$' + '{TOPDIR}"\n')
        f.write('BBLAYERS := " \\\n')
        for layerrelpath in sdkbblayers:
            f.write('    $' + '{SDKBASEMETAPATH}/layers/%s \\\n' % layerrelpath)
        f.write('    $' + '{SDKBASEMETAPATH}/workspace \\\n')
        f.write('    "\n')

    # Create local.conf
    local_conf_whitelist = (d.getVar('SDK_LOCAL_CONF_WHITELIST', True) or '').split()
    local_conf_blacklist = (d.getVar('SDK_LOCAL_CONF_BLACKLIST', True) or '').split()
    def handle_var(varname, origvalue, op, newlines):
        if varname in local_conf_blacklist or (origvalue.strip().startswith('/') and not varname in local_conf_whitelist):
            newlines.append('# Removed original setting of %s\n' % varname)
            return None, op, 0, True
        else:
            return origvalue, op, 0, True
    varlist = ['[^#=+ ]*']
    builddir = d.getVar('TOPDIR', True)
    with open(builddir + '/conf/local.conf', 'r') as f:
        oldlines = f.readlines()
    (updated, newlines) = bb.utils.edit_metadata(oldlines, varlist, handle_var)

    with open(baseoutpath + '/conf/local.conf', 'w') as f:
        f.write('# WARNING: this configuration has been automatically generated and in\n')
        f.write('# most cases should not be edited. If you need more flexibility than\n')
        f.write('# this configuration provides, it is strongly suggested that you set\n')
        f.write('# up a proper instance of the full build system and use that instead.\n\n')
        for line in newlines:
            if line.strip() and not line.startswith('#'):
                f.write(line)

        f.write('INHERIT += "%s"\n\n' % 'uninative')
        f.write('CONF_VERSION = "%s"\n\n' % d.getVar('CONF_VERSION', False))

        # Some classes are not suitable for SDK, remove them from INHERIT
        f.write('INHERIT_remove = "%s"\n' % d.getVar('SDK_INHERIT_BLACKLIST'))

        # Bypass the default connectivity check if any
        f.write('CONNECTIVITY_CHECK_URIS = ""\n\n')

        # Another hack, but we want the native part of sstate to be kept the same
        # regardless of the host distro
        fixedlsbstring = 'SDK-Fixed'
        f.write('NATIVELSBSTRING_forcevariable = "%s"\n\n' % fixedlsbstring)

        # Ensure locked sstate cache objects are re-used without error
        f.write('SIGGEN_LOCKEDSIGS_CHECK_LEVEL = "none"\n\n')

        # If you define a sdk_extraconf() function then it can contain additional config
        extraconf = (d.getVar('sdk_extraconf', True) or '').strip()
        if extraconf:
            # Strip off any leading / trailing spaces
            for line in extraconf.splitlines():
                f.write(line.strip() + '\n')

        f.write('require conf/locked-sigs.inc\n')

    sigfile = d.getVar('WORKDIR', True) + '/locked-sigs.inc'
    oe.copy_buildsystem.generate_locked_sigs(sigfile, d)

    # Filter the locked signatures file to just the sstate tasks we are interested in
    allowed_tasks = ['do_populate_lic', 'do_populate_sysroot', 'do_packagedata', 'do_package_write_ipk', 'do_package_write_rpm', 'do_package_write_deb', 'do_package_qa', 'do_deploy']
    excluded_targets = d.getVar('SDK_TARGETS', True)
    lockedsigs_pruned = baseoutpath + '/conf/locked-sigs.inc'
    oe.copy_buildsystem.prune_lockedsigs(allowed_tasks,
                                         excluded_targets,
                                         sigfile,
                                         lockedsigs_pruned)

    sstate_out = baseoutpath + '/sstate-cache'
    bb.utils.remove(sstate_out, True)
    oe.copy_buildsystem.create_locked_sstate_cache(lockedsigs_pruned,
                                                   d.getVar('SSTATE_DIR', True),
                                                   sstate_out, d,
                                                   fixedlsbstring)
}

def extsdk_get_buildtools_filename(d):
    return '*-buildtools-nativesdk-standalone-*.sh'

install_tools() {
	install -d ${SDK_OUTPUT}/${SDKPATHNATIVE}${bindir_nativesdk}
	lnr ${SDK_OUTPUT}/${SDKPATH}/${scriptrelpath}/devtool ${SDK_OUTPUT}/${SDKPATHNATIVE}${bindir_nativesdk}/devtool
	lnr ${SDK_OUTPUT}/${SDKPATH}/${scriptrelpath}/recipetool ${SDK_OUTPUT}/${SDKPATHNATIVE}${bindir_nativesdk}/recipetool
	touch ${SDK_OUTPUT}/${SDKPATH}/.devtoolbase

	# find latest buildtools-tarball and install it
	buildtools_path=`ls -t1 ${SDK_DEPLOY}/${@extsdk_get_buildtools_filename(d)} | head -n1`
	install $buildtools_path ${SDK_OUTPUT}/${SDKPATH}

	install ${SDK_DEPLOY}/${BUILD_ARCH}-nativesdk-libc.tar.bz2 ${SDK_OUTPUT}/${SDKPATH}

	install -m 0755 ${COREBASE}/meta/files/ext-sdk-prepare.sh ${SDK_OUTPUT}/${SDKPATH}
}

# Since bitbake won't run as root it doesn't make sense to try and install
# the extensible sdk as root.
sdk_ext_preinst() {
	if [ "`id -u`" = "0" ]; then
		echo "ERROR: The extensible sdk cannot be installed as root."
		exit 1
	fi
	SDK_EXTENSIBLE="1"
}
SDK_PRE_INSTALL_COMMAND_task-populate-sdk-ext = "${sdk_ext_preinst}"

# FIXME this preparation should be done as part of the SDK construction
sdk_ext_postinst() {
	printf "\nExtracting buildtools...\n"
	cd $target_sdk_dir
	printf "buildtools\ny" | ./*buildtools-nativesdk-standalone* > /dev/null || ( printf 'ERROR: buildtools installation failed\n' ; exit 1 )

	# Make sure when the user sets up the environment, they also get
	# the buildtools-tarball tools in their path.
	env_setup_script="$target_sdk_dir/environment-setup-${REAL_MULTIMACH_TARGET_SYS}"
	echo ". $target_sdk_dir/buildtools/environment-setup*" >> $env_setup_script

	# Allow bitbake environment setup to be ran as part of this sdk.
	echo "export OE_SKIP_SDK_CHECK=1" >> $env_setup_script

	# A bit of another hack, but we need this in the path only for devtool
	# so put it at the end of $PATH.
	echo "export PATH=\$PATH:$target_sdk_dir/sysroots/${SDK_SYS}/${bindir_nativesdk}" >> $env_setup_script

	echo "printf 'SDK environment now set up; additionally you may now run devtool to perform development tasks.\nRun devtool --help for further details.\n'" >> $env_setup_script

	# Warn if trying to use external bitbake and the ext SDK together
	echo "(which bitbake > /dev/null 2>&1 && echo 'WARNING: attempting to use the extensible SDK in an environment set up to run bitbake - this may lead to unexpected results. Please source this script in a new shell session instead.') || true" >> $env_setup_script

	# For now this is where uninative.bbclass expects the tarball
	mv *-nativesdk-libc.tar.* $target_sdk_dir/`dirname ${oe_init_build_env_path}`

	if [ "$prepare_buildsystem" != "no" ]; then
		printf "Preparing build system...\n"
		# dash which is /bin/sh on Ubuntu will not preserve the
		# current working directory when first ran, nor will it set $1 when
		# sourcing a script. That is why this has to look so ugly.
		LOGFILE="$target_sdk_dir/preparing_build_system.log"
		sh -c ". buildtools/environment-setup* > $LOGFILE && cd $target_sdk_dir/`dirname ${oe_init_build_env_path}` && set $target_sdk_dir && . $target_sdk_dir/${oe_init_build_env_path} $target_sdk_dir >> $LOGFILE && $target_sdk_dir/ext-sdk-prepare.sh $target_sdk_dir '${SDK_TARGETS}' >> $LOGFILE 2>&1" || { echo "ERROR: SDK preparation failed: see $LOGFILE"; echo "printf 'ERROR: this SDK was not fully installed and needs reinstalling\n'" >> $env_setup_script ; exit 1 ; }
	fi
	rm -f $target_sdk_dir/ext-sdk-prepare.sh
	echo done
}

SDK_POST_INSTALL_COMMAND_task-populate-sdk-ext = "${sdk_ext_postinst}"

SDK_POSTPROCESS_COMMAND_prepend_task-populate-sdk-ext = "copy_buildsystem; install_tools; "

fakeroot python do_populate_sdk_ext() {
    # FIXME hopefully we can remove this restriction at some point, but uninative
    # currently forces this upon us
    if d.getVar('SDK_ARCH', True) != d.getVar('BUILD_ARCH', True):
        bb.fatal('The extensible SDK can currently only be built for the same architecture as the machine being built on - SDK_ARCH is set to %s (likely via setting SDKMACHINE) which is different from the architecture of the build machine (%s). Unable to continue.' % (d.getVar('SDK_ARCH', True), d.getVar('BUILD_ARCH', True)))

    bb.build.exec_func("do_populate_sdk", d)
}

def get_sdk_ext_rdepends(d):
    localdata = d.createCopy()
    localdata.appendVar('OVERRIDES', ':task-populate-sdk-ext')
    bb.data.update_data(localdata)
    return localdata.getVarFlag('do_populate_sdk', 'rdepends', True)

do_populate_sdk_ext[dirs] = "${@d.getVarFlag('do_populate_sdk', 'dirs', False)}"
do_populate_sdk_ext[depends] += "${@d.getVarFlag('do_populate_sdk', 'depends', False)}"
do_populate_sdk_ext[rdepends] = "${@get_sdk_ext_rdepends(d)}"
do_populate_sdk_ext[recrdeptask] += "${@d.getVarFlag('do_populate_sdk', 'recrdeptask', False)}"


do_populate_sdk_ext[depends] += "buildtools-tarball:do_populate_sdk uninative-tarball:do_populate_sdk"

do_populate_sdk_ext[rdepends] += "${@' '.join([x + ':do_build' for x in d.getVar('SDK_TARGETS', True).split()])}"
do_populate_sdk_ext[recrdeptask] += "do_populate_lic do_package_qa do_populate_sysroot do_deploy"

# Make sure codes change in copy_buildsystem can result in rebuilt
do_populate_sdk_ext[vardeps] += "copy_buildsystem"

addtask populate_sdk_ext
