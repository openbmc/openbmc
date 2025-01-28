#
# This function is intended to add root to corresponding groups if 'allow-root-login' is in IMAGE_FEATURES.
#
update_root_user_groups () {
    if [ -e ${IMAGE_ROOTFS}/etc/group ]; then
        sed -i '/^\(ipmi\|web\|redfish\|priv-admin\):.*:.*:$/s/$/root/' ${IMAGE_ROOTFS}/etc/group
    fi
}
# Add root user to the needed groups
ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains_any("IMAGE_FEATURES", [ 'allow-root-login' ], "update_root_user_groups", "", d)}'
