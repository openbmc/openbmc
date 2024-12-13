#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Zap the root password if debug-tweaks and empty-root-password features are not enabled
ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains_any("IMAGE_FEATURES", [ 'debug-tweaks', 'empty-root-password' ], "", "zap_empty_root_password ",d)}'

# Allow dropbear/openssh to accept logins from accounts with an empty password string if debug-tweaks or allow-empty-password is enabled
ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains_any("IMAGE_FEATURES", [ 'debug-tweaks', 'allow-empty-password' ], "ssh_allow_empty_password ", "",d)}'

# Allow dropbear/openssh to accept root logins if debug-tweaks or allow-root-login is enabled
ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains_any("IMAGE_FEATURES", [ 'debug-tweaks', 'allow-root-login' ], "ssh_allow_root_login ", "",d)}'

# Autologin the root user on the serial console, if empty-root-password and serial-autologin-root are active
ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains("IMAGE_FEATURES", [ 'empty-root-password', 'serial-autologin-root' ], "serial_autologin_root ", "",d)}'

# Enable postinst logging if debug-tweaks or post-install-logging is enabled
ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains_any("IMAGE_FEATURES", [ 'debug-tweaks', 'post-install-logging' ], "postinst_enable_logging ", "",d)}'

# Create /etc/timestamp during image construction to give a reasonably sane default time setting
ROOTFS_POSTPROCESS_COMMAND += "rootfs_update_timestamp "

# Tweak files in /etc if read-only-rootfs is enabled
ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains("IMAGE_FEATURES", "read-only-rootfs", "read_only_rootfs_hook ", "",d)}'

# We also need to do the same for the kernel boot parameters,
# otherwise kernel or initramfs end up mounting the rootfs read/write
# (the default) if supported by the underlying storage.
#
# We do this with :append because the default value might get set later with ?=
# and we don't want to disable such a default that by setting a value here.
APPEND:append = '${@bb.utils.contains("IMAGE_FEATURES", "read-only-rootfs", " ro", "", d)}'

# Generates test data file with data store variables expanded in json format
ROOTFS_POSTPROCESS_COMMAND += "write_image_test_data "

# Write manifest
IMAGE_MANIFEST = "${IMGDEPLOYDIR}/${IMAGE_NAME}.manifest"
ROOTFS_POSTUNINSTALL_COMMAND =+ "write_image_manifest"
# Set default postinst log file
POSTINST_LOGFILE ?= "${localstatedir}/log/postinstall.log"
# Set default target for systemd images
SYSTEMD_DEFAULT_TARGET ?= '${@bb.utils.contains_any("IMAGE_FEATURES", [ "x11-base", "weston" ], "graphical.target", "multi-user.target", d)}'
ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains("DISTRO_FEATURES", "systemd", "set_systemd_default_target systemd_sysusers_check", "", d)}'

ROOTFS_POSTPROCESS_COMMAND += 'empty_var_volatile'

ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains("DISTRO_FEATURES", "overlayfs", "overlayfs_qa_check overlayfs_postprocess", "", d)}'

inherit image-artifact-names

# Sort the user and group entries in /etc by ID in order to make the content
# deterministic. Package installs are not deterministic, causing the ordering
# of entries to change between builds. In case that this isn't desired,
# the command can be overridden.
SORT_PASSWD_POSTPROCESS_COMMAND ??= "tidy_shadowutils_files"
ROOTFS_POSTPROCESS_COMMAND += '${SORT_PASSWD_POSTPROCESS_COMMAND}'

#
# Note that useradd-staticids.bbclass has to be used to ensure that
# the numeric IDs of dynamically created entries remain stable.
#
ROOTFS_POSTPROCESS_COMMAND += 'rootfs_reproducible'

# Resolve the ID as described in the sysusers.d(5) manual: ID can be a numeric
# uid, a couple uid:gid or uid:groupname or it is '-' meaning leaving it
# automatic or it can be a path. In the latter, the uid/gid matches the
# user/group owner of that file.
def resolve_sysusers_id(d, sid):
    # If the id is a path, the uid/gid matchs to the target's uid/gid in the
    # rootfs.
    if '/' in sid:
        try:
            osstat = os.stat(os.path.join(d.getVar('IMAGE_ROOTFS'), sid))
        except FileNotFoundError:
            bb.error('sysusers.d: file %s is required but it does not exist in the rootfs', sid)
            return ('-', '-')
        return (osstat.st_uid, osstat.st_gid)
    # Else it is a uid:gid or uid:groupname syntax
    if ':' in sid:
        return sid.split(':')
    else:
        return (sid, '-')

# Check a user exists in the rootfs password file and return its properties
def check_user_exists(d, uname=None, uid=None):
    with open(os.path.join(d.getVar('IMAGE_ROOTFS'), 'etc/passwd'), 'r') as pwfile:
        for line in pwfile:
            (name, _, u_id, gid, comment, homedir, ushell) = line.strip().split(':')
            if uname == name or uid == u_id:
                return (name, u_id, gid, comment or '-', homedir or '/', ushell or '-')
    return None

# Check a group exists in the rootfs group file and return its properties
def check_group_exists(d, gname=None, gid=None):
    with open(os.path.join(d.getVar('IMAGE_ROOTFS'), 'etc/group'), 'r') as gfile:
        for line in gfile:
            (name, _, g_id, _) = line.strip().split(':')
            if name == gname or g_id == gid:
                return (name, g_id)
    return None

def compare_users(user, e_user):
    # user and e_user must not have None values. Unset values must be '-'.
    (name, uid, gid, comment, homedir, ushell) = user
    (e_name, e_uid, e_gid, e_comment, e_homedir, e_ushell) = e_user
    # Ignore 'uid', 'gid' or 'homedir' if they are not set
    # Ignore 'shell' and 'ushell' if one is not set
    return name == e_name \
        and (uid == '-' or uid == e_uid) \
        and (gid == '-' or gid == e_gid) \
        and (homedir == '-' or e_homedir == '-' or homedir == e_homedir) \
        and (ushell == '-' or e_ushell == '-' or ushell == e_ushell)

# Open sysusers.d configuration files and parse each line to check the users and
# groups are already defined in /etc/passwd and /etc/groups with similar
# properties. Refer to the sysusers.d(5) manual for its syntax.
python systemd_sysusers_check() {
    import glob
    import re

    pattern_comment = r'(-|\"[^:\"]+\")'
    pattern_word    = r'[^\s]+'
    pattern_line   = r'(' + pattern_word + r')\s+(' + pattern_word + r')\s+(' + pattern_word + r')(\s+' \
        + pattern_comment + r')?' + r'(\s+(' + pattern_word + r'))?' + r'(\s+(' + pattern_word + r'))?'

    for conffile in glob.glob(os.path.join(d.getVar('IMAGE_ROOTFS'), 'usr/lib/sysusers.d/*.conf')):
        with open(conffile, 'r') as f:
            for line in f:
                line = line.strip()
                if not len(line) or line[0] == '#': continue
                ret = re.fullmatch(pattern_line, line.strip())
                if not ret: continue
                (stype, sname, sid, _, scomment, _, shomedir, _, sshell) = ret.groups()
                if stype == 'u':
                    if sid:
                        (suid, sgid) = resolve_sysusers_id(d, sid)
                        if sgid.isalpha():
                            sgid = check_group_exists(d, gname=sgid)
                        elif sgid.isdigit():
                            check_group_exists(d, gid=sgid)
                        else:
                            sgid = '-'
                    else:
                        suid = '-'
                        sgid = '-'
                    scomment = scomment.replace('"', '') if scomment else '-'
                    shomedir = shomedir or '-'
                    sshell = sshell or '-'
                    e_user = check_user_exists(d, uname=sname)
                    if not e_user:
                        bb.warn('User %s has never been defined' % sname)
                    elif not compare_users((sname, suid, sgid, scomment, shomedir, sshell), e_user):
                        bb.warn('User %s has been defined as (%s) but sysusers.d expects it as (%s)'
                                % (sname, ', '.join(e_user),
                                ', '.join((sname, suid, sgid, scomment, shomedir, sshell))))
                elif stype == 'g':
                    gid = sid or '-'
                    if '/' in gid:
                        (_, gid) = resolve_sysusers_id(d, sid)
                    e_group = check_group_exists(d, gname=sname)
                    if not e_group:
                        bb.warn('Group %s has never been defined' % sname)
                    elif gid != '-':
                        (_, e_gid) = e_group
                        if gid != e_gid:
                            bb.warn('Group %s has been defined with id (%s) but sysusers.d expects gid (%s)'
                                    % (sname, e_gid, gid))
                elif stype == 'm':
                    check_user_exists(d, sname)
                    check_group_exists(d, sid)
}

#
# A hook function to support read-only-rootfs IMAGE_FEATURES
#
read_only_rootfs_hook () {
	# Tweak the mount option and fs_passno for rootfs in fstab
	if [ -f ${IMAGE_ROOTFS}/etc/fstab ]; then
		sed -i -e '/^[#[:space:]]*\/dev\/root/{s/defaults/ro/;s/\([[:space:]]*[[:digit:]]\)\([[:space:]]*\)[[:digit:]]$/\1\20/}' ${IMAGE_ROOTFS}/etc/fstab
	fi

	# Tweak the "mount -o remount,rw /" command in busybox-inittab inittab
	if [ -f ${IMAGE_ROOTFS}/etc/inittab ]; then
		sed -i 's|/bin/mount -o remount,rw /|/bin/mount -o remount,ro /|' ${IMAGE_ROOTFS}/etc/inittab
	fi

	# If we're using openssh and the /etc/ssh directory has no pre-generated keys,
	# we should configure openssh to use the configuration file /etc/ssh/sshd_config_readonly
	# and the keys under /var/run/ssh.
	# If overlayfs-etc is used this is not done as /etc is treated as writable
	# If stateless-rootfs is enabled this is always done as we don't want to save keys then
	if ${@ 'true' if not bb.utils.contains('IMAGE_FEATURES', 'overlayfs-etc', True, False, d) or bb.utils.contains('IMAGE_FEATURES', 'stateless-rootfs', True, False, d) else 'false'}; then
		if [ -d ${IMAGE_ROOTFS}/etc/ssh ]; then
			if [ -e ${IMAGE_ROOTFS}/etc/ssh/ssh_host_rsa_key ]; then
				echo "SYSCONFDIR=\${SYSCONFDIR:-/etc/ssh}" >> ${IMAGE_ROOTFS}/etc/default/ssh
				echo "SSHD_OPTS=" >> ${IMAGE_ROOTFS}/etc/default/ssh
			else
				echo "SYSCONFDIR=\${SYSCONFDIR:-/var/run/ssh}" >> ${IMAGE_ROOTFS}/etc/default/ssh
				echo "SSHD_OPTS='-f /etc/ssh/sshd_config_readonly'" >> ${IMAGE_ROOTFS}/etc/default/ssh
			fi
		fi

		# Also tweak the key location for dropbear in the same way.
		if [ -d ${IMAGE_ROOTFS}/etc/dropbear ]; then
			if [ ! -e ${IMAGE_ROOTFS}/etc/dropbear/dropbear_rsa_host_key ]; then
				if ! grep -q "^DROPBEAR_RSAKEY_DIR=" ${IMAGE_ROOTFS}/etc/default/dropbear ; then
					echo "DROPBEAR_RSAKEY_DIR=/var/lib/dropbear" >> ${IMAGE_ROOTFS}/etc/default/dropbear
				fi
			fi
		fi
	fi

	if ${@bb.utils.contains("DISTRO_FEATURES", "sysvinit", "true", "false", d)}; then
		# Change the value of ROOTFS_READ_ONLY in /etc/default/rcS to yes
		if [ -e ${IMAGE_ROOTFS}/etc/default/rcS ]; then
			sed -i 's/ROOTFS_READ_ONLY=no/ROOTFS_READ_ONLY=yes/' ${IMAGE_ROOTFS}/etc/default/rcS
		fi
		# Run populate-volatile.sh at rootfs time to set up basic files
		# and directories to support read-only rootfs.
		if [ -x ${IMAGE_ROOTFS}/etc/init.d/populate-volatile.sh ]; then
			${IMAGE_ROOTFS}/etc/init.d/populate-volatile.sh
		fi
	fi

	if ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "true", "false", d)}; then
	# Create machine-id
	# 20:12 < mezcalero> koen: you have three options: a) run systemd-machine-id-setup at install time, b) have / read-only and an empty file there (for stateless) and c) boot with / writable
		touch ${IMAGE_ROOTFS}${sysconfdir}/machine-id
	fi
}

#
# This function disallows empty root passwords
#
zap_empty_root_password () {
	if [ -e ${IMAGE_ROOTFS}/etc/shadow ]; then
		sed --follow-symlinks -i 's%^root::%root:*:%' ${IMAGE_ROOTFS}/etc/shadow
        fi
	if [ -e ${IMAGE_ROOTFS}/etc/passwd ]; then
		sed --follow-symlinks -i 's%^root::%root:*:%' ${IMAGE_ROOTFS}/etc/passwd
	fi
}

#
# allow dropbear/openssh to accept logins from accounts with an empty password string
#
ssh_allow_empty_password () {
	for config in sshd_config sshd_config_readonly; do
		if [ -e ${IMAGE_ROOTFS}${sysconfdir}/ssh/$config ]; then
			sed -i 's/^[#[:space:]]*PermitEmptyPasswords.*/PermitEmptyPasswords yes/' ${IMAGE_ROOTFS}${sysconfdir}/ssh/$config
		fi
	done

	if [ -e ${IMAGE_ROOTFS}${sbindir}/dropbear ] ; then
		if grep -q DROPBEAR_EXTRA_ARGS ${IMAGE_ROOTFS}${sysconfdir}/default/dropbear 2>/dev/null ; then
			if ! grep -q "DROPBEAR_EXTRA_ARGS=.*-B" ${IMAGE_ROOTFS}${sysconfdir}/default/dropbear ; then
				sed -i 's/^DROPBEAR_EXTRA_ARGS="*\([^"]*\)"*/DROPBEAR_EXTRA_ARGS="\1 -B"/' ${IMAGE_ROOTFS}${sysconfdir}/default/dropbear
			fi
		else
			printf '\nDROPBEAR_EXTRA_ARGS="-B"\n' >> ${IMAGE_ROOTFS}${sysconfdir}/default/dropbear
		fi
	fi

	if [ -d ${IMAGE_ROOTFS}${sysconfdir}/pam.d ] ; then
		for f in `find ${IMAGE_ROOTFS}${sysconfdir}/pam.d/* -type f -exec test -e {} \; -print`
		do
			sed -i 's/nullok_secure/nullok/' $f
		done
	fi
}

#
# allow dropbear/openssh to accept root logins
#
ssh_allow_root_login () {
	for config in sshd_config sshd_config_readonly; do
		if [ -e ${IMAGE_ROOTFS}${sysconfdir}/ssh/$config ]; then
			sed -i 's/^[#[:space:]]*PermitRootLogin.*/PermitRootLogin yes/' ${IMAGE_ROOTFS}${sysconfdir}/ssh/$config
		fi
	done

	if [ -e ${IMAGE_ROOTFS}${sbindir}/dropbear ] ; then
		if grep -q DROPBEAR_EXTRA_ARGS ${IMAGE_ROOTFS}${sysconfdir}/default/dropbear 2>/dev/null ; then
			sed -i '/^DROPBEAR_EXTRA_ARGS=/ s/-w//' ${IMAGE_ROOTFS}${sysconfdir}/default/dropbear
			sed -i '/^# Disallow root/d' ${IMAGE_ROOTFS}${sysconfdir}/default/dropbear
		fi
	fi
}

#
# Autologin the 'root' user on the serial terminal,
# if empty-root-password' AND 'serial-autologin-root are enabled
#
serial_autologin_root () {
	if ${@bb.utils.contains("DISTRO_FEATURES", "sysvinit", "true", "false", d)}; then
		# add autologin option to util-linux getty only
		sed -i 's/options="/&--autologin root /' \
			"${IMAGE_ROOTFS}${base_bindir}/start_getty"
	elif ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "true", "false", d)}; then
		if [ -e ${IMAGE_ROOTFS}${systemd_system_unitdir}/serial-getty@.service ]; then
			sed -i '/^\s*ExecStart\b/ s/getty /&--autologin root /' \
				"${IMAGE_ROOTFS}${systemd_system_unitdir}/serial-getty@.service"
		fi
	fi
}

python tidy_shadowutils_files () {
    import rootfspostcommands
    rootfspostcommands.tidy_shadowutils_files(d.expand('${IMAGE_ROOTFS}${sysconfdir}'))
}

python sort_passwd () {
    """
    Deprecated in the favour of tidy_shadowutils_files.
    """
    import rootfspostcommands
    bb.warn('[sort_passwd] You are using a deprecated function for '
        'SORT_PASSWD_POSTPROCESS_COMMAND. The default one is now called '
        '"tidy_shadowutils_files".')
    rootfspostcommands.tidy_shadowutils_files(d.expand('${IMAGE_ROOTFS}${sysconfdir}'))
}

#
# Enable postinst logging
#
postinst_enable_logging () {
	mkdir -p ${IMAGE_ROOTFS}${sysconfdir}/default
	echo "POSTINST_LOGGING=1" >> ${IMAGE_ROOTFS}${sysconfdir}/default/postinst
	echo "LOGFILE=${POSTINST_LOGFILE}" >> ${IMAGE_ROOTFS}${sysconfdir}/default/postinst
}

#
# Modify systemd default target
#
set_systemd_default_target () {
	if [ -d ${IMAGE_ROOTFS}${sysconfdir}/systemd/system -a -e ${IMAGE_ROOTFS}${systemd_system_unitdir}/${SYSTEMD_DEFAULT_TARGET} ]; then
		ln -sf ${systemd_system_unitdir}/${SYSTEMD_DEFAULT_TARGET} ${IMAGE_ROOTFS}${sysconfdir}/systemd/system/default.target
	fi
}

# If /var/volatile is not empty, we have seen problems where programs such as the
# journal make assumptions based on the contents of /var/volatile. The journal
# would then write to /var/volatile before it was mounted, thus hiding the
# items previously written.
#
# This change is to attempt to fix those types of issues in a way that doesn't
# affect users that may not be using /var/volatile.
empty_var_volatile () {
	if [ -e ${IMAGE_ROOTFS}/etc/fstab ]; then
		match=`awk '$1 !~ "#" && $2 ~ /\/var\/volatile/{print $2}' ${IMAGE_ROOTFS}/etc/fstab 2> /dev/null`
		if [ -n "$match" ]; then
			find ${IMAGE_ROOTFS}/var/volatile -mindepth 1 -delete
		fi
	fi
}

# Turn any symbolic /sbin/init link into a file
remove_init_link () {
	if [ -h ${IMAGE_ROOTFS}/sbin/init ]; then
		LINKFILE=${IMAGE_ROOTFS}`readlink ${IMAGE_ROOTFS}/sbin/init`
		rm ${IMAGE_ROOTFS}/sbin/init
		cp $LINKFILE ${IMAGE_ROOTFS}/sbin/init
	fi
}

python write_image_manifest () {
    from oe.rootfs import image_list_installed_packages
    from oe.utils import format_pkg_list

    deploy_dir = d.getVar('IMGDEPLOYDIR')
    link_name = d.getVar('IMAGE_LINK_NAME')
    manifest_name = d.getVar('IMAGE_MANIFEST')

    if not manifest_name:
        return

    pkgs = image_list_installed_packages(d)
    with open(manifest_name, 'w+') as image_manifest:
        image_manifest.write(format_pkg_list(pkgs, "ver"))

    if os.path.exists(manifest_name) and link_name:
        manifest_link = deploy_dir + "/" + link_name + ".manifest"
        if manifest_link != manifest_name:
            if os.path.lexists(manifest_link):
                os.remove(manifest_link)
            os.symlink(os.path.basename(manifest_name), manifest_link)
}

# Can be used to create /etc/timestamp during image construction to give a reasonably
# sane default time setting
rootfs_update_timestamp () {
	if [ "${REPRODUCIBLE_TIMESTAMP_ROOTFS}" != "" ]; then
		# Convert UTC into %4Y%2m%2d%2H%2M%2S
		sformatted=`date -u -d @${REPRODUCIBLE_TIMESTAMP_ROOTFS} +%4Y%2m%2d%2H%2M%2S`
	else
		sformatted=`date -u +%4Y%2m%2d%2H%2M%2S`
	fi
	echo $sformatted > ${IMAGE_ROOTFS}/etc/timestamp
	bbnote "rootfs_update_timestamp: set /etc/timestamp to $sformatted"
}

# Prevent X from being started
rootfs_no_x_startup () {
	if [ -f ${IMAGE_ROOTFS}/etc/init.d/xserver-nodm ]; then
		chmod a-x ${IMAGE_ROOTFS}/etc/init.d/xserver-nodm
	fi
}

rootfs_trim_schemas () {
	for schema in ${IMAGE_ROOTFS}/etc/gconf/schemas/*.schemas
	do
		# Need this in case no files exist
		if [ -e $schema ]; then
			oe-trim-schemas $schema > $schema.new
			mv $schema.new $schema
		fi
	done
}

rootfs_check_host_user_contaminated () {
	contaminated="${S}/host-user-contaminated.txt"
	HOST_USER_UID="$(PSEUDO_UNLOAD=1 id -u)"
	HOST_USER_GID="$(PSEUDO_UNLOAD=1 id -g)"

	find "${IMAGE_ROOTFS}" -path "${IMAGE_ROOTFS}/home" -prune -o \
	    -user "$HOST_USER_UID" -print -o -group "$HOST_USER_GID" -print >"$contaminated"

	sed -e "s,${IMAGE_ROOTFS},," $contaminated | while read line; do
		bbwarn "Path in the rootfs is owned by the same user or group as the user running bitbake:" $line `ls -lan ${IMAGE_ROOTFS}/$line`
	done

	if [ -s "$contaminated" ]; then
		bbwarn "/etc/passwd:" `cat ${IMAGE_ROOTFS}/etc/passwd`
		bbwarn "/etc/group:" `cat ${IMAGE_ROOTFS}/etc/group`
	fi
}

# Make any absolute links in a sysroot relative
rootfs_sysroot_relativelinks () {
	sysroot-relativelinks.py ${SDK_OUTPUT}/${SDKTARGETSYSROOT}
}

# Generated test data json file
python write_image_test_data() {
    from oe.data import export2json

    deploy_dir = d.getVar('IMGDEPLOYDIR')
    link_name = d.getVar('IMAGE_LINK_NAME')
    testdata_name = os.path.join(deploy_dir, "%s.testdata.json" % d.getVar('IMAGE_NAME'))

    searchString = "%s/"%(d.getVar("TOPDIR")).replace("//","/")
    export2json(d, testdata_name, searchString=searchString, replaceString="")

    if os.path.exists(testdata_name) and link_name:
        testdata_link = os.path.join(deploy_dir, "%s.testdata.json" % link_name)
        if testdata_link != testdata_name:
            if os.path.lexists(testdata_link):
                os.remove(testdata_link)
            os.symlink(os.path.basename(testdata_name), testdata_link)
}
write_image_test_data[vardepsexclude] += "TOPDIR"

# Check for unsatisfied recommendations (RRECOMMENDS)
python rootfs_log_check_recommends() {
    log_path = d.expand("${T}/log.do_rootfs")
    with open(log_path, 'r') as log:
        for line in log:
            if 'log_check' in line:
                continue

            if 'unsatisfied recommendation for' in line:
                bb.warn('[log_check] %s: %s' % (d.getVar('PN'), line))
}

# Perform any additional adjustments needed to make rootf binary reproducible
rootfs_reproducible () {
	if [ "${REPRODUCIBLE_TIMESTAMP_ROOTFS}" != "" ]; then
		# Convert UTC into %4Y%2m%2d%2H%2M%2S
		sformatted=`date -u -d @${REPRODUCIBLE_TIMESTAMP_ROOTFS} +%4Y%2m%2d%2H%2M%2S`
		echo $sformatted > ${IMAGE_ROOTFS}/etc/version
		bbnote "rootfs_reproducible: set /etc/version to $sformatted"

		if [ -d ${IMAGE_ROOTFS}${sysconfdir}/gconf ]; then
			find ${IMAGE_ROOTFS}${sysconfdir}/gconf -name '%gconf.xml' -print0 | xargs -0r \
			sed -i -e 's@\bmtime="[0-9][0-9]*"@mtime="'${REPRODUCIBLE_TIMESTAMP_ROOTFS}'"@g'
		fi

		if [ -f ${IMAGE_ROOTFS}${localstatedir}/lib/opkg/status ]; then
			sed -i 's/^Installed-Time: .*/Installed-Time: ${REPRODUCIBLE_TIMESTAMP_ROOTFS}/' ${IMAGE_ROOTFS}${localstatedir}/lib/opkg/status
		fi
	fi
}

# Perform a dumb check for unit existence, not its validity
python overlayfs_qa_check() {
    from oe.overlayfs import mountUnitName

    overlayMountPoints = d.getVarFlags("OVERLAYFS_MOUNT_POINT") or {}
    imagepath = d.getVar("IMAGE_ROOTFS")
    sysconfdir = d.getVar("sysconfdir")
    searchpaths = [oe.path.join(imagepath, sysconfdir, "systemd", "system"),
                   oe.path.join(imagepath, d.getVar("systemd_system_unitdir"))]
    fstabpath = oe.path.join(imagepath, sysconfdir, "fstab")

    if not any(os.path.exists(path) for path in [*searchpaths, fstabpath]):
        return

    fstabDevices = []
    if os.path.isfile(fstabpath):
        with open(fstabpath, 'r') as f:
            for line in f:
                if line[0] == '#':
                    continue
                path = line.split(maxsplit=2)
                if len(path) > 2:
                    fstabDevices.append(path[1])

    allUnitExist = True;
    for mountPoint in overlayMountPoints:
        qaSkip = (d.getVarFlag("OVERLAYFS_QA_SKIP", mountPoint) or "").split()
        if "mount-configured" in qaSkip:
            continue

        mountPath = d.getVarFlag('OVERLAYFS_MOUNT_POINT', mountPoint)
        if mountPath in fstabDevices:
            continue

        mountUnit = mountUnitName(mountPath)
        if any(os.path.isfile(oe.path.join(dirpath, mountUnit))
               for dirpath in searchpaths):
            continue

        bb.warn(f'Mount path {mountPath} not found in fstab and unit '
                f'{mountUnit} not found in systemd unit directories.')
        bb.warn(f'Skip this check by setting OVERLAYFS_QA_SKIP[{mountPoint}] = '
                '"mount-configured"')
        allUnitExist = False;

    if not allUnitExist:
        bb.fatal('Not all mount paths and units are installed in the image')
}

python overlayfs_postprocess() {
    import shutil

    # install helper script
    helperScriptName = "overlayfs-create-dirs.sh"
    helperScriptSource = oe.path.join(d.getVar("COREBASE"), "meta/files", helperScriptName)
    helperScriptDest = oe.path.join(d.getVar("IMAGE_ROOTFS"), "/usr/sbin/", helperScriptName)
    shutil.copyfile(helperScriptSource, helperScriptDest)
    os.chmod(helperScriptDest, 0o755)
}
