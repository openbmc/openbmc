do_compile_append () {
	# now generate script to drive testing
	echo "#!/usr/bin/env sh" >${B}/${HOST_PREFIX}testglibc
	set >> ${B}/${HOST_PREFIX}testglibc
	# prune out the unneeded vars
	sed -i -e "/^BASH/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^USER/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^OPT/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^DIRSTACK/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^EUID/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^FUNCNAME/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^GROUPS/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^HOST/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^HOME/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^IFS/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^LC_ALL/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^LOGNAME/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^MACHTYPE/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^OSTYPE/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^PIPE/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^SHELL/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^'/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^UID/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^TERM/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^PKG_/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^POSIXLY_/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^PPID/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^PS4/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^Q/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^SHLVL/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^STAGING/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^LD_LIBRARY_PATH/d" ${B}/${HOST_PREFIX}testglibc
	sed -i -e "/^PSEUDO/d" ${B}/${HOST_PREFIX}testglibc

	# point to real sysroot not the toolchain bootstrap sysroot
	sed -i -e "s/\-tcbootstrap//g" ${B}/${HOST_PREFIX}testglibc

	# use the final cross-gcc to test since some tests need libstdc++
	sed -i -e "s/^PATH=.*\.gcc-cross-initial\:/PATH=/g" ${B}/${HOST_PREFIX}testglibc

	# append execution part script
cat >> ${B}/${HOST_PREFIX}testglibc << STOP
target="\$1"
if [ "x\$target" = "x" ]
then
	echo "Please specify the target machine and remote user in form of user@target"
	exit 1;
fi
ssh \$target ls \$PWD\  2>&1 > /dev/null
if [ "x\$?" != "x0" ]
then
	echo "Failed connecting to \$target it could be because of:"
	echo "1. You dont have passwordless ssh setup to access \$target"
	echo "2. NFS share on \$target is not mounted or if mounted then not matching the build tree layout."
	echo "   The tree should be accessible at same location on build host and target"
	echo "   You can add nfs-client to IMAGE_FEATURES to get the nfs client on target"
	echo "3. nfs server on build host is not running."
	echo "   Please make sure that you have 'no_root_squash' added in /etc/exports if you want"
	echo "   to test as root user on target (usually its recommended to create a non"
	echo "   root user."
	echo "   As a sanity check make sure that target can read/write to the glibc build tree"
	echo "   Please refer to ${S}/EGLIBC.cross-testing for further instructions on setup"
	exit 1
fi
	echo "# we test using cross compiler from real sysroot therefore override the" > ${B}/configparms
	echo "# definitions that come from ${B}/config.make" >> ${B}/configparms
	fgrep tcbootstrap ${B}/config.make > ${B}/configparms
	sed -i -e "s/\-tcbootstrap//g" ${B}/configparms

# g++ uses flag -nostdinc, so the locations of system include headers must be explicitly specified
# If the locations are not already specified in config.make, then we provide the following locations:
#    <sysroot>/usr/include/c++/<g++ version>
#    <sysroot>/usr/include/c++/<g++ version>/<machine>

cxxincludes=\`cat ${B}/config.make | gawk '\$1 == "c++-sysincludes"' | gawk -F"=" '{print \$2}' | sed "s/[ \t]\?//g"\`

if [ -z "\$cxxincludes" ]; then
  sysroot=\`cat ${B}/configparms | sed -n "/CXX/p" | sed -e "s/^.*--sysroot=//"\`
  cxx=\`cat ${B}/configparms | gawk '\$1 ~ /^CXX/' | gawk -F"=" '{print \$2}' | gawk '{print \$1}'\`
  cxxmachine=\`\$cxx -dumpmachine\`
  cxxversion=\`\$cxx -dumpversion\`
  # pass the new value of c++-sysincludes via configparms
  echo "# c++-sysincludes added:" >> ${B}/configparms
  echo "c++-sysincludes = -isystem \$sysroot/usr/include/c++/\$cxxversion -isystem \$sysroot/usr/include/c++/\$cxxversion/\$cxxmachine" >> ${B}/configparms
fi

wrapper="${S}/scripts/cross-test-ssh.sh \$target"
localedef="${STAGING_BINDIR_NATIVE}/cross-localedef --little-endian --uint32-align=4"
make tests-clean
make cross-localedef="\$localedef" cross-test-wrapper="\$wrapper" -k check
rm -rf ${B}/configparms
STOP

	chmod +x ${B}/${HOST_PREFIX}testglibc
}
