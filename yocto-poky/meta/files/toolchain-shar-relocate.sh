# fix dynamic loader paths in all ELF SDK binaries
native_sysroot=$($SUDO_EXEC cat $env_setup_script |grep 'OECORE_NATIVE_SYSROOT='|cut -d'=' -f2|tr -d '"')
dl_path=$($SUDO_EXEC find $native_sysroot/lib -name "ld-linux*")
if [ "$dl_path" = "" ] ; then
	echo "SDK could not be set up. Relocate script unable to find ld-linux.so. Abort!"
	exit 1
fi
executable_files=$($SUDO_EXEC find $native_sysroot -type f \
	\( -perm -0100 -o -perm -0010 -o -perm -0001 \) -printf "'%h/%f' ")

tdir=`mktemp -d`
if [ x$tdir = x ] ; then
   echo "SDK relocate failed, could not create a temporary directory"
   exit 1
fi
echo "#!/bin/bash" > $tdir/relocate_sdk.sh
echo exec ${env_setup_script%/*}/relocate_sdk.py $target_sdk_dir $dl_path $executable_files >> $tdir/relocate_sdk.sh
$SUDO_EXEC mv $tdir/relocate_sdk.sh ${env_setup_script%/*}/relocate_sdk.sh
$SUDO_EXEC chmod 755 ${env_setup_script%/*}/relocate_sdk.sh
rm -rf $tdir
if [ $relocate = 1 ] ; then
	$SUDO_EXEC ${env_setup_script%/*}/relocate_sdk.sh
	if [ $? -ne 0 ]; then
		echo "SDK could not be set up. Relocate script failed. Abort!"
		exit 1
	fi
fi

# replace @SDKPATH@ with the new prefix in all text files: configs/scripts/etc.
# replace the host perl with SDK perl.
for replace in "$target_sdk_dir -maxdepth 1" "$native_sysroot"; do
	$SUDO_EXEC find $replace -type f
done | xargs -n100 file | grep ":.*\(ASCII\|script\|source\).*text" | \
    awk -F':' '{printf "\"%s\"\n", $1}' | \
    grep -v "$target_sdk_dir/environment-setup-*" | \
    xargs -n100 $SUDO_EXEC sed -i \
        -e "s:$DEFAULT_INSTALL_DIR:$target_sdk_dir:g" \
        -e "s:^#! */usr/bin/perl.*:#! /usr/bin/env perl:g" \
        -e "s: /usr/bin/perl: /usr/bin/env perl:g"

# change all symlinks pointing to @SDKPATH@
for l in $($SUDO_EXEC find $native_sysroot -type l); do
	$SUDO_EXEC ln -sfn $(readlink $l|$SUDO_EXEC sed -e "s:$DEFAULT_INSTALL_DIR:$target_sdk_dir:") $l
done

echo done
