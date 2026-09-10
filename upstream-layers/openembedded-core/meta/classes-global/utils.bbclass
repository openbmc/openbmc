#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

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
	sonamelink=`${OBJDUMP} -p $1 | grep SONAME | awk '{print $2}'`
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
			sonamelink=`${OBJDUMP} -p $libfile | grep SONAME | awk '{print $2}'`
			solink=`echo $baselibfile | sed -e 's/\.so\..*/.so/'`
			if [ -n "$sonamelink" -a x"$baselibfile" != x"$sonamelink" ]; then
				__runcmd ln -sf $baselibfile $destpath/$sonamelink
			fi
			__runcmd ln -sf $baselibfile $destpath/$solink
		fi
	fi

	__runcmd cd "$olddir"
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
exec -a \$realdir/$cmdname \$realdir/$cmdname.real $cmdoptions "\$@"
END
	chmod +x $cmd
}

create_cmdline_shebang_wrapper () {
	# Create a wrapper script where commandline options are needed
	#
	# These are useful to work around shebang relocation issues, where shebangs are too
	# long or have arguments in them, thus preventing them from using the /usr/bin/env
	# shebang
	#
	# Usage: create_cmdline_wrapper FILENAME <extra-options>

	cmd=$1
	shift

	echo "Generating wrapper script for $cmd"

	# Strip #! and get remaining interpreter + arg
	argument="$(sed -ne 's/^#! *//p;q' $cmd)"
	# strip the shebang from the real script as we do not want it to be usable anyway
	tail -n +2 $cmd > $cmd.real
	chown --reference=$cmd $cmd.real
	chmod --reference=$cmd $cmd.real
	rm -f $cmd
	cmdname=$(basename $cmd)
	dirname=$(dirname $cmd)
	cmdoptions=$@
	if [ "${base_prefix}" != "" ]; then
		relpath=`python3 -c "import os; print(os.path.relpath('${D}${base_prefix}', '$dirname'))"`
		cmdoptions=`echo $@ | sed -e "s:${base_prefix}:\\$realdir/$relpath:g"`
	fi
	cat <<END >$cmd
#!/usr/bin/env bash
realpath=\`readlink -fn \$0\`
realdir=\`dirname \$realpath\`
exec -a \$realdir/$cmdname $argument \$realdir/$cmdname.real $cmdoptions "\$@"
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

# If the user hasn't set up their name/email, set some defaults
check_git_config() {
	if ! git config user.email > /dev/null ; then
		git config --local user.email "${PATCH_GIT_USER_EMAIL}"
	fi
	if ! git config user.name > /dev/null ; then
		git config --local user.name "${PATCH_GIT_USER_NAME}"
	fi
}

# Sets fixed git committer and author for reproducible commits
reproducible_git_committer_author() {
	export GIT_COMMITTER_NAME="${PATCH_GIT_USER_NAME}"
	export GIT_COMMITTER_EMAIL="${PATCH_GIT_USER_EMAIL}"
	export GIT_COMMITTER_DATE="$(date -d @${SOURCE_DATE_EPOCH})"
	export GIT_AUTHOR_NAME="${PATCH_GIT_USER_NAME}"
	export GIT_AUTHOR_EMAIL="${PATCH_GIT_USER_EMAIL}"
	export GIT_AUTHOR_DATE="$(date -d @${SOURCE_DATE_EPOCH})"
}
