inherit useradd_base

# base-passwd-cross provides the default passwd and group files in the
# target sysroot, and shadow -native and -sysroot provide the utilities
# and support files needed to add and modify user and group accounts
DEPENDS_append = "${USERADDDEPENDS}"
USERADDDEPENDS = " base-files shadow-native shadow-sysroot shadow"
USERADDDEPENDS_class-cross = ""
USERADDDEPENDS_class-native = ""
USERADDDEPENDS_class-nativesdk = ""

# This preinstall function can be run in four different contexts:
#
# a) Before do_install
# b) At do_populate_sysroot_setscene when installing from sstate packages
# c) As the preinst script in the target package at do_rootfs time
# d) As the preinst script in the target package on device as a package upgrade
#
useradd_preinst () {
OPT=""
SYSROOT=""

if test "x$D" != "x"; then
	# Installing into a sysroot
	SYSROOT="$D"
	OPT="--root $D"

	# Make sure login.defs is there, this is to make debian package backend work
	# correctly while doing rootfs.
	# The problem here is that if /etc/login.defs is treated as a config file for
	# shadow package, then while performing preinsts for packages that depend on
	# shadow, there might only be /etc/login.def.dpkg-new there in root filesystem.
	if [ ! -e $D${sysconfdir}/login.defs -a -e $D${sysconfdir}/login.defs.dpkg-new ]; then
	    cp $D${sysconfdir}/login.defs.dpkg-new $D${sysconfdir}/login.defs
	fi

	# user/group lookups should match useradd/groupadd --root
	export PSEUDO_PASSWD="$SYSROOT:${STAGING_DIR_NATIVE}"
fi

# If we're not doing a special SSTATE/SYSROOT install
# then set the values, otherwise use the environment
if test "x$UA_SYSROOT" = "x"; then
	# Installing onto a target
	# Add groups and users defined only for this package
	GROUPADD_PARAM="${GROUPADD_PARAM}"
	USERADD_PARAM="${USERADD_PARAM}"
	GROUPMEMS_PARAM="${GROUPMEMS_PARAM}"
fi

# Perform group additions first, since user additions may depend
# on these groups existing
if test "x$GROUPADD_PARAM" != "x"; then
	echo "Running groupadd commands..."
	# Invoke multiple instances of groupadd for parameter lists
	# separated by ';'
	opts=`echo "$GROUPADD_PARAM" | cut -d ';' -f 1`
	remaining=`echo "$GROUPADD_PARAM" | cut -d ';' -f 2-`
	while test "x$opts" != "x"; do
		perform_groupadd "$SYSROOT" "$OPT $opts" 10
		if test "x$opts" = "x$remaining"; then
			break
		fi
		opts=`echo "$remaining" | cut -d ';' -f 1`
		remaining=`echo "$remaining" | cut -d ';' -f 2-`
	done
fi 

if test "x$USERADD_PARAM" != "x"; then
	echo "Running useradd commands..."
	# Invoke multiple instances of useradd for parameter lists
	# separated by ';'
	opts=`echo "$USERADD_PARAM" | cut -d ';' -f 1`
	remaining=`echo "$USERADD_PARAM" | cut -d ';' -f 2-`
	while test "x$opts" != "x"; do
		perform_useradd "$SYSROOT" "$OPT $opts" 10
		if test "x$opts" = "x$remaining"; then
			break
		fi
		opts=`echo "$remaining" | cut -d ';' -f 1`
		remaining=`echo "$remaining" | cut -d ';' -f 2-`
	done
fi

if test "x$GROUPMEMS_PARAM" != "x"; then
	echo "Running groupmems commands..."
	# Invoke multiple instances of groupmems for parameter lists
	# separated by ';'
	opts=`echo "$GROUPMEMS_PARAM" | cut -d ';' -f 1`
	remaining=`echo "$GROUPMEMS_PARAM" | cut -d ';' -f 2-`
	while test "x$opts" != "x"; do
		perform_groupmems "$SYSROOT" "$OPT $opts" 10
		if test "x$opts" = "x$remaining"; then
			break
		fi
		opts=`echo "$remaining" | cut -d ';' -f 1`
		remaining=`echo "$remaining" | cut -d ';' -f 2-`
	done
fi
}

useradd_sysroot () {
	# Pseudo may (do_install) or may not (do_populate_sysroot_setscene) be running 
	# at this point so we're explicit about the environment so pseudo can load if 
	# not already present.
	export PSEUDO="${FAKEROOTENV} PSEUDO_LOCALSTATEDIR=${STAGING_DIR_TARGET}${localstatedir}/pseudo ${STAGING_DIR_NATIVE}${bindir}/pseudo"

	# Explicitly set $D since it isn't set to anything
	# before do_install
	D=${STAGING_DIR_TARGET}

	# Add groups and users defined for all recipe packages
	GROUPADD_PARAM="${@get_all_cmd_params(d, 'groupadd')}"
	USERADD_PARAM="${@get_all_cmd_params(d, 'useradd')}"
	GROUPMEMS_PARAM="${@get_all_cmd_params(d, 'groupmems')}"

	# Tell the system to use the environment vars
	UA_SYSROOT=1

	useradd_preinst
}

useradd_sysroot_sstate () {
	if [ "${BB_CURRENTTASK}" = "package_setscene" -o "${BB_CURRENTTASK}" = "populate_sysroot_setscene" ]
	then
		useradd_sysroot
	fi
}

do_install[prefuncs] += "${SYSROOTFUNC}"
SYSROOTFUNC = "useradd_sysroot"
SYSROOTFUNC_class-cross = ""
SYSROOTFUNC_class-native = ""
SYSROOTFUNC_class-nativesdk = ""
SSTATEPREINSTFUNCS += "${SYSROOTPOSTFUNC}"
SYSROOTPOSTFUNC = "useradd_sysroot_sstate"
SYSROOTPOSTFUNC_class-cross = ""
SYSROOTPOSTFUNC_class-native = ""
SYSROOTPOSTFUNC_class-nativesdk = ""

USERADDSETSCENEDEPS = "${MLPREFIX}base-passwd:do_populate_sysroot_setscene pseudo-native:do_populate_sysroot_setscene shadow-native:do_populate_sysroot_setscene ${MLPREFIX}shadow-sysroot:do_populate_sysroot_setscene"
USERADDSETSCENEDEPS_class-cross = ""
USERADDSETSCENEDEPS_class-native = ""
USERADDSETSCENEDEPS_class-nativesdk = ""
do_package_setscene[depends] += "${USERADDSETSCENEDEPS}"
do_populate_sysroot_setscene[depends] += "${USERADDSETSCENEDEPS}"

# Recipe parse-time sanity checks
def update_useradd_after_parse(d):
    useradd_packages = d.getVar('USERADD_PACKAGES', True)

    if not useradd_packages:
        raise bb.build.FuncFailed("%s inherits useradd but doesn't set USERADD_PACKAGES" % d.getVar('FILE', False))

    for pkg in useradd_packages.split():
        if not d.getVar('USERADD_PARAM_%s' % pkg, True) and not d.getVar('GROUPADD_PARAM_%s' % pkg, True) and not d.getVar('GROUPMEMS_PARAM_%s' % pkg, True):
            bb.fatal("%s inherits useradd but doesn't set USERADD_PARAM, GROUPADD_PARAM or GROUPMEMS_PARAM for package %s" % (d.getVar('FILE', False), pkg))

python __anonymous() {
    if not bb.data.inherits_class('nativesdk', d) \
        and not bb.data.inherits_class('native', d):
        update_useradd_after_parse(d)
}

# Return a single [GROUP|USER]ADD_PARAM formatted string which includes the
# [group|user]add parameters for all USERADD_PACKAGES in this recipe
def get_all_cmd_params(d, cmd_type):
    import string
    
    param_type = cmd_type.upper() + "_PARAM_%s"
    params = []

    useradd_packages = d.getVar('USERADD_PACKAGES', True) or ""
    for pkg in useradd_packages.split():
        param = d.getVar(param_type % pkg, True)
        if param:
            params.append(param)

    return "; ".join(params)

# Adds the preinst script into generated packages
fakeroot python populate_packages_prepend () {
    def update_useradd_package(pkg):
        bb.debug(1, 'adding user/group calls to preinst for %s' % pkg)

        """
        useradd preinst is appended here because pkg_preinst may be
        required to execute on the target. Not doing so may cause
        useradd preinst to be invoked twice, causing unwanted warnings.
        """
        preinst = d.getVar('pkg_preinst_%s' % pkg, True) or d.getVar('pkg_preinst', True)
        if not preinst:
            preinst = '#!/bin/sh\n'
        preinst += 'bbnote () {\n\techo "NOTE: $*"\n}\n'
        preinst += 'bbwarn () {\n\techo "WARNING: $*"\n}\n'
        preinst += 'bbfatal () {\n\techo "ERROR: $*"\n\texit 1\n}\n'
        preinst += 'perform_groupadd () {\n%s}\n' % d.getVar('perform_groupadd', True)
        preinst += 'perform_useradd () {\n%s}\n' % d.getVar('perform_useradd', True)
        preinst += 'perform_groupmems () {\n%s}\n' % d.getVar('perform_groupmems', True)
        preinst += d.getVar('useradd_preinst', True)
        d.setVar('pkg_preinst_%s' % pkg, preinst)

        # RDEPENDS setup
        rdepends = d.getVar("RDEPENDS_%s" % pkg, True) or ""
        rdepends += ' ' + d.getVar('MLPREFIX', False) + 'base-passwd'
        rdepends += ' ' + d.getVar('MLPREFIX', False) + 'shadow'
        # base-files is where the default /etc/skel is packaged
        rdepends += ' ' + d.getVar('MLPREFIX', False) + 'base-files'
        d.setVar("RDEPENDS_%s" % pkg, rdepends)

    # Add the user/group preinstall scripts and RDEPENDS requirements
    # to packages specified by USERADD_PACKAGES
    if not bb.data.inherits_class('nativesdk', d) \
        and not bb.data.inherits_class('native', d):
        useradd_packages = d.getVar('USERADD_PACKAGES', True) or ""
        for pkg in useradd_packages.split():
            update_useradd_package(pkg)
}

# Use the following to extend the useradd with custom functions
USERADDEXTENSION ?= ""

inherit ${USERADDEXTENSION}
