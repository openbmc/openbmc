#!/bin/sh
### BEGIN INIT INFO
# Provides:             volatile
# Required-Start:       $local_fs
# Required-Stop:      $local_fs
# Default-Start:        S
# Default-Stop:
# Short-Description:  Populate the volatile filesystem
### END INIT INFO

# Get ROOT_DIR
DIRNAME="$(dirname "$0")"
ROOT_DIR="$(echo "$DIRNAME" | sed -ne 's:/etc/.*::p')"

[ -e "${ROOT_DIR}/etc/default/rcS" ] && . "${ROOT_DIR}/etc/default/rcS"
# When running populate-volatile.sh at rootfs time, disable cache.
[ -n "$ROOT_DIR" ] && VOLATILE_ENABLE_CACHE=no
# If rootfs is read-only, disable cache.
[ "$ROOTFS_READ_ONLY" = "yes" ] && VOLATILE_ENABLE_CACHE=no

CFGDIR="${ROOT_DIR}/etc/default/volatiles"
TMPROOT="${ROOT_DIR}/var/volatile/tmp"
COREDEF="00_core"

[ "${VERBOSE}" != "no" ] && echo "Populating volatile Filesystems."

create_file() {
	EXEC=""
	if [ -z "$2" ]; then
		EXEC="
		touch \"$1\";
		"
	else
		EXEC="
		cp \"$2\" \"$1\";
		"
	fi
	EXEC="
	${EXEC}
	chown ${TUSER}:${TGROUP} $1 || echo \"Failed to set owner -${TUSER}- for -$1-.\" >/dev/tty0 2>&1;
	chmod ${TMODE} $1 || echo \"Failed to set mode -${TMODE}- for -$1-.\" >/dev/tty0 2>&1 "

	test "$VOLATILE_ENABLE_CACHE" = yes && echo "$EXEC" >> /etc/volatile.cache.build

	if [ -e "$1" ]; then
		[ "${VERBOSE}" != "no" ] && echo "Target already exists. Skipping."
	else
		if [ -z "$ROOT_DIR" ]; then
			eval "$EXEC"
		else
			# Creating some files at rootfs time may fail and should fail,
			# but these failures should not be logged to make sure the do_rootfs
			# process doesn't fail. This does no harm, as this script will
			# run on target to set up the correct files and directories.
			eval "$EXEC" > /dev/null 2>&1
		fi
	fi
}

mk_dir() {
	EXEC="
	mkdir -p \"$1\";
	chown ${TUSER}:${TGROUP} $1 || echo \"Failed to set owner -${TUSER}- for -$1-.\" >/dev/tty0 2>&1;
	chmod ${TMODE} $1 || echo \"Failed to set mode -${TMODE}- for -$1-.\" >/dev/tty0 2>&1 "

	test "$VOLATILE_ENABLE_CACHE" = yes && echo "$EXEC" >> /etc/volatile.cache.build
	if [ -e "$1" ]; then
		[ "${VERBOSE}" != "no" ] && echo "Target already exists. Skipping."
	else
		if [ -z "$ROOT_DIR" ]; then
			eval "$EXEC"
		else
			# For the same reason with create_file(), failures should
			# not be logged.
			eval "$EXEC" > /dev/null 2>&1
		fi
	fi
}

link_file() {
	EXEC="
	if [ -L \"$2\" ]; then
		[ \"\$(readlink \"$2\")\" != \"$1\" ] && { rm -f \"$2\"; ln -sf \"$1\" \"$2\"; };
	elif [ -d \"$2\" ]; then
		if awk '\$2 == \"$2\" {exit 1}' /proc/mounts; then
			cp -a $2/* $1 2>/dev/null;
			cp -a $2/.[!.]* $1 2>/dev/null;
			rm -rf \"$2\";
			ln -sf \"$1\" \"$2\";
		fi
	else
		ln -sf \"$1\" \"$2\";
	fi
        "

	test "$VOLATILE_ENABLE_CACHE" = yes && echo "	$EXEC" >> /etc/volatile.cache.build

	if [ -z "$ROOT_DIR" ]; then
		eval "$EXEC"
	else
		# For the same reason with create_file(), failures should
		# not be logged.
		eval "$EXEC" > /dev/null 2>&1
	fi
}

check_requirements() {
	cleanup() {
		rm "${TMP_INTERMED}"
		rm "${TMP_DEFINED}"
		rm "${TMP_COMBINED}"
	}

	CFGFILE="$1"

	TMP_INTERMED="${TMPROOT}/tmp.$$"
	TMP_DEFINED="${TMPROOT}/tmpdefined.$$"
	TMP_COMBINED="${TMPROOT}/tmpcombined.$$"

	sed 's@\(^:\)*:.*@\1@' "${ROOT_DIR}/etc/passwd" | sort | uniq > "${TMP_DEFINED}"
	grep -v "^#" "${CFGFILE}" | cut -s -d " " -f 2 > "${TMP_INTERMED}"
	cat "${TMP_DEFINED}" "${TMP_INTERMED}" | sort | uniq > "${TMP_COMBINED}"
	NR_DEFINED_USERS="$(wc -l < "${TMP_DEFINED}")"
	NR_COMBINED_USERS="$(wc -l < "${TMP_COMBINED}")"

	[ "${NR_DEFINED_USERS}" -ne "${NR_COMBINED_USERS}" ] && {
		echo "Undefined users:"
		diff "${TMP_DEFINED}" "${TMP_COMBINED}" | grep "^>"
		cleanup
		return 1
	}


	sed 's@\(^:\)*:.*@\1@' "${ROOT_DIR}/etc/group" | sort | uniq > "${TMP_DEFINED}"
	grep -v "^#" "${CFGFILE}" | cut -s -d " " -f 3 > "${TMP_INTERMED}"
	cat "${TMP_DEFINED}" "${TMP_INTERMED}" | sort | uniq > "${TMP_COMBINED}"

	NR_DEFINED_GROUPS="$(wc -l < "${TMP_DEFINED}")"
	NR_COMBINED_GROUPS="$(wc -l < "${TMP_COMBINED}")"

	[ "${NR_DEFINED_GROUPS}" -ne "${NR_COMBINED_GROUPS}" ] && {
		echo "Undefined groups:"
		diff "${TMP_DEFINED}" "${TMP_COMBINED}" | grep "^>"
		cleanup
		return 1
	}

	# Add checks for required directories here

	cleanup
	return 0
}

apply_cfgfile() {
	CFGFILE="$1"
	SKIP_REQUIREMENTS="$2"

	[ "${VERBOSE}" != "no" ] && echo "Applying ${CFGFILE}"

	[ "${SKIP_REQUIREMENTS}" = "yes" ] || check_requirements "${CFGFILE}" || {
		echo "Skipping ${CFGFILE}"
		return 1
	}

	sed 's/#.*//' "${CFGFILE}" | \
	while read -r TTYPE TUSER TGROUP TMODE TNAME TLTARGET; do
		test -z "${TLTARGET}" && continue
		TNAME=${ROOT_DIR}${TNAME}
		[ "${VERBOSE}" != "no" ] && echo "Checking for -${TNAME}-."

		[ "${TTYPE}" = "l" ] && {
			TSOURCE="$TLTARGET"
			[ "${VERBOSE}" != "no" ] && echo "Creating link -${TNAME}- pointing to -${TSOURCE}-."
			link_file "${TSOURCE}" "${TNAME}"
			continue
		}

		[ "${TTYPE}" = "b" ] && {
			TSOURCE="$TLTARGET"
			[ "${VERBOSE}" != "no" ] && echo "Creating mount-bind -${TNAME}- from -${TSOURCE}-."
			mount --bind "${TSOURCE}" "${TNAME}"
			EXEC="
	mount --bind \"${TSOURCE}\" \"${TNAME}\""
			test "$VOLATILE_ENABLE_CACHE" = yes && echo "$EXEC" >> /etc/volatile.cache.build
			continue
		}

		[ -L "${TNAME}" ] && {
			[ "${VERBOSE}" != "no" ] && echo "Found link."
			NEWNAME=$(ls -l "${TNAME}" | sed -e 's/^.*-> \(.*\)$/\1/')
			if echo "${NEWNAME}" | grep -v "^/" >/dev/null; then
				TNAME="$(echo "${TNAME}" | sed -e 's@\(.*\)/.*@\1@')/${NEWNAME}"
				[ "${VERBOSE}" != "no" ] && echo "Converted relative linktarget to absolute path -${TNAME}-."
			else
				TNAME="${NEWNAME}"
				[ "${VERBOSE}" != "no" ] && echo "Using absolute link target -${TNAME}-."
			fi
		}

		case "${TTYPE}" in
			"f")  [ "${VERBOSE}" != "no" ] && echo "Creating file -${TNAME}-."
				TSOURCE="$TLTARGET"
				[ "${TSOURCE}" = "none" ] && TSOURCE=""
				create_file "${TNAME}" "${TSOURCE}" &
				;;
			"d")  [ "${VERBOSE}" != "no" ] && echo "Creating directory -${TNAME}-."
				mk_dir "${TNAME}"
				# Add check to see if there's an entry in fstab to mount.
				;;
			*)    [ "${VERBOSE}" != "no" ] && echo "Invalid type -${TTYPE}-."
				continue
				;;
		esac
	done
	return 0
}

clearcache=0
exec 9</proc/cmdline
while read -r line <&9
do
	case "$line" in
		*clearcache*)  clearcache=1
			       ;;
		*)	       continue
			       ;;
	esac
done
exec 9>&-

if test -e "${ROOT_DIR}/etc/volatile.cache" -a "$VOLATILE_ENABLE_CACHE" = "yes" -a "x$1" != "xupdate" -a "x$clearcache" = "x0"
then
	sh "${ROOT_DIR}/etc/volatile.cache"
else
	rm -f "${ROOT_DIR}/etc/volatile.cache" "${ROOT_DIR}/etc/volatile.cache.build"

	# Apply the core file with out checking requirements. ${TMPROOT} is
	# needed by check_requirements but is setup by this file, so it must be
	# processed first and without being checked.
	[ -e "${CFGDIR}/${COREDEF}" ] && apply_cfgfile "${CFGDIR}/${COREDEF}" "yes"

	# Fast path: check_requirements is slow and most of the time doesn't
	# find any problems. If there are a lot of config files, it is much
	# faster to to concatenate them all together and process them once to
	# avoid the overhead of calling check_requirements repeatedly
	TMP_FILE="${TMPROOT}/tmp_volatile.$$"
	rm -f "$TMP_FILE"

	CFGFILES="$(ls -1 "${CFGDIR}" | grep -v "^${COREDEF}\$" | sort)"
	for file in ${CFGFILES}; do
		cat "${CFGDIR}/${file}" >> "$TMP_FILE"
	done

	if check_requirements "$TMP_FILE"
	then
		apply_cfgfile "$TMP_FILE" "yes"
	else
		# Slow path: One or more config files failed requirements.
		# Process each one individually so the offending one can be
		# skipped
		for file in ${CFGFILES}; do
			apply_cfgfile "${CFGDIR}/${file}"
		done
	fi
	rm "$TMP_FILE"

	[ -e "${ROOT_DIR}/etc/volatile.cache.build" ] && sync && mv "${ROOT_DIR}/etc/volatile.cache.build" "${ROOT_DIR}/etc/volatile.cache"
fi

if [ -z "${ROOT_DIR}" ] && [ -f /etc/ld.so.cache ] && [ ! -f /var/run/ld.so.cache ]
then
	ln -s /etc/ld.so.cache /var/run/ld.so.cache
fi
