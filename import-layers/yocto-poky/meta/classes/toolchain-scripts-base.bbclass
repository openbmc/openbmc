#This function create a version information file
toolchain_create_sdk_version () {
	local versionfile=$1
	rm -f $versionfile
	touch $versionfile
	echo 'Distro: ${DISTRO}' >> $versionfile
	echo 'Distro Version: ${DISTRO_VERSION}' >> $versionfile
	echo 'Metadata Revision: ${METADATA_REVISION}' >> $versionfile
	echo 'Timestamp: ${DATETIME}' >> $versionfile
}
toolchain_create_sdk_version[vardepsexclude] = "DATETIME"
