#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Extensible SDK

inherit populate_sdk_base

# Used to override TOOLCHAIN_HOST_TASK in the eSDK case
TOOLCHAIN_HOST_TASK_ESDK = " \
    meta-environment-extsdk-${MACHINE} \
    "

SDK_RELOCATE_AFTER_INSTALL:task-populate-sdk-ext = "0"

SDK_EXT = ""
SDK_EXT:task-populate-sdk-ext = "-ext"

# Options are full or minimal
SDK_EXT_TYPE ?= "full"
SDK_INCLUDE_PKGDATA ?= "0"
SDK_INCLUDE_TOOLCHAIN ?= "${@'1' if d.getVar('SDK_EXT_TYPE') == 'full' else '0'}"
SDK_INCLUDE_NATIVESDK ?= "0"
SDK_INCLUDE_BUILDTOOLS ?= '1'

SDK_RECRDEP_TASKS ?= ""
SDK_CUSTOM_TEMPLATECONF ?= "0"

ESDK_LOCALCONF_ALLOW ?= ""
ESDK_LOCALCONF_REMOVE ?= "CONF_VERSION \
                             BB_NUMBER_THREADS \
                             BB_NUMBER_PARSE_THREADS \
                             PARALLEL_MAKE \
                             PRSERV_HOST \
                             SSTATE_MIRRORS \
                             DL_DIR \
                             SSTATE_DIR \
                             TMPDIR \
                             BB_SERVER_TIMEOUT \
                            "
ESDK_CLASS_INHERIT_DISABLE ?= "buildhistory icecc"
SDK_UPDATE_URL ?= ""

SDK_TARGETS ?= "${PN}"

def get_sdk_install_targets(d, images_only=False):
    sdk_install_targets = ''
    if images_only or d.getVar('SDK_EXT_TYPE') != 'minimal':
        sdk_install_targets = d.getVar('SDK_TARGETS')

        depd = d.getVar('BB_TASKDEPDATA', False)
        tasklist = bb.build.tasksbetween('do_image_complete', 'do_build', d)
        tasklist.remove('do_build')
        for v in depd.values():
            if v[1] in tasklist:
                if v[0] not in sdk_install_targets:
                    sdk_install_targets += ' {}'.format(v[0])

    if not images_only:
        if d.getVar('SDK_INCLUDE_PKGDATA') == '1':
            sdk_install_targets += ' meta-world-pkgdata:do_allpackagedata'
        if d.getVar('SDK_INCLUDE_TOOLCHAIN') == '1':
            sdk_install_targets += ' meta-extsdk-toolchain:do_populate_sysroot'

    return sdk_install_targets

get_sdk_install_targets[vardepsexclude] = "BB_TASKDEPDATA"

OE_INIT_ENV_SCRIPT ?= "oe-init-build-env"

# The files from COREBASE that you want preserved in the COREBASE copied
# into the sdk. This allows someone to have their own setup scripts in
# COREBASE be preserved as well as untracked files.
COREBASE_FILES ?= " \
    oe-init-build-env \
    scripts \
    LICENSE \
    .templateconf \
"

SDK_DIR:task-populate-sdk-ext = "${WORKDIR}/sdk-ext"
B:task-populate-sdk-ext = "${SDK_DIR}"
TOOLCHAINEXT_OUTPUTNAME ?= "${SDK_NAME}-toolchain-ext-${SDK_VERSION}"
TOOLCHAIN_OUTPUTNAME:task-populate-sdk-ext = "${TOOLCHAINEXT_OUTPUTNAME}"

SDK_EXT_TARGET_MANIFEST = "${SDK_DEPLOY}/${TOOLCHAINEXT_OUTPUTNAME}.target.manifest"
SDK_EXT_HOST_MANIFEST = "${SDK_DEPLOY}/${TOOLCHAINEXT_OUTPUTNAME}.host.manifest"

python write_target_sdk_ext_manifest () {
    from oe.sdk import get_extra_sdkinfo
    sstate_dir = d.expand('${SDK_OUTPUT}/${SDKPATH}/sstate-cache')
    extra_info = get_extra_sdkinfo(sstate_dir)

    target = d.getVar('TARGET_SYS')
    target_multimach = d.getVar('MULTIMACH_TARGET_SYS')
    real_target_multimach = d.getVar('REAL_MULTIMACH_TARGET_SYS')

    pkgs = {}
    os.makedirs(os.path.dirname(d.getVar('SDK_EXT_TARGET_MANIFEST')), exist_ok=True)
    with open(d.getVar('SDK_EXT_TARGET_MANIFEST'), 'w') as f:
        for fn in extra_info['filesizes']:
            info = fn.split(':')
            if info[2] in (target, target_multimach, real_target_multimach) \
                    or info[5] == 'allarch':
                if not info[1] in pkgs:
                    f.write("%s %s %s\n" % (info[1], info[2], info[3]))
                    pkgs[info[1]] = {}
}
python write_host_sdk_ext_manifest () {
    from oe.sdk import get_extra_sdkinfo
    sstate_dir = d.expand('${SDK_OUTPUT}/${SDKPATH}/sstate-cache')
    extra_info = get_extra_sdkinfo(sstate_dir)
    host = d.getVar('BUILD_SYS')
    with open(d.getVar('SDK_EXT_HOST_MANIFEST'), 'w') as f:
        for fn in extra_info['filesizes']:
            info = fn.split(':')
            if info[2] == host:
                f.write("%s %s %s\n" % (info[1], info[2], info[3]))
}

SDK_POSTPROCESS_COMMAND:append:task-populate-sdk-ext = " write_target_sdk_ext_manifest write_host_sdk_ext_manifest"    

SDK_TITLE:task-populate-sdk-ext = "${@d.getVar('DISTRO_NAME') or d.getVar('DISTRO')} Extensible SDK"

def clean_esdk_builddir(d, sdkbasepath):
    """Clean up traces of the fake build for create_filtered_tasklist()"""
    import shutil
    cleanpaths = ['cache', 'tmp']
    for pth in cleanpaths:
        fullpth = os.path.join(sdkbasepath, pth)
        if os.path.isdir(fullpth):
            shutil.rmtree(fullpth)
        elif os.path.isfile(fullpth):
            os.remove(fullpth)

def create_filtered_tasklist(d, sdkbasepath, tasklistfile, conf_initpath):
    """
    Create a filtered list of tasks. Also double-checks that the build system
    within the SDK basically works and required sstate artifacts are available.
    """
    import tempfile
    import shutil
    import oe.copy_buildsystem

    # Create a temporary build directory that we can pass to the env setup script
    shutil.copyfile(sdkbasepath + '/conf/local.conf', sdkbasepath + '/conf/local.conf.bak')
    try:
        with open(sdkbasepath + '/conf/local.conf', 'a') as f:
            # Force the use of sstate from the build system
            f.write('\nSSTATE_DIR:forcevariable = "%s"\n' % d.getVar('SSTATE_DIR'))
            f.write('SSTATE_MIRRORS:forcevariable = "file://universal/(.*) file://universal-4.9/\\1 file://universal-4.9/(.*) file://universal-4.8/\\1"\n')
            # Ensure TMPDIR is the default so that clean_esdk_builddir() can delete it
            f.write('TMPDIR:forcevariable = "${TOPDIR}/tmp"\n')
            # Drop uninative if the build isn't using it (or else NATIVELSBSTRING will
            # be different and we won't be able to find our native sstate)
            if not bb.data.inherits_class('uninative', d):
                f.write('INHERIT:remove = "uninative"\n')

        # Unfortunately the default SDKPATH (or even a custom value) may contain characters that bitbake
        # will not allow in its COREBASE path, so we need to rename the directory temporarily
        temp_sdkbasepath = d.getVar('SDK_OUTPUT') + '/tmp-renamed-sdk'
        # Delete any existing temp dir
        try:
            shutil.rmtree(temp_sdkbasepath)
        except FileNotFoundError:
            pass
        bb.utils.rename(sdkbasepath, temp_sdkbasepath)
        cmdprefix = '. %s .; ' % conf_initpath
        logfile = d.getVar('WORKDIR') + '/tasklist_bb_log.txt'
        try:
            oe.copy_buildsystem.check_sstate_task_list(d, get_sdk_install_targets(d), tasklistfile, cmdprefix=cmdprefix, cwd=temp_sdkbasepath, logfile=logfile)
        except bb.process.ExecutionError as e:
            msg = 'Failed to generate filtered task list for extensible SDK:\n%s' %  e.stdout.rstrip()
            if 'attempted to execute unexpectedly and should have been setscened' in e.stdout:
                msg += '\n----------\n\nNOTE: "attempted to execute unexpectedly and should have been setscened" errors indicate this may be caused by missing sstate artifacts that were likely produced in earlier builds, but have been subsequently deleted for some reason.\n'
            bb.fatal(msg)
        bb.utils.rename(temp_sdkbasepath, sdkbasepath)
        # Clean out residue of running bitbake, which check_sstate_task_list()
        # will effectively do
        clean_esdk_builddir(d, sdkbasepath)
    finally:
        localconf = sdkbasepath + '/conf/local.conf'
        if os.path.exists(localconf + '.bak'):
            os.replace(localconf + '.bak', localconf)

def copy_bitbake_and_layers(d, baseoutpath, derivative):
    oe_init_env_script = d.getVar('OE_INIT_ENV_SCRIPT')

    conf_bbpath = ''
    conf_initpath = ''
    core_meta_subdir = ''

    # Copy in all metadata layers + bitbake (as repositories)
    buildsystem = oe.copy_buildsystem.BuildSystem('extensible SDK', d)

    if derivative:
        workspace_name = 'orig-workspace'
    else:
        workspace_name = None

    corebase, sdkbblayers = buildsystem.copy_bitbake_and_layers(baseoutpath + '/layers', workspace_name)
    conf_bbpath = os.path.join('layers', corebase, 'bitbake')

    for path in os.listdir(baseoutpath + '/layers'):
        relpath = os.path.join('layers', path, oe_init_env_script)
        if os.path.exists(os.path.join(baseoutpath, relpath)):
            conf_initpath = relpath

        relpath = os.path.join('layers', path, 'scripts', 'esdk-tools', 'devtool')
        if os.path.exists(os.path.join(baseoutpath, relpath)):
            esdk_tools_path = os.path.dirname(relpath)

        relpath = os.path.join('layers', path, 'meta')
        if os.path.exists(os.path.join(baseoutpath, relpath, 'lib', 'oe')):
            core_meta_subdir = relpath

    d.setVar('oe_init_build_env_path', conf_initpath)
    d.setVar('esdk_tools_path', esdk_tools_path)

    return (conf_initpath, conf_bbpath, core_meta_subdir, sdkbblayers)

def write_devtool_config(d, baseoutpath, conf_bbpath, conf_initpath, core_meta_subdir):
    # Write out config file for devtool
    import configparser
    config = configparser.ConfigParser()
    config.add_section('General')
    config.set('General', 'bitbake_subdir', conf_bbpath)
    config.set('General', 'init_path', conf_initpath)
    config.set('General', 'core_meta_subdir', core_meta_subdir)
    config.add_section('SDK')
    config.set('SDK', 'sdk_targets', d.getVar('SDK_TARGETS'))
    updateurl = d.getVar('SDK_UPDATE_URL')
    if updateurl:
        config.set('SDK', 'updateserver', updateurl)
    bb.utils.mkdirhier(os.path.join(baseoutpath, 'conf'))
    with open(os.path.join(baseoutpath, 'conf', 'devtool.conf'), 'w') as f:
        config.write(f)

def write_unlocked_sigs(d, baseoutpath):
    unlockedsigs =  os.path.join(baseoutpath, 'conf', 'unlocked-sigs.inc')
    with open(unlockedsigs, 'w') as f:
        pass

def write_bblayers_conf(d, baseoutpath, sdkbblayers):
    # Create a layer for new recipes / appends
    bbpath = d.getVar('BBPATH')
    env = os.environ.copy()
    env['PYTHONDONTWRITEBYTECODE'] = '1'
    bb.process.run(['devtool', '--bbpath', bbpath, '--basepath', baseoutpath, 'create-workspace', '--layerseries', d.getVar("LAYERSERIES_CORENAMES"), '--create-only', os.path.join(baseoutpath, 'workspace')], env=env)

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

def copy_uninative(d, baseoutpath):
    import shutil

    uninative_checksum = None

    # Copy uninative tarball
    # For now this is where uninative.bbclass expects the tarball
    if bb.data.inherits_class('uninative', d):
        uninative_file = d.expand('${UNINATIVE_DLDIR}/' + d.getVarFlag("UNINATIVE_CHECKSUM", d.getVar("BUILD_ARCH")) + '/${UNINATIVE_TARBALL}')
        uninative_checksum = bb.utils.sha256_file(uninative_file)
        uninative_outdir = '%s/downloads/uninative/%s' % (baseoutpath, uninative_checksum)
        bb.utils.mkdirhier(uninative_outdir)
        shutil.copy(uninative_file, uninative_outdir)

    return uninative_checksum

def write_local_conf(d, baseoutpath, derivative, core_meta_subdir, uninative_checksum):
    import shutil

    #check if custome templateconf path is set
    use_custom_templateconf = d.getVar('SDK_CUSTOM_TEMPLATECONF')

    env_passthrough = (d.getVar('BB_ENV_PASSTHROUGH_ADDITIONS') or '').split()
    env_passthrough_values = {}

    # Create local.conf
    builddir = d.getVar('TOPDIR')
    if derivative and os.path.exists(builddir + '/conf/site.conf'):
        shutil.copyfile(builddir + '/conf/site.conf', baseoutpath + '/conf/site.conf')
    if derivative and os.path.exists(builddir + '/conf/auto.conf'):
        shutil.copyfile(builddir + '/conf/auto.conf', baseoutpath + '/conf/auto.conf')
    if derivative:
        shutil.copyfile(builddir + '/conf/local.conf', baseoutpath + '/conf/local.conf')
    else:
        local_conf_allowed = (d.getVar('ESDK_LOCALCONF_ALLOW') or '').split()
        local_conf_remove = (d.getVar('ESDK_LOCALCONF_REMOVE') or '').split()
        def handle_var(varname, origvalue, op, newlines):
            if varname in local_conf_remove or (origvalue.strip().startswith('/') and not varname in local_conf_allowed):
                newlines.append('# Removed original setting of %s\n' % varname)
                return None, op, 0, True
            else:
                if varname in env_passthrough:
                    env_passthrough_values[varname] = origvalue
                return origvalue, op, 0, True
        varlist = ['[^#=+ ]*']
        oldlines = []
        if os.path.exists(builddir + '/conf/site.conf'):
            with open(builddir + '/conf/site.conf', 'r') as f:
                oldlines += f.readlines()
        if os.path.exists(builddir + '/conf/auto.conf'):
            with open(builddir + '/conf/auto.conf', 'r') as f:
                oldlines += f.readlines()
        if os.path.exists(builddir + '/conf/local.conf'):
            with open(builddir + '/conf/local.conf', 'r') as f:
                oldlines += f.readlines()
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

            f.write('TMPDIR = "${TOPDIR}/tmp"\n')
            f.write('DL_DIR = "${TOPDIR}/downloads"\n')

            if bb.data.inherits_class('uninative', d):
               f.write('INHERIT += "%s"\n' % 'uninative')
               f.write('UNINATIVE_CHECKSUM[%s] = "%s"\n\n' % (d.getVar('BUILD_ARCH'), uninative_checksum))
            f.write('CONF_VERSION = "%s"\n\n' % d.getVar('CONF_VERSION', False))

            # Some classes are not suitable for SDK, remove them from INHERIT
            f.write('INHERIT:remove = "%s"\n' % d.getVar('ESDK_CLASS_INHERIT_DISABLE', False))

            # Bypass the default connectivity check if any
            f.write('CONNECTIVITY_CHECK_URIS = ""\n\n')

            # This warning will come out if reverse dependencies for a task
            # don't have sstate as well as the task itself. We already know
            # this will be the case for the extensible sdk, so turn off the
            # warning.
            f.write('SIGGEN_LOCKEDSIGS_SSTATE_EXISTS_CHECK = "none"\n\n')

            # Warn if the sigs in the locked-signature file don't match
            # the sig computed from the metadata.
            f.write('SIGGEN_LOCKEDSIGS_TASKSIG_CHECK = "warn"\n\n')

            # We want to be able to set this without a full reparse
            f.write('BB_HASHCONFIG_IGNORE_VARS:append = " SIGGEN_UNLOCKED_RECIPES"\n\n')

            # Set up which tasks are ignored for run on install
            f.write('BB_SETSCENE_ENFORCE_IGNORE_TASKS = "%:* *:do_shared_workdir *:do_rm_work wic-tools:* *:do_addto_recipe_sysroot"\n\n')

            # Hide the config information from bitbake output (since it's fixed within the SDK)
            f.write('BUILDCFG_HEADER = ""\n\n')

            # Write METADATA_REVISION
            # Needs distro override so it can override the value set in the bbclass code (later than local.conf)
            f.write('METADATA_REVISION:%s = "%s"\n\n' % (d.getVar('DISTRO'), d.getVar('METADATA_REVISION')))

            f.write('# Provide a flag to indicate we are in the EXT_SDK Context\n')
            f.write('WITHIN_EXT_SDK = "1"\n\n')

            # Map gcc-dependent uninative sstate cache for installer usage
            f.write('SSTATE_MIRRORS += " file://universal/(.*) file://universal-4.9/\\1 file://universal-4.9/(.*) file://universal-4.8/\\1"\n\n')

            if d.getVar("PRSERV_HOST"):
                # Override this, we now include PR data, so it should only point ot the local database
                f.write('PRSERV_HOST = "localhost:0"\n\n')

            # Allow additional config through sdk-extra.conf
            fn = bb.cookerdata.findConfigFile('sdk-extra.conf', d)
            if fn:
                with open(fn, 'r') as xf:
                    for line in xf:
                        f.write(line)

            # If you define a sdk_extraconf() function then it can contain additional config
            # (Though this is awkward; sdk-extra.conf should probably be used instead)
            extraconf = (d.getVar('sdk_extraconf') or '').strip()
            if extraconf:
                # Strip off any leading / trailing spaces
                for line in extraconf.splitlines():
                    f.write(line.strip() + '\n')

            f.write('require conf/locked-sigs.inc\n')
            f.write('require conf/unlocked-sigs.inc\n')

    # Copy multiple configurations if they exist in the users config directory
    if d.getVar('BBMULTICONFIG') is not None:
        bb.utils.mkdirhier(os.path.join(baseoutpath, 'conf', 'multiconfig'))
        for mc in d.getVar('BBMULTICONFIG').split():
            dest_stub = "/conf/multiconfig/%s.conf" % (mc,)
            if os.path.exists(builddir + dest_stub):
                shutil.copyfile(builddir + dest_stub, baseoutpath + dest_stub)

    # If PR Service is in use, we need to export this as well
    bb.note('Do we have a pr database?')
    if d.getVar("PRSERV_HOST"):
        bb.note('Writing PR database...')
        # Based on the code in classes/prexport.bbclass
        import oe.prservice
        #dump meta info of tables
        localdata = d.createCopy()
        localdata.setVar('PRSERV_DUMPOPT_COL', "1")
        localdata.setVar('PRSERV_DUMPDIR', os.path.join(baseoutpath, 'conf'))
        localdata.setVar('PRSERV_DUMPFILE', '${PRSERV_DUMPDIR}/prserv.inc')

        bb.note('PR Database write to %s' % (localdata.getVar('PRSERV_DUMPFILE')))

        retval = oe.prservice.prserv_dump_db(localdata)
        if not retval:
            bb.error("prexport_handler: export failed!")
            return
        (metainfo, datainfo) = retval
        oe.prservice.prserv_export_tofile(localdata, metainfo, datainfo, True)

    # Use templateconf.cfg file from builddir if exists
    if os.path.exists(builddir + '/conf/templateconf.cfg') and use_custom_templateconf == '1':
        shutil.copyfile(builddir + '/conf/templateconf.cfg', baseoutpath + '/conf/templateconf.cfg')
    else:
        # Write a templateconf.cfg
        with open(baseoutpath + '/conf/templateconf.cfg', 'w') as f:
            f.write('meta/conf/templates/default\n')
        os.makedirs(os.path.join(baseoutpath, core_meta_subdir, 'conf/templates/default'), exist_ok=True)

    # Ensure any variables set from the external environment (by way of
    # BB_ENV_PASSTHROUGH_ADDITIONS) are set in the SDK's configuration
    extralines = []
    for name, value in env_passthrough_values.items():
        actualvalue = d.getVar(name) or ''
        if value != actualvalue:
            extralines.append('%s = "%s"\n' % (name, actualvalue))
    if extralines:
        with open(baseoutpath + '/conf/local.conf', 'a') as f:
            f.write('\n')
            f.write('# Extra settings from environment:\n')
            for line in extralines:
                f.write(line)
            f.write('\n')

def prepare_locked_cache(d, baseoutpath, derivative, conf_initpath):
    import shutil

    # Filter the locked signatures file to just the sstate tasks we are interested in
    excluded_targets = get_sdk_install_targets(d, images_only=True)
    sigfile = d.getVar('WORKDIR') + '/locked-sigs.inc'
    lockedsigs_pruned = baseoutpath + '/conf/locked-sigs.inc'
    #nativesdk-only sigfile to merge into locked-sigs.inc
    sdk_include_nativesdk = (d.getVar("SDK_INCLUDE_NATIVESDK") == '1')
    nativesigfile = d.getVar('WORKDIR') + '/locked-sigs_nativesdk.inc'
    nativesigfile_pruned = d.getVar('WORKDIR') + '/locked-sigs_nativesdk_pruned.inc'

    if sdk_include_nativesdk:
        oe.copy_buildsystem.prune_lockedsigs([],
                                             excluded_targets.split(),
                                             nativesigfile,
                                             True,
                                             nativesigfile_pruned)

        oe.copy_buildsystem.merge_lockedsigs([],
                                             sigfile,
                                             nativesigfile_pruned,
                                             sigfile)

    oe.copy_buildsystem.prune_lockedsigs([],
                                         excluded_targets.split(),
                                         sigfile,
                                         False,
                                         lockedsigs_pruned)

    sstate_out = baseoutpath + '/sstate-cache'
    bb.utils.remove(sstate_out, True)

    # uninative.bbclass sets NATIVELSBSTRING to 'universal%s' % oe.utils.host_gcc_version(d)
    fixedlsbstring = "universal%s" % oe.utils.host_gcc_version(d) if bb.data.inherits_class('uninative', d) else ""

    sdk_include_toolchain = (d.getVar('SDK_INCLUDE_TOOLCHAIN') == '1')
    sdk_ext_type = d.getVar('SDK_EXT_TYPE')
    if (sdk_ext_type != 'minimal' or sdk_include_toolchain or derivative) and not sdk_include_nativesdk:
        # Create the filtered task list used to generate the sstate cache shipped with the SDK
        tasklistfn = d.getVar('WORKDIR') + '/tasklist.txt'
        create_filtered_tasklist(d, baseoutpath, tasklistfn, conf_initpath)
    else:
        tasklistfn = None

    # Add packagedata if enabled
    if d.getVar('SDK_INCLUDE_PKGDATA') == '1':
        lockedsigs_base = d.getVar('WORKDIR') + '/locked-sigs-base.inc'
        lockedsigs_copy = d.getVar('WORKDIR') + '/locked-sigs-copy.inc'
        shutil.move(lockedsigs_pruned, lockedsigs_base)
        oe.copy_buildsystem.merge_lockedsigs(['do_packagedata'],
                                             lockedsigs_base,
                                             d.getVar('STAGING_DIR_HOST') + '/world-pkgdata/locked-sigs-pkgdata.inc',
                                             lockedsigs_pruned,
                                             lockedsigs_copy)

    if sdk_include_toolchain:
        lockedsigs_base = d.getVar('WORKDIR') + '/locked-sigs-base2.inc'
        lockedsigs_toolchain = d.expand("${STAGING_DIR}/${TUNE_PKGARCH}/meta-extsdk-toolchain/locked-sigs/locked-sigs-extsdk-toolchain.inc")
        shutil.move(lockedsigs_pruned, lockedsigs_base)
        oe.copy_buildsystem.merge_lockedsigs([],
                                             lockedsigs_base,
                                             lockedsigs_toolchain,
                                             lockedsigs_pruned)
        oe.copy_buildsystem.create_locked_sstate_cache(lockedsigs_toolchain,
                                                       d.getVar('SSTATE_DIR'),
                                                       sstate_out, d,
                                                       fixedlsbstring,
                                                       filterfile=tasklistfn)

    if sdk_ext_type == 'minimal':
        if derivative:
            # Assume the user is not going to set up an additional sstate
            # mirror, thus we need to copy the additional artifacts (from
            # workspace recipes) into the derivative SDK
            lockedsigs_orig = d.getVar('TOPDIR') + '/conf/locked-sigs.inc'
            if os.path.exists(lockedsigs_orig):
                lockedsigs_extra = d.getVar('WORKDIR') + '/locked-sigs-extra.inc'
                oe.copy_buildsystem.merge_lockedsigs(None,
                                                     lockedsigs_orig,
                                                     lockedsigs_pruned,
                                                     None,
                                                     lockedsigs_extra)
                oe.copy_buildsystem.create_locked_sstate_cache(lockedsigs_extra,
                                                               d.getVar('SSTATE_DIR'),
                                                               sstate_out, d,
                                                               fixedlsbstring,
                                                               filterfile=tasklistfn)
    else:
        oe.copy_buildsystem.create_locked_sstate_cache(lockedsigs_pruned,
                                                       d.getVar('SSTATE_DIR'),
                                                       sstate_out, d,
                                                       fixedlsbstring,
                                                       filterfile=tasklistfn)

    # We don't need sstate do_package files
    for root, dirs, files in os.walk(sstate_out):
        for name in files:
            if name.endswith("_package.tar.zst"):
                f = os.path.join(root, name)
                os.remove(f)

def write_manifest(d, baseoutpath):
    import glob

    # Write manifest file
    # Note: at the moment we cannot include the env setup script here to keep
    # it updated, since it gets modified during SDK installation (see
    # sdk_ext_postinst() below) thus the checksum we take here would always
    # be different.
    manifest_file_list = ['conf/*']
    if d.getVar('BBMULTICONFIG') is not None:
        manifest_file_list.append('conf/multiconfig/*')

    esdk_manifest_excludes = (d.getVar('ESDK_MANIFEST_EXCLUDES') or '').split()
    esdk_manifest_excludes_list = []
    for exclude_item in esdk_manifest_excludes:
        esdk_manifest_excludes_list += glob.glob(os.path.join(baseoutpath, exclude_item))
    manifest_file = os.path.join(baseoutpath, 'conf', 'sdk-conf-manifest')
    with open(manifest_file, 'w') as f:
        for item in manifest_file_list:
            for fn in glob.glob(os.path.join(baseoutpath, item)):
                if fn == manifest_file or os.path.isdir(fn):
                    continue
                if fn in esdk_manifest_excludes_list:
                    continue
                chksum = bb.utils.sha256_file(fn)
                f.write('%s\t%s\n' % (chksum, os.path.relpath(fn, baseoutpath)))


python copy_buildsystem () {
    import oe.copy_buildsystem

    baseoutpath = d.getVar('SDK_OUTPUT') + '/' + d.getVar('SDKPATH')

    # Determine if we're building a derivative extensible SDK (from devtool build-sdk)
    derivative = (d.getVar('SDK_DERIVATIVE') or '') == '1'

    conf_initpath, conf_bbpath, core_meta_subdir, sdkbblayers = copy_bitbake_and_layers(d, baseoutpath, derivative)

    write_devtool_config(d, baseoutpath, conf_bbpath, conf_initpath, core_meta_subdir)

    write_unlocked_sigs(d, baseoutpath)

    write_bblayers_conf(d, baseoutpath, sdkbblayers)

    uninative_checksum = copy_uninative(d, baseoutpath)

    write_local_conf(d, baseoutpath, derivative, core_meta_subdir, uninative_checksum)

    prepare_locked_cache(d, baseoutpath, derivative, conf_initpath)

    write_manifest(d, baseoutpath)

}

def get_current_buildtools(d):
    """Get the file name of the current buildtools installer"""
    import glob
    btfiles = glob.glob(os.path.join(d.getVar('SDK_DEPLOY'), '*-buildtools-nativesdk-standalone-*.sh'))
    btfiles.sort(key=os.path.getctime)
    return os.path.basename(btfiles[-1])

def get_sdk_required_utilities(buildtools_fn, d):
    """Find required utilities that aren't provided by the buildtools"""
    sanity_required_utilities = (d.getVar('SANITY_REQUIRED_UTILITIES') or '').split()
    sanity_required_utilities.append(d.expand('${BUILD_PREFIX}gcc'))
    sanity_required_utilities.append(d.expand('${BUILD_PREFIX}g++'))
    if buildtools_fn:
        buildtools_installer = os.path.join(d.getVar('SDK_DEPLOY'), buildtools_fn)
        filelist, _ = bb.process.run('%s -l' % buildtools_installer)
    else:
        buildtools_installer = None
        filelist = ""
    localdata = bb.data.createCopy(d)
    localdata.setVar('SDKPATH', '.')
    sdkpathnative = localdata.getVar('SDKPATHNATIVE')
    sdkbindirs = [localdata.getVar('bindir_nativesdk'),
                  localdata.getVar('sbindir_nativesdk'),
                  localdata.getVar('base_bindir_nativesdk'),
                  localdata.getVar('base_sbindir_nativesdk')]
    for line in filelist.splitlines():
        splitline = line.split()
        if len(splitline) > 5:
            fn = splitline[5]
            if not fn.startswith('./'):
                fn = './%s' % fn
            if fn.startswith(sdkpathnative):
                relpth = '/' + os.path.relpath(fn, sdkpathnative)
                for bindir in sdkbindirs:
                    if relpth.startswith(bindir):
                        relpth = os.path.relpath(relpth, bindir)
                        if relpth in sanity_required_utilities:
                            sanity_required_utilities.remove(relpth)
                        break
    return ' '.join(sanity_required_utilities)

install_tools() {
	touch ${SDK_OUTPUT}/${SDKPATH}/.devtoolbase

	# find latest buildtools-tarball and install it
	if [ -n "${SDK_BUILDTOOLS_INSTALLER}" ]; then
		install ${SDK_DEPLOY}/${SDK_BUILDTOOLS_INSTALLER} ${SDK_OUTPUT}/${SDKPATH}
	fi

	install -m 0644 ${COREBASE}/meta/files/ext-sdk-prepare.py ${SDK_OUTPUT}/${SDKPATH}
}
do_populate_sdk_ext[file-checksums] += "${COREBASE}/meta/files/ext-sdk-prepare.py:True"

sdk_ext_preinst() {
	# Since bitbake won't run as root it doesn't make sense to try and install
	# the extensible sdk as root.
	if [ "`id -u`" = "0" ]; then
		echo "ERROR: The extensible sdk cannot be installed as root."
		exit 1
	fi
	if ! command -v locale > /dev/null; then
		echo "ERROR: The installer requires the locale command, please install it first"
		exit 1
	fi
        # Check setting of LC_ALL set above
	canonicalised_locale=`echo $LC_ALL | sed 's/UTF-8/utf8/'`
	if ! locale -a | grep -q $canonicalised_locale ; then
		echo "ERROR: the installer requires the $LC_ALL locale to be installed (but not selected), please install it first"
		exit 1
	fi
	# The relocation script used by buildtools installer requires python
	if ! command -v python3 > /dev/null; then
		echo "ERROR: The installer requires python3, please install it first"
		exit 1
	fi
	missing_utils=""
	for util in ${SDK_REQUIRED_UTILITIES}; do
		if ! command -v $util > /dev/null; then
			missing_utils="$missing_utils $util"
		fi
	done
	if [ -n "$missing_utils" ] ; then
		echo "ERROR: the SDK requires the following missing utilities, please install them: $missing_utils"
		exit 1
	fi
	SDK_EXTENSIBLE="1"
	if [ "$publish" = "1" ] && [ "${SDK_EXT_TYPE}" = "minimal" ] ; then
		EXTRA_TAR_OPTIONS="$EXTRA_TAR_OPTIONS --exclude=sstate-cache"
	fi
}
SDK_PRE_INSTALL_COMMAND:task-populate-sdk-ext = "${sdk_ext_preinst}"

# FIXME this preparation should be done as part of the SDK construction
sdk_ext_postinst() {
	printf "\nExtracting buildtools...\n"
	cd $target_sdk_dir
	env_setup_script="$target_sdk_dir/environment-setup-${REAL_MULTIMACH_TARGET_SYS}"
        if [ -n "${SDK_BUILDTOOLS_INSTALLER}" ]; then
		printf "buildtools\ny" | ./${SDK_BUILDTOOLS_INSTALLER} > buildtools.log || { printf 'ERROR: buildtools installation failed:\n' ; cat buildtools.log ; echo "printf 'ERROR: this SDK was not fully installed and needs reinstalling\n'" >> $env_setup_script ; exit 1 ; }

		# Delete the buildtools tar file since it won't be used again
		rm -f ./${SDK_BUILDTOOLS_INSTALLER}
		# We don't need the log either since it succeeded
		rm -f buildtools.log

		# Make sure when the user sets up the environment, they also get
		# the buildtools-tarball tools in their path.
		echo "# Save and reset OECORE_NATIVE_SYSROOT as buildtools may change it" >> $env_setup_script
		echo "SAVED=\"\$OECORE_NATIVE_SYSROOT\"" >> $env_setup_script
		echo ". $target_sdk_dir/buildtools/environment-setup*" >> $env_setup_script
		echo "export OECORE_NATIVE_SYSROOT=\"\$SAVED\"" >> $env_setup_script
	fi

	# Allow bitbake environment setup to be ran as part of this sdk.
	echo "export OE_SKIP_SDK_CHECK=1" >> $env_setup_script
	# Work around runqemu not knowing how to get this information within the eSDK
	echo "export DEPLOY_DIR_IMAGE=$target_sdk_dir/tmp/${@os.path.relpath(d.getVar('DEPLOY_DIR_IMAGE'), d.getVar('TMPDIR'))}" >> $env_setup_script

	# A bit of another hack, but we need this in the path only for devtool
	# so put it at the end of $PATH.
	echo "export PATH=\"$target_sdk_dir/${esdk_tools_path}:\$PATH\"" >> $env_setup_script

	echo "printf 'SDK environment now set up; additionally you may now run devtool to perform development tasks.\nRun devtool --help for further details.\n'" >> $env_setup_script

	# Warn if trying to use external bitbake and the ext SDK together
	echo "(which bitbake > /dev/null 2>&1 && echo 'WARNING: attempting to use the extensible SDK in an environment set up to run bitbake - this may lead to unexpected results. Please source this script in a new shell session instead.') || true" >> $env_setup_script

	if [ "$prepare_buildsystem" != "no" -a -n "${SDK_BUILDTOOLS_INSTALLER}" ]; then
		printf "Preparing build system...\n"
		# dash which is /bin/sh on Ubuntu will not preserve the
		# current working directory when first ran, nor will it set $1 when
		# sourcing a script. That is why this has to look so ugly.
		LOGFILE="$target_sdk_dir/preparing_build_system.log"
		sh -c ". buildtools/environment-setup* > $LOGFILE 2>&1 && cd $target_sdk_dir/`dirname ${oe_init_build_env_path}` && set $target_sdk_dir && . $target_sdk_dir/${oe_init_build_env_path} $target_sdk_dir >> $LOGFILE 2>&1 && python3 $target_sdk_dir/ext-sdk-prepare.py $LOGFILE '${SDK_INSTALL_TARGETS}'" || { echo "printf 'ERROR: this SDK was not fully installed and needs reinstalling\n'" >> $env_setup_script ; exit 1 ; }
	fi
	if [ -e $target_sdk_dir/ext-sdk-prepare.py ]; then
		rm $target_sdk_dir/ext-sdk-prepare.py
	fi
	echo done
}

SDK_POST_INSTALL_COMMAND:task-populate-sdk-ext = "${sdk_ext_postinst}"

SDK_POSTPROCESS_COMMAND:prepend:task-populate-sdk-ext = "copy_buildsystem install_tools "

SDK_INSTALL_TARGETS = ""
fakeroot python do_populate_sdk_ext() {
    # FIXME hopefully we can remove this restriction at some point, but uninative
    # currently forces this upon us
    if d.getVar('SDK_ARCH') != d.getVar('BUILD_ARCH'):
        bb.fatal('The extensible SDK can currently only be built for the same architecture as the machine being built on - SDK_ARCH is set to %s (likely via setting SDKMACHINE) which is different from the architecture of the build machine (%s). Unable to continue.' % (d.getVar('SDK_ARCH'), d.getVar('BUILD_ARCH')))

    # FIXME hopefully we can remove this restriction at some point, but the eSDK
    # can only be built for the primary (default) multiconfig
    if d.getVar('BB_CURRENT_MC') != '':
        bb.fatal('The extensible SDK can currently only be built for the default multiconfig.  Currently trying to build for %s.' % d.getVar('BB_CURRENT_MC'))

    # eSDK dependencies don't use the traditional variables and things don't work properly if they are set
    d.setVar("TOOLCHAIN_HOST_TASK", "${TOOLCHAIN_HOST_TASK_ESDK}")
    d.setVar("TOOLCHAIN_TARGET_TASK", "")

    d.setVar('SDK_INSTALL_TARGETS', get_sdk_install_targets(d))
    if d.getVar('SDK_INCLUDE_BUILDTOOLS') == '1':
        buildtools_fn = get_current_buildtools(d)
    else:
        buildtools_fn = None
    d.setVar('SDK_REQUIRED_UTILITIES', get_sdk_required_utilities(buildtools_fn, d))
    d.setVar('SDK_BUILDTOOLS_INSTALLER', buildtools_fn)
    d.setVar('SDKDEPLOYDIR', '${SDKEXTDEPLOYDIR}')
    # ESDKs have a libc from the buildtools so ensure we don't ship linguas twice
    d.delVar('SDKIMAGE_LINGUAS')
    if d.getVar("SDK_INCLUDE_NATIVESDK") == '1':
        generate_nativesdk_lockedsigs(d)
    populate_sdk_common(d)
}

def generate_nativesdk_lockedsigs(d):
    import oe.copy_buildsystem
    sigfile = d.getVar('WORKDIR') + '/locked-sigs_nativesdk.inc'
    oe.copy_buildsystem.generate_locked_sigs(sigfile, d)

def get_ext_sdk_depends(d):
    # Note: the deps varflag is a list not a string, so we need to specify expand=False
    deps = d.getVarFlag('do_image_complete', 'deps', False)
    pn = d.getVar('PN')
    deplist = ['%s:%s' % (pn, dep) for dep in deps]
    tasklist = bb.build.tasksbetween('do_image_complete', 'do_build', d)
    tasklist.append('do_rootfs')
    for task in tasklist:
        deplist.extend((d.getVarFlag(task, 'depends') or '').split())
    return ' '.join(deplist)

python do_sdk_depends() {
    # We have to do this separately in its own task so we avoid recursing into
    # dependencies we don't need to (e.g. buildtools-tarball) and bringing those
    # into the SDK's sstate-cache
    import oe.copy_buildsystem
    sigfile = d.getVar('WORKDIR') + '/locked-sigs.inc'
    oe.copy_buildsystem.generate_locked_sigs(sigfile, d)
}
addtask sdk_depends

do_sdk_depends[dirs] = "${WORKDIR}"
do_sdk_depends[depends] = "${@get_ext_sdk_depends(d)} meta-extsdk-toolchain:do_populate_sysroot"
do_sdk_depends[recrdeptask] = "${@d.getVarFlag('do_populate_sdk', 'recrdeptask', False)}"
do_sdk_depends[recrdeptask] += "do_populate_lic do_package_qa do_populate_sysroot do_deploy ${SDK_RECRDEP_TASKS}"
do_sdk_depends[rdepends] = "${@' '.join([x + ':do_package_write_${IMAGE_PKGTYPE} ' + x + ':do_packagedata' for x in d.getVar('TOOLCHAIN_HOST_TASK_ESDK').split()])}"

do_populate_sdk_ext[dirs] = "${@d.getVarFlag('do_populate_sdk', 'dirs', False)}"

do_populate_sdk_ext[depends] = "${@d.getVarFlag('do_populate_sdk', 'depends', False)} \
                                ${@'buildtools-tarball:do_populate_sdk' if d.getVar('SDK_INCLUDE_BUILDTOOLS') == '1' else ''} \
                                ${@'meta-world-pkgdata:do_collect_packagedata' if d.getVar('SDK_INCLUDE_PKGDATA') == '1' else ''} \
                                ${@'meta-extsdk-toolchain:do_locked_sigs' if d.getVar('SDK_INCLUDE_TOOLCHAIN') == '1' else ''}"

# We must avoid depending on do_build here if rm_work.bbclass is active,
# because otherwise do_rm_work may run before do_populate_sdk_ext itself.
# We can't mark do_populate_sdk_ext and do_sdk_depends as having to
# run before do_rm_work, because then they would also run as part
# of normal builds.
do_populate_sdk_ext[rdepends] += "${@' '.join([x + ':' + (d.getVar('RM_WORK_BUILD_WITHOUT') or 'do_build') for x in d.getVar('SDK_TARGETS').split()])}"

# Make sure code changes can result in rebuild
do_populate_sdk_ext[vardeps] += "copy_buildsystem \
                                 sdk_ext_postinst"

# Since any change in the metadata of any layer should cause a rebuild of the
# sdk(since the layers are put in the sdk) set the task to nostamp so it
# always runs.
do_populate_sdk_ext[nostamp] = "1"

SDKEXTDEPLOYDIR = "${WORKDIR}/deploy-${PN}-populate-sdk-ext"

SSTATETASKS += "do_populate_sdk_ext"
SSTATE_SKIP_CREATION:task-populate-sdk-ext = '1'
do_populate_sdk_ext[cleandirs] = "${SDKEXTDEPLOYDIR}"
do_populate_sdk_ext[sstate-inputdirs] = "${SDKEXTDEPLOYDIR}"
do_populate_sdk_ext[sstate-outputdirs] = "${SDK_DEPLOY}"
do_populate_sdk_ext[stamp-extra-info] = "${MACHINE_ARCH}"

addtask populate_sdk_ext after do_sdk_depends
