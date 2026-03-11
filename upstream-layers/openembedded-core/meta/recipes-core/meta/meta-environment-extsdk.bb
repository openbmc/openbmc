# meta-environment for extensible SDK

require meta-environment.bb

PN = "meta-environment-extsdk-${MACHINE}"

create_sdk_files:append() {
	local sysroot=${SDKPATH}/tmp/${@os.path.relpath(d.getVar('STAGING_DIR'), d.getVar('TMPDIR'))}/${MACHINE}
	local sdkpathnative=${SDKPATH}/tmp/${@os.path.relpath(d.getVar('STAGING_DIR'), d.getVar('TMPDIR'))}/${BUILD_ARCH}

	toolchain_create_sdk_env_script '' '' $sysroot '' ${bindir_native} ${prefix_native} $sdkpathnative
}
