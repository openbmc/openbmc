
oe_soinstall() {
	# Purpose: Install shared library file and
	#          create the necessary links
	# Example: oe_soinstall libfoo.so.1.2.3 ${D}${libdir}
	libname=`basename $1`
	case "$libname" in
	    *.so)
	        bbfatal "oe_soinstall: Shared library must haved versioned filename (e.g. libfoo.so.1.2.3)"
	        ;;
	esac
	install -m 755 $1 $2/$libname
	sonamelink=`${HOST_PREFIX}readelf -d $1 |grep 'Library soname:' |sed -e 's/.*\[\(.*\)\].*/\1/'`
	if [ -z $sonamelink ]; then
		bbfatal "oe_soinstall: $libname is missing ELF tag 'SONAME'."
	fi
	solink=`echo $libname | sed -e 's/\.so\..*/.so/'`
	ln -sf $libname $2/$sonamelink
	ln -sf $libname $2/$solink
}

oe_libinstall() {
	# Purpose: Install a library, in all its forms
	# Example
	#
	# oe_libinstall libltdl ${STAGING_LIBDIR}/
	# oe_libinstall -C src/libblah libblah ${D}/${libdir}/
	dir=""
	libtool=""
	silent=""
	require_static=""
	require_shared=""
	staging_install=""
	while [ "$#" -gt 0 ]; do
		case "$1" in
		-C)
			shift
			dir="$1"
			;;
		-s)
			silent=1
			;;
		-a)
			require_static=1
			;;
		-so)
			require_shared=1
			;;
		-*)
			bbfatal "oe_libinstall: unknown option: $1"
			;;
		*)
			break;
			;;
		esac
		shift
	done

	libname="$1"
	shift
	destpath="$1"
	if [ -z "$destpath" ]; then
		bbfatal "oe_libinstall: no destination path specified"
	fi
	if echo "$destpath/" | egrep '^${STAGING_LIBDIR}/' >/dev/null
	then
		staging_install=1
	fi

	__runcmd () {
		if [ -z "$silent" ]; then
			echo >&2 "oe_libinstall: $*"
		fi
		$*
	}

	if [ -z "$dir" ]; then
		dir=`pwd`
	fi

	dotlai=$libname.lai

	# Sanity check that the libname.lai is unique
	number_of_files=`(cd $dir; find . -name "$dotlai") | wc -l`
	if [ $number_of_files -gt 1 ]; then
		bbfatal "oe_libinstall: $dotlai is not unique in $dir"
	fi


	dir=$dir`(cd $dir;find . -name "$dotlai") | sed "s/^\.//;s/\/$dotlai\$//;q"`
	olddir=`pwd`
	__runcmd cd $dir

	lafile=$libname.la

	# If such file doesn't exist, try to cut version suffix
	if [ ! -f "$lafile" ]; then
		libname1=`echo "$libname" | sed 's/-[0-9.]*$//'`
		lafile1=$libname.la
		if [ -f "$lafile1" ]; then
			libname=$libname1
			lafile=$lafile1
		fi
	fi

	if [ -f "$lafile" ]; then
		# libtool archive
		eval `cat $lafile|grep "^library_names="`
		libtool=1
	else
		library_names="$libname.so* $libname.dll.a $libname.*.dylib"
	fi

	__runcmd install -d $destpath/
	dota=$libname.a
	if [ -f "$dota" -o -n "$require_static" ]; then
		rm -f $destpath/$dota
		__runcmd install -m 0644 $dota $destpath/
	fi
	if [ -f "$dotlai" -a -n "$libtool" ]; then
		rm -f $destpath/$libname.la
		__runcmd install -m 0644 $dotlai $destpath/$libname.la
	fi

	for name in $library_names; do
		files=`eval echo $name`
		for f in $files; do
			if [ ! -e "$f" ]; then
				if [ -n "$libtool" ]; then
					bbfatal "oe_libinstall: $dir/$f not found."
				fi
			elif [ -L "$f" ]; then
				__runcmd cp -P "$f" $destpath/
			elif [ ! -L "$f" ]; then
				libfile="$f"
				rm -f $destpath/$libfile
				__runcmd install -m 0755 $libfile $destpath/
			fi
		done
	done

	if [ -z "$libfile" ]; then
		if  [ -n "$require_shared" ]; then
			bbfatal "oe_libinstall: unable to locate shared library"
		fi
	elif [ -z "$libtool" ]; then
		# special case hack for non-libtool .so.#.#.# links
		baselibfile=`basename "$libfile"`
		if (echo $baselibfile | grep -qE '^lib.*\.so\.[0-9.]*$'); then
			sonamelink=`${HOST_PREFIX}readelf -d $libfile |grep 'Library soname:' |sed -e 's/.*\[\(.*\)\].*/\1/'`
			solink=`echo $baselibfile | sed -e 's/\.so\..*/.so/'`
			if [ -n "$sonamelink" -a x"$baselibfile" != x"$sonamelink" ]; then
				__runcmd ln -sf $baselibfile $destpath/$sonamelink
			fi
			__runcmd ln -sf $baselibfile $destpath/$solink
		fi
	fi

	__runcmd cd "$olddir"
}

oe_machinstall() {
	# Purpose: Install machine dependent files, if available
	#          If not available, check if there is a default
	#          If no default, just touch the destination
	# Example:
	#                $1  $2   $3         $4
	# oe_machinstall -m 0644 fstab ${D}/etc/fstab
	#
	# TODO: Check argument number?
	#
	filename=`basename $3`
	dirname=`dirname $3`

	for o in `echo ${OVERRIDES} | tr ':' ' '`; do
		if [ -e $dirname/$o/$filename ]; then
			bbnote $dirname/$o/$filename present, installing to $4
			install $1 $2 $dirname/$o/$filename $4
			return
		fi
	done
#	bbnote overrides specific file NOT present, trying default=$3...
	if [ -e $3 ]; then
		bbnote $3 present, installing to $4
		install $1 $2 $3 $4
	else
		bbnote $3 NOT present, touching empty $4
		touch $4
	fi
}

create_cmdline_wrapper () {
	# Create a wrapper script where commandline options are needed
	#
	# These are useful to work around relocation issues, by passing extra options
	# to a program
	#
	# Usage: create_cmdline_wrapper FILENAME <extra-options>

	cmd=$1
	shift

	echo "Generating wrapper script for $cmd"

	mv $cmd $cmd.real
	cmdname=`basename $cmd`
	dirname=`dirname $cmd`
	cmdoptions=$@
	if [ "${base_prefix}" != "" ]; then
		relpath=`python3 -c "import os; print(os.path.relpath('${D}${base_prefix}', '$dirname'))"`
		cmdoptions=`echo $@ | sed -e "s:${base_prefix}:\\$realdir/$relpath:g"`
	fi
	cat <<END >$cmd
#!/bin/bash
realpath=\`readlink -fn \$0\`
realdir=\`dirname \$realpath\`
exec -a \`dirname \$realpath\`/$cmdname \`dirname \$realpath\`/$cmdname.real $cmdoptions "\$@"
END
	chmod +x $cmd
}

create_wrapper () {
	# Create a wrapper script where extra environment variables are needed
	#
	# These are useful to work around relocation issues, by setting environment
	# variables which point to paths in the filesystem.
	#
	# Usage: create_wrapper FILENAME [[VAR=VALUE]..]

	cmd=$1
	shift

	echo "Generating wrapper script for $cmd"

	mv $cmd $cmd.real
	cmdname=`basename $cmd`
	dirname=`dirname $cmd`
	exportstring=$@
	if [ "${base_prefix}" != "" ]; then
		relpath=`python3 -c "import os; print(os.path.relpath('${D}${base_prefix}', '$dirname'))"`
		exportstring=`echo $@ | sed -e "s:${base_prefix}:\\$realdir/$relpath:g"`
	fi
	cat <<END >$cmd
#!/bin/bash
realpath=\`readlink -fn \$0\`
realdir=\`dirname \$realpath\`
export $exportstring
exec -a "\$0" \$realdir/$cmdname.real "\$@"
END
	chmod +x $cmd
}

# Copy files/directories from $1 to $2 but using hardlinks
# (preserve symlinks)
hardlinkdir () {
	from=$1
	to=$2
	(cd $from; find . -print0 | cpio --null -pdlu $to)
}


def check_app_exists(app, d):
    app = d.expand(app).split()[0].strip()
    path = d.getVar('PATH')
    return bool(bb.utils.which(path, app))

def explode_deps(s):
    return bb.utils.explode_deps(s)

def base_set_filespath(path, d):
    filespath = []
    extrapaths = (d.getVar("FILESEXTRAPATHS") or "")
    # Remove default flag which was used for checking
    extrapaths = extrapaths.replace("__default:", "")
    # Don't prepend empty strings to the path list
    if extrapaths != "":
        path = extrapaths.split(":") + path
    # The ":" ensures we have an 'empty' override
    overrides = (":" + (d.getVar("FILESOVERRIDES") or "")).split(":")
    overrides.reverse()
    for o in overrides:
        for p in path:
            if p != "":
                filespath.append(os.path.join(p, o))
    return ":".join(filespath)

def extend_variants(d, var, extend, delim=':'):
    """Return a string of all bb class extend variants for the given extend"""
    variants = []
    whole = d.getVar(var) or ""
    for ext in whole.split():
        eext = ext.split(delim)
        if len(eext) > 1 and eext[0] == extend:
            variants.append(eext[1])
    return " ".join(variants)

def multilib_pkg_extend(d, pkg):
    variants = (d.getVar("MULTILIB_VARIANTS") or "").split()
    if not variants:
        return pkg
    pkgs = pkg
    for v in variants:
        pkgs = pkgs + " " + v + "-" + pkg
    return pkgs

def get_multilib_datastore(variant, d):
    return oe.utils.get_multilib_datastore(variant, d)

def all_multilib_tune_values(d, var, unique = True, need_split = True, delim = ' '):
    """Return a string of all ${var} in all multilib tune configuration"""
    values = []
    variants = (d.getVar("MULTILIB_VARIANTS") or "").split() + ['']
    for item in variants:
        localdata = get_multilib_datastore(item, d)
        # We need WORKDIR to be consistent with the original datastore
        localdata.setVar("WORKDIR", d.getVar("WORKDIR"))
        value = localdata.getVar(var) or ""
        if value != "":
            if need_split:
                for item in value.split(delim):
                    values.append(item)
            else:
                values.append(value)
    if unique:
        #we do this to keep order as much as possible
        ret = []
        for value in values:
            if not value in ret:
                ret.append(value)
    else:
        ret = values
    return " ".join(ret)

def all_multilib_tune_list(vars, d):
    """
    Return a list of ${VAR} for each variable VAR in vars from each 
    multilib tune configuration.
    Is safe to be called from a multilib recipe/context as it can
    figure out the original tune and remove the multilib overrides.
    """
    values = {}
    for v in vars:
        values[v] = []
    values['ml'] = ['']

    variants = (d.getVar("MULTILIB_VARIANTS") or "").split() + ['']
    for item in variants:
        localdata = get_multilib_datastore(item, d)
        values[v].append(localdata.getVar(v))
        values['ml'].append(item)
    return values
all_multilib_tune_list[vardepsexclude] = "OVERRIDES"

# If the user hasn't set up their name/email, set some defaults
check_git_config() {
	if ! git config user.email > /dev/null ; then
		git config --local user.email "${PATCH_GIT_USER_EMAIL}"
	fi
	if ! git config user.name > /dev/null ; then
		git config --local user.name "${PATCH_GIT_USER_NAME}"
	fi
}
