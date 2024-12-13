#!/bin/sh

export LC_ALL=en_US.UTF-8
#Make sure at least one python is installed
INIT_PYTHON=$(which python3 2>/dev/null )
[ -z "$INIT_PYTHON" ] && INIT_PYTHON=$(which python2 2>/dev/null)
[ -z "$INIT_PYTHON" ] && echo "Error: The SDK needs a python installed" && exit 1

# Remove invalid PATH elements first (maybe from a previously setup toolchain now deleted
PATH=`$INIT_PYTHON -c 'import os; print(":".join(e for e in os.environ["PATH"].split(":") if os.path.exists(e)))'`

tweakpath () {
    case ":${PATH}:" in
        *:"$1":*)
            ;;
        *)
            PATH=$PATH:$1
    esac
}

# Some systems don't have /usr/sbin or /sbin in the cleaned environment PATH but we make need it 
# for the system's host tooling checks
tweakpath /usr/sbin
tweakpath /sbin

INST_ARCH=$(uname -m | sed -e "s/i[3-6]86/ix86/" -e "s/x86[-_]64/x86_64/")
SDK_ARCH=$(echo @SDK_ARCH@ | sed -e "s/i[3-6]86/ix86/" -e "s/x86[-_]64/x86_64/")

INST_GCC_VER=$(gcc --version 2>/dev/null | sed -ne 's/.* \([0-9]\+\.[0-9]\+\)\.[0-9]\+.*/\1/p')
SDK_GCC_VER='@SDK_GCC_VER@'

verlte () {
	[  "$1" = "`printf "$1\n$2" | sort -V | head -n1`" ]
}

verlt() {
	[ "$1" = "$2" ] && return 1 || verlte $1 $2
}

verlt `uname -r` @OLDEST_KERNEL@
if [ $? = 0 ]; then
	echo "Error: The SDK needs a kernel > @OLDEST_KERNEL@"
	exit 1
fi

if [ "$INST_ARCH" != "$SDK_ARCH" ]; then
	# Allow for installation of ix86 SDK on x86_64 host
	if [ "$INST_ARCH" != x86_64 -o "$SDK_ARCH" != ix86 ]; then
		echo "Error: Incompatible SDK installer! Your host is $INST_ARCH and this SDK was built for $SDK_ARCH hosts."
		exit 1
	fi
fi

if ! xz -V > /dev/null 2>&1; then
	echo "Error: xz is required for installation of this SDK, please install it first"
	exit 1
fi

SDK_BUILD_PATH="@SDKPATH@"
DEFAULT_INSTALL_DIR="@SDKPATHINSTALL@"
SUDO_EXEC=""
EXTRA_TAR_OPTIONS=""
target_sdk_dir=""
answer=""
relocate=1
savescripts=0
verbose=0
publish=0
listcontents=0
while getopts ":yd:npDRSl" OPT; do
	case $OPT in
	y)
		answer="Y"
		;;
	d)
		target_sdk_dir=$OPTARG
		;;
	n)
		prepare_buildsystem="no"
		;;
	p)
		prepare_buildsystem="no"
		publish=1
		;;
	D)
		verbose=1
		;;
	R)
		relocate=0
		savescripts=1
		;;
	S)
		savescripts=1
		;;
	l)
		listcontents=1
		;;
	*)
		echo "Usage: $(basename "$0") [-y] [-d <dir>]"
		echo "  -y         Automatic yes to all prompts"
		echo "  -d <dir>   Install the SDK to <dir>"
		echo "======== Extensible SDK only options ============"
		echo "  -n         Do not prepare the build system"
		echo "  -p         Publish mode (implies -n)"
		echo "======== Advanced DEBUGGING ONLY OPTIONS ========"
		echo "  -S         Save relocation scripts"
		echo "  -R         Do not relocate executables"
		echo "  -D         use set -x to see what is going on"
		echo "  -l         list files that will be extracted"
		exit 1
		;;
	esac
done

payload_offset=$(($(grep -na -m1 "^MARKER:$" "$0"|cut -d':' -f1) + 1))
if [ "$listcontents" = "1" ] ; then
    if [ @SDK_ARCHIVE_TYPE@ = "zip" ]; then
        tail -n +$payload_offset "$0" > sdk.zip
        if unzip -l sdk.zip;then
            rm sdk.zip
        else
            rm sdk.zip && exit 1
        fi
    else
        tail -n +$payload_offset "$0"| tar tvJ || exit 1
    fi
    exit
fi

titlestr="@SDK_TITLE@ installer version @SDK_VERSION@"
printf "%s\n" "$titlestr"
printf "%${#titlestr}s\n" | tr " " "="

if [ $verbose = 1 ] ; then
	set -x
fi

@SDK_PRE_INSTALL_COMMAND@

# SDK_EXTENSIBLE is exposed from the SDK_PRE_INSTALL_COMMAND above
if [ "$SDK_EXTENSIBLE" = "1" ]; then
	DEFAULT_INSTALL_DIR="@SDKEXTPATH@"
	if [ "$INST_GCC_VER" = '4.8' -a "$SDK_GCC_VER" = '4.9' ] || [ "$INST_GCC_VER" = '4.8' -a "$SDK_GCC_VER" = '' ] || \
		[ "$INST_GCC_VER" = '4.9' -a "$SDK_GCC_VER" = '' ]; then
		echo "Error: Incompatible SDK installer! Your host gcc version is $INST_GCC_VER and this SDK was built by gcc higher version."
		exit 1
	fi
fi

if [ "$target_sdk_dir" = "" ]; then
	if [ "$answer" = "Y" ]; then
		target_sdk_dir="$DEFAULT_INSTALL_DIR"
	else
		read -p "Enter target directory for SDK (default: $DEFAULT_INSTALL_DIR): " target_sdk_dir
		[ "$target_sdk_dir" = "" ] && target_sdk_dir=$DEFAULT_INSTALL_DIR
	fi
fi

eval target_sdk_dir=$(echo "$target_sdk_dir"|sed 's/ /\\ /g')
if [ -d "$target_sdk_dir" ]; then
	target_sdk_dir=$(cd "$target_sdk_dir"; pwd)
else
	target_sdk_dir=$(readlink -m "$target_sdk_dir")
fi

# limit the length for target_sdk_dir, ensure the relocation behaviour in relocate_sdk.py has right result.
# This is due to ELF interpreter being set to 'a'*1024 in
# meta/recipes-core/meta/uninative-tarball.bb
if [ ${#target_sdk_dir} -gt 1024 ]; then
	echo "Error: The target directory path is too long!!!"
	exit 1
fi

if [ "$SDK_EXTENSIBLE" = "1" ]; then
	# We're going to be running the build system, additional restrictions apply
	if echo "$target_sdk_dir" | grep -q '[+\ @$]'; then
		echo "The target directory path ($target_sdk_dir) contains illegal" \
		     "characters such as spaces, @, \$ or +. Abort!"
		exit 1
	fi
	# The build system doesn't work well with /tmp on NFS
	fs_dev_path="$target_sdk_dir"
	while [ ! -d "$fs_dev_path" ] ; do
		fs_dev_path=`dirname $fs_dev_path`
        done
	fs_dev_type=`stat -f -c '%t' "$fs_dev_path"`
	if [ "$fsdevtype" = "6969" ] ; then
		echo "The target directory path $target_sdk_dir is on NFS, this is not possible. Abort!"
		exit 1
	fi
else
	if [ -n "$(echo $target_sdk_dir|grep ' ')" ]; then
		echo "The target directory path ($target_sdk_dir) contains spaces. Abort!"
		exit 1
	fi
fi

if [ -e "$target_sdk_dir/environment-setup-@REAL_MULTIMACH_TARGET_SYS@" ]; then
	echo "The directory \"$target_sdk_dir\" already contains a SDK for this architecture."
	printf "If you continue, existing files will be overwritten! Proceed [y/N]? "

	default_answer="n"
else
	printf "You are about to install the SDK to \"$target_sdk_dir\". Proceed [Y/n]? "

	default_answer="y"
fi

if [ "$answer" = "" ]; then
	read answer
	[ "$answer" = "" ] && answer="$default_answer"
else
	echo $answer
fi

if [ "$answer" != "Y" -a "$answer" != "y" ]; then
	echo "Installation aborted!"
	exit 1
fi

# Try to create the directory (this will not succeed if user doesn't have rights)
mkdir -p $target_sdk_dir >/dev/null 2>&1

# if don't have the right to access dir, gain by sudo 
if [ ! -x $target_sdk_dir -o ! -w $target_sdk_dir -o ! -r $target_sdk_dir ]; then 
	if [ "$SDK_EXTENSIBLE" = "1" ]; then
		echo "Unable to access \"$target_sdk_dir\", will not attempt to use" \
		     "sudo as as extensible SDK cannot be used as root."
		exit 1
	fi

	SUDO_EXEC=$(which "sudo")
	if [ -z $SUDO_EXEC ]; then
		echo "No command 'sudo' found, please install sudo first. Abort!"
		exit 1
	fi

	# test sudo could gain root right
	$SUDO_EXEC pwd >/dev/null 2>&1
	[ $? -ne 0 ] && echo "Sorry, you are not allowed to execute as root." && exit 1

	# now that we have sudo rights, create the directory
	$SUDO_EXEC mkdir -p $target_sdk_dir >/dev/null 2>&1
fi

printf "Extracting SDK..."
if [ @SDK_ARCHIVE_TYPE@ = "zip" ]; then
    tail -n +$payload_offset "$0" > sdk.zip
    if $SUDO_EXEC unzip $EXTRA_TAR_OPTIONS sdk.zip -d $target_sdk_dir;then
        rm sdk.zip
    else
        rm sdk.zip && exit 1
    fi
else
    tail -n +$payload_offset "$0"| $SUDO_EXEC tar mxJ -C $target_sdk_dir --checkpoint=.2500 $EXTRA_TAR_OPTIONS || exit 1
fi
echo "done"

printf "Setting it up..."
# fix environment paths
real_env_setup_script=""
for env_setup_script in `ls $target_sdk_dir/environment-setup-*`; do
	if grep -q 'OECORE_NATIVE_SYSROOT=' $env_setup_script; then
		# Handle custom env setup scripts that are only named
		# environment-setup-* so that they have relocation
		# applied - what we want beyond here is the main one
		# rather than the one that simply sorts last
		real_env_setup_script="$env_setup_script"
	fi
	$SUDO_EXEC sed -e "s:@SDKPATH@:$target_sdk_dir:g" -i $env_setup_script
done
if [ -n "$real_env_setup_script" ] ; then
	env_setup_script="$real_env_setup_script"
fi

@SDK_POST_INSTALL_COMMAND@

# delete the relocating script, so that user is forced to re-run the installer
# if he/she wants another location for the sdk
if [ $savescripts = 0 ] ; then
	$SUDO_EXEC rm -f ${env_setup_script%/*}/relocate_sdk.py ${env_setup_script%/*}/relocate_sdk.sh
fi

# Execute post-relocation script
post_relocate="$target_sdk_dir/post-relocate-setup.sh"
if [ -e "$post_relocate" ]; then
	$SUDO_EXEC sed -e "s:@SDKPATH@:$target_sdk_dir:g" -i $post_relocate
	$SUDO_EXEC /bin/sh $post_relocate "$target_sdk_dir" "@SDKPATH@"
	if [ $? -ne 0 ]; then
		echo "Executing $post_relocate failed"
		exit 1
	fi
	$SUDO_EXEC rm -f $post_relocate
fi

echo "SDK has been successfully set up and is ready to be used."
echo "Each time you wish to use the SDK in a new shell session, you need to source the environment setup script e.g."
for env_setup_script in `ls $target_sdk_dir/environment-setup-*`; do
	echo " \$ . $env_setup_script"
done

exit 0

MARKER:
