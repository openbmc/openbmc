inherit meta

# Wildcards specifying complementary packages to install for every package that has been explicitly
# installed into the rootfs
COMPLEMENTARY_GLOB[dev-pkgs] = '*-dev'
COMPLEMENTARY_GLOB[staticdev-pkgs] = '*-staticdev'
COMPLEMENTARY_GLOB[doc-pkgs] = '*-doc'
COMPLEMENTARY_GLOB[dbg-pkgs] = '*-dbg'
COMPLEMENTARY_GLOB[ptest-pkgs] = '*-ptest'

def complementary_globs(featurevar, d):
    all_globs = d.getVarFlags('COMPLEMENTARY_GLOB')
    globs = []
    features = set((d.getVar(featurevar, True) or '').split())
    for name, glob in all_globs.items():
        if name in features:
            globs.append(glob)
    return ' '.join(globs)

SDKIMAGE_FEATURES ??= "dev-pkgs dbg-pkgs"
SDKIMAGE_INSTALL_COMPLEMENTARY = '${@complementary_globs("SDKIMAGE_FEATURES", d)}'

inherit rootfs_${IMAGE_PKGTYPE}

SDK_DIR = "${WORKDIR}/sdk"
SDK_OUTPUT = "${SDK_DIR}/image"
SDK_DEPLOY = "${DEPLOY_DIR}/sdk"

B_task-populate-sdk = "${SDK_DIR}"

SDKTARGETSYSROOT = "${SDKPATH}/sysroots/${REAL_MULTIMACH_TARGET_SYS}"

TOOLCHAIN_HOST_TASK ?= "nativesdk-packagegroup-sdk-host packagegroup-cross-canadian-${MACHINE}"
TOOLCHAIN_HOST_TASK_ATTEMPTONLY ?= ""
TOOLCHAIN_TARGET_TASK ?= " \
    ${@multilib_pkg_extend(d, 'packagegroup-core-standalone-sdk-target')} \
    ${@multilib_pkg_extend(d, 'packagegroup-core-standalone-sdk-target-dbg')} \
    "
TOOLCHAIN_TARGET_TASK_ATTEMPTONLY ?= ""
TOOLCHAIN_OUTPUTNAME ?= "${SDK_NAME}-toolchain-${SDK_VERSION}"

SDK_RDEPENDS = "${TOOLCHAIN_TARGET_TASK} ${TOOLCHAIN_HOST_TASK}"
SDK_DEPENDS = "virtual/fakeroot-native pixz-native"

# We want the MULTIARCH_TARGET_SYS to point to the TUNE_PKGARCH, not PACKAGE_ARCH as it
# could be set to the MACHINE_ARCH
REAL_MULTIMACH_TARGET_SYS = "${TUNE_PKGARCH}${TARGET_VENDOR}-${TARGET_OS}"

PID = "${@os.getpid()}"

EXCLUDE_FROM_WORLD = "1"

SDK_PACKAGING_FUNC ?= "create_shar"
SDK_PRE_INSTALL_COMMAND ?= ""
SDK_POST_INSTALL_COMMAND ?= ""
SDK_RELOCATE_AFTER_INSTALL ?= "1"

SDKEXTPATH ?= "~/${@d.getVar('DISTRO', True)}_sdk"
SDK_TITLE ?= "${@d.getVar('DISTRO_NAME', True) or d.getVar('DISTRO', True)} SDK"

SDK_TARGET_MANIFEST = "${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.target.manifest"
SDK_HOST_MANIFEST = "${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.host.manifest"
python write_target_sdk_manifest () {
    from oe.sdk import sdk_list_installed_packages
    from oe.utils import format_pkg_list
    sdkmanifestdir = os.path.dirname(d.getVar("SDK_TARGET_MANIFEST", True))
    pkgs = sdk_list_installed_packages(d, True)
    if not os.path.exists(sdkmanifestdir):
        bb.utils.mkdirhier(sdkmanifestdir)
    with open(d.getVar('SDK_TARGET_MANIFEST', True), 'w') as output:
        output.write(format_pkg_list(pkgs, 'ver'))
}

python write_host_sdk_manifest () {
    from oe.sdk import sdk_list_installed_packages
    from oe.utils import format_pkg_list
    sdkmanifestdir = os.path.dirname(d.getVar("SDK_HOST_MANIFEST", True))
    pkgs = sdk_list_installed_packages(d, False)
    if not os.path.exists(sdkmanifestdir):
        bb.utils.mkdirhier(sdkmanifestdir)
    with open(d.getVar('SDK_HOST_MANIFEST', True), 'w') as output:
        output.write(format_pkg_list(pkgs, 'ver'))
}

POPULATE_SDK_POST_TARGET_COMMAND_append = " write_target_sdk_manifest ; "
POPULATE_SDK_POST_HOST_COMMAND_append = " write_host_sdk_manifest; "
SDK_PACKAGING_COMMAND = "${@'${SDK_PACKAGING_FUNC};' if '${SDK_PACKAGING_FUNC}' else ''}"
SDK_POSTPROCESS_COMMAND = " create_sdk_files; check_sdk_sysroots; tar_sdk; ${SDK_PACKAGING_COMMAND} "

# Some archs override this, we need the nativesdk version
# turns out this is hard to get from the datastore due to TRANSLATED_TARGET_ARCH
# manipulation.
SDK_OLDEST_KERNEL = "2.6.32"

fakeroot python do_populate_sdk() {
    from oe.sdk import populate_sdk
    from oe.manifest import create_manifest, Manifest

    pn = d.getVar('PN', True)
    runtime_mapping_rename("TOOLCHAIN_TARGET_TASK", pn, d)
    runtime_mapping_rename("TOOLCHAIN_TARGET_TASK_ATTEMPTONLY", pn, d)

    ld = bb.data.createCopy(d)
    ld.setVar("PKGDATA_DIR", "${STAGING_DIR}/${SDK_ARCH}-${SDKPKGSUFFIX}${SDK_VENDOR}-${SDK_OS}/pkgdata")
    runtime_mapping_rename("TOOLCHAIN_HOST_TASK", pn, ld)
    runtime_mapping_rename("TOOLCHAIN_HOST_TASK_ATTEMPTONLY", pn, ld)
    d.setVar("TOOLCHAIN_HOST_TASK", ld.getVar("TOOLCHAIN_HOST_TASK", True))
    d.setVar("TOOLCHAIN_HOST_TASK_ATTEMPTONLY", ld.getVar("TOOLCHAIN_HOST_TASK_ATTEMPTONLY", True))
    
    # create target/host SDK manifests
    create_manifest(d, manifest_dir=d.getVar('SDK_DIR', True),
                    manifest_type=Manifest.MANIFEST_TYPE_SDK_HOST)
    create_manifest(d, manifest_dir=d.getVar('SDK_DIR', True),
                    manifest_type=Manifest.MANIFEST_TYPE_SDK_TARGET)

    populate_sdk(d)
}

fakeroot create_sdk_files() {
	cp ${COREBASE}/scripts/relocate_sdk.py ${SDK_OUTPUT}/${SDKPATH}/

	# Replace the ##DEFAULT_INSTALL_DIR## with the correct pattern.
	# Escape special characters like '+' and '.' in the SDKPATH
	escaped_sdkpath=$(echo ${SDKPATH} |sed -e "s:[\+\.]:\\\\\\\\\0:g")
	sed -i -e "s:##DEFAULT_INSTALL_DIR##:$escaped_sdkpath:" ${SDK_OUTPUT}/${SDKPATH}/relocate_sdk.py
}

python check_sdk_sysroots() {
    # Fails build if there are broken or dangling symlinks in SDK sysroots

    if d.getVar('CHECK_SDK_SYSROOTS', True) != '1':
        # disabled, bail out
        return

    def norm_path(path):
        return os.path.abspath(path)

    # Get scan root
    SCAN_ROOT = norm_path("${SDK_OUTPUT}/${SDKPATH}/sysroots/")

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

fakeroot tar_sdk() {
	# Package it up
	mkdir -p ${SDK_DEPLOY}
	cd ${SDK_OUTPUT}/${SDKPATH}
	tar ${SDKTAROPTS} -cf - . | pixz > ${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.tar.xz
}

fakeroot create_shar() {
	# copy in the template shar extractor script
	cp ${COREBASE}/meta/files/toolchain-shar-extract.sh ${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.sh

	rm -f ${T}/pre_install_command ${T}/post_install_command

	if [ ${SDK_RELOCATE_AFTER_INSTALL} -eq 1 ] ; then
		cp ${COREBASE}/meta/files/toolchain-shar-relocate.sh ${T}/post_install_command
	fi
	cat << "EOF" >> ${T}/pre_install_command
${SDK_PRE_INSTALL_COMMAND}
EOF

	cat << "EOF" >> ${T}/post_install_command
${SDK_POST_INSTALL_COMMAND}
EOF
	sed -i -e '/@SDK_PRE_INSTALL_COMMAND@/r ${T}/pre_install_command' \
		-e '/@SDK_POST_INSTALL_COMMAND@/r ${T}/post_install_command' \
		${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.sh

	# substitute variables
	sed -i -e 's#@SDK_ARCH@#${SDK_ARCH}#g' \
		-e 's#@SDKPATH@#${SDKPATH}#g' \
		-e 's#@SDKEXTPATH@#${SDKEXTPATH}#g' \
		-e 's#@OLDEST_KERNEL@#${SDK_OLDEST_KERNEL}#g' \
		-e 's#@REAL_MULTIMACH_TARGET_SYS@#${REAL_MULTIMACH_TARGET_SYS}#g' \
		-e 's#@SDK_TITLE@#${SDK_TITLE}#g' \
		-e 's#@SDK_VERSION@#${SDK_VERSION}#g' \
		-e '/@SDK_PRE_INSTALL_COMMAND@/d' \
		-e '/@SDK_POST_INSTALL_COMMAND@/d' \
		${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.sh

	# add execution permission
	chmod +x ${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.sh

	# append the SDK tarball
	cat ${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.tar.xz >> ${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.sh

	# delete the old tarball, we don't need it anymore
	rm ${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.tar.xz
}

populate_sdk_log_check() {
	for target in $*
	do
		lf_path="`dirname ${BB_LOGFILE}`/log.do_$target.${PID}"

		echo "log_check: Using $lf_path as logfile"

		if test -e "$lf_path"
		then
			${IMAGE_PKGTYPE}_log_check $target $lf_path
		else
			echo "Cannot find logfile [$lf_path]"
		fi
		echo "Logfile is clean"
	done
}

def sdk_command_variables(d):
    return ['OPKG_PREPROCESS_COMMANDS','OPKG_POSTPROCESS_COMMANDS','POPULATE_SDK_POST_HOST_COMMAND','POPULATE_SDK_POST_TARGET_COMMAND','SDK_POSTPROCESS_COMMAND','RPM_PREPROCESS_COMMANDS',
            'RPM_POSTPROCESS_COMMANDS']

def sdk_variables(d):
    variables = ['BUILD_IMAGES_FROM_FEEDS','SDK_OS','SDK_OUTPUT','SDKPATHNATIVE','SDKTARGETSYSROOT','SDK_DIR','SDK_VENDOR','SDKIMAGE_INSTALL_COMPLEMENTARY','SDK_PACKAGE_ARCHS','SDK_OUTPUT',
                 'SDKTARGETSYSROOT','MULTILIB_VARIANTS','MULTILIBS','ALL_MULTILIB_PACKAGE_ARCHS','MULTILIB_GLOBAL_VARIANTS','BAD_RECOMMENDATIONS','NO_RECOMMENDATIONS','PACKAGE_ARCHS',
                 'PACKAGE_CLASSES','TARGET_VENDOR','TARGET_VENDOR','TARGET_ARCH','TARGET_OS','BBEXTENDVARIANT','FEED_DEPLOYDIR_BASE_URI']
    variables.extend(sdk_command_variables(d))
    return " ".join(variables)

do_populate_sdk[vardeps] += "${@sdk_variables(d)}"

do_populate_sdk[file-checksums] += "${COREBASE}/meta/files/toolchain-shar-relocate.sh:True \
                                    ${COREBASE}/meta/files/toolchain-shar-extract.sh:True"

do_populate_sdk[dirs] = "${PKGDATA_DIR} ${TOPDIR}"
do_populate_sdk[depends] += "${@' '.join([x + ':do_populate_sysroot' for x in d.getVar('SDK_DEPENDS', True).split()])}  ${@d.getVarFlag('do_rootfs', 'depends', False)}"
do_populate_sdk[rdepends] = "${@' '.join([x + ':do_populate_sysroot' for x in d.getVar('SDK_RDEPENDS', True).split()])}"
do_populate_sdk[recrdeptask] += "do_packagedata do_package_write_rpm do_package_write_ipk do_package_write_deb"
addtask populate_sdk
