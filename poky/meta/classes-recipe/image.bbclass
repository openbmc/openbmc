#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

IMAGE_CLASSES ??= ""

# rootfs bootstrap install
# warning -  image-container resets this
ROOTFS_BOOTSTRAP_INSTALL = "run-postinsts"

# Handle inherits of any of the image classes we need
IMGCLASSES = "rootfs_${IMAGE_PKGTYPE} image_types ${IMAGE_CLASSES}"
# Only Linux SDKs support populate_sdk_ext, fall back to populate_sdk_base
# in the non-Linux SDK_OS case, such as mingw32
inherit populate_sdk_base
IMGCLASSES += "${@['', 'populate_sdk_ext']['linux' in d.getVar("SDK_OS")]}"
IMGCLASSES += "${@bb.utils.contains_any('IMAGE_FSTYPES', 'live iso hddimg', 'image-live', '', d)}"
IMGCLASSES += "${@bb.utils.contains('IMAGE_FSTYPES', 'container', 'image-container', '', d)}"
IMGCLASSES += "image_types_wic"
IMGCLASSES += "rootfs-postcommands"
IMGCLASSES += "image-postinst-intercepts"
IMGCLASSES += "overlayfs-etc"
inherit_defer ${IMGCLASSES}

TOOLCHAIN_TARGET_TASK += "${PACKAGE_INSTALL}"
TOOLCHAIN_TARGET_TASK_ATTEMPTONLY += "${PACKAGE_INSTALL_ATTEMPTONLY}"
POPULATE_SDK_POST_TARGET_COMMAND += "rootfs_sysroot_relativelinks"

LICENSE ?= "MIT"
PACKAGES = ""
DEPENDS += "${@' '.join(["%s-qemuwrapper-cross" % m for m in d.getVar("MULTILIB_VARIANTS").split()])} qemuwrapper-cross depmodwrapper-cross cross-localedef-native"
RDEPENDS += "${PACKAGE_INSTALL} ${LINGUAS_INSTALL} ${IMAGE_INSTALL_DEBUGFS}"
RRECOMMENDS += "${PACKAGE_INSTALL_ATTEMPTONLY}"
PATH:prepend = "${@":".join(all_multilib_tune_values(d, 'STAGING_BINDIR_CROSS').split())}:"

INHIBIT_DEFAULT_DEPS = "1"

# IMAGE_FEATURES may contain any available package group
IMAGE_FEATURES ?= ""
IMAGE_FEATURES[type] = "list"
IMAGE_FEATURES[validitems] += "read-only-rootfs read-only-rootfs-delayed-postinsts stateless-rootfs empty-root-password allow-empty-password allow-root-login serial-autologin-root post-install-logging overlayfs-etc"

# Generate companion debugfs?
IMAGE_GEN_DEBUGFS ?= "0"

# These packages will be installed as additional into debug rootfs
IMAGE_INSTALL_DEBUGFS ?= ""

# These packages will be removed from a read-only rootfs after all other
# packages have been installed
ROOTFS_RO_UNNEEDED ??= "update-rc.d base-passwd shadow ${VIRTUAL-RUNTIME_update-alternatives} ${ROOTFS_BOOTSTRAP_INSTALL}"

# packages to install from features
FEATURE_INSTALL = "${@' '.join(oe.packagegroup.required_packages(oe.data.typed_value('IMAGE_FEATURES', d), d))}"
FEATURE_INSTALL[vardepvalue] = "${FEATURE_INSTALL}"
FEATURE_INSTALL_OPTIONAL = "${@' '.join(oe.packagegroup.optional_packages(oe.data.typed_value('IMAGE_FEATURES', d), d))}"
FEATURE_INSTALL_OPTIONAL[vardepvalue] = "${FEATURE_INSTALL_OPTIONAL}"

# Define some very basic feature package groups
FEATURE_PACKAGES_package-management = "${ROOTFS_PKGMANAGE}"
SPLASH ?= "${@bb.utils.contains("MACHINE_FEATURES", "screen", "psplash", "", d)}"
FEATURE_PACKAGES_splash = "${SPLASH}"

IMAGE_INSTALL_COMPLEMENTARY = '${@complementary_globs("IMAGE_FEATURES", d)}'

def check_image_features(d):
    valid_features = (d.getVarFlag('IMAGE_FEATURES', 'validitems') or "").split()
    valid_features += d.getVarFlags('COMPLEMENTARY_GLOB').keys()
    for var in d:
       if var.startswith("FEATURE_PACKAGES_"):
           valid_features.append(var[17:])
    valid_features.sort()

    features = set(oe.data.typed_value('IMAGE_FEATURES', d))
    for feature in features:
        if feature not in valid_features:
            if bb.utils.contains('EXTRA_IMAGE_FEATURES', feature, True, False, d):
                raise bb.parse.SkipRecipe("'%s' in IMAGE_FEATURES (added via EXTRA_IMAGE_FEATURES) is not a valid image feature. Valid features: %s" % (feature, ' '.join(valid_features)))
            else:
                raise bb.parse.SkipRecipe("'%s' in IMAGE_FEATURES is not a valid image feature. Valid features: %s" % (feature, ' '.join(valid_features)))

IMAGE_INSTALL ?= ""
IMAGE_INSTALL[type] = "list"
export PACKAGE_INSTALL ?= "${IMAGE_INSTALL} ${ROOTFS_BOOTSTRAP_INSTALL} ${FEATURE_INSTALL}"
PACKAGE_INSTALL_ATTEMPTONLY ?= "${FEATURE_INSTALL_OPTIONAL}"

IMGDEPLOYDIR = "${WORKDIR}/deploy-${PN}-image-complete"

IMGMANIFESTDIR = "${WORKDIR}/image-task-manifest"

IMAGE_OUTPUT_MANIFEST_DIR = "${WORKDIR}/deploy-image-output-manifest"
IMAGE_OUTPUT_MANIFEST = "${IMAGE_OUTPUT_MANIFEST_DIR}/manifest.json"

# Images are generally built explicitly, do not need to be part of world.
EXCLUDE_FROM_WORLD = "1"

USE_DEVFS ?= "1"
USE_DEPMOD ?= "1"

PID = "${@os.getpid()}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
SSTATE_ARCHS_TUNEPKG = "${@all_multilib_tune_values(d, 'TUNE_PKGARCH')}"

LDCONFIGDEPEND ?= "ldconfig-native:do_populate_sysroot"
LDCONFIGDEPEND:libc-musl = ""

# This is needed to have depmod data in PKGDATA_DIR,
# but if you're building small initramfs image
# e.g. to include it in your kernel, you probably
# don't want this dependency, which is causing dependency loop
KERNELDEPMODDEPEND ?= "virtual/kernel:do_packagedata"

do_rootfs[depends] += " \
    makedevs-native:do_populate_sysroot virtual/fakeroot-native:do_populate_sysroot ${LDCONFIGDEPEND} \
    virtual/update-alternatives-native:do_populate_sysroot update-rc.d-native:do_populate_sysroot \
    ${KERNELDEPMODDEPEND} \
"
do_rootfs[recrdeptask] += "do_packagedata"

def rootfs_command_variables(d):
    return ['ROOTFS_POSTPROCESS_COMMAND','ROOTFS_PREPROCESS_COMMAND','ROOTFS_POSTINSTALL_COMMAND','ROOTFS_POSTUNINSTALL_COMMAND','OPKG_PREPROCESS_COMMANDS','OPKG_POSTPROCESS_COMMANDS','IMAGE_POSTPROCESS_COMMAND',
            'IMAGE_PREPROCESS_COMMAND','RPM_PREPROCESS_COMMANDS','RPM_POSTPROCESS_COMMANDS','DEB_PREPROCESS_COMMANDS','DEB_POSTPROCESS_COMMANDS']

python () {
    variables = rootfs_command_variables(d)
    for var in variables:
        d.setVarFlag(var, 'vardeps', d.getVar(var))
}

def rootfs_variables(d):
    from oe.rootfs import variable_depends
    variables = ['IMAGE_DEVICE_TABLE','IMAGE_DEVICE_TABLES','BUILD_IMAGES_FROM_FEEDS','IMAGE_TYPES_MASKED','IMAGE_ROOTFS_ALIGNMENT','IMAGE_OVERHEAD_FACTOR','IMAGE_ROOTFS_SIZE','IMAGE_ROOTFS_EXTRA_SPACE',
                 'IMAGE_ROOTFS_MAXSIZE','IMAGE_NAME','IMAGE_LINK_NAME','IMAGE_MANIFEST','DEPLOY_DIR_IMAGE','IMAGE_FSTYPES','IMAGE_INSTALL_COMPLEMENTARY','IMAGE_LINGUAS', 'IMAGE_LINGUAS_COMPLEMENTARY', 'IMAGE_LOCALES_ARCHIVE',
                 'MULTILIBRE_ALLOW_REP','MULTILIB_TEMP_ROOTFS','MULTILIB_VARIANTS','MULTILIBS','ALL_MULTILIB_PACKAGE_ARCHS','MULTILIB_GLOBAL_VARIANTS','BAD_RECOMMENDATIONS','NO_RECOMMENDATIONS',
                 'PACKAGE_ARCHS','PACKAGE_CLASSES','TARGET_VENDOR','TARGET_ARCH','TARGET_OS','OVERRIDES','BBEXTENDVARIANT','FEED_DEPLOYDIR_BASE_URI','INTERCEPT_DIR','USE_DEVFS',
                 'CONVERSIONTYPES', 'IMAGE_GEN_DEBUGFS', 'ROOTFS_RO_UNNEEDED', 'IMGDEPLOYDIR', 'PACKAGE_EXCLUDE_COMPLEMENTARY', 'REPRODUCIBLE_TIMESTAMP_ROOTFS', 'IMAGE_INSTALL_DEBUGFS']
    variables.extend(rootfs_command_variables(d))
    variables.extend(variable_depends(d))
    return " ".join(variables)

do_rootfs[vardeps] += "${@rootfs_variables(d)}"

# This is needed to have kernel image in DEPLOY_DIR.
# This follows many common usecases and user expectations.
# But if you are building an image which doesn't need the kernel image at all,
# you can unset this variable manually.
KERNEL_DEPLOY_DEPEND ?= "virtual/kernel:do_deploy"
do_build[depends] += "${KERNEL_DEPLOY_DEPEND}"


python () {
    def extraimage_getdepends(task):
        deps = ""
        for dep in (d.getVar('EXTRA_IMAGEDEPENDS') or "").split():
            if ":" in dep:
                deps += " %s " % (dep)
            else:
                deps += " %s:%s" % (dep, task)
        return deps

    d.appendVarFlag('do_image_complete', 'depends', extraimage_getdepends('do_populate_sysroot'))

    deps = " " + imagetypes_getdepends(d)
    d.appendVarFlag('do_rootfs', 'depends', deps)

    #process IMAGE_FEATURES, we must do this before runtime_mapping_rename
    #Check for replaces image features
    features = set(oe.data.typed_value('IMAGE_FEATURES', d))
    remain_features = features.copy()
    for feature in features:
        replaces = set((d.getVar("IMAGE_FEATURES_REPLACES_%s" % feature) or "").split())
        remain_features -= replaces

    #Check for conflict image features
    for feature in remain_features:
        conflicts = set((d.getVar("IMAGE_FEATURES_CONFLICTS_%s" % feature) or "").split())
        temp = conflicts & remain_features
        if temp:
            bb.fatal("%s contains conflicting IMAGE_FEATURES %s %s" % (d.getVar('PN'), feature, ' '.join(list(temp))))

    d.setVar('IMAGE_FEATURES', ' '.join(sorted(list(remain_features))))

    check_image_features(d)
}

IMAGE_POSTPROCESS_COMMAND ?= ""

IMAGE_LINGUAS ??= ""

LINGUAS_INSTALL ?= "${@" ".join(map(lambda s: "locale-base-%s" % s, d.getVar('IMAGE_LINGUAS').split()))}"

# per default create a locale archive
IMAGE_LOCALES_ARCHIVE ?= '1'

# Prefer image, but use the fallback files for lookups if the image ones
# aren't yet available.
PSEUDO_PASSWD = "${IMAGE_ROOTFS}:${STAGING_DIR_NATIVE}"

PSEUDO_IGNORE_PATHS .= ",${WORKDIR}/intercept_scripts,${WORKDIR}/oe-rootfs-repo,${WORKDIR}/sstate-build-image_complete"

PACKAGE_EXCLUDE ??= ""
PACKAGE_EXCLUDE[type] = "list"

fakeroot python do_rootfs () {
    from oe.rootfs import create_rootfs
    from oe.manifest import create_manifest
    import logging
    import oe.packagedata

    logger = d.getVar('BB_TASK_LOGGER', False)
    if logger:
        logcatcher = bb.utils.LogCatcher()
        logger.addHandler(logcatcher)
    else:
        logcatcher = None

    # NOTE: if you add, remove or significantly refactor the stages of this
    # process then you should recalculate the weightings here. This is quite
    # easy to do - just change the MultiStageProgressReporter line temporarily
    # to pass debug=True as the last parameter and you'll get a printout of
    # the weightings as well as a map to the lines where next_stage() was
    # called. Of course this isn't critical, but it helps to keep the progress
    # reporting accurate.
    stage_weights = [1, 203, 354, 186, 65, 4228, 1, 353, 49, 330, 382, 23, 1]
    progress_reporter = bb.progress.MultiStageProgressReporter(d, stage_weights)
    progress_reporter.next_stage()

    # Handle package exclusions
    excl_pkgs = d.getVar("PACKAGE_EXCLUDE").split()
    inst_pkgs = d.getVar("PACKAGE_INSTALL").split()
    inst_attempt_pkgs = d.getVar("PACKAGE_INSTALL_ATTEMPTONLY").split()

    d.setVar('PACKAGE_INSTALL_ORIG', ' '.join(inst_pkgs))
    d.setVar('PACKAGE_INSTALL_ATTEMPTONLY', ' '.join(inst_attempt_pkgs))

    for pkg in excl_pkgs:
        if pkg in inst_pkgs:
            bb.warn("Package %s, set to be excluded, is in %s PACKAGE_INSTALL (%s).  It will be removed from the list." % (pkg, d.getVar('PN'), inst_pkgs))
            inst_pkgs.remove(pkg)

        if pkg in inst_attempt_pkgs:
            bb.warn("Package %s, set to be excluded, is in %s PACKAGE_INSTALL_ATTEMPTONLY (%s).  It will be removed from the list." % (pkg, d.getVar('PN'), inst_pkgs))
            inst_attempt_pkgs.remove(pkg)

    d.setVar("PACKAGE_INSTALL", ' '.join(inst_pkgs))
    d.setVar("PACKAGE_INSTALL_ATTEMPTONLY", ' '.join(inst_attempt_pkgs))

    # Ensure we handle package name remapping
    # We have to delay the runtime_mapping_rename until just before rootfs runs
    # otherwise, the multilib renaming could step in and squash any fixups that
    # may have occurred.
    pn = d.getVar('PN')
    oe.packagedata.runtime_mapping_rename("PACKAGE_INSTALL", pn, d)
    oe.packagedata.runtime_mapping_rename("PACKAGE_INSTALL_ATTEMPTONLY", pn, d)
    oe.packagedata.runtime_mapping_rename("BAD_RECOMMENDATIONS", pn, d)

    # Generate the initial manifest
    create_manifest(d)

    progress_reporter.next_stage()

    # generate rootfs
    d.setVarFlag('REPRODUCIBLE_TIMESTAMP_ROOTFS', 'export', '1')
    create_rootfs(d, progress_reporter=progress_reporter, logcatcher=logcatcher)

    progress_reporter.finish()
}
do_rootfs[dirs] = "${TOPDIR}"
do_rootfs[cleandirs] += "${IMAGE_ROOTFS} ${IMGDEPLOYDIR} ${S}"
do_rootfs[file-checksums] += "${POSTINST_INTERCEPT_CHECKSUMS}"
addtask rootfs after do_prepare_recipe_sysroot

fakeroot python do_image () {
    from oe.utils import execute_pre_post_process

    d.setVarFlag('REPRODUCIBLE_TIMESTAMP_ROOTFS', 'export', '1')
    pre_process_cmds = d.getVar("IMAGE_PREPROCESS_COMMAND")

    execute_pre_post_process(d, pre_process_cmds)
}
do_image[dirs] = "${TOPDIR}"
do_image[cleandirs] += "${IMGMANIFESTDIR}"
addtask do_image after do_rootfs

fakeroot python do_image_complete () {
    from oe.utils import execute_pre_post_process
    from pathlib import Path
    import json

    post_process_cmds = d.getVar("IMAGE_POSTPROCESS_COMMAND")

    execute_pre_post_process(d, post_process_cmds)

    image_manifest_dir = Path(d.getVar('IMGMANIFESTDIR'))

    data = []

    for manifest_path in image_manifest_dir.glob("*.json"):
        with manifest_path.open("r") as f:
            data.extend(json.load(f))

    with open(d.getVar("IMAGE_OUTPUT_MANIFEST"), "w") as f:
        json.dump(data, f)
}
do_image_complete[dirs] = "${TOPDIR}"
SSTATETASKS += "do_image_complete"
SSTATE_SKIP_CREATION:task-image-complete = '1'
do_image_complete[sstate-inputdirs] = "${IMGDEPLOYDIR}"
do_image_complete[sstate-outputdirs] = "${DEPLOY_DIR_IMAGE}"
do_image_complete[stamp-extra-info] = "${MACHINE_ARCH}"
do_image_complete[sstate-plaindirs] += "${IMAGE_OUTPUT_MANIFEST_DIR}"
do_image_complete[dirs] += "${IMAGE_OUTPUT_MANIFEST_DIR}"
addtask do_image_complete after do_image before do_build
python do_image_complete_setscene () {
    sstate_setscene(d)
}
addtask do_image_complete_setscene

# Add image-level QA/sanity checks to IMAGE_QA_COMMANDS
#
# IMAGE_QA_COMMANDS += " \
#     image_check_everything_ok \
# "
#
# This task runs all functions in IMAGE_QA_COMMANDS after the rootfs
# construction has completed in order to validate the resulting image.
#
# The functions should use ${IMAGE_ROOTFS} to find the unpacked rootfs
# directory, which if QA passes will be the basis for the images.
#
# The functions are expected to call oe.qa.handle_error() to report any
# problems.
fakeroot python do_image_qa () {
    qa_cmds = (d.getVar('IMAGE_QA_COMMANDS') or '').split()

    for cmd in qa_cmds:
        bb.build.exec_func(cmd, d)

    oe.qa.exit_if_errors(d)
}
addtask do_image_qa after do_rootfs before do_image

SSTATETASKS += "do_image_qa"
SSTATE_SKIP_CREATION:task-image-qa = '1'
do_image_qa[sstate-inputdirs] = ""
do_image_qa[sstate-outputdirs] = ""
python do_image_qa_setscene () {
    sstate_setscene(d)
}
addtask do_image_qa_setscene

def setup_debugfs_variables(d):
    d.appendVar('IMAGE_ROOTFS', '-dbg')
    if d.getVar('IMAGE_LINK_NAME'):
        d.appendVar('IMAGE_LINK_NAME', '-dbg')
    d.appendVar('IMAGE_NAME','-dbg')
    d.setVar('IMAGE_BUILDING_DEBUGFS', 'true')
    debugfs_image_fstypes = d.getVar('IMAGE_FSTYPES_DEBUGFS')
    if debugfs_image_fstypes:
        d.setVar('IMAGE_FSTYPES', debugfs_image_fstypes)

python setup_debugfs () {
    setup_debugfs_variables(d)
}

python () {
    vardeps = set()
    # We allow CONVERSIONTYPES to have duplicates. That avoids breaking
    # derived distros when OE-core or some other layer independently adds
    # the same type. There is still only one command for each type, but
    # presumably the commands will do the same when the type is the same,
    # even when added in different places.
    #
    # Without de-duplication, gen_conversion_cmds() below
    # would create the same compression command multiple times.
    ctypes = set(d.getVar('CONVERSIONTYPES').split())
    old_overrides = d.getVar('OVERRIDES', False)

    def _image_base_type(type):
        basetype = type
        for ctype in ctypes:
            if type.endswith("." + ctype):
                basetype = type[:-len("." + ctype)]
                break

        if basetype != type:
            # New base type itself might be generated by a conversion command.
            basetype = _image_base_type(basetype)

        return basetype

    basetypes = {}
    alltypes = d.getVar('IMAGE_FSTYPES').split()
    typedeps = {}

    if d.getVar('IMAGE_GEN_DEBUGFS') == "1":
        debugfs_fstypes = d.getVar('IMAGE_FSTYPES_DEBUGFS').split()
        for t in debugfs_fstypes:
            alltypes.append("debugfs_" + t)

    def _add_type(t):
        baset = _image_base_type(t)
        input_t = t
        if baset not in basetypes:
            basetypes[baset]= []
        if t not in basetypes[baset]:
            basetypes[baset].append(t)
        debug = ""
        if t.startswith("debugfs_"):
            t = t[8:]
            debug = "debugfs_"
        deps = (d.getVar('IMAGE_TYPEDEP:' + t) or "").split()
        vardeps.add('IMAGE_TYPEDEP:' + t)
        if baset not in typedeps:
            typedeps[baset] = set()
        deps = [debug + dep for dep in deps]
        for dep in deps:
            if dep not in alltypes:
                alltypes.append(dep)
            _add_type(dep)
            basedep = _image_base_type(dep)
            typedeps[baset].add(basedep)

        if baset != input_t:
            _add_type(baset)

    for t in alltypes[:]:
        _add_type(t)

    d.appendVarFlag('do_image', 'vardeps', ' '.join(vardeps))

    maskedtypes = (d.getVar('IMAGE_TYPES_MASKED') or "").split()
    maskedtypes = [dbg + t for t in maskedtypes for dbg in ("", "debugfs_")]

    for t in basetypes:
        vardeps = set()
        cmds = []
        subimages = []
        realt = t

        if t in maskedtypes:
            continue

        localdata = bb.data.createCopy(d)
        debug = ""
        if t.startswith("debugfs_"):
            setup_debugfs_variables(localdata)
            debug = "setup_debugfs "
            realt = t[8:]
        localdata.setVar('OVERRIDES', '%s:%s' % (realt, old_overrides))
        localdata.setVar('type', realt)
        # Delete DATETIME so we don't expand any references to it now
        # This means the task's hash can be stable rather than having hardcoded
        # date/time values. It will get expanded at execution time.
        # Similarly TMPDIR since otherwise we see QA stamp comparision problems
        # Expand PV else it can trigger get_srcrev which can fail due to these variables being unset
        localdata.setVar('PV', d.getVar('PV'))
        localdata.delVar('DATETIME')
        localdata.delVar('DATE')
        localdata.delVar('TMPDIR')
        localdata.delVar('IMAGE_VERSION_SUFFIX')
        vardepsexclude = (d.getVarFlag('IMAGE_CMD:' + realt, 'vardepsexclude') or '').split()
        for dep in vardepsexclude:
            localdata.delVar(dep)

        image_cmd = localdata.getVar("IMAGE_CMD")
        vardeps.add('IMAGE_CMD:' + realt)
        if image_cmd:
            cmds.append("\t" + image_cmd)
        else:
            bb.fatal("No IMAGE_CMD defined for IMAGE_FSTYPES entry '%s' - possibly invalid type name or missing support class" % t)
        cmds.append(localdata.expand("\tcd ${IMGDEPLOYDIR}"))

        # Since a copy of IMAGE_CMD:xxx will be inlined within do_image_xxx,
        # prevent a redundant copy of IMAGE_CMD:xxx being emitted as a function.
        d.delVarFlag('IMAGE_CMD:' + realt, 'func')

        rm_tmp_images = set()
        def gen_conversion_cmds(bt):
            for ctype in sorted(ctypes):
                if bt.endswith("." + ctype):
                    type = bt[0:-len(ctype) - 1]
                    if type.startswith("debugfs_"):
                        type = type[8:]
                    # Create input image first.
                    gen_conversion_cmds(type)
                    localdata.setVar('type', type)
                    cmd = "\t" + localdata.getVar("CONVERSION_CMD:" + ctype)
                    if cmd not in cmds:
                        cmds.append(cmd)
                    vardeps.add('CONVERSION_CMD:' + ctype)
                    subimage = type + "." + ctype
                    if subimage not in subimages:
                        subimages.append(subimage)
                    if type not in alltypes:
                        rm_tmp_images.add(localdata.expand("${IMAGE_NAME}.${type}"))

        for bt in basetypes[t]:
            gen_conversion_cmds(bt)

        localdata.setVar('type', realt)
        if t not in alltypes:
            rm_tmp_images.add(localdata.expand("${IMAGE_NAME}.${type}"))
        else:
            subimages.append(realt)

        # Clean up after applying all conversion commands. Some of them might
        # use the same input, therefore we cannot delete sooner without applying
        # some complex dependency analysis.
        for image in sorted(rm_tmp_images):
            cmds.append("\trm " + image)

        after = 'do_image'
        for dep in typedeps[t]:
            after += ' do_image_%s' % dep.replace("-", "_").replace(".", "_")

        task = "do_image_%s" % t.replace("-", "_").replace(".", "_")

        d.setVar(task, '\n'.join(cmds))
        d.setVarFlag(task, 'func', '1')
        d.setVarFlag(task, 'fakeroot', '1')
        d.setVarFlag(task, 'imagetype', t)

        d.appendVarFlag(task, 'prefuncs', ' ' + debug + ' set_image_size')
        d.prependVarFlag(task, 'postfuncs', 'create_symlinks ')
        d.appendVarFlag(task, 'subimages', ' ' + ' '.join(subimages))
        d.appendVarFlag(task, 'vardeps', ' ' + ' '.join(vardeps))
        d.appendVarFlag(task, 'vardepsexclude', ' DATETIME DATE ' + ' '.join(vardepsexclude))
        d.appendVarFlag(task, 'postfuncs', ' write_image_output_manifest')

        bb.debug(2, "Adding task %s before %s, after %s" % (task, 'do_image_complete', after))
        bb.build.addtask(task, 'do_image_complete', after, d)
}

#
# Compute the rootfs size
#
def get_rootfs_size(d):
    import subprocess, oe.utils

    rootfs_alignment = int(d.getVar('IMAGE_ROOTFS_ALIGNMENT'))
    overhead_factor = float(d.getVar('IMAGE_OVERHEAD_FACTOR'))
    rootfs_req_size = int(d.getVar('IMAGE_ROOTFS_SIZE'))
    rootfs_extra_space = eval(d.getVar('IMAGE_ROOTFS_EXTRA_SPACE'))
    rootfs_maxsize = d.getVar('IMAGE_ROOTFS_MAXSIZE')
    image_fstypes = d.getVar('IMAGE_FSTYPES') or ''
    initramfs_fstypes = d.getVar('INITRAMFS_FSTYPES') or ''
    initramfs_maxsize = d.getVar('INITRAMFS_MAXSIZE')

    size_kb = oe.utils.directory_size(d.getVar("IMAGE_ROOTFS")) / 1024

    base_size = size_kb * overhead_factor
    bb.debug(1, '%f = %d * %f' % (base_size, size_kb, overhead_factor))
    base_size2 = max(base_size, rootfs_req_size) + rootfs_extra_space
    bb.debug(1, '%f = max(%f, %d)[%f] + %d' % (base_size2, base_size, rootfs_req_size, max(base_size, rootfs_req_size), rootfs_extra_space))

    base_size = base_size2
    if base_size != int(base_size):
        base_size = int(base_size + 1)
    else:
        base_size = int(base_size)
    bb.debug(1, '%f = int(%f)' % (base_size, base_size2))

    base_size_saved = base_size
    base_size += rootfs_alignment - 1
    base_size -= base_size % rootfs_alignment
    bb.debug(1, '%d = aligned(%d)' % (base_size, base_size_saved))

    # Do not check image size of the debugfs image. This is not supposed
    # to be deployed, etc. so it doesn't make sense to limit the size
    # of the debug.
    if (d.getVar('IMAGE_BUILDING_DEBUGFS') or "") == "true":
        bb.debug(1, 'returning debugfs size %d' % (base_size))
        return base_size

    # Check the rootfs size against IMAGE_ROOTFS_MAXSIZE (if set)
    if rootfs_maxsize:
        rootfs_maxsize_int = int(rootfs_maxsize)
        if base_size > rootfs_maxsize_int:
            bb.fatal("The rootfs size %d(K) exceeds IMAGE_ROOTFS_MAXSIZE: %d(K)" % \
                (base_size, rootfs_maxsize_int))

    # Check the initramfs size against INITRAMFS_MAXSIZE (if set)
    if image_fstypes == initramfs_fstypes != ''  and initramfs_maxsize:
        initramfs_maxsize_int = int(initramfs_maxsize)
        if base_size > initramfs_maxsize_int:
            bb.error("The initramfs size %d(K) exceeds INITRAMFS_MAXSIZE: %d(K)" % \
                (base_size, initramfs_maxsize_int))
            bb.error("You can set INITRAMFS_MAXSIZE a larger value. Usually, it should")
            bb.fatal("be less than 1/2 of ram size, or you may fail to boot it.\n")

    bb.debug(1, 'returning %d' % (base_size))
    return base_size

python set_image_size () {
        rootfs_size = get_rootfs_size(d)
        d.setVar('ROOTFS_SIZE', str(rootfs_size))
        d.setVarFlag('ROOTFS_SIZE', 'export', '1')
}

#
# Create symlinks to the newly created image
#
python create_symlinks() {

    deploy_dir = d.getVar('IMGDEPLOYDIR')
    img_name = d.getVar('IMAGE_NAME')
    link_name = d.getVar('IMAGE_LINK_NAME')
    manifest_name = d.getVar('IMAGE_MANIFEST')
    taskname = d.getVar("BB_CURRENTTASK")
    subimages = (d.getVarFlag("do_" + taskname, 'subimages', False) or "").split()

    if not link_name:
        return
    for type in subimages:
        dst = os.path.join(deploy_dir, link_name + "." + type)
        src = img_name + "." + type
        if os.path.exists(os.path.join(deploy_dir, src)):
            bb.note("Creating symlink: %s -> %s" % (dst, src))
            if os.path.islink(dst):
                os.remove(dst)
            os.symlink(src, dst)
        else:
            bb.note("Skipping symlink, source does not exist: %s -> %s" % (dst, src))
}

python write_image_output_manifest() {
    import json
    from pathlib import Path

    taskname = d.getVar("BB_CURRENTTASK")
    image_deploy_dir = Path(d.getVar('IMGDEPLOYDIR'))
    image_manifest_dir = Path(d.getVar('IMGMANIFESTDIR'))
    manifest_path = image_manifest_dir / ("do_" + d.getVar("BB_CURRENTTASK") + ".json")

    image_name = d.getVar("IMAGE_NAME")
    image_basename = d.getVar("IMAGE_BASENAME")
    machine = d.getVar("MACHINE")

    subimages = (d.getVarFlag("do_" + taskname, 'subimages', False) or "").split()
    imagetype = d.getVarFlag("do_" + taskname, 'imagetype', False)

    data = {
        "taskname": taskname,
        "imagetype": imagetype,
        "images": []
    }

    for type in subimages:
        image_filename = image_name + "." + type
        image_path = image_deploy_dir / image_filename
        if not image_path.exists():
            continue
        data["images"].append({
            "filename": image_filename,
        })

    with manifest_path.open("w") as f:
        json.dump([data], f)
}

MULTILIBRE_ALLOW_REP += "${base_bindir} ${base_sbindir} ${bindir} ${sbindir} ${libexecdir} ${sysconfdir} ${nonarch_base_libdir}/udev /lib/modules/[^/]*/modules.*"
MULTILIB_CHECK_FILE = "${WORKDIR}/multilib_check.py"
MULTILIB_TEMP_ROOTFS = "${WORKDIR}/multilib"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
deltask do_populate_lic
deltask do_populate_sysroot
do_package[noexec] = "1"
deltask do_package_qa
deltask do_packagedata
deltask do_package_write_ipk
deltask do_package_write_deb
deltask do_package_write_rpm

# Prepare the root links to point to the /usr counterparts.
create_merged_usr_symlinks() {
    root="$1"
    install -d $root${base_bindir} $root${base_sbindir} $root${base_libdir}
    ln -rs $root${base_bindir} $root/bin
    ln -rs $root${base_sbindir} $root/sbin
    ln -rs $root${base_libdir} $root/${baselib}

    if [ "${nonarch_base_libdir}" != "${base_libdir}" ]; then
       install -d $root${nonarch_base_libdir}
       ln -rs $root${nonarch_base_libdir} $root/lib
    fi

    # create base links for multilibs
    multi_libdirs="${@d.getVar('MULTILIB_VARIANTS')}"
    for d in $multi_libdirs; do
        install -d $root${exec_prefix}/$d
        ln -rs $root${exec_prefix}/$d $root/$d
    done
}

create_merged_usr_symlinks_rootfs() {
    create_merged_usr_symlinks ${IMAGE_ROOTFS}
}

create_merged_usr_symlinks_sdk() {
    create_merged_usr_symlinks ${SDK_OUTPUT}${SDKTARGETSYSROOT}
}

ROOTFS_PREPROCESS_COMMAND += "${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', 'create_merged_usr_symlinks_rootfs', '',d)}"
POPULATE_SDK_PRE_TARGET_COMMAND += "${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', 'create_merged_usr_symlinks_sdk', '',d)}"

reproducible_final_image_task () {
    if [ "$REPRODUCIBLE_TIMESTAMP_ROOTFS" = "" ]; then
        REPRODUCIBLE_TIMESTAMP_ROOTFS=`git -C "${COREBASE}" log -1 --pretty=%ct 2>/dev/null` || true
        if [ "$REPRODUCIBLE_TIMESTAMP_ROOTFS" = "" ]; then
            REPRODUCIBLE_TIMESTAMP_ROOTFS=`stat -c%Y ${@bb.utils.which(d.getVar("BBPATH"), "conf/bitbake.conf")}`
        fi
    fi
    # Set mtime of all files to a reproducible value
    bbnote "reproducible_final_image_task: mtime set to $REPRODUCIBLE_TIMESTAMP_ROOTFS"
    find  ${IMAGE_ROOTFS} -print0 | xargs -0 touch -h  --date=@$REPRODUCIBLE_TIMESTAMP_ROOTFS
}

systemd_preset_all () {
    if [ -e ${IMAGE_ROOTFS}${root_prefix}/lib/systemd/systemd ]; then
	systemctl --root="${IMAGE_ROOTFS}" --preset-mode=enable-only preset-all
    fi
}

IMAGE_PREPROCESS_COMMAND:append = " ${@ 'systemd_preset_all' if bb.utils.contains('DISTRO_FEATURES', 'systemd', True, False, d) and not bb.utils.contains('IMAGE_FEATURES', 'stateless-rootfs', True, False, d) else ''} reproducible_final_image_task "

CVE_PRODUCT = ""
