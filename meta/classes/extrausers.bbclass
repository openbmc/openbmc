# This bbclass is mainly used for image level user/group configuration.
# Inherit this class if you want to make EXTRA_USERS_PARAMS effective.

# Below is an example showing how to use this functionality.
# INHERIT += "extrausers"
# EXTRA_USERS_PARAMS = "\
# useradd -p '' tester; \
# groupadd developers; \
# userdel nobody; \
# groupdel -g video; \
# groupmod -g 1020 developers; \
# usermod -s /bin/sh tester; \
# "


inherit useradd_base

IMAGE_INSTALL_append = " ${@['', 'base-passwd shadow'][bool(d.getVar('EXTRA_USERS_PARAMS', True))]}"

# Image level user / group settings
ROOTFS_POSTPROCESS_COMMAND_append = " set_user_group;"

# Image level user / group settings
set_user_group () {
	user_group_settings="${EXTRA_USERS_PARAMS}"
	export PSEUDO="${FAKEROOTENV} ${STAGING_DIR_NATIVE}${bindir}/pseudo"
	setting=`echo $user_group_settings | cut -d ';' -f1`
	remaining=`echo $user_group_settings | cut -d ';' -f2-`
	while test "x$setting" != "x"; do
		cmd=`echo $setting | cut -d ' ' -f1`
		opts=`echo $setting | cut -d ' ' -f2-`
		# Different from useradd.bbclass, there's no file locking issue here, as
		# this setting is actually a serial process. So we only retry once.
		case $cmd in
			useradd)
				perform_useradd "${IMAGE_ROOTFS}" "-R ${IMAGE_ROOTFS} $opts" 1
				;;
			groupadd)
				perform_groupadd "${IMAGE_ROOTFS}" "-R ${IMAGE_ROOTFS} $opts" 1
				;;
			userdel)
				perform_userdel "${IMAGE_ROOTFS}" "-R ${IMAGE_ROOTFS} $opts" 1
				;;
			groupdel)
				perform_groupdel "${IMAGE_ROOTFS}" "-R ${IMAGE_ROOTFS} $opts" 1
				;;
			usermod)
				perform_usermod "${IMAGE_ROOTFS}" "-R ${IMAGE_ROOTFS} $opts" 1
				;;
			groupmod)
				perform_groupmod "${IMAGE_ROOTFS}" "-R ${IMAGE_ROOTFS} $opts" 1
				;;
			*)
				bbfatal "Invalid command in EXTRA_USERS_PARAMS: $cmd"
				;;
		esac
		# Avoid infinite loop if the last parameter doesn't end with ';'
		if [ "$setting" = "$remaining" ]; then
			break
		fi
		# iterate to the next setting
		setting=`echo $remaining | cut -d ';' -f1`
		remaining=`echo $remaining | cut -d ';' -f2-`
	done
}
