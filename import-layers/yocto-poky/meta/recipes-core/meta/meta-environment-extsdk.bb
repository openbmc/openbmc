# meta-environment for extensible SDK

require meta-environment.bb

PN = "meta-environment-extsdk-${MACHINE}"

create_sdk_files_append() {
	local sysroot=${SDKPATH}/${@os.path.relpath(d.getVar('STAGING_DIR_TARGET', True), d.getVar('TOPDIR', True))}
	local sdkpathnative=${SDKPATH}/${@os.path.relpath(d.getVar('STAGING_DIR_NATIVE',True), d.getVar('TOPDIR', True))}

	toolchain_create_sdk_env_script '' '' $sysroot '' ${bindir_native} ${prefix_native} $sdkpathnative
}
