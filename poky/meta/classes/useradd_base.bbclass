# This bbclass provides basic functionality for user/group settings.
# This bbclass is intended to be inherited by useradd.bbclass and
# extrausers.bbclass.

# The following functions basically have similar logic.
# *) Perform necessary checks before invoking the actual command
# *) Invoke the actual command with flock
# *) Error out if an error occurs.

# Note that before invoking these functions, make sure the global variable
# PSEUDO is set up correctly.

perform_groupadd () {
	local rootdir="$1"
	local opts="$2"
	bbnote "${PN}: Performing groupadd with [$opts]"
	local groupname=`echo "$opts" | awk '{ print $NF }'`
	local group_exists="`grep "^$groupname:" $rootdir/etc/group || true`"
	if test "x$group_exists" = "x"; then
		eval flock -x $rootdir${sysconfdir} -c \"$PSEUDO groupadd \$opts\" || true
		group_exists="`grep "^$groupname:" $rootdir/etc/group || true`"
		if test "x$group_exists" = "x"; then
			bbfatal "${PN}: groupadd command did not succeed."
		fi
	else
		bbnote "${PN}: group $groupname already exists, not re-creating it"
	fi
}

perform_useradd () {
	local rootdir="$1"
	local opts="$2"
	bbnote "${PN}: Performing useradd with [$opts]"
	local username=`echo "$opts" | awk '{ print $NF }'`
	local user_exists="`grep "^$username:" $rootdir/etc/passwd || true`"
	if test "x$user_exists" = "x"; then
		eval flock -x $rootdir${sysconfdir} -c  \"$PSEUDO useradd \$opts\" || true
		user_exists="`grep "^$username:" $rootdir/etc/passwd || true`"
		if test "x$user_exists" = "x"; then
			bbfatal "${PN}: useradd command did not succeed."
		fi
	else
		bbnote "${PN}: user $username already exists, not re-creating it"
	fi
}

perform_groupmems () {
	local rootdir="$1"
	local opts="$2"
	bbnote "${PN}: Performing groupmems with [$opts]"
	local groupname=`echo "$opts" | awk '{ for (i = 1; i < NF; i++) if ($i == "-g" || $i == "--group") print $(i+1) }'`
	local username=`echo "$opts" | awk '{ for (i = 1; i < NF; i++) if ($i == "-a" || $i == "--add") print $(i+1) }'`
	bbnote "${PN}: Running groupmems command with group $groupname and user $username"
	local mem_exists="`grep "^$groupname:[^:]*:[^:]*:\([^,]*,\)*$username\(,[^,]*\)*$" $rootdir/etc/group || true`"
	if test "x$mem_exists" = "x"; then
		eval flock -x $rootdir${sysconfdir} -c \"$PSEUDO groupmems \$opts\" || true
		mem_exists="`grep "^$groupname:[^:]*:[^:]*:\([^,]*,\)*$username\(,[^,]*\)*$" $rootdir/etc/group || true`"
		if test "x$mem_exists" = "x"; then
			bbfatal "${PN}: groupmems command did not succeed."
		fi
	else
		bbnote "${PN}: group $groupname already contains $username, not re-adding it"
	fi
}

perform_groupdel () {
	local rootdir="$1"
	local opts="$2"
	bbnote "${PN}: Performing groupdel with [$opts]"
	local groupname=`echo "$opts" | awk '{ print $NF }'`
	local group_exists="`grep "^$groupname:" $rootdir/etc/group || true`"

	if test "x$group_exists" != "x"; then
		local awk_input='BEGIN {FS=":"}; $1=="'$groupname'" { print $3 }'
		local groupid=`echo "$awk_input" | awk -f- $rootdir/etc/group`
		local awk_check_users='BEGIN {FS=":"}; $4=="'$groupid'" {print $1}'
		local other_users=`echo "$awk_check_users" | awk -f- $rootdir/etc/passwd`

		if test "x$other_users" = "x"; then
			eval flock -x $rootdir${sysconfdir} -c \"$PSEUDO groupdel \$opts\" || true
			group_exists="`grep "^$groupname:" $rootdir/etc/group || true`"
			if test "x$group_exists" != "x"; then
				bbfatal "${PN}: groupdel command did not succeed."
			fi
		else
			bbnote "${PN}: '$groupname' is primary group for users '$other_users', not removing it"
		fi
	else
		bbnote "${PN}: group $groupname doesn't exist, not removing it"
	fi
}

perform_userdel () {
	local rootdir="$1"
	local opts="$2"
	bbnote "${PN}: Performing userdel with [$opts]"
	local username=`echo "$opts" | awk '{ print $NF }'`
	local user_exists="`grep "^$username:" $rootdir/etc/passwd || true`"
	if test "x$user_exists" != "x"; then
		eval flock -x $rootdir${sysconfdir} -c \"$PSEUDO userdel \$opts\" || true
		user_exists="`grep "^$username:" $rootdir/etc/passwd || true`"
		if test "x$user_exists" != "x"; then
			bbfatal "${PN}: userdel command did not succeed."
		fi
	else
		bbnote "${PN}: user $username doesn't exist, not removing it"
	fi
}

perform_groupmod () {
	# Other than the return value of groupmod, there's no simple way to judge whether the command
	# succeeds, so we disable -e option temporarily
	set +e
	local rootdir="$1"
	local opts="$2"
	bbnote "${PN}: Performing groupmod with [$opts]"
	local groupname=`echo "$opts" | awk '{ print $NF }'`
	local group_exists="`grep "^$groupname:" $rootdir/etc/group || true`"
	if test "x$group_exists" != "x"; then
		eval flock -x $rootdir${sysconfdir} -c \"$PSEUDO groupmod \$opts\"
		if test $? != 0; then
			bbwarn "${PN}: groupmod command did not succeed."
		fi
	else
		bbwarn "${PN}: group $groupname doesn't exist, unable to modify it"
	fi
	set -e
}

perform_usermod () {
	# Same reason with groupmod, temporarily disable -e option
	set +e
	local rootdir="$1"
	local opts="$2"
	bbnote "${PN}: Performing usermod with [$opts]"
	local username=`echo "$opts" | awk '{ print $NF }'`
	local user_exists="`grep "^$username:" $rootdir/etc/passwd || true`"
	if test "x$user_exists" != "x"; then
		eval flock -x $rootdir${sysconfdir} -c \"$PSEUDO usermod \$opts\"
		if test $? != 0; then
			bbfatal "${PN}: usermod command did not succeed."
		fi
	else
		bbwarn "${PN}: user $username doesn't exist, unable to modify it"
	fi
	set -e
}
