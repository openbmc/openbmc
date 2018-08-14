#!/bin/sh
# Modified version of setup.sh distributed with webmin

if [ "$wadir" = "" ]; then
	echo "ERROR: wadir not specified"
	echo ""
	exit 1
fi

config_dir_runtime=$config_dir
config_dir=$prefix$config_dir

wadir_runtime=$wadir
wadir=$prefix$wadir

ver=`cat "$wadir/version"`

cd "$wadir"

# Work out perl library path
PERLLIB=$wadir

# Validate source directory
allmods=`cd "$wadir"; echo */module.info | sed -e 's/\/module.info//g'`
if [ "$allmods" = "" ]; then
	echo "ERROR: Failed to get module list"
	echo ""
	exit 1
fi
echo ""

if [ "$login" = "webmin" ]; then
	echo "ERROR: Username 'webmin' is reserved for internal use"
	echo ""
	exit 14
fi

# Create webserver config file
echo $perl > $config_dir/perl-path
echo $var_dir > $config_dir/var-path
echo "Creating web server config files.."
cfile=$config_dir/miniserv.conf
echo "port=$port" >> $cfile
echo "root=$wadir_runtime" >> $cfile
echo "mimetypes=$wadir_runtime/mime.types" >> $cfile
echo "addtype_cgi=internal/cgi" >> $cfile
echo "realm=Webmin Server" >> $cfile
echo "logfile=$var_dir/miniserv.log" >> $cfile
echo "errorlog=$var_dir/miniserv.error" >> $cfile
echo "pidfile=$var_dir/miniserv.pid" >> $cfile
echo "logtime=168" >> $cfile
echo "ppath=$ppath" >> $cfile
echo "ssl=$ssl" >> $cfile
echo "env_WEBMIN_CONFIG=$config_dir_runtime" >> $cfile
echo "env_WEBMIN_VAR=$var_dir" >> $cfile
echo "atboot=$atboot" >> $cfile
echo "logout=$config_dir_runtime/logout-flag" >> $cfile
if [ "$listen" != "" ]; then
	echo "listen=$listen" >> $cfile
else
	echo "listen=10000" >> $cfile
fi
echo "denyfile=\\.pl\$" >> $cfile
echo "log=1" >> $cfile
echo "blockhost_failures=5" >> $cfile
echo "blockhost_time=60" >> $cfile
echo "syslog=1" >> $cfile
if [ "$allow" != "" ]; then
	echo "allow=$allow" >> $cfile
fi
if [ "$session" != "" ]; then
	echo "session=$session" >> $cfile
else
	echo "session=1" >> $cfile
fi
if [ "$pam" != "" ]; then
	echo "pam=$pam" >> $cfile
fi
if [ "$no_pam" != "" ]; then
    echo "no_pam=$no_pam" >> $cfile
fi
echo premodules=WebminCore >> $cfile
echo "server=MiniServ/$ver" >> $cfile

md5pass=`$perl -e 'print crypt("test", "\\$1\\$A9wB3O18\\$zaZgqrEmb9VNltWTL454R/") eq "\\$1\\$A9wB3O18\\$zaZgqrEmb9VNltWTL454R/" ? "1\n" : "0\n"'`

ufile=$config_dir/miniserv.users
if [ "$crypt" != "" ]; then
	echo "$login:$crypt:0" > $ufile
else
	if [ "$md5pass" = "1" ]; then
		$perl -e 'print "$ARGV[0]:",crypt($ARGV[1], "\$1\$XXXXXXXX"),":0\n"' "$login" "$password" > $ufile
	else
		$perl -e 'print "$ARGV[0]:",crypt($ARGV[1], "XX"),":0\n"' "$login" "$password" > $ufile
	fi
fi
chmod 600 $ufile
echo "userfile=$config_dir_runtime/miniserv.users" >> $cfile

kfile=$config_dir/miniserv.pem
openssl version >/dev/null 2>&1
if [ "$?" = "0" ]; then
	# We can generate a new SSL key for this host
	host=`hostname`
	openssl req -newkey rsa:512 -x509 -nodes -out $tempdir/cert -keyout $tempdir/key -days 1825 >/dev/null 2>&1 <<EOF
.
.
.
Webmin Webserver on $host
.
*
root@$host
EOF
	if [ "$?" = "0" ]; then
		cat $tempdir/cert $tempdir/key >$kfile
	fi
	rm -f $tempdir/cert $tempdir/key
fi
if [ ! -r $kfile ]; then
	# Fall back to the built-in key
	cp "$wadir/miniserv.pem" $kfile
fi
chmod 600 $kfile
echo "keyfile=$config_dir_runtime/miniserv.pem" >> $cfile

chmod 600 $cfile
echo "..done"
echo ""

echo "Creating access control file.."
afile=$config_dir/webmin.acl
rm -f $afile
if [ "$defaultmods" = "" ]; then
	echo "$login: $allmods" >> $afile
else
	echo "$login: $defaultmods" >> $afile
fi
chmod 600 $afile
echo "..done"
echo ""

if [ "$login" != "root" -a "$login" != "admin" ]; then
	# Allow use of RPC by this user
	echo rpc=1 >>$config_dir/$login.acl
fi

if [ "$noperlpath" = "" ]; then
	echo "Inserting path to perl into scripts.."
	(find "$wadir" -name '*.cgi' -print ; find "$wadir" -name '*.pl' -print) | $perl "$wadir/perlpath.pl" $perl_runtime -
	echo "..done"
	echo ""
fi

echo "Creating start and stop scripts.."
rm -f $config_dir/stop $config_dir/start $config_dir/restart $config_dir/reload
echo "#!/bin/sh" >>$config_dir/start
echo "echo Starting Webmin server in $wadir_runtime" >>$config_dir/start
echo "trap '' 1" >>$config_dir/start
echo "LANG=" >>$config_dir/start
echo "export LANG" >>$config_dir/start
echo "#PERLIO=:raw" >>$config_dir/start
echo "unset PERLIO" >>$config_dir/start
echo "export PERLIO" >>$config_dir/start
echo "PERLLIB=$PERLLIB" >>$config_dir/start
echo "export PERLLIB" >>$config_dir/start
uname -a | grep -i 'HP/*UX' >/dev/null
if [ $? = "0" ]; then
	echo "exec '$wadir_runtime/miniserv.pl' $config_dir_runtime/miniserv.conf &" >>$config_dir/start
else
	echo "exec '$wadir_runtime/miniserv.pl' $config_dir_runtime/miniserv.conf" >>$config_dir/start
fi

echo "#!/bin/sh" >>$config_dir/stop
echo "echo Stopping Webmin server in $wadir_runtime" >>$config_dir/stop
echo "pidfile=\`grep \"^pidfile=\" $config_dir_runtime/miniserv.conf | sed -e 's/pidfile=//g'\`" >>$config_dir/stop
echo "kill \`cat \$pidfile\`" >>$config_dir/stop

echo "#!/bin/sh" >>$config_dir/restart
echo "$config_dir_runtime/stop && $config_dir_runtime/start" >>$config_dir/restart

echo "#!/bin/sh" >>$config_dir/reload
echo "echo Reloading Webmin server in $wadir_runtime" >>$config_dir/reload
echo "pidfile=\`grep \"^pidfile=\" $config_dir_runtime/miniserv.conf | sed -e 's/pidfile=//g'\`" >>$config_dir/reload
echo "kill -USR1 \`cat \$pidfile\`" >>$config_dir/reload

chmod 755 $config_dir/start $config_dir/stop $config_dir/restart $config_dir/reload
echo "..done"
echo ""

if [ "$upgrading" = 1 ]; then
	echo "Updating config files.."
else
	echo "Copying config files.."
fi
newmods=`$perl "$wadir/copyconfig.pl" "$os_type/$real_os_type" "$os_version/$real_os_version" "$wadir" $config_dir "" $allmods`
# Store the OS and version
echo "os_type=$os_type" >> $config_dir/config
echo "os_version=$os_version" >> $config_dir/config
echo "real_os_type=$real_os_type" >> $config_dir/config
echo "real_os_version=$real_os_version" >> $config_dir/config
if [ -r /etc/system.cnf ]; then
	# Found a caldera system config file .. get the language
	source /etc/system.cnf
	if [ "$CONF_LST_LANG" = "us" ]; then
		CONF_LST_LANG=en
	elif [ "$CONF_LST_LANG" = "uk" ]; then
		CONF_LST_LANG=en
	fi
	grep "lang=$CONF_LST_LANG," "$wadir/lang_list.txt" >/dev/null 2>&1
	if [ "$?" = 0 ]; then
		echo "lang=$CONF_LST_LANG" >> $config_dir/config
	fi
fi

# Turn on logging by default
echo "log=1" >> $config_dir/config

# Use licence module specified by environment variable
if [ "$licence_module" != "" ]; then
	echo licence_module=$licence_module >>$config_dir/config
fi

# Disallow unknown referers by default
echo "referers_none=1" >>$config_dir/config
echo $ver > $config_dir/version
echo "..done"
echo ""

# Set passwd_ fields in miniserv.conf from global config
for field in passwd_file passwd_uindex passwd_pindex passwd_cindex passwd_mindex; do
	grep $field= $config_dir/miniserv.conf >/dev/null
	if [ "$?" != "0" ]; then
		grep $field= $config_dir/config >> $config_dir/miniserv.conf
	fi
done
grep passwd_mode= $config_dir/miniserv.conf >/dev/null
if [ "$?" != "0" ]; then
	echo passwd_mode=0 >> $config_dir/miniserv.conf
fi

# If Perl crypt supports MD5, then make it the default
if [ "$md5pass" = "1" ]; then
	echo md5pass=1 >> $config_dir/config
fi

# Set a special theme if none was set before
if [ "$theme" = "" ]; then
	theme=`cat "$wadir/defaulttheme" 2>/dev/null`
fi
oldthemeline=`grep "^theme=" $config_dir/config`
oldtheme=`echo $oldthemeline | sed -e 's/theme=//g'`
if [ "$theme" != "" ] && [ "$oldthemeline" = "" ] && [ -d "$wadir/$theme" ]; then
	themelist=$theme
fi

# Set a special overlay if none was set before
if [ "$overlay" = "" ]; then
	overlay=`cat "$wadir/defaultoverlay" 2>/dev/null`
fi
if [ "$overlay" != "" ] && [ "$theme" != "" ] && [ -d "$wadir/$overlay" ]; then
	themelist="$themelist $overlay"
fi

# Apply the theme and maybe overlay
if [ "$themelist" != "" ]; then
	echo "theme=$themelist" >> $config_dir/config
	echo "preroot=$themelist" >> $config_dir/miniserv.conf
fi

# Set the product field in the global config
grep product= $config_dir/config >/dev/null
if [ "$?" != "0" ]; then
	echo product=webmin >> $config_dir/config
fi

if [ "$makeboot" = "1" ]; then
	echo "Configuring Webmin to start at boot time.."
	(cd "$wadir/init" ; WEBMIN_CONFIG=$config_dir WEBMIN_VAR=$var_dir "$wadir/init/atboot.pl" $bootscript)
	echo "..done"
	echo ""
fi

# If password delays are not specifically disabled, enable them
grep passdelay= $config_dir/miniserv.conf >/dev/null
if [ "$?" != "0" ]; then
	echo passdelay=1 >> $config_dir/miniserv.conf
fi

echo "Changing ownership and permissions .."
# Make all config dirs non-world-readable
for m in $newmods; do
	chown -R root $config_dir/$m
	chgrp -R bin $config_dir/$m
	chmod -R og-rw $config_dir/$m
done
# Make miniserv config files non-world-readable
for f in miniserv.conf miniserv.pem miniserv.users; do
	chown -R root $config_dir/$f
	chgrp -R bin $config_dir/$f
	chmod -R og-rw $config_dir/$f
done
chmod +r $config_dir/version
if [ "$nochown" = "" ]; then
	# Make program directory non-world-writable, but executable
	chown -R root "$wadir"
	chgrp -R bin "$wadir"
	chmod -R og-w "$wadir"
	chmod -R a+rx "$wadir"
fi
if [ $var_dir != "/var" ]; then
	# Make log directory non-world-readable or writable
	chown -R root $prefix$var_dir
	chgrp -R bin $prefix$var_dir
	chmod -R og-rwx $prefix$var_dir
fi
# Fix up bad permissions from some older installs
for m in ldap-client ldap-server ldap-useradmin mailboxes mysql postgresql servers virtual-server; do
	if [ -d "$config_dir/$m" ]; then
		chown root $config_dir/$m
		chgrp bin $config_dir/$m
		chmod og-rw $config_dir/$m
		chmod og-rw $config_dir/$m/config 2>/dev/null
	fi
done

if [ "$nopostinstall" = "" ]; then
	echo "Running postinstall scripts .."
	(cd "$wadir" ; WEBMIN_CONFIG=$config_dir WEBMIN_VAR=$var_dir "$wadir/run-postinstalls.pl")
	echo "..done"
	echo ""
fi

# Enable background collection
if [ "$upgrading" != 1 -a -r $config_dir/system-status/enable-collection.pl ]; then
	echo "Enabling background status collection .."
	$config_dir/system-status/enable-collection.pl 5
	echo "..done"
	echo ""
fi

