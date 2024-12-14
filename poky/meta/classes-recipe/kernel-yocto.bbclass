#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# remove tasks that modify the source tree in case externalsrc is inherited
SRCTREECOVEREDTASKS += "do_validate_branches do_kernel_configcheck do_kernel_checkout do_fetch do_unpack do_patch"
PATCH_GIT_USER_EMAIL ?= "kernel-yocto@oe"
PATCH_GIT_USER_NAME ?= "OpenEmbedded"

# The distro or local.conf should set this, but if nobody cares...
LINUX_KERNEL_TYPE ??= "standard"

# KMETA ?= ""
KBRANCH ?= "master"
KMACHINE ?= "${MACHINE}"
SRCREV_FORMAT ?= "meta_machine"

# LEVELS:
#   0: no reporting
#   1: report options that are specified, but not in the final config
#   2: report options that are not hardware related, but set by a BSP
KCONF_AUDIT_LEVEL ?= "1"
KCONF_BSP_AUDIT_LEVEL ?= "0"
KMETA_AUDIT ?= "yes"
KMETA_AUDIT_WERROR ?= ""

# returns local (absolute) path names for all valid patches in the
# src_uri
def find_patches(d,subdir):
    patches = src_patches(d)
    patch_list=[]
    for p in patches:
        _, _, local, _, _, parm = bb.fetch.decodeurl(p)
        # if patchdir has been passed, we won't be able to apply it so skip
        # the patch for now, and special processing happens later
        patchdir = ''
        if "patchdir" in parm:
            patchdir = parm["patchdir"]
        if subdir:
            if subdir == patchdir:
                patch_list.append(local)
        else:
            # skip the patch if a patchdir was supplied, it won't be handled
            # properly
            if not patchdir:
                patch_list.append(local)

    return patch_list

# returns all the elements from the src uri that are .scc files
def find_sccs(d):
    sources=src_patches(d, True)
    sources_list=[]
    for s in sources:
        base, ext = os.path.splitext(os.path.basename(s))
        if ext and ext in [".scc", ".cfg"]:
            sources_list.append(s)
        elif base and 'defconfig' in base:
            sources_list.append(s)

    return sources_list

# check the SRC_URI for "kmeta" type'd git repositories. Return the name of
# the repository as it will be found in UNPACKDIR
def find_kernel_feature_dirs(d):
    feature_dirs=[]
    fetch = bb.fetch2.Fetch([], d)
    for url in fetch.urls:
        urldata = fetch.ud[url]
        parm = urldata.parm
        type=""
        if "type" in parm:
            type = parm["type"]
        if "destsuffix" in parm:
            destdir = parm["destsuffix"]
            if type == "kmeta":
                feature_dirs.append(destdir)
	    
    return feature_dirs

# find the master/machine source branch. In the same way that the fetcher proceses
# git repositories in the SRC_URI we take the first repo found, first branch.
def get_machine_branch(d, default):
    fetch = bb.fetch2.Fetch([], d)
    for url in fetch.urls:
        urldata = fetch.ud[url]
        parm = urldata.parm
        if "branch" in parm:
            branches = urldata.parm.get("branch").split(',')
            btype = urldata.parm.get("type")
            if btype != "kmeta":
                return branches[0]
	    
    return default

# returns a list of all directories that are on FILESEXTRAPATHS (and
# hence available to the build) that contain .scc or .cfg files
def get_dirs_with_fragments(d):
    extrapaths = []
    extrafiles = []
    extrapathsvalue = (d.getVar("FILESEXTRAPATHS") or "")
    # Remove default flag which was used for checking
    extrapathsvalue = extrapathsvalue.replace("__default:", "")
    extrapaths = extrapathsvalue.split(":")
    for path in extrapaths:
        if path + ":True" not in extrafiles:
            extrafiles.append(path + ":" + str(os.path.exists(path)))

    return " ".join(extrafiles)

do_kernel_metadata() {
	set +e

	if [ -n "$1" ]; then
		mode="$1"
	else
		mode="patch"
	fi

	cd ${S}
	export KMETA=${KMETA}

	bbnote "do_kernel_metadata: for summary/debug, set KCONF_AUDIT_LEVEL > 0"

	# if kernel tools are available in-tree, they are preferred
	# and are placed on the path before any external tools. Unless
	# the external tools flag is set, in that case we do nothing.
	if [ -f "${S}/scripts/util/configme" ]; then
		if [ -z "${EXTERNAL_KERNEL_TOOLS}" ]; then
			PATH=${S}/scripts/util:${PATH}
		fi
	fi

	# In a similar manner to the kernel itself:
	#
	#   defconfig: $(obj)/conf
	#   ifeq ($(KBUILD_DEFCONFIG),)
	#	$< --defconfig $(Kconfig)
	#   else
	#	@echo "*** Default configuration is based on '$(KBUILD_DEFCONFIG)'"
	#	$(Q)$< --defconfig=arch/$(SRCARCH)/configs/$(KBUILD_DEFCONFIG) $(Kconfig)
	#   endif
	#
	# If a defconfig is specified via the KBUILD_DEFCONFIG variable, we copy it
	# from the source tree, into a common location and normalized "defconfig" name,
	# where the rest of the process will include and incoroporate it into the build
	#
	# If the fetcher has already placed a defconfig in UNPACKDIR (from the SRC_URI),
	# we don't overwrite it, but instead warn the user that SRC_URI defconfigs take
	# precendence.
	#
	if [ -n "${KBUILD_DEFCONFIG}" ]; then
		if [ -f "${S}/arch/${ARCH}/configs/${KBUILD_DEFCONFIG}" ]; then
			if [ -f "${UNPACKDIR}/defconfig" ]; then
				# If the two defconfig's are different, warn that we overwrote the
				# one already placed in UNPACKDIR
				cmp "${UNPACKDIR}/defconfig" "${S}/arch/${ARCH}/configs/${KBUILD_DEFCONFIG}"
				if [ $? -ne 0 ]; then
					bbdebug 1 "detected SRC_URI or patched defconfig in UNPACKDIR. ${KBUILD_DEFCONFIG} copied over it"
				fi
				cp -f ${S}/arch/${ARCH}/configs/${KBUILD_DEFCONFIG} ${UNPACKDIR}/defconfig
			else
				cp -f ${S}/arch/${ARCH}/configs/${KBUILD_DEFCONFIG} ${UNPACKDIR}/defconfig
			fi
			in_tree_defconfig="${UNPACKDIR}/defconfig"
		else
			bbfatal "A KBUILD_DEFCONFIG '${KBUILD_DEFCONFIG}' was specified, but not present in the source tree (${S}/arch/${ARCH}/configs/)"
		fi
	fi

	if [ "$mode" = "patch" ]; then
		# was anyone trying to patch the kernel meta data ?, we need to do
		# this here, since the scc commands migrate the .cfg fragments to the
		# kernel source tree, where they'll be used later.
		check_git_config
		patches="${@" ".join(find_patches(d,'kernel-meta'))}"
		if [ -n "$patches" ]; then
		    (
			    cd ${UNPACKDIR}/kernel-meta

			    # take the SRC_URI patches, and create a series file
			    # this is required to support some better processing
			    # of issues with the patches
			    rm -f series
			    for p in $patches; do
				cp $p .
				echo "$(basename $p)" >> series
			    done

			    # process the series with kgit-s2q, which is what is
			    # handling the rest of the kernel. This allows us
			    # more flexibility for handling failures or advanced
			    # mergeing functinoality
			    message=$(kgit-s2q --gen -v --patches ${UNPACKDIR}/kernel-meta 2>&1)
			    if [ $? -ne 0 ]; then
				# setup to try the patch again
				kgit-s2q --prev
				bberror "Problem applying patches to: ${UNPACKDIR}/kernel-meta"
				bbfatal_log "\n($message)"
			    fi
			)
		fi
	fi

	sccs_from_src_uri="${@" ".join(find_sccs(d))}"
	patches="${@" ".join(find_patches(d,''))}"
	feat_dirs="${@" ".join(find_kernel_feature_dirs(d))}"

	# a quick check to make sure we don't have duplicate defconfigs If
	# there's a defconfig in the SRC_URI, did we also have one from the
	# KBUILD_DEFCONFIG processing above ?
	src_uri_defconfig=$(echo $sccs_from_src_uri | awk '(match($0, "defconfig") != 0) { print $0 }' RS=' ')
	# drop and defconfig's from the src_uri variable, we captured it just above here if it existed
	sccs_from_src_uri=$(echo $sccs_from_src_uri | awk '(match($0, "defconfig") == 0) { print $0 }' RS=' ')

	if [ -n "$in_tree_defconfig" ]; then
		sccs_defconfig=$in_tree_defconfig
		if [ -n "$src_uri_defconfig" ]; then
			bbwarn "[NOTE]: defconfig was supplied both via KBUILD_DEFCONFIG and SRC_URI. Dropping SRC_URI entry $src_uri_defconfig"
		fi
	else
		# if we didn't have an in-tree one, make our defconfig the one
		# from the src_uri. Note: there may not have been one from the
		# src_uri, so this can be an empty variable.
		sccs_defconfig=$src_uri_defconfig
	fi
	sccs="$sccs_from_src_uri"

	# check for feature directories/repos/branches that were part of the
	# SRC_URI. If they were supplied, we convert them into include directives
	# for the update part of the process
	for f in ${feat_dirs}; do
		if [ -d "${UNPACKDIR}/$f/kernel-meta" ]; then
			includes="$includes -I${UNPACKDIR}/$f/kernel-meta"
	        elif [ -d "${UNPACKDIR}/$f" ]; then
			includes="$includes -I${UNPACKDIR}/$f"
		fi
	done
	for s in ${sccs} ${patches}; do
		sdir=$(dirname $s)
		includes="$includes -I${sdir}"
                # if a SRC_URI passed patch or .scc has a subdir of "kernel-meta",
                # then we add it to the search path
                if [ -d "${sdir}/kernel-meta" ]; then
			includes="$includes -I${sdir}/kernel-meta"
                fi
	done

	# allow in-tree config fragments to be used in KERNEL_FEATURES
	includes="$includes -I${S}/arch/${ARCH}/configs -I${S}/kernel/configs"

	# expand kernel features into their full path equivalents
	bsp_definition=$(spp ${includes} --find -DKMACHINE=${KMACHINE} -DKTYPE=${LINUX_KERNEL_TYPE})
	if [ -z "$bsp_definition" ]; then
		if [ -z "$sccs_defconfig" ]; then
			bbfatal_log "Could not locate BSP definition for ${KMACHINE}/${LINUX_KERNEL_TYPE} and no defconfig was provided"
		fi
	else
		# if the bsp definition has "define KMETA_EXTERNAL_BSP t",
		# then we need to set a flag that will instruct the next
		# steps to use the BSP as both configuration and patches.
		grep -q KMETA_EXTERNAL_BSP $bsp_definition
		if [ $? -eq 0 ]; then
		    KMETA_EXTERNAL_BSPS="t"
		fi
	fi
	meta_dir=$(kgit --meta)

	KERNEL_FEATURES_FINAL=""
	if [ -n "${KERNEL_FEATURES}" ]; then
		for feature in ${KERNEL_FEATURES}; do
			feature_as_specified="$feature"
			feature="$(echo $feature_as_specified | cut -d: -f1)"
			feature_specifier="$(echo $feature_as_specified | cut -d: -f2)"
			feature_found=f
			for d in $includes; do
				path_to_check=$(echo $d | sed 's/^-I//')
				if [ "$feature_found" = "f" ] && [ -e "$path_to_check/$feature" ]; then
				    feature_found=t
				fi
			done
			if [ "$feature_found" = "f" ]; then
				if [ -n "${KERNEL_DANGLING_FEATURES_WARN_ONLY}" ]; then
				    bbwarn "Feature '$feature' not found, but KERNEL_DANGLING_FEATURES_WARN_ONLY is set"
				    bbwarn "This may cause runtime issues, dropping feature and allowing configuration to continue"
				else
				    bberror "Feature '$feature' not found, this will cause configuration failures."
				    bberror "Check the SRC_URI for meta-data repositories or directories that may be missing"
				    bbfatal_log "Set KERNEL_DANGLING_FEATURES_WARN_ONLY to ignore this issue"
				fi
			else
				KERNEL_FEATURES_FINAL="$KERNEL_FEATURES_FINAL $feature_as_specified"
			fi
		done
        fi

	if [ "$mode" = "config" ]; then
		# run1: pull all the configuration fragments, no matter where they come from
		elements="`echo -n ${bsp_definition} $sccs_defconfig ${sccs} ${patches} $KERNEL_FEATURES_FINAL`"
		if [ -n "${elements}" ]; then
			echo "${bsp_definition}" > ${S}/${meta_dir}/bsp_definition
			scc --force -o ${S}/${meta_dir}:cfg,merge,meta ${includes} $sccs_defconfig $bsp_definition $sccs $patches $KERNEL_FEATURES_FINAL
			if [ $? -ne 0 ]; then
				bbfatal_log "Could not generate configuration queue for ${KMACHINE}."
			fi
		fi
	fi

	# if KMETA_EXTERNAL_BSPS has been set, or it has been detected from
	# the bsp definition, then we inject the bsp_definition into the
	# patch phase below.  we'll piggy back on the sccs variable.
	if [ -n "${KMETA_EXTERNAL_BSPS}" ]; then
		sccs="${bsp_definition} ${sccs}"
	fi

	if [ "$mode" = "patch" ]; then
		# run2: only generate patches for elements that have been passed on the SRC_URI
		elements="`echo -n ${sccs} ${patches} $KERNEL_FEATURES_FINAL`"
		if [ -n "${elements}" ]; then
			scc --force -o ${S}/${meta_dir}:patch --cmds patch ${includes} ${sccs} ${patches} $KERNEL_FEATURES_FINAL
			if [ $? -ne 0 ]; then
				bbfatal_log "Could not generate configuration queue for ${KMACHINE}."
			fi
		fi
	fi

	if [ ${KCONF_AUDIT_LEVEL} -gt 0 ]; then
		bbnote "kernel meta data summary for ${KMACHINE} (${LINUX_KERNEL_TYPE}):"
		bbnote "======================================================================"
		if [ -n "${KMETA_EXTERNAL_BSPS}" ]; then
			bbnote "Non kernel-cache (external) bsp"
		fi
		bbnote "BSP entry point / definition: $bsp_definition"
		if [ -n "$in_tree_defconfig" ]; then
			bbnote "KBUILD_DEFCONFIG: ${KBUILD_DEFCONFIG}"
		fi
		bbnote "Fragments from SRC_URI: $sccs_from_src_uri"
		bbnote "KERNEL_FEATURES: $KERNEL_FEATURES_FINAL"
		bbnote "Final scc/cfg list: $sccs_defconfig $bsp_definition $sccs $KERNEL_FEATURES_FINAL"
	fi

	set -e
}

do_patch() {
	set +e
	cd ${S}

	check_git_config
	meta_dir=$(kgit --meta)
	(cd ${meta_dir}; ln -sf patch.queue series)
	if [ -f "${meta_dir}/series" ]; then
		kgit_extra_args=""
		if [ "${KERNEL_DEBUG_TIMESTAMPS}" != "1" ]; then
		    kgit_extra_args="--commit-sha author"
		fi
		kgit-s2q --gen -v $kgit_extra_args --patches .kernel-meta/
		if [ $? -ne 0 ]; then
			bberror "Could not apply patches for ${KMACHINE}."
			bbfatal_log "Patch failures can be resolved in the linux source directory ${S})"
		fi
	fi

	if [ -f "${meta_dir}/merge.queue" ]; then
		# we need to merge all these branches
		for b in $(cat ${meta_dir}/merge.queue); do
			git show-ref --verify --quiet refs/heads/${b}
			if [ $? -eq 0 ]; then
				bbnote "Merging branch ${b}"
				git merge -q --no-ff -m "Merge branch ${b}" ${b}
			else
				bbfatal "branch ${b} does not exist, cannot merge"
			fi
		done
	fi

	set -e
}

do_kernel_checkout() {
	set +e

	source_dir=`echo ${S} | sed 's%/$%%'`
	source_workdir="${UNPACKDIR}/git"
	if [ -d "${UNPACKDIR}/git/" ]; then
		# case: git repository
		# if S is WORKDIR/git, then we shouldn't be moving or deleting the tree.
		if [ "${source_dir}" != "${source_workdir}" ]; then
			if [ -d "${source_workdir}/.git" ]; then
				# regular git repository with .git
				rm -rf ${S}
				mv ${UNPACKDIR}/git ${S}
			else
				# create source for bare cloned git repository
				git clone ${WORKDIR}/git ${S}
				rm -rf ${UNPACKDIR}/git
			fi
		fi
		cd ${S}

		# convert any remote branches to local tracking ones
		for i in `git branch -a --no-color | grep remotes | grep -v HEAD`; do
			b=`echo $i | cut -d' ' -f2 | sed 's%remotes/origin/%%'`;
			git show-ref --quiet --verify -- "refs/heads/$b"
			if [ $? -ne 0 ]; then
				git branch $b $i > /dev/null
			fi
		done

		# Create a working tree copy of the kernel by checking out a branch
		machine_branch="${@ get_machine_branch(d, "${KBRANCH}" )}"

		# checkout and clobber any unimportant files
		git checkout -f ${machine_branch}
	else
		# case: we have no git repository at all. 
		# To support low bandwidth options for building the kernel, we'll just 
		# convert the tree to a git repo and let the rest of the process work unchanged
		
		# if ${S} hasn't been set to the proper subdirectory a default of "linux" is 
		# used, but we can't initialize that empty directory. So check it and throw a
		# clear error

	        cd ${S}
		if [ ! -f "Makefile" ]; then
			bberror "S is not set to the linux source directory. Check "
			bbfatal "the recipe and set S to the proper extracted subdirectory"
		fi
		rm -f .gitignore
		git init
		check_git_config
		git add .
		git commit -q -n -m "baseline commit: creating repo for ${PN}-${PV}"
		git clean -d -f
	fi

	set -e
}
do_kernel_checkout[dirs] = "${S} ${UNPACKDIR}"

addtask kernel_checkout before do_kernel_metadata after do_symlink_kernsrc
addtask kernel_metadata after do_validate_branches do_unpack before do_patch
do_kernel_metadata[depends] = "kern-tools-native:do_populate_sysroot"
do_kernel_metadata[file-checksums] = " ${@get_dirs_with_fragments(d)}"
do_validate_branches[depends] = "kern-tools-native:do_populate_sysroot"

# ${S} doesn't exist for us at unpack
do_qa_unpack() {
    return
}

do_kernel_configme[depends] += "virtual/${TARGET_PREFIX}binutils:do_populate_sysroot"
do_kernel_configme[depends] += "virtual/${TARGET_PREFIX}gcc:do_populate_sysroot"
do_kernel_configme[depends] += "bc-native:do_populate_sysroot bison-native:do_populate_sysroot"
do_kernel_configme[depends] += "kern-tools-native:do_populate_sysroot"
do_kernel_configme[dirs] += "${S} ${B}"
do_kernel_configme() {
	do_kernel_metadata config

	# translate the kconfig_mode into something that merge_config.sh
	# understands
	case ${KCONFIG_MODE} in
		*allnoconfig)
			config_flags="-n"
			;;
		*alldefconfig)
			config_flags=""
			;;
		*)
			if [ -f ${UNPACKDIR}/defconfig ]; then
				config_flags="-n"
			fi
			;;
	esac

	cd ${S}

	meta_dir=$(kgit --meta)
	configs="$(scc --configs -o ${meta_dir})"
	if [ $? -ne 0 ]; then
		bberror "${configs}"
		bbfatal_log "Could not find configuration queue (${meta_dir}/config.queue)"
	fi

	CFLAGS="${CFLAGS} ${TOOLCHAIN_OPTIONS}" HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}" HOSTCPP="${BUILD_CPP}" CC="${KERNEL_CC}" LD="${KERNEL_LD}" OBJCOPY="${KERNEL_OBJCOPY}" STRIP="${KERNEL_STRIP}" ARCH=${ARCH} merge_config.sh -O ${B} ${config_flags} ${configs} > ${meta_dir}/cfg/merge_config_build.log 2>&1
	if [ $? -ne 0 -o ! -f ${B}/.config ]; then
		bberror "Could not generate a .config for ${KMACHINE}-${LINUX_KERNEL_TYPE}"
		if [ ${KCONF_AUDIT_LEVEL} -gt 1 ]; then
			bbfatal_log "`cat ${meta_dir}/cfg/merge_config_build.log`"
		else
			bbfatal_log "Details can be found at: ${S}/${meta_dir}/cfg/merge_config_build.log"
		fi
	fi

	if [ ! -z "${LINUX_VERSION_EXTENSION}" ]; then
		echo "# Global settings from linux recipe" >> ${B}/.config
		echo "CONFIG_LOCALVERSION="\"${LINUX_VERSION_EXTENSION}\" >> ${B}/.config
	fi
}

addtask kernel_configme before do_configure after do_patch
addtask config_analysis

do_config_analysis[depends] = "virtual/kernel:do_configure"
do_config_analysis[depends] += "kern-tools-native:do_populate_sysroot"

CONFIG_AUDIT_FILE ?= "${WORKDIR}/config-audit.txt"
CONFIG_ANALYSIS_FILE ?= "${WORKDIR}/config-analysis.txt"

python do_config_analysis() {
    import re, string, sys, subprocess

    s = d.getVar('S')

    env = os.environ.copy()
    env['PATH'] = "%s:%s%s" % (d.getVar('PATH'), s, "/scripts/util/")
    env['LD'] = d.getVar('KERNEL_LD')
    env['CC'] = d.getVar('KERNEL_CC')
    env['OBJCOPY'] = d.getVar('KERNEL_OBJCOPY')
    env['STRIP'] = d.getVar('KERNEL_STRIP')
    env['ARCH'] = d.getVar('ARCH')
    env['srctree'] = s

    # read specific symbols from the kernel recipe or from local.conf
    # i.e.: CONFIG_ANALYSIS:pn-linux-yocto-dev = 'NF_CONNTRACK LOCALVERSION'
    config = d.getVar( 'CONFIG_ANALYSIS' )
    if not config:
       config = [ "" ]
    else:
       config = config.split()

    for c in config:
        for action in ["analysis","audit"]:
            if action == "analysis":
                try:
                    analysis = subprocess.check_output(['symbol_why.py', '--dotconfig',  '{}'.format( d.getVar('B') + '/.config' ), '--blame', c], cwd=s, env=env ).decode('utf-8')
                except subprocess.CalledProcessError as e:
                    bb.fatal( "config analysis failed when running '%s': %s" % (" ".join(e.cmd), e.output.decode('utf-8')))

                outfile = d.getVar( 'CONFIG_ANALYSIS_FILE' )

            if action == "audit":
                try:
                    analysis = subprocess.check_output(['symbol_why.py', '--dotconfig',  '{}'.format( d.getVar('B') + '/.config' ), '--summary', '--extended', '--sanity', c], cwd=s, env=env ).decode('utf-8')
                except subprocess.CalledProcessError as e:
                    bb.fatal( "config analysis failed when running '%s': %s" % (" ".join(e.cmd), e.output.decode('utf-8')))

                outfile = d.getVar( 'CONFIG_AUDIT_FILE' )

            if c:
                outdir = os.path.dirname( outfile )
                outname = os.path.basename( outfile )
                outfile = outdir + '/'+ c + '-' + outname

            if config and os.path.isfile(outfile):
                os.remove(outfile)

            with open(outfile, 'w+') as f:
                f.write( analysis )

            bb.warn( "Configuration {} executed, see: {} for details".format(action,outfile ))
            if c:
                bb.warn( analysis )
}

python do_kernel_configcheck() {
    import re, string, sys, subprocess

    audit_flag = d.getVar( "KMETA_AUDIT" )
    if not audit_flag:
       bb.note( "kernel config audit disabled, skipping .." )
       return

    s = d.getVar('S')

    # if KMETA isn't set globally by a recipe using this routine, use kgit to
    # locate or create the meta directory. Otherwise, kconf_check is not
    # passed a valid meta-series for processing
    kmeta = d.getVar("KMETA")
    if not kmeta or not os.path.exists('{}/{}'.format(s,kmeta)):
        kmeta = subprocess.check_output(['kgit', '--meta'], cwd=d.getVar('S')).decode('utf-8').rstrip()

    env = os.environ.copy()
    env['PATH'] = "%s:%s%s" % (d.getVar('PATH'), s, "/scripts/util/")
    env['LD'] = d.getVar('KERNEL_LD')
    env['CC'] = d.getVar('KERNEL_CC')
    env['OBJCOPY'] = d.getVar('KERNEL_OBJCOPY')
    env['STRIP'] = d.getVar('KERNEL_STRIP')
    env['ARCH'] = d.getVar('ARCH')
    env['srctree'] = s

    try:
        configs = subprocess.check_output(['scc', '--configs', '-o', s + '/.kernel-meta'], env=env).decode('utf-8')
    except subprocess.CalledProcessError as e:
        bb.fatal( "Cannot gather config fragments for audit: %s" % e.output.decode("utf-8") )

    config_check_visibility = int(d.getVar("KCONF_AUDIT_LEVEL") or 0)
    bsp_check_visibility = int(d.getVar("KCONF_BSP_AUDIT_LEVEL") or 0)
    kmeta_audit_werror = d.getVar("KMETA_AUDIT_WERROR") or ""
    warnings_detected = False

    # if config check visibility is "1", that's the lowest level of audit. So
    # we add the --classify option to the run, since classification will
    # streamline the output to only report options that could be boot issues,
    # or are otherwise required for proper operation.
    extra_params = ""
    if config_check_visibility == 1:
       extra_params = "--classify"

    # category #1: mismatches
    try:
        analysis = subprocess.check_output(['symbol_why.py', '--dotconfig',  '{}'.format( d.getVar('B') + '/.config' ), '--mismatches', extra_params], cwd=s, env=env ).decode('utf-8')
    except subprocess.CalledProcessError as e:
        bb.fatal( "config analysis failed when running '%s': %s" % (" ".join(e.cmd), e.output.decode('utf-8')))

    if analysis:
        outfile = "{}/{}/cfg/mismatch.txt".format( s, kmeta )
        if os.path.isfile(outfile):
           os.remove(outfile)
        with open(outfile, 'w+') as f:
            f.write( analysis )

        if config_check_visibility and os.stat(outfile).st_size > 0:
            with open (outfile, "r") as myfile:
                results = myfile.read()
                bb.warn( "[kernel config]: specified values did not make it into the kernel's final configuration:\n\n%s" % results)
                warnings_detected = True

    # category #2: invalid fragment elements
    extra_params = ""
    if bsp_check_visibility > 1:
        extra_params = "--strict"
    try:
        analysis = subprocess.check_output(['symbol_why.py', '--dotconfig',  '{}'.format( d.getVar('B') + '/.config' ), '--invalid', extra_params], cwd=s, env=env ).decode('utf-8')
    except subprocess.CalledProcessError as e:
        bb.fatal( "config analysis failed when running '%s': %s" % (" ".join(e.cmd), e.output.decode('utf-8')))

    if analysis:
        outfile = "{}/{}/cfg/invalid.txt".format(s,kmeta)
        if os.path.isfile(outfile):
           os.remove(outfile)
        with open(outfile, 'w+') as f:
            f.write( analysis )

        if bsp_check_visibility and os.stat(outfile).st_size > 0:
            with open (outfile, "r") as myfile:
                results = myfile.read()
                bb.warn( "[kernel config]: This BSP contains fragments with warnings:\n\n%s" % results)
                warnings_detected = True

    # category #3: redefined options (this is pretty verbose and is debug only)
    try:
        analysis = subprocess.check_output(['symbol_why.py', '--dotconfig',  '{}'.format( d.getVar('B') + '/.config' ), '--sanity'], cwd=s, env=env ).decode('utf-8')
    except subprocess.CalledProcessError as e:
        bb.fatal( "config analysis failed when running '%s': %s" % (" ".join(e.cmd), e.output.decode('utf-8')))

    if analysis:
        outfile = "{}/{}/cfg/redefinition.txt".format(s,kmeta)
        if os.path.isfile(outfile):
           os.remove(outfile)
        with open(outfile, 'w+') as f:
            f.write( analysis )

        # if the audit level is greater than two, we report if a fragment has overriden
        # a value from a base fragment. This is really only used for new kernel introduction
        if bsp_check_visibility > 2 and os.stat(outfile).st_size > 0:
            with open (outfile, "r") as myfile:
                results = myfile.read()
                bb.warn( "[kernel config]: This BSP has configuration options defined in more than one config, with differing values:\n\n%s" % results)
                warnings_detected = True

    if warnings_detected and kmeta_audit_werror:
        bb.fatal( "configuration warnings detected, werror is set, promoting to fatal" )
}

# Ensure that the branches (BSP and meta) are on the locations specified by
# their SRCREV values. If they are NOT on the right commits, the branches
# are corrected to the proper commit.
do_validate_branches() {
	set +e
	cd ${S}

	machine_branch="${@ get_machine_branch(d, "${KBRANCH}" )}"
	machine_srcrev="${SRCREV_machine}"

	# if SRCREV is AUTOREV it shows up as AUTOINC there's nothing to
	# check and we can exit early
	if [ "${machine_srcrev}" = "AUTOINC" ]; then
	    linux_yocto_dev='${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-yocto-dev", "1", "", d)}'
	    if [ -n "$linux_yocto_dev" ]; then
		git checkout -q -f ${machine_branch}
		ver=$(grep "^VERSION =" ${S}/Makefile | sed s/.*=\ *//)
		patchlevel=$(grep "^PATCHLEVEL =" ${S}/Makefile | sed s/.*=\ *//)
		sublevel=$(grep "^SUBLEVEL =" ${S}/Makefile | sed s/.*=\ *//)
		kver="$ver.$patchlevel"
		bbnote "dev kernel: performing version -> branch -> SRCREV validation"
		bbnote "dev kernel: recipe version ${LINUX_VERSION}, src version: $kver"
		echo "${LINUX_VERSION}" | grep -q $kver
		if [ $? -ne 0 ]; then
		    version="$(echo ${LINUX_VERSION} | sed 's/\+.*$//g')"
		    versioned_branch="v$version/$machine_branch"

		    machine_branch=$versioned_branch
		    force_srcrev="$(git rev-parse $machine_branch 2> /dev/null)"
		    if [ $? -ne 0 ]; then
			bbfatal "kernel version mismatch detected, and no valid branch $machine_branch detected"
		    fi

		    bbnote "dev kernel: adjusting branch to $machine_branch, srcrev to: $force_srcrev"
		fi
	    else
		bbnote "SRCREV validation is not required for AUTOREV"
	    fi
	elif [ "${machine_srcrev}" = "" ]; then
		if [ "${SRCREV}" != "AUTOINC" ] && [ "${SRCREV}" != "INVALID" ]; then
		       # SRCREV_machine_<MACHINE> was not set. This means that a custom recipe
		       # that doesn't use the SRCREV_FORMAT "machine_meta" is being built. In
		       # this case, we need to reset to the give SRCREV before heading to patching
		       bbnote "custom recipe is being built, forcing SRCREV to ${SRCREV}"
		       force_srcrev="${SRCREV}"
		fi
	else
		git cat-file -t ${machine_srcrev} > /dev/null
		if [ $? -ne 0 ]; then
			bberror "${machine_srcrev} is not a valid commit ID."
			bbfatal_log "The kernel source tree may be out of sync"
		fi
		force_srcrev=${machine_srcrev}
	fi

	git checkout -q -f ${machine_branch}
	if [ -n "${force_srcrev}" ]; then
		# see if the branch we are about to patch has been properly reset to the defined
		# SRCREV .. if not, we reset it.
		branch_head=`git rev-parse HEAD`
		if [ "${force_srcrev}" != "${branch_head}" ]; then
			current_branch=`git rev-parse --abbrev-ref HEAD`
			git branch "$current_branch-orig"
			git reset --hard ${force_srcrev}
			# We've checked out HEAD, make sure we cleanup kgit-s2q fence post check
			# so the patches are applied as expected otherwise no patching
			# would be done in some corner cases.
			kgit-s2q --clean
		fi
	fi

	set -e
}

OE_TERMINAL_EXPORTS += "KBUILD_OUTPUT"
KBUILD_OUTPUT = "${B}"

python () {
    # If diffconfig is available, ensure it runs after kernel_configme
    if 'do_diffconfig' in d:
        bb.build.addtask('do_diffconfig', None, 'do_kernel_configme', d)

    externalsrc = d.getVar('EXTERNALSRC')
    if externalsrc:
        # If we deltask do_patch, do_kernel_configme is left without
        # dependencies and runs too early
        d.setVarFlag('do_kernel_configme', 'deps', (d.getVarFlag('do_kernel_configme', 'deps', False) or []) + ['do_unpack'])
}

# extra tasks
addtask kernel_version_sanity_check after do_kernel_metadata do_kernel_checkout before do_compile
addtask validate_branches before do_patch after do_kernel_checkout
addtask kernel_configcheck after do_configure before do_compile
