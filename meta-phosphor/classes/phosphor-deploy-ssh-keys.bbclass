####
# Copyright 2020 Hewlett Packard Enterprise Development LP.
# Copyright 2021 Intel Corporation
#
# Add a basic class to add a privileged user from an ssh
# standpoint and a public key passed as an input parameter
# from the local.conf file
# Example:
# INHERIT += "phosphor-deploy-ssh-keys"
#
# SSH_KEYS = "vejmarie:/home/openbmc/openbmc/meta-hpe/keys/test.pub"
# or
# SSH_KEYS = "vejmarie:/home/openbmc/openbmc/meta-hpe/keys/test.pub;root:/path/to/id_rsa.pub"
####

inherit useradd_base

IMAGE_PREPROCESS_COMMAND += "deploy_local_user;"

deploy_local_user () {
	if [ "${SSH_KEYS}" == "" ]; then
		bbwarn "Trying to deploy SSH keys but input variable is empty (SSH_KEYS)"
		return
	fi

	ssh_keys="${SSH_KEYS}"
	while [ "${ssh_keys}" != "" ]; do
		current_key=`echo "$ssh_keys" | cut -d ';' -f1`
		ssh_keys=`echo "$ssh_keys" | cut -s -d ';' -f2-`

		username=`echo "$current_key" | awk -F":" '{ print $1}'`
		key_path=`echo "$current_key" | awk -F":" '{ print $2}'`

		if [ ! -d ${IMAGE_ROOTFS}/home/${username} ]; then
			perform_useradd "${IMAGE_ROOTFS}" "-R ${IMAGE_ROOTFS} -p '' ${username}"
		fi

		if [ ! -d ${IMAGE_ROOTFS}/home/${username}.ssh/ ]; then
			install -d ${IMAGE_ROOTFS}/home/${username}/.ssh/
		fi

		if [ ! -f ${IMAGE_ROOTFS}/home/${username}/.ssh/authorized_keys ]; then
			install -m 0600 ${key_path} ${IMAGE_ROOTFS}/home/${username}/.ssh/authorized_keys
		else
			cat ${key_path} >> ${IMAGE_ROOTFS}/home/${username}/.ssh/authorized_keys
		fi

		uid=`cat ${IMAGE_ROOTFS}/etc/passwd | grep "${username}:" | awk -F ":" '{print $3}'`
		guid=`cat ${IMAGE_ROOTFS}/etc/passwd | grep "${username}:" | awk -F ":" '{print $4}'`

		chown -R ${uid}:${guid} ${IMAGE_ROOTFS}/home/${username}/.ssh
		chmod 600  ${IMAGE_ROOTFS}/home/${username}/.ssh/authorized_keys
		chmod 700 ${IMAGE_ROOTFS}/home/${username}/.ssh

		is_group=`grep "priv-admin" ${IMAGE_ROOTFS}/etc/group || true`

		if [ -z "${is_group}" ]; then
			perform_groupadd "${IMAGE_ROOTFS}" "-R ${IMAGE_ROOTFS} priv-admin"
		fi

		perform_usermod "${IMAGE_ROOTFS}" "-R ${IMAGE_ROOTFS} -a -G priv-admin ${username}"
	done
}
