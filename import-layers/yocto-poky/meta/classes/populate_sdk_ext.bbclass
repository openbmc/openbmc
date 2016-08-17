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

SDK_EXT = ""
SDK_EXT_task-populate-sdk-ext = "-ext"

# Options are full or minimal
SDK_EXT_TYPE ?= "full"

SDK_RECRDEP_TASKS ?= ""

SDK_LOCAL_CONF_WHITELIST ?= ""
SDK_LOCAL_CONF_BLACKLIST ?= "CONF_VERSION \
                             BB_NUMBER_THREADS \
                             PARALLEL_MAKE \
                             PRSERV_HOST \
                             SSTATE_MIRRORS \
                            "
SDK_INHERIT_BLACKLIST ?= "buildhistory icecc"
SDK_UPDATE_URL ?= ""

SDK_TARGETS ?= "${PN}"

def get_sdk_install_targets(d):
    sdk_install_targets = ''
    if d.getVar('SDK_EXT_TYPE', True) != 'minimal':
        sdk_install_targets = d.getVar('SDK_TARGETS', True)

        depd = d.getVar('BB_TASKDEPDATA', False)
        for v in depd.itervalues():
            if v[1] == 'do_image_complete':
                if v[0] not in sdk_install_targets:
                    sdk_install_targets += ' {}'.format(v[0])

    if d.getVar('SDK_INCLUDE_PKGDATA', True) == '1':
        sdk_install_targets += ' meta-world-pkgdata:do_allpackagedata'

    return sdk_install_targets

get_sdk_install_targets[vardepsexclude] = "BB_TASKDEPDATA"

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
TOOLCHAINEXT_OUTPUTNAME = "${SDK_NAME}-toolchain-ext-${SDK_VERSION}"
TOOLCHAIN_OUTPUTNAME_task-populate-sdk-ext = "${TOOLCHAINEXT_OUTPUTNAME}"

SDK_EXT_TARGET_MANIFEST = "${SDK_DEPLOY}/${TOOLCHAINEXT_OUTPUTNAME}.target.manifest"
SDK_EXT_HOST_MANIFEST = "${SDK_DEPLOY}/${TOOLCHAINEXT_OUTPUTNAME}.host.manifest"

SDK_TITLE_task-populate-sdk-ext = "${@d.getVar('DISTRO_NAME', True) or d.getVar('DISTRO', True)} Extensible SDK"

python copy_buildsystem () {
    import re
    import shutil
    import glob
    import oe.copy_buildsystem

    oe_init_env_script = d.getVar('OE_INIT_ENV_SCRIPT', True)

    conf_bbpath = ''
    conf_initpath = ''
    core_meta_subdir = ''

    # Copy in all metadata layers + bitbake (as repositories)
    buildsystem = oe.copy_buildsystem.BuildSystem('extensible SDK', d)
    baseoutpath = d.getVar('SDK_OUTPUT', True) + '/' + d.getVar('SDKPATH', True)

    # Determine if we're building a derivative extensible SDK (from devtool build-sdk)
    derivative = (d.getVar('SDK_DERIVATIVE', True) or '') == '1'
    if derivative:
        workspace_name = 'orig-workspace'
    else:
        workspace_name = None
    layers_copied = buildsystem.copy_bitbake_and_layers(baseoutpath + '/layers', workspace_name)

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

    unlockedsigs =  os.path.join(baseoutpath, 'conf', 'unlocked-sigs.inc')
    with open(unlockedsigs, 'w') as f:
        pass

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

        # LCONF_VERSION may not be set, for example when using meta-poky
        # so don't error if it isn't found
        lconf_version = d.getVar('LCONF_VERSION', False)
        if lconf_version is not None:
            f.write('LCONF_VERSION = "%s"\n\n' % lconf_version)

        f.write('BBPATH = "$' + '{TOPDIR}"\n')
        f.write('SDKBASEMETAPATH = "$' + '{TOPDIR}"\n')
        f.write('BBLAYERS := " \\\n')
        for layerrelpath in sdkbblayers:
            f.write('    $' + '{SDKBASEMETAPATH}/layers/%s \\\n' % layerrelpath)
        f.write('    $' + '{SDKBASEMETAPATH}/workspace \\\n')
        f.write('    "\n')

    env_whitelist = (d.getVar('BB_ENV_EXTRAWHITE', True) or '').split()
    env_whitelist_values = {}

    # Create local.conf
    builddir = d.getVar('TOPDIR', True)
    if derivative:
        shutil.copyfile(builddir + '/conf/local.conf', baseoutpath + '/conf/local.conf')
    else:
        local_conf_whitelist = (d.getVar('SDK_LOCAL_CONF_WHITELIST', True) or '').split()
        local_conf_blacklist = (d.getVar('SDK_LOCAL_CONF_BLACKLIST', True) or '').split()
        def handle_var(varname, origvalue, op, newlines):
            if varname in local_conf_blacklist or (origvalue.strip().startswith('/') and not varname in local_conf_whitelist):
                newlines.append('# Removed original setting of %s\n' % varname)
                return None, op, 0, True
            else:
                if varname in env_whitelist:
                    env_whitelist_values[varname] = origvalue
                return origvalue, op, 0, True
        varlist = ['[^#=+ ]*']
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
            # Write a newline just in case there's none at the end of the original
            f.write('\n')

            f.write('INHERIT += "%s"\n\n' % 'uninative')
            f.write('CONF_VERSION = "%s"\n\n' % d.getVar('CONF_VERSION', False))

            # Some classes are not suitable for SDK, remove them from INHERIT
            f.write('INHERIT_remove = "%s"\n' % d.getVar('SDK_INHERIT_BLACKLIST', False))

            # Bypass the default connectivity check if any
            f.write('CONNECTIVITY_CHECK_URIS = ""\n\n')

            # This warning will come out if reverse dependencies for a task
            # don't have sstate as well as the task itself. We already know
            # this will be the case for the extensible sdk, so turn off the
            # warning.
            f.write('SIGGEN_LOCKEDSIGS_SSTATE_EXISTS_CHECK = "none"\n\n')

            # Error if the sigs in the locked-signature file don't match
            # the sig computed from the metadata.
            f.write('SIGGEN_LOCKEDSIGS_TASKSIG_CHECK = "error"\n\n')

            # Hide the config information from bitbake output (since it's fixed within the SDK)
            f.write('BUILDCFG_HEADER = ""\n')

            # Allow additional config through sdk-extra.conf
            fn = bb.cookerdata.findConfigFile('sdk-extra.conf', d)
            if fn:
                with open(fn, 'r') as xf:
                    for line in xf:
                        f.write(line)

            # If you define a sdk_extraconf() function then it can contain additional config
            # (Though this is awkward; sdk-extra.conf should probably be used instead)
            extraconf = (d.getVar('sdk_extraconf', True) or '').strip()
            if extraconf:
                # Strip off any leading / trailing spaces
                for line in extraconf.splitlines():
                    f.write(line.strip() + '\n')

            f.write('require conf/locked-sigs.inc\n')
            f.write('require conf/unlocked-sigs.inc\n')

    if os.path.exists(builddir + '/conf/auto.conf'):
        if derivative:
            shutil.copyfile(builddir + '/conf/auto.conf', baseoutpath + '/conf/auto.conf')
        else:
            with open(builddir + '/conf/auto.conf', 'r') as f:
                oldlines = f.readlines()
            (updated, newlines) = bb.utils.edit_metadata(oldlines, varlist, handle_var)
            with open(baseoutpath + '/conf/auto.conf', 'w') as f:
                f.write('# WARNING: this configuration has been automatically generated and in\n')
                f.write('# most cases should not be edited. If you need more flexibility than\n')
                f.write('# this configuration provides, it is strongly suggested that you set\n')
                f.write('# up a proper instance of the full build system and use that instead.\n\n')
                for line in newlines:
                    if line.strip() and not line.startswith('#'):
                        f.write(line)

    # Ensure any variables set from the external environment (by way of
    # BB_ENV_EXTRAWHITE) are set in the SDK's configuration
    extralines = []
    for name, value in env_whitelist_values.iteritems():
        actualvalue = d.getVar(name, True) or ''
        if value != actualvalue:
            extralines.append('%s = "%s"\n' % (name, actualvalue))
    if extralines:
        with open(baseoutpath + '/conf/local.conf', 'a') as f:
            f.write('\n')
            f.write('# Extra settings from environment:\n')
            for line in extralines:
                f.write(line)
            f.write('\n')

    # Filter the locked signatures file to just the sstate tasks we are interested in
    excluded_targets = d.getVar('SDK_TARGETS', True)
    sigfile = d.getVar('WORKDIR', True) + '/locked-sigs.inc'
    lockedsigs_pruned = baseoutpath + '/conf/locked-sigs.inc'
    oe.copy_buildsystem.prune_lockedsigs([],
                                         excluded_targets.split(),
                                         sigfile,
                                         lockedsigs_pruned)

    sstate_out = baseoutpath + '/sstate-cache'
    bb.utils.remove(sstate_out, True)
    # uninative.bbclass sets NATIVELSBSTRING to 'universal'
    fixedlsbstring = 'universal'

    # Add packagedata if enabled
    if d.getVar('SDK_INCLUDE_PKGDATA', True) == '1':
        lockedsigs_base = d.getVar('WORKDIR', True) + '/locked-sigs-base.inc'
        lockedsigs_copy = d.getVar('WORKDIR', True) + '/locked-sigs-copy.inc'
        shutil.move(lockedsigs_pruned, lockedsigs_base)
        oe.copy_buildsystem.merge_lockedsigs(['do_packagedata'],
                                             lockedsigs_base,
                                             d.getVar('STAGING_DIR_HOST', True) + '/world-pkgdata/locked-sigs-pkgdata.inc',
                                             lockedsigs_pruned,
                                             lockedsigs_copy)

    if d.getVar('SDK_EXT_TYPE', True) == 'minimal':
        if derivative:
            # Assume the user is not going to set up an additional sstate
            # mirror, thus we need to copy the additional artifacts (from
            # workspace recipes) into the derivative SDK
            lockedsigs_orig = d.getVar('TOPDIR', True) + '/conf/locked-sigs.inc'
            if os.path.exists(lockedsigs_orig):
                lockedsigs_extra = d.getVar('WORKDIR', True) + '/locked-sigs-extra.inc'
                oe.copy_buildsystem.merge_lockedsigs(None,
                                                     lockedsigs_orig,
                                                     lockedsigs_pruned,
                                                     None,
                                                     lockedsigs_extra)
                oe.copy_buildsystem.create_locked_sstate_cache(lockedsigs_extra,
                                                               d.getVar('SSTATE_DIR', True),
                                                               sstate_out, d,
                                                               fixedlsbstring)
    else:
        oe.copy_buildsystem.create_locked_sstate_cache(lockedsigs_pruned,
                                                       d.getVar('SSTATE_DIR', True),
                                                       sstate_out, d,
                                                       fixedlsbstring)

    # We don't need sstate do_package files
    for root, dirs, files in os.walk(sstate_out):
        for name in files:
            if name.endswith("_package.tgz"):
                f = os.path.join(root, name)
                os.remove(f)

    # Write manifest file
    # Note: at the moment we cannot include the env setup script here to keep
    # it updated, since it gets modified during SDK installation (see
    # sdk_ext_postinst() below) thus the checksum we take here would always
    # be different.
    manifest_file_list = ['conf/*']
    manifest_file = os.path.join(baseoutpath, 'conf', 'sdk-conf-manifest')
    with open(manifest_file, 'w') as f:
        for item in manifest_file_list:
            for fn in glob.glob(os.path.join(baseoutpath, item)):
                if fn == manifest_file:
                    continue
                chksum = bb.utils.sha256_file(fn)
                f.write('%s\t%s\n' % (chksum, os.path.relpath(fn, baseoutpath)))
}

def extsdk_get_buildtools_filename(d):
    return '*-buildtools-nativesdk-standalone-*.sh'

install_tools() {
	install -d ${SDK_OUTPUT}/${SDKPATHNATIVE}${bindir_nativesdk}
	lnr ${SDK_OUTPUT}/${SDKPATH}/${scriptrelpath}/devtool ${SDK_OUTPUT}/${SDKPATHNATIVE}${bindir_nativesdk}/devtool
	lnr ${SDK_OUTPUT}/${SDKPATH}/${scriptrelpath}/recipetool ${SDK_OUTPUT}/${SDKPATHNATIVE}${bindir_nativesdk}/recipetool
	touch ${SDK_OUTPUT}/${SDKPATH}/.devtoolbase

	localconf=${SDK_OUTPUT}/${SDKPATH}/conf/local.conf

	# find latest buildtools-tarball and install it
	buildtools_path=`ls -t1 ${SDK_DEPLOY}/${@extsdk_get_buildtools_filename(d)} | head -n1`
	install $buildtools_path ${SDK_OUTPUT}/${SDKPATH}

	# For now this is where uninative.bbclass expects the tarball
	chksum=`sha256sum ${SDK_DEPLOY}/${BUILD_ARCH}-nativesdk-libc.tar.bz2 | cut -f 1 -d ' '`
	install -d ${SDK_OUTPUT}/${SDKPATH}/downloads/uninative/$chksum/
	install ${SDK_DEPLOY}/${BUILD_ARCH}-nativesdk-libc.tar.bz2 ${SDK_OUTPUT}/${SDKPATH}/downloads/uninative/$chksum/
	echo "UNINATIVE_CHECKSUM[${BUILD_ARCH}] = '$chksum'" >> ${SDK_OUTPUT}/${SDKPATH}/conf/local.conf

	install -m 0644 ${COREBASE}/meta/files/ext-sdk-prepare.py ${SDK_OUTPUT}/${SDKPATH}
}
do_populate_sdk_ext[file-checksums] += "${COREBASE}/meta/files/ext-sdk-prepare.py:True"

# Since bitbake won't run as root it doesn't make sense to try and install
# the extensible sdk as root.
sdk_ext_preinst() {
	if [ "`id -u`" = "0" ]; then
		echo "ERROR: The extensible sdk cannot be installed as root."
		exit 1
	fi
	SDK_EXTENSIBLE="1"
	if [ "$publish" = "1" ] ; then
		EXTRA_TAR_OPTIONS="$EXTRA_TAR_OPTIONS --exclude=ext-sdk-prepare.py"
		if [ "${SDK_EXT_TYPE}" = "minimal" ] ; then
			EXTRA_TAR_OPTIONS="$EXTRA_TAR_OPTIONS --exclude=sstate-cache"
		fi
	fi
}
SDK_PRE_INSTALL_COMMAND_task-populate-sdk-ext = "${sdk_ext_preinst}"

# FIXME this preparation should be done as part of the SDK construction
sdk_ext_postinst() {
	printf "\nExtracting buildtools...\n"
	cd $target_sdk_dir
	printf "buildtools\ny" | ./*buildtools-nativesdk-standalone* > /dev/null || ( printf 'ERROR: buildtools installation failed\n' ; exit 1 )

	# Delete the buildtools tar file since it won't be used again
	rm ./*buildtools-nativesdk-standalone*.sh -f

	# Make sure when the user sets up the environment, they also get
	# the buildtools-tarball tools in their path.
	env_setup_script="$target_sdk_dir/environment-setup-${REAL_MULTIMACH_TARGET_SYS}"
	echo ". $target_sdk_dir/buildtools/environment-setup*" >> $env_setup_script

	# Allow bitbake environment setup to be ran as part of this sdk.
	echo "export OE_SKIP_SDK_CHECK=1" >> $env_setup_script

	# A bit of another hack, but we need this in the path only for devtool
	# so put it at the end of $PATH.
	echo "export PATH=$target_sdk_dir/sysroots/${SDK_SYS}${bindir_nativesdk}:\$PATH" >> $env_setup_script

	echo "printf 'SDK environment now set up; additionally you may now run devtool to perform development tasks.\nRun devtool --help for further details.\n'" >> $env_setup_script

	# Warn if trying to use external bitbake and the ext SDK together
	echo "(which bitbake > /dev/null 2>&1 && echo 'WARNING: attempting to use the extensible SDK in an environment set up to run bitbake - this may lead to unexpected results. Please source this script in a new shell session instead.') || true" >> $env_setup_script

	if [ "$prepare_buildsystem" != "no" ]; then
		printf "Preparing build system...\n"
		# dash which is /bin/sh on Ubuntu will not preserve the
		# current working directory when first ran, nor will it set $1 when
		# sourcing a script. That is why this has to look so ugly.
		LOGFILE="$target_sdk_dir/preparing_build_system.log"
		sh -c ". buildtools/environment-setup* > $LOGFILE && cd $target_sdk_dir/`dirname ${oe_init_build_env_path}` && set $target_sdk_dir && . $target_sdk_dir/${oe_init_build_env_path} $target_sdk_dir >> $LOGFILE && python $target_sdk_dir/ext-sdk-prepare.py '${SDK_INSTALL_TARGETS}' >> $LOGFILE 2>&1" || { echo "ERROR: SDK preparation failed: see $LOGFILE"; echo "printf 'ERROR: this SDK was not fully installed and needs reinstalling\n'" >> $env_setup_script ; exit 1 ; }
		rm $target_sdk_dir/ext-sdk-prepare.py
	fi
	echo done
}

SDK_POST_INSTALL_COMMAND_task-populate-sdk-ext = "${sdk_ext_postinst}"

SDK_POSTPROCESS_COMMAND_prepend_task-populate-sdk-ext = "copy_buildsystem; install_tools; "

SDK_INSTALL_TARGETS = ""
fakeroot python do_populate_sdk_ext() {
    # FIXME hopefully we can remove this restriction at some point, but uninative
    # currently forces this upon us
    if d.getVar('SDK_ARCH', True) != d.getVar('BUILD_ARCH', True):
        bb.fatal('The extensible SDK can currently only be built for the same architecture as the machine being built on - SDK_ARCH is set to %s (likely via setting SDKMACHINE) which is different from the architecture of the build machine (%s). Unable to continue.' % (d.getVar('SDK_ARCH', True), d.getVar('BUILD_ARCH', True)))

    d.setVar('SDK_INSTALL_TARGETS', get_sdk_install_targets(d))

    bb.build.exec_func("do_populate_sdk", d)
}

def get_ext_sdk_depends(d):
    return d.getVarFlag('do_rootfs', 'depends', True) + ' ' + d.getVarFlag('do_build', 'depends', True)

python do_sdk_depends() {
    # We have to do this separately in its own task so we avoid recursing into
    # dependencies we don't need to (e.g. buildtools-tarball) and bringing those
    # into the SDK's sstate-cache
    import oe.copy_buildsystem
    sigfile = d.getVar('WORKDIR', True) + '/locked-sigs.inc'
    oe.copy_buildsystem.generate_locked_sigs(sigfile, d)
}
addtask sdk_depends

do_sdk_depends[dirs] = "${WORKDIR}"
do_sdk_depends[depends] = "${@get_ext_sdk_depends(d)}"
do_sdk_depends[recrdeptask] = "${@d.getVarFlag('do_populate_sdk', 'recrdeptask', False)}"
do_sdk_depends[recrdeptask] += "do_populate_lic do_package_qa do_populate_sysroot do_deploy ${SDK_RECRDEP_TASKS}"
do_sdk_depends[rdepends] = "${@get_sdk_ext_rdepends(d)}"

def get_sdk_ext_rdepends(d):
    localdata = d.createCopy()
    localdata.appendVar('OVERRIDES', ':task-populate-sdk-ext')
    bb.data.update_data(localdata)
    return localdata.getVarFlag('do_populate_sdk', 'rdepends', True)

do_populate_sdk_ext[dirs] = "${@d.getVarFlag('do_populate_sdk', 'dirs', False)}"

do_populate_sdk_ext[depends] = "${@d.getVarFlag('do_populate_sdk', 'depends', False)} \
                                buildtools-tarball:do_populate_sdk uninative-tarball:do_populate_sdk \
                                ${@'meta-world-pkgdata:do_collect_packagedata' if d.getVar('SDK_INCLUDE_PKGDATA', True) == '1' else ''}"

do_populate_sdk_ext[rdepends] += "${@' '.join([x + ':do_build' for x in d.getVar('SDK_TARGETS', True).split()])}"

# Make sure code changes can result in rebuild
do_populate_sdk_ext[vardeps] += "copy_buildsystem \
                                 sdk_ext_postinst"

# Since any change in the metadata of any layer should cause a rebuild of the
# sdk(since the layers are put in the sdk) set the task to nostamp so it
# always runs.
do_populate_sdk_ext[nostamp] = "1"

addtask populate_sdk_ext after do_sdk_depends
