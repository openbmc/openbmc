inherit rootfs_${IMAGE_PKGTYPE}

# Only Linux SDKs support populate_sdk_ext, fall back to populate_sdk
# in the non-Linux SDK_OS case, such as mingw32
SDKEXTCLASS ?= "${@['populate_sdk', 'populate_sdk_ext']['linux' in d.getVar("SDK_OS", True)]}"
inherit ${SDKEXTCLASS}

TOOLCHAIN_TARGET_TASK += "${PACKAGE_INSTALL}"
TOOLCHAIN_TARGET_TASK_ATTEMPTONLY += "${PACKAGE_INSTALL_ATTEMPTONLY}"
POPULATE_SDK_POST_TARGET_COMMAND += "rootfs_sysroot_relativelinks; "

inherit gzipnative

LICENSE = "MIT"
PACKAGES = ""
DEPENDS += "${MLPREFIX}qemuwrapper-cross ${MLPREFIX}depmodwrapper-cross"
RDEPENDS += "${PACKAGE_INSTALL} ${LINGUAS_INSTALL}"
RRECOMMENDS += "${PACKAGE_INSTALL_ATTEMPTONLY}"

INHIBIT_DEFAULT_DEPS = "1"

TESTIMAGECLASS = "${@base_conditional('TEST_IMAGE', '1', 'testimage-auto', '', d)}"
inherit ${TESTIMAGECLASS}

# IMAGE_FEATURES may contain any available package group
IMAGE_FEATURES ?= ""
IMAGE_FEATURES[type] = "list"
IMAGE_FEATURES[validitems] += "debug-tweaks read-only-rootfs empty-root-password allow-empty-password post-install-logging"

# Generate companion debugfs?
IMAGE_GEN_DEBUGFS ?= "0"

# rootfs bootstrap install
ROOTFS_BOOTSTRAP_INSTALL = "${@bb.utils.contains("IMAGE_FEATURES", "package-management", "", "${ROOTFS_PKGMANAGE_BOOTSTRAP}",d)}"

# These packages will be removed from a read-only rootfs after all other
# packages have been installed
ROOTFS_RO_UNNEEDED = "update-rc.d base-passwd shadow ${VIRTUAL-RUNTIME_update-alternatives} ${ROOTFS_BOOTSTRAP_INSTALL}"

# packages to install from features
FEATURE_INSTALL = "${@' '.join(oe.packagegroup.required_packages(oe.data.typed_value('IMAGE_FEATURES', d), d))}"
FEATURE_INSTALL[vardepvalue] = "${FEATURE_INSTALL}"
FEATURE_INSTALL_OPTIONAL = "${@' '.join(oe.packagegroup.optional_packages(oe.data.typed_value('IMAGE_FEATURES', d), d))}"
FEATURE_INSTALL_OPTIONAL[vardepvalue] = "${FEATURE_INSTALL_OPTIONAL}"

# Define some very basic feature package groups
FEATURE_PACKAGES_package-management = "${ROOTFS_PKGMANAGE}"
SPLASH ?= "psplash"
FEATURE_PACKAGES_splash = "${SPLASH}"

IMAGE_INSTALL_COMPLEMENTARY = '${@complementary_globs("IMAGE_FEATURES", d)}'

def check_image_features(d):
    valid_features = (d.getVarFlag('IMAGE_FEATURES', 'validitems', True) or "").split()
    valid_features += d.getVarFlags('COMPLEMENTARY_GLOB').keys()
    for var in d:
       if var.startswith("PACKAGE_GROUP_"):
           bb.warn("PACKAGE_GROUP is deprecated, please use FEATURE_PACKAGES instead")
           valid_features.append(var[14:])
       elif var.startswith("FEATURE_PACKAGES_"):
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

# Images are generally built explicitly, do not need to be part of world.
EXCLUDE_FROM_WORLD = "1"

USE_DEVFS ?= "1"
USE_DEPMOD ?= "1"

PID = "${@os.getpid()}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

LDCONFIGDEPEND ?= "ldconfig-native:do_populate_sysroot"
LDCONFIGDEPEND_libc-uclibc = ""
LDCONFIGDEPEND_libc-musl = ""

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
    variables = rootfs_command_variables(d) + sdk_command_variables(d)
    for var in variables:
        if d.getVar(var, False):
            d.setVarFlag(var, 'func', '1')
}

def rootfs_variables(d):
    from oe.rootfs import variable_depends
    variables = ['IMAGE_DEVICE_TABLES','BUILD_IMAGES_FROM_FEEDS','IMAGE_TYPES_MASKED','IMAGE_ROOTFS_ALIGNMENT','IMAGE_OVERHEAD_FACTOR','IMAGE_ROOTFS_SIZE','IMAGE_ROOTFS_EXTRA_SPACE',
                 'IMAGE_ROOTFS_MAXSIZE','IMAGE_NAME','IMAGE_LINK_NAME','IMAGE_MANIFEST','DEPLOY_DIR_IMAGE','RM_OLD_IMAGE','IMAGE_FSTYPES','IMAGE_INSTALL_COMPLEMENTARY','IMAGE_LINGUAS',
                 'MULTILIBRE_ALLOW_REP','MULTILIB_TEMP_ROOTFS','MULTILIB_VARIANTS','MULTILIBS','ALL_MULTILIB_PACKAGE_ARCHS','MULTILIB_GLOBAL_VARIANTS','BAD_RECOMMENDATIONS','NO_RECOMMENDATIONS',
                 'PACKAGE_ARCHS','PACKAGE_CLASSES','TARGET_VENDOR','TARGET_ARCH','TARGET_OS','OVERRIDES','BBEXTENDVARIANT','FEED_DEPLOYDIR_BASE_URI','INTERCEPT_DIR','USE_DEVFS',
                 'COMPRESSIONTYPES', 'IMAGE_GEN_DEBUGFS', 'ROOTFS_RO_UNNEEDED']
    variables.extend(rootfs_command_variables(d))
    variables.extend(variable_depends(d))
    return " ".join(variables)

do_rootfs[vardeps] += "${@rootfs_variables(d)}"

do_build[depends] += "virtual/kernel:do_deploy"

def build_live(d):
    if bb.utils.contains("IMAGE_FSTYPES", "live", "live", "0", d) == "0": # live is not set but hob might set iso or hddimg
        d.setVar('NOISO', bb.utils.contains('IMAGE_FSTYPES', "iso", "0", "1", d))
        d.setVar('NOHDD', bb.utils.contains('IMAGE_FSTYPES', "hddimg", "0", "1", d))
        if d.getVar('NOISO', True) == "0" or d.getVar('NOHDD', True) == "0":
            return "image-live"
        return ""
    return "image-live"

IMAGE_TYPE_live = "${@build_live(d)}"
inherit ${IMAGE_TYPE_live}

IMAGE_TYPE_vm = '${@bb.utils.contains_any("IMAGE_FSTYPES", ["vmdk", "vdi", "qcow2", "hdddirect"], "image-vm", "", d)}'
inherit ${IMAGE_TYPE_vm}

python () {
    deps = " " + imagetypes_getdepends(d)
    d.appendVarFlag('do_rootfs', 'depends', deps)

    deps = ""
    for dep in (d.getVar('EXTRA_IMAGEDEPENDS', True) or "").split():
        deps += " %s:do_populate_sysroot" % dep
    d.appendVarFlag('do_build', 'depends', deps)

    #process IMAGE_FEATURES, we must do this before runtime_mapping_rename
    #Check for replaces image features
    features = set(oe.data.typed_value('IMAGE_FEATURES', d))
    remain_features = features.copy()
    for feature in features:
        replaces = set((d.getVar("IMAGE_FEATURES_REPLACES_%s" % feature, True) or "").split())
        remain_features -= replaces

    #Check for conflict image features
    for feature in remain_features:
        conflicts = set((d.getVar("IMAGE_FEATURES_CONFLICTS_%s" % feature, True) or "").split())
        temp = conflicts & remain_features
        if temp:
            bb.fatal("%s contains conflicting IMAGE_FEATURES %s %s" % (d.getVar('PN', True), feature, ' '.join(list(temp))))

    d.setVar('IMAGE_FEATURES', ' '.join(list(remain_features)))

    check_image_features(d)
    initramfs_image = d.getVar('INITRAMFS_IMAGE', True) or ""
    if initramfs_image != "":
        d.appendVarFlag('do_build', 'depends', " %s:do_bundle_initramfs" %  d.getVar('PN', True))
        d.appendVarFlag('do_bundle_initramfs', 'depends', " %s:do_image_complete" % initramfs_image)
}

IMAGE_CLASSES += "image_types"
inherit ${IMAGE_CLASSES}

IMAGE_POSTPROCESS_COMMAND ?= ""

# some default locales
IMAGE_LINGUAS ?= "de-de fr-fr en-gb"

LINGUAS_INSTALL ?= "${@" ".join(map(lambda s: "locale-base-%s" % s, d.getVar('IMAGE_LINGUAS', True).split()))}"

# Prefer image, but use the fallback files for lookups if the image ones
# aren't yet available.
PSEUDO_PASSWD = "${IMAGE_ROOTFS}:${STAGING_DIR_NATIVE}"

inherit rootfs-postcommands

PACKAGE_EXCLUDE ??= ""
PACKAGE_EXCLUDE[type] = "list"

fakeroot python do_rootfs () {
    from oe.rootfs import create_rootfs
    from oe.manifest import create_manifest

    # Handle package exclusions
    excl_pkgs = d.getVar("PACKAGE_EXCLUDE", True).split()
    inst_pkgs = d.getVar("PACKAGE_INSTALL", True).split()
    inst_attempt_pkgs = d.getVar("PACKAGE_INSTALL_ATTEMPTONLY", True).split()

    d.setVar('PACKAGE_INSTALL_ORIG', ' '.join(inst_pkgs))
    d.setVar('PACKAGE_INSTALL_ATTEMPTONLY', ' '.join(inst_attempt_pkgs))

    for pkg in excl_pkgs:
        if pkg in inst_pkgs:
            bb.warn("Package %s, set to be excluded, is in %s PACKAGE_INSTALL (%s).  It will be removed from the list." % (pkg, d.getVar('PN', True), inst_pkgs))
            inst_pkgs.remove(pkg)

        if pkg in inst_attempt_pkgs:
            bb.warn("Package %s, set to be excluded, is in %s PACKAGE_INSTALL_ATTEMPTONLY (%s).  It will be removed from the list." % (pkg, d.getVar('PN', True), inst_pkgs))
            inst_attempt_pkgs.remove(pkg)

    d.setVar("PACKAGE_INSTALL", ' '.join(inst_pkgs))
    d.setVar("PACKAGE_INSTALL_ATTEMPTONLY", ' '.join(inst_attempt_pkgs))

    # Ensure we handle package name remapping
    # We have to delay the runtime_mapping_rename until just before rootfs runs
    # otherwise, the multilib renaming could step in and squash any fixups that
    # may have occurred.
    pn = d.getVar('PN', True)
    runtime_mapping_rename("PACKAGE_INSTALL", pn, d)
    runtime_mapping_rename("PACKAGE_INSTALL_ATTEMPTONLY", pn, d)
    runtime_mapping_rename("BAD_RECOMMENDATIONS", pn, d)

    # Generate the initial manifest
    create_manifest(d)

    # Generate rootfs
    create_rootfs(d)
}
do_rootfs[dirs] = "${TOPDIR}"
do_rootfs[cleandirs] += "${S}"
do_rootfs[umask] = "022"
addtask rootfs before do_build

fakeroot python do_image () {
    from oe.utils import execute_pre_post_process

    pre_process_cmds = d.getVar("IMAGE_PREPROCESS_COMMAND", True)

    execute_pre_post_process(d, pre_process_cmds)
}
do_image[dirs] = "${TOPDIR}"
do_image[umask] = "022"
addtask do_image after do_rootfs before do_build

fakeroot python do_image_complete () {
    from oe.utils import execute_pre_post_process

    post_process_cmds = d.getVar("IMAGE_POSTPROCESS_COMMAND", True)

    execute_pre_post_process(d, post_process_cmds)
}
do_image_complete[dirs] = "${TOPDIR}"
do_image_complete[umask] = "022"
addtask do_image_complete after do_image before do_build

#
# Write environment variables used by wic
# to tmp/sysroots/<machine>/imgdata/<image>.env
#
python do_rootfs_wicenv () {
    wicvars = d.getVar('WICVARS', True)
    if not wicvars:
        return

    stdir = d.getVar('STAGING_DIR_TARGET', True)
    outdir = os.path.join(stdir, 'imgdata')
    bb.utils.mkdirhier(outdir)
    basename = d.getVar('IMAGE_BASENAME', True)
    with open(os.path.join(outdir, basename) + '.env', 'w') as envf:
        for var in wicvars.split():
            value = d.getVar(var, True)
            if value:
                envf.write('%s="%s"\n' % (var, value.strip()))
}
addtask do_rootfs_wicenv after do_image before do_image_wic
do_rootfs_wicenv[vardeps] += "${WICVARS}"
do_rootfs_wicenv[prefuncs] = 'set_image_size'

def setup_debugfs_variables(d):
    d.appendVar('IMAGE_ROOTFS', '-dbg')
    d.appendVar('IMAGE_LINK_NAME', '-dbg')
    d.appendVar('IMAGE_NAME','-dbg')
    debugfs_image_fstypes = d.getVar('IMAGE_FSTYPES_DEBUGFS', True)
    if debugfs_image_fstypes:
        d.setVar('IMAGE_FSTYPES', debugfs_image_fstypes)

python setup_debugfs () {
    setup_debugfs_variables(d)
}

python () {
    vardeps = set()
    ctypes = d.getVar('COMPRESSIONTYPES', True).split()
    old_overrides = d.getVar('OVERRIDES', 0)

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
    alltypes = d.getVar('IMAGE_FSTYPES', True).split()
    typedeps = {}

    if d.getVar('IMAGE_GEN_DEBUGFS', True) == "1":
        debugfs_fstypes = d.getVar('IMAGE_FSTYPES_DEBUGFS', True).split()
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
        deps = (d.getVar('IMAGE_TYPEDEP_' + t, True) or "").split()
        vardeps.add('IMAGE_TYPEDEP_' + t)
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

    maskedtypes = (d.getVar('IMAGE_TYPES_MASKED', True) or "").split()

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
        bb.data.update_data(localdata)
        localdata.setVar('type', realt)
        # Delete DATETIME so we don't expand any references to it now
        # This means the task's hash can be stable rather than having hardcoded
        # date/time values. It will get expanded at execution time.
        # Similarly TMPDIR since otherwise we see QA stamp comparision problems
        localdata.delVar('DATETIME')
        localdata.delVar('TMPDIR')

        image_cmd = localdata.getVar("IMAGE_CMD", True)
        vardeps.add('IMAGE_CMD_' + realt)
        if image_cmd:
            cmds.append("\t" + image_cmd)
        else:
            bb.fatal("No IMAGE_CMD defined for IMAGE_FSTYPES entry '%s' - possibly invalid type name or missing support class" % t)
        cmds.append(localdata.expand("\tcd ${DEPLOY_DIR_IMAGE}"))

        rm_tmp_images = set()
        def gen_conversion_cmds(bt):
            for ctype in ctypes:
                if bt.endswith("." + ctype):
                    type = bt[0:-len(ctype) - 1]
                    if type.startswith("debugfs_"):
                        type = type[8:]
                    # Create input image first.
                    gen_conversion_cmds(type)
                    localdata.setVar('type', type)
                    cmds.append("\t" + localdata.getVar("COMPRESS_CMD_" + ctype, True))
                    vardeps.add('COMPRESS_CMD_' + ctype)
                    subimages.append(type + "." + ctype)
                    if type not in alltypes:
                        rm_tmp_images.add(localdata.expand("${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}"))

        for bt in basetypes[t]:
            gen_conversion_cmds(bt)

        localdata.setVar('type', realt)
        if t not in alltypes:
            rm_tmp_images.add(localdata.expand("${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}"))
        else:
            subimages.append(realt)

        # Clean up after applying all conversion commands. Some of them might
        # use the same input, therefore we cannot delete sooner without applying
        # some complex dependency analysis.
        for image in rm_tmp_images:
            cmds.append("\trm " + image)

        after = 'do_image'
        for dep in typedeps[t]:
            after += ' do_image_%s' % dep.replace("-", "_").replace(".", "_")

        t = t.replace("-", "_").replace(".", "_")

        d.setVar('do_image_%s' % t, '\n'.join(cmds))
        d.setVarFlag('do_image_%s' % t, 'func', '1')
        d.setVarFlag('do_image_%s' % t, 'fakeroot', '1')
        d.setVarFlag('do_image_%s' % t, 'prefuncs', debug + 'set_image_size')
        d.setVarFlag('do_image_%s' % t, 'postfuncs', 'create_symlinks')
        d.setVarFlag('do_image_%s' % t, 'subimages', ' '.join(subimages))
        d.appendVarFlag('do_image_%s' % t, 'vardeps', ' '.join(vardeps))
        d.appendVarFlag('do_image_%s' % t, 'vardepsexclude', 'DATETIME')

        bb.debug(2, "Adding type %s before %s, after %s" % (t, 'do_image_complete', after))
        bb.build.addtask('do_image_%s' % t, 'do_image_complete', after, d)
}

#
# Compute the rootfs size
#
def get_rootfs_size(d):
    import subprocess

    rootfs_alignment = int(d.getVar('IMAGE_ROOTFS_ALIGNMENT', True))
    overhead_factor = float(d.getVar('IMAGE_OVERHEAD_FACTOR', True))
    rootfs_req_size = int(d.getVar('IMAGE_ROOTFS_SIZE', True))
    rootfs_extra_space = eval(d.getVar('IMAGE_ROOTFS_EXTRA_SPACE', True))
    rootfs_maxsize = d.getVar('IMAGE_ROOTFS_MAXSIZE', True)
    image_fstypes = d.getVar('IMAGE_FSTYPES', True) or ''
    initramfs_fstypes = d.getVar('INITRAMFS_FSTYPES', True) or ''
    initramfs_maxsize = d.getVar('INITRAMFS_MAXSIZE', True)

    output = subprocess.check_output(['du', '-ks',
                                      d.getVar('IMAGE_ROOTFS', True)])
    size_kb = int(output.split()[0])
    base_size = size_kb * overhead_factor
    base_size = max(base_size, rootfs_req_size) + rootfs_extra_space

    if base_size != int(base_size):
        base_size = int(base_size + 1)
    else:
        base_size = int(base_size)

    base_size += rootfs_alignment - 1
    base_size -= base_size % rootfs_alignment

    # Check the rootfs size against IMAGE_ROOTFS_MAXSIZE (if set)
    if rootfs_maxsize:
        rootfs_maxsize_int = int(rootfs_maxsize)
        if base_size > rootfs_maxsize_int:
            bb.fatal("The rootfs size %d(K) overrides IMAGE_ROOTFS_MAXSIZE: %d(K)" % \
                (base_size, rootfs_maxsize_int))

    # Check the initramfs size against INITRAMFS_MAXSIZE (if set)
    if image_fstypes == initramfs_fstypes != ''  and initramfs_maxsize:
        initramfs_maxsize_int = int(initramfs_maxsize)
        if base_size > initramfs_maxsize_int:
            bb.error("The initramfs size %d(K) overrides INITRAMFS_MAXSIZE: %d(K)" % \
                (base_size, initramfs_maxsize_int))
            bb.error("You can set INITRAMFS_MAXSIZE a larger value. Usually, it should")
            bb.fatal("be less than 1/2 of ram size, or you may fail to boot it.\n")
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

    deploy_dir = d.getVar('DEPLOY_DIR_IMAGE', True)
    img_name = d.getVar('IMAGE_NAME', True)
    link_name = d.getVar('IMAGE_LINK_NAME', True)
    manifest_name = d.getVar('IMAGE_MANIFEST', True)
    taskname = d.getVar("BB_CURRENTTASK", True)
    subimages = (d.getVarFlag("do_" + taskname, 'subimages', False) or "").split()
    imgsuffix = d.getVarFlag("do_" + taskname, 'imgsuffix', True) or d.expand("${IMAGE_NAME_SUFFIX}.")
    os.chdir(deploy_dir)

    if not link_name:
        return
    for type in subimages:
        if os.path.exists(img_name + imgsuffix + type):
            dst = deploy_dir + "/" + link_name + "." + type
            src = img_name + imgsuffix + type
            bb.note("Creating symlink: %s -> %s" % (dst, src))
            if os.path.islink(dst):
                if d.getVar('RM_OLD_IMAGE', True) == "1" and \
                        os.path.exists(os.path.realpath(dst)):
                    os.remove(os.path.realpath(dst))
                os.remove(dst)
            os.symlink(src, dst)
}

MULTILIBRE_ALLOW_REP =. "${base_bindir}|${base_sbindir}|${bindir}|${sbindir}|${libexecdir}|${sysconfdir}|${nonarch_base_libdir}/udev|/lib/modules/[^/]*/modules.*|"
MULTILIB_CHECK_FILE = "${WORKDIR}/multilib_check.py"
MULTILIB_TEMP_ROOTFS = "${WORKDIR}/multilib"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
do_populate_sysroot[noexec] = "1"
do_package[noexec] = "1"
do_package_qa[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_package_write_rpm[noexec] = "1"

# Allow the kernel to be repacked with the initramfs and boot image file as a single file
do_bundle_initramfs[depends] += "virtual/kernel:do_bundle_initramfs"
do_bundle_initramfs[nostamp] = "1"
do_bundle_initramfs[noexec] = "1"
do_bundle_initramfs () {
	:
}
addtask bundle_initramfs after do_image_complete
