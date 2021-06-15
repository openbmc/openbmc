#
# This function is intended to add root to corresponding groups if 'debug-tweaks' or 'allow-root-login' is in IMAGE_FEATURES.
#
update_root_user_groups () {
	if [ -e ${IMAGE_ROOTFS}/etc/group ]; then
		sed -i '/^\(ipmi\|web\|redfish\|priv-admin\):.*:.*:$/s/$/root/' ${IMAGE_ROOTFS}/etc/group
	fi
}
# Add root user to the needed groups
ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains_any("IMAGE_FEATURES", [ 'debug-tweaks', 'allow-root-login' ], "update_root_user_groups; ", "", d)}'
