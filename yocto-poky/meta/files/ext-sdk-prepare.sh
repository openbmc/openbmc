#!/bin/sh

# Prepare the build system within the extensible SDK

target_sdk_dir="$1"
sdk_targets="$2"

# Avoid actually building images during this phase, but still
# ensure all dependencies are extracted from sstate
# This is a hack, to be sure, but we really don't need to do this here
for sdktarget in $sdk_targets ; do
	bbappend=`recipetool newappend $target_sdk_dir/workspace $sdktarget`
	printf 'python do_rootfs_forcevariable () {\n    bb.utils.mkdirhier(d.getVar("IMAGE_ROOTFS", True))\n}\n' > $bbappend
	printf 'python do_bootimg () {\n    pass\n}\n' >> $bbappend
	printf 'python do_bootdirectdisk () {\n    pass\n}\n' >> $bbappend
	printf 'python do_vmimg () {\n    pass\n}\n' >> $bbappend
	printf "Created bbappend %s\n" "$bbappend"
done
bitbake $sdk_targets || exit 1
rm -rf $target_sdk_dir/workspace/appends/*
