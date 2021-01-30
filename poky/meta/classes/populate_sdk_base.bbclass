inherit meta image-postinst-intercepts

# Wildcards specifying complementary packages to install for every package that has been explicitly
# installed into the rootfs
COMPLEMENTARY_GLOB[dev-pkgs] = '*-dev'
COMPLEMENTARY_GLOB[staticdev-pkgs] = '*-staticdev'
COMPLEMENTARY_GLOB[doc-pkgs] = '*-doc'
COMPLEMENTARY_GLOB[dbg-pkgs] = '*-dbg'
COMPLEMENTARY_GLOB[src-pkgs] = '*-src'
COMPLEMENTARY_GLOB[ptest-pkgs] = '*-ptest'
COMPLEMENTARY_GLOB[bash-completion-pkgs] = '*-bash-completion'

def complementary_globs(featurevar, d):
    all_globs = d.getVarFlags('COMPLEMENTARY_GLOB')
    globs = []
    features = set((d.getVar(featurevar) or '').split())
    for name, glob in all_globs.items():
        if name in features:
            globs.append(glob)
    return ' '.join(globs)

SDKIMAGE_FEATURES ??= "dev-pkgs dbg-pkgs src-pkgs ${@bb.utils.contains('DISTRO_FEATURES', 'api-documentation', 'doc-pkgs', '', d)}"
SDKIMAGE_INSTALL_COMPLEMENTARY = '${@complementary_globs("SDKIMAGE_FEATURES", d)}'
SDKIMAGE_INSTALL_COMPLEMENTARY[vardeps] += "SDKIMAGE_FEATURES"

PACKAGE_ARCHS_append_task-populate-sdk = " sdk-provides-dummy-target"
SDK_PACKAGE_ARCHS += "sdk-provides-dummy-${SDKPKGSUFFIX}"

# List of locales to install, or "all" for all of them, or unset for none.
SDKIMAGE_LINGUAS ?= "all"

inherit rootfs_${IMAGE_PKGTYPE}

SDK_DIR = "${WORKDIR}/sdk"
SDK_OUTPUT = "${SDK_DIR}/image"
SDK_DEPLOY = "${DEPLOY_DIR}/sdk"

SDKDEPLOYDIR = "${WORKDIR}/${SDKMACHINE}-deploy-${PN}-populate-sdk"

B_task-populate-sdk = "${SDK_DIR}"

SDKTARGETSYSROOT = "${SDKPATH}/sysroots/${REAL_MULTIMACH_TARGET_SYS}"

TOOLCHAIN_HOST_TASK ?= "nativesdk-packagegroup-sdk-host packagegroup-cross-canadian-${MACHINE}"
TOOLCHAIN_HOST_TASK_ATTEMPTONLY ?= ""
TOOLCHAIN_TARGET_TASK ?= "${@multilib_pkg_extend(d, 'packagegroup-core-standalone-sdk-target')} target-sdk-provides-dummy"
TOOLCHAIN_TARGET_TASK_ATTEMPTONLY ?= ""
TOOLCHAIN_OUTPUTNAME ?= "${SDK_NAME}-toolchain-${SDK_VERSION}"

# Default archived SDK's suffix
SDK_ARCHIVE_TYPE ?= "tar.xz"
SDK_XZ_COMPRESSION_LEVEL ?= "-9"
SDK_XZ_OPTIONS ?= "${XZ_DEFAULTS} ${SDK_XZ_COMPRESSION_LEVEL}"

# To support different sdk type according to SDK_ARCHIVE_TYPE, now support zip and tar.xz
python () {
    if d.getVar('SDK_ARCHIVE_TYPE') == 'zip':
       d.setVar('SDK_ARCHIVE_DEPENDS', 'zip-native')
       # SDK_ARCHIVE_CMD used to generate archived sdk ${TOOLCHAIN_OUTPUTNAME}.${SDK_ARCHIVE_TYPE} from input dir ${SDK_OUTPUT}/${SDKPATH} to output dir ${SDKDEPLOYDIR}
       # recommand to cd into input dir first to avoid archive with buildpath
       d.setVar('SDK_ARCHIVE_CMD', 'cd ${SDK_OUTPUT}/${SDKPATH}; zip -r -y ${SDKDEPLOYDIR}/${TOOLCHAIN_OUTPUTNAME}.${SDK_ARCHIVE_TYPE} .')
    else:
       d.setVar('SDK_ARCHIVE_DEPENDS', 'xz-native')
       d.setVar('SDK_ARCHIVE_CMD', 'cd ${SDK_OUTPUT}/${SDKPATH}; tar ${SDKTAROPTS} -cf - . | xz ${SDK_XZ_OPTIONS} > ${SDKDEPLOYDIR}/${TOOLCHAIN_OUTPUTNAME}.${SDK_ARCHIVE_TYPE}')
}

SDK_RDEPENDS = "${TOOLCHAIN_TARGET_TASK} ${TOOLCHAIN_HOST_TASK}"
SDK_DEPENDS = "virtual/fakeroot-native ${SDK_ARCHIVE_DEPENDS} cross-localedef-native nativesdk-qemuwrapper-cross ${@' '.join(["%s-qemuwrapper-cross" % m for m in d.getVar("MULTILIB_VARIANTS").split()])} qemuwrapper-cross"
PATH_prepend = "${STAGING_DIR_HOST}${SDKPATHNATIVE}${bindir}/crossscripts:${@":".join(all_multilib_tune_values(d, 'STAGING_BINDIR_CROSS').split())}:"
SDK_DEPENDS += "nativesdk-glibc-locale"

# We want the MULTIARCH_TARGET_SYS to point to the TUNE_PKGARCH, not PACKAGE_ARCH as it
# could be set to the MACHINE_ARCH
REAL_MULTIMACH_TARGET_SYS = "${TUNE_PKGARCH}${TARGET_VENDOR}-${TARGET_OS}"

PID = "${@os.getpid()}"

EXCLUDE_FROM_WORLD = "1"

SDK_PACKAGING_FUNC ?= "create_shar"
SDK_PRE_INSTALL_COMMAND ?= ""
SDK_POST_INSTALL_COMMAND ?= ""
SDK_RELOCATE_AFTER_INSTALL ?= "1"

SDKEXTPATH ??= "~/${@d.getVar('DISTRO')}_sdk"
SDK_TITLE ??= "${@d.getVar('DISTRO_NAME') or d.getVar('DISTRO')} SDK"

SDK_TARGET_MANIFEST = "${SDKDEPLOYDIR}/${TOOLCHAIN_OUTPUTNAME}.target.manifest"
SDK_HOST_MANIFEST = "${SDKDEPLOYDIR}/${TOOLCHAIN_OUTPUTNAME}.host.manifest"
SDK_EXT_TARGET_MANIFEST = "${SDK_DEPLOY}/${TOOLCHAINEXT_OUTPUTNAME}.target.manifest"
SDK_EXT_HOST_MANIFEST = "${SDK_DEPLOY}/${TOOLCHAINEXT_OUTPUTNAME}.host.manifest"

python write_target_sdk_manifest () {
    from oe.sdk import sdk_list_installed_packages
    from oe.utils import format_pkg_list
    sdkmanifestdir = os.path.dirname(d.getVar("SDK_TARGET_MANIFEST"))
    pkgs = sdk_list_installed_packages(d, True)
    if not os.path.exists(sdkmanifestdir):
        bb.utils.mkdirhier(sdkmanifestdir)
    with open(d.getVar('SDK_TARGET_MANIFEST'), 'w') as output:
        output.write(format_pkg_list(pkgs, 'ver'))
}

python write_sdk_test_data() {
    from oe.data import export2json
    testdata = "%s/%s.testdata.json" % (d.getVar('SDKDEPLOYDIR'), d.getVar('TOOLCHAIN_OUTPUTNAME'))
    bb.utils.mkdirhier(os.path.dirname(testdata))
    export2json(d, testdata)
}

python write_host_sdk_manifest () {
    from oe.sdk import sdk_list_installed_packages
    from oe.utils import format_pkg_list
    sdkmanifestdir = os.path.dirname(d.getVar("SDK_HOST_MANIFEST"))
    pkgs = sdk_list_installed_packages(d, False)
    if not os.path.exists(sdkmanifestdir):
        bb.utils.mkdirhier(sdkmanifestdir)
    with open(d.getVar('SDK_HOST_MANIFEST'), 'w') as output:
        output.write(format_pkg_list(pkgs, 'ver'))
}

POPULATE_SDK_POST_TARGET_COMMAND_append = " write_sdk_test_data ; "
POPULATE_SDK_POST_TARGET_COMMAND_append_task-populate-sdk  = " write_target_sdk_manifest ; "
POPULATE_SDK_POST_HOST_COMMAND_append_task-populate-sdk = " write_host_sdk_manifest; "
SDK_PACKAGING_COMMAND = "${@'${SDK_PACKAGING_FUNC};' if '${SDK_PACKAGING_FUNC}' else ''}"
SDK_POSTPROCESS_COMMAND = " create_sdk_files; check_sdk_sysroots; archive_sdk; ${SDK_PACKAGING_COMMAND} "

def populate_sdk_common(d):
    from oe.sdk import populate_sdk
    from oe.manifest import create_manifest, Manifest

    # Handle package exclusions
    excl_pkgs = (d.getVar("PACKAGE_EXCLUDE") or "").split()
    inst_pkgs = (d.getVar("PACKAGE_INSTALL") or "").split()
    inst_attempt_pkgs = (d.getVar("PACKAGE_INSTALL_ATTEMPTONLY") or "").split()

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

    pn = d.getVar('PN')
    runtime_mapping_rename("TOOLCHAIN_TARGET_TASK", pn, d)
    runtime_mapping_rename("TOOLCHAIN_TARGET_TASK_ATTEMPTONLY", pn, d)

    ld = bb.data.createCopy(d)
    ld.setVar("PKGDATA_DIR", "${STAGING_DIR}/${SDK_ARCH}-${SDKPKGSUFFIX}${SDK_VENDOR}-${SDK_OS}/pkgdata")
    runtime_mapping_rename("TOOLCHAIN_HOST_TASK", pn, ld)
    runtime_mapping_rename("TOOLCHAIN_HOST_TASK_ATTEMPTONLY", pn, ld)
    d.setVar("TOOLCHAIN_HOST_TASK", ld.getVar("TOOLCHAIN_HOST_TASK"))
    d.setVar("TOOLCHAIN_HOST_TASK_ATTEMPTONLY", ld.getVar("TOOLCHAIN_HOST_TASK_ATTEMPTONLY"))
    
    # create target/host SDK manifests
    create_manifest(d, manifest_dir=d.getVar('SDK_DIR'),
                    manifest_type=Manifest.MANIFEST_TYPE_SDK_HOST)
    create_manifest(d, manifest_dir=d.getVar('SDK_DIR'),
                    manifest_type=Manifest.MANIFEST_TYPE_SDK_TARGET)

    populate_sdk(d)

fakeroot python do_populate_sdk() {
    populate_sdk_common(d)
}
SSTATETASKS += "do_populate_sdk"
SSTATE_SKIP_CREATION_task-populate-sdk = '1'
do_populate_sdk[cleandirs] = "${SDKDEPLOYDIR}"
do_populate_sdk[sstate-inputdirs] = "${SDKDEPLOYDIR}"
do_populate_sdk[sstate-outputdirs] = "${SDK_DEPLOY}"
do_populate_sdk[stamp-extra-info] = "${MACHINE_ARCH}${SDKMACHINE}"

fakeroot create_sdk_files() {
	cp ${COREBASE}/scripts/relocate_sdk.py ${SDK_OUTPUT}/${SDKPATH}/

	# Replace the ##DEFAULT_INSTALL_DIR## with the correct pattern.
	# Escape special characters like '+' and '.' in the SDKPATH
	escaped_sdkpath=$(echo ${SDKPATH} |sed -e "s:[\+\.]:\\\\\\\\\0:g")
	sed -i -e "s:##DEFAULT_INSTALL_DIR##:$escaped_sdkpath:" ${SDK_OUTPUT}/${SDKPATH}/relocate_sdk.py
}

python check_sdk_sysroots() {
    # Fails build if there are broken or dangling symlinks in SDK sysroots

    if d.getVar('CHECK_SDK_SYSROOTS') != '1':
        # disabled, bail out
        return

    def norm_path(path):
        return os.path.abspath(path)

    # Get scan root
    SCAN_ROOT = norm_path("%s/%s/sysroots/" % (d.getVar('SDK_OUTPUT'),
                                               d.getVar('SDKPATH')))

    bb.note('Checking SDK sysroots at ' + SCAN_ROOT)

    def check_symlink(linkPath):
        if not os.path.islink(linkPath):
            return

        linkDirPath = os.path.dirname(linkPath)

        targetPath = os.readlink(linkPath)
        if not os.path.isabs(targetPath):
            targetPath = os.path.join(linkDirPath, targetPath)
        targetPath = norm_path(targetPath)

        if SCAN_ROOT != os.path.commonprefix( [SCAN_ROOT, targetPath] ):
            bb.error("Escaping symlink {0!s} --> {1!s}".format(linkPath, targetPath))
            return

        if not os.path.exists(targetPath):
            bb.error("Broken symlink {0!s} --> {1!s}".format(linkPath, targetPath))
            return

        if os.path.isdir(targetPath):
            dir_walk(targetPath)

    def walk_error_handler(e):
        bb.error(str(e))

    def dir_walk(rootDir):
        for dirPath,subDirEntries,fileEntries in os.walk(rootDir, followlinks=False, onerror=walk_error_handler):
            entries = subDirEntries + fileEntries
            for e in entries:
                ePath = os.path.join(dirPath, e)
                check_symlink(ePath)

    # start
    dir_walk(SCAN_ROOT)
}

SDKTAROPTS = "--owner=root --group=root"

fakeroot archive_sdk() {
	# Package it up
	mkdir -p ${SDKDEPLOYDIR}
	${SDK_ARCHIVE_CMD}
}

TOOLCHAIN_SHAR_EXT_TMPL ?= "${COREBASE}/meta/files/toolchain-shar-extract.sh"
TOOLCHAIN_SHAR_REL_TMPL ?= "${COREBASE}/meta/files/toolchain-shar-relocate.sh"

fakeroot create_shar() {
	# copy in the template shar extractor script
	cp ${TOOLCHAIN_SHAR_EXT_TMPL} ${SDKDEPLOYDIR}/${TOOLCHAIN_OUTPUTNAME}.sh

	rm -f ${T}/pre_install_command ${T}/post_install_command

	if [ "${SDK_RELOCATE_AFTER_INSTALL}" = "1" ] ; then
		cp ${TOOLCHAIN_SHAR_REL_TMPL} ${T}/post_install_command
	fi
	cat << "EOF" >> ${T}/pre_install_command
${SDK_PRE_INSTALL_COMMAND}
EOF

	cat << "EOF" >> ${T}/post_install_command
${SDK_POST_INSTALL_COMMAND}
EOF
	sed -i -e '/@SDK_PRE_INSTALL_COMMAND@/r ${T}/pre_install_command' \
		-e '/@SDK_POST_INSTALL_COMMAND@/r ${T}/post_install_command' \
		${SDKDEPLOYDIR}/${TOOLCHAIN_OUTPUTNAME}.sh

	# substitute variables
	sed -i -e 's#@SDK_ARCH@#${SDK_ARCH}#g' \
		-e 's#@SDKPATH@#${SDKPATH}#g' \
		-e 's#@SDKEXTPATH@#${SDKEXTPATH}#g' \
		-e 's#@OLDEST_KERNEL@#${SDK_OLDEST_KERNEL}#g' \
		-e 's#@REAL_MULTIMACH_TARGET_SYS@#${REAL_MULTIMACH_TARGET_SYS}#g' \
		-e 's#@SDK_TITLE@#${@d.getVar("SDK_TITLE").replace('&', '\\&')}#g' \
		-e 's#@SDK_VERSION@#${SDK_VERSION}#g' \
		-e '/@SDK_PRE_INSTALL_COMMAND@/d' \
		-e '/@SDK_POST_INSTALL_COMMAND@/d' \
		-e 's#@SDK_GCC_VER@#${@oe.utils.host_gcc_version(d, taskcontextonly=True)}#g' \
		-e 's#@SDK_ARCHIVE_TYPE@#${SDK_ARCHIVE_TYPE}#g' \
		${SDKDEPLOYDIR}/${TOOLCHAIN_OUTPUTNAME}.sh

	# add execution permission
	chmod +x ${SDKDEPLOYDIR}/${TOOLCHAIN_OUTPUTNAME}.sh

	# append the SDK tarball
	cat ${SDKDEPLOYDIR}/${TOOLCHAIN_OUTPUTNAME}.${SDK_ARCHIVE_TYPE} >> ${SDKDEPLOYDIR}/${TOOLCHAIN_OUTPUTNAME}.sh

	# delete the old tarball, we don't need it anymore
	rm ${SDKDEPLOYDIR}/${TOOLCHAIN_OUTPUTNAME}.${SDK_ARCHIVE_TYPE}
}

populate_sdk_log_check() {
	for target in $*
	do
		lf_path="`dirname ${BB_LOGFILE}`/log.do_$target.${PID}"

		echo "log_check: Using $lf_path as logfile"

		if [ -e "$lf_path" ]; then
			${IMAGE_PKGTYPE}_log_check $target $lf_path
		else
			echo "Cannot find logfile [$lf_path]"
		fi
		echo "Logfile is clean"
	done
}

def sdk_command_variables(d):
    return ['OPKG_PREPROCESS_COMMANDS','OPKG_POSTPROCESS_COMMANDS','POPULATE_SDK_POST_HOST_COMMAND','POPULATE_SDK_PRE_TARGET_COMMAND','POPULATE_SDK_POST_TARGET_COMMAND','SDK_POSTPROCESS_COMMAND','RPM_PREPROCESS_COMMANDS','RPM_POSTPROCESS_COMMANDS']

def sdk_variables(d):
    variables = ['BUILD_IMAGES_FROM_FEEDS','SDK_OS','SDK_OUTPUT','SDKPATHNATIVE','SDKTARGETSYSROOT','SDK_DIR','SDK_VENDOR','SDKIMAGE_INSTALL_COMPLEMENTARY','SDK_PACKAGE_ARCHS','SDK_OUTPUT',
                 'SDKTARGETSYSROOT','MULTILIB_VARIANTS','MULTILIBS','ALL_MULTILIB_PACKAGE_ARCHS','MULTILIB_GLOBAL_VARIANTS','BAD_RECOMMENDATIONS','NO_RECOMMENDATIONS','PACKAGE_ARCHS',
                 'PACKAGE_CLASSES','TARGET_VENDOR','TARGET_VENDOR','TARGET_ARCH','TARGET_OS','BBEXTENDVARIANT','FEED_DEPLOYDIR_BASE_URI', 'PACKAGE_EXCLUDE_COMPLEMENTARY', 'IMAGE_INSTALL_DEBUGFS']
    variables.extend(sdk_command_variables(d))
    return " ".join(variables)

do_populate_sdk[vardeps] += "${@sdk_variables(d)}"

do_populate_sdk[file-checksums] += "${TOOLCHAIN_SHAR_REL_TMPL}:True \
                                    ${TOOLCHAIN_SHAR_EXT_TMPL}:True"

do_populate_sdk[dirs] = "${PKGDATA_DIR} ${TOPDIR}"
do_populate_sdk[depends] += "${@' '.join([x + ':do_populate_sysroot' for x in d.getVar('SDK_DEPENDS').split()])}  ${@d.getVarFlag('do_rootfs', 'depends', False)}"
do_populate_sdk[rdepends] = "${@' '.join([x + ':do_package_write_${IMAGE_PKGTYPE} ' + x + ':do_packagedata' for x in d.getVar('SDK_RDEPENDS').split()])}"
do_populate_sdk[recrdeptask] += "do_packagedata do_package_write_rpm do_package_write_ipk do_package_write_deb"
do_populate_sdk[file-checksums] += "${POSTINST_INTERCEPT_CHECKSUMS}"
addtask populate_sdk
