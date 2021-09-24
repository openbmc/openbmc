SUMMARY = "Package of environment files for SDK"
LICENSE = "MIT"
PR = "r8"

EXCLUDE_FROM_WORLD = "1"

MODIFYTOS = "0"

REAL_MULTIMACH_TARGET_SYS = "${TUNE_PKGARCH}${TARGET_VENDOR}-${TARGET_OS}"

inherit toolchain-scripts
TOOLCHAIN_NEED_CONFIGSITE_CACHE:append = " zlib"
# Need to expand here before cross-candian changes HOST_ARCH -> SDK_ARCH
TOOLCHAIN_CONFIGSITE_NOCACHE := "${TOOLCHAIN_CONFIGSITE_NOCACHE}"

SDK_DIR = "${WORKDIR}/sdk"
SDK_OUTPUT = "${SDK_DIR}/image"
SDKTARGETSYSROOT = "${SDKPATH}/sysroots/${REAL_MULTIMACH_TARGET_SYS}"

inherit cross-canadian

do_generate_content[cleandirs] = "${SDK_OUTPUT}"
do_generate_content[dirs] = "${SDK_OUTPUT}/${SDKPATH}"
# Need to ensure we have the virtual mappings and site files for all multtilib variants
do_generate_content[depends] = "${@oe.utils.build_depends_string(all_multilib_tune_values(d, 'TOOLCHAIN_NEED_CONFIGSITE_CACHE'), 'do_populate_sysroot')}"
python do_generate_content() {
    # Handle multilibs in the SDK environment, siteconfig, etc files...
    localdata = bb.data.createCopy(d)

    # make sure we only use the WORKDIR value from 'd', or it can change
    localdata.setVar('WORKDIR', d.getVar('WORKDIR'))

    # make sure we only use the SDKTARGETSYSROOT value from 'd'
    localdata.setVar('SDKTARGETSYSROOT', d.getVar('SDKTARGETSYSROOT'))
    localdata.setVar('libdir', d.getVar('target_libdir', False))

    # Process DEFAULTTUNE
    bb.build.exec_func("create_sdk_files", localdata)

    variants = d.getVar("MULTILIB_VARIANTS") or ""
    for item in variants.split():
        # Load overrides from 'd' to avoid having to reset the value...
        overrides = d.getVar("OVERRIDES", False) + ":virtclass-multilib-" + item
        localdata.setVar("OVERRIDES", overrides)
        localdata.setVar("MLPREFIX", item + "-")
        bb.build.exec_func("create_sdk_files", localdata)
}
addtask generate_content before do_install after do_compile

python () {
    sitefiles, searched = siteinfo_get_files(d, sysrootcache=False)
    d.appendVarFlag("do_generate_content", "file-checksums", " " + " ".join(searched))
}

create_sdk_files() {
	# Setup site file for external use
	toolchain_create_sdk_siteconfig ${SDK_OUTPUT}/${SDKPATH}/site-config-${REAL_MULTIMACH_TARGET_SYS}

	toolchain_create_sdk_env_script ${SDK_OUTPUT}/${SDKPATH}/environment-setup-${REAL_MULTIMACH_TARGET_SYS}

	# Add version information
	toolchain_create_sdk_version ${SDK_OUTPUT}/${SDKPATH}/version-${REAL_MULTIMACH_TARGET_SYS}

	toolchain_create_post_relocate_script ${SDK_OUTPUT}/${SDKPATH}/post-relocate-setup.sh ${SDKPATH}
}

do_install() {
    install -d ${D}/${SDKPATH}
    install -m 0644 -t ${D}/${SDKPATH} ${SDK_OUTPUT}/${SDKPATH}/*
}

PN = "meta-environment-${MACHINE}"
PACKAGES = "${PN}"
FILES:${PN}= " \
    ${SDKPATH}/* \
    "

deltask do_fetch
deltask do_unpack
deltask do_patch
deltask do_configure
deltask do_compile
deltask do_populate_sysroot
