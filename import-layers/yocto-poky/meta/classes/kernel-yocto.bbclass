# remove tasks that modify the source tree in case externalsrc is inherited
SRCTREECOVEREDTASKS += "do_kernel_configme do_validate_branches do_kernel_configcheck do_kernel_checkout do_fetch do_unpack do_patch"
PATCH_GIT_USER_EMAIL ?= "kernel-yocto@oe"
PATCH_GIT_USER_NAME ?= "OpenEmbedded"

# returns local (absolute) path names for all valid patches in the
# src_uri
def find_patches(d):
    patches = src_patches(d)
    patch_list=[]
    for p in patches:
        _, _, local, _, _, _ = bb.fetch.decodeurl(p)
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
        elif base and base in 'defconfig':
            sources_list.append(s)

    return sources_list

# check the SRC_URI for "kmeta" type'd git repositories. Return the name of
# the repository as it will be found in WORKDIR
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

do_kernel_metadata() {
	set +e
	cd ${S}
	export KMETA=${KMETA}

	# if kernel tools are available in-tree, they are preferred
	# and are placed on the path before any external tools. Unless
	# the external tools flag is set, in that case we do nothing.
	if [ -f "${S}/scripts/util/configme" ]; then
		if [ -z "${EXTERNAL_KERNEL_TOOLS}" ]; then
			PATH=${S}/scripts/util:${PATH}
		fi
	fi

	machine_branch="${@ get_machine_branch(d, "${KBRANCH}" )}"
	machine_srcrev="${SRCREV_machine}"
	if [ -z "${machine_srcrev}" ]; then
		# fallback to SRCREV if a non machine_meta tree is being built
		machine_srcrev="${SRCREV}"
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
	# If the fetcher has already placed a defconfig in WORKDIR (from the SRC_URI),
	# we don't overwrite it, but instead warn the user that SRC_URI defconfigs take
	# precendence.
	#
	if [ -n "${KBUILD_DEFCONFIG}" ]; then
		if [ -f "${S}/arch/${ARCH}/configs/${KBUILD_DEFCONFIG}" ]; then
			if [ -f "${WORKDIR}/defconfig" ]; then
				# If the two defconfig's are different, warn that we didn't overwrite the
				# one already placed in WORKDIR by the fetcher.
				cmp "${WORKDIR}/defconfig" "${S}/arch/${ARCH}/configs/${KBUILD_DEFCONFIG}"
				if [ $? -ne 0 ]; then
					bbwarn "defconfig detected in WORKDIR. ${KBUILD_DEFCONFIG} skipped"
				fi
			else
				cp -f ${S}/arch/${ARCH}/configs/${KBUILD_DEFCONFIG} ${WORKDIR}/defconfig
				sccs="${WORKDIR}/defconfig"
			fi
		else
			bbfatal "A KBUILD_DECONFIG '${KBUILD_DEFCONFIG}' was specified, but not present in the source tree"
		fi
	fi

	sccs="$sccs ${@" ".join(find_sccs(d))}"
	patches="${@" ".join(find_patches(d))}"
	feat_dirs="${@" ".join(find_kernel_feature_dirs(d))}"

	# check for feature directories/repos/branches that were part of the
	# SRC_URI. If they were supplied, we convert them into include directives
	# for the update part of the process
	for f in ${feat_dirs}; do
		if [ -d "${WORKDIR}/$f/meta" ]; then
			includes="$includes -I${WORKDIR}/$f/kernel-meta"
	        elif [ -d "${WORKDIR}/$f" ]; then
			includes="$includes -I${WORKDIR}/$f"
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

	# expand kernel features into their full path equivalents
	bsp_definition=$(spp ${includes} --find -DKMACHINE=${KMACHINE} -DKTYPE=${LINUX_KERNEL_TYPE})
	meta_dir=$(kgit --meta)

	# run1: pull all the configuration fragments, no matter where they come from
	elements="`echo -n ${bsp_definition} ${sccs} ${patches} ${KERNEL_FEATURES}`"
	if [ -n "${elements}" ]; then
		scc --force -o ${S}/${meta_dir}:cfg,meta ${includes} ${bsp_definition} ${sccs} ${patches} ${KERNEL_FEATURES}
	fi

	# run2: only generate patches for elements that have been passed on the SRC_URI
	elements="`echo -n ${sccs} ${patches} ${KERNEL_FEATURES}`"
	if [ -n "${elements}" ]; then
		scc --force -o ${S}/${meta_dir}:patch --cmds patch ${includes} ${sccs} ${patches} ${KERNEL_FEATURES}
	fi
}

do_patch() {
	cd ${S}

	check_git_config
	meta_dir=$(kgit --meta)
	(cd ${meta_dir}; ln -sf patch.queue series)
	if [ -f "${meta_dir}/series" ]; then
		kgit-s2q --gen -v --patches .kernel-meta/
		if [ $? -ne 0 ]; then
			bberror "Could not apply patches for ${KMACHINE}."
			bbfatal_log "Patch failures can be resolved in the linux source directory ${S})"
		fi
	fi
}

do_kernel_checkout() {
	set +e

	source_dir=`echo ${S} | sed 's%/$%%'`
	source_workdir="${WORKDIR}/git"
	if [ -d "${WORKDIR}/git/" ]; then
		# case: git repository
		# if S is WORKDIR/git, then we shouldn't be moving or deleting the tree.
		if [ "${source_dir}" != "${source_workdir}" ]; then
			if [ -d "${source_workdir}/.git" ]; then
				# regular git repository with .git
				rm -rf ${S}
				mv ${WORKDIR}/git ${S}
			else
				# create source for bare cloned git repository
				git clone ${WORKDIR}/git ${S}
				rm -rf ${WORKDIR}/git
			fi
		fi
		cd ${S}
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
		git add .
		git commit -q -m "baseline commit: creating repo for ${PN}-${PV}"
		git clean -d -f
	fi

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
}
do_kernel_checkout[dirs] = "${S}"

addtask kernel_checkout before do_kernel_metadata after do_unpack
addtask kernel_metadata after do_validate_branches do_unpack before do_patch
do_kernel_metadata[depends] = "kern-tools-native:do_populate_sysroot"

do_kernel_configme[dirs] += "${S} ${B}"
do_kernel_configme() {
	set +e

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
		if [ -f ${WORKDIR}/defconfig ]; then
			config_flags="-n"
		fi
	    ;;
	esac

	cd ${S}

	meta_dir=$(kgit --meta)
	configs="$(scc --configs -o ${meta_dir})"
	if [ -z "${configs}" ]; then
		bbfatal_log "Could not find configuration queue (${meta_dir}/config.queue)"
	fi

	CFLAGS="${CFLAGS} ${TOOLCHAIN_OPTIONS}"	ARCH=${ARCH} merge_config.sh -O ${B} ${config_flags} ${configs} > ${meta_dir}/cfg/merge_config_build.log 2>&1
	if [ $? -ne 0 ]; then
		bbfatal_log "Could not configure ${KMACHINE}-${LINUX_KERNEL_TYPE}"
	fi

	echo "# Global settings from linux recipe" >> ${B}/.config
	echo "CONFIG_LOCALVERSION="\"${LINUX_VERSION_EXTENSION}\" >> ${B}/.config
}

addtask kernel_configme before do_configure after do_patch

python do_kernel_configcheck() {
    import re, string, sys

    # if KMETA isn't set globally by a recipe using this routine, we need to
    # set the default to 'meta'. Otherwise, kconf_check is not passed a valid
    # meta-series for processing
    kmeta = d.getVar( "KMETA", True ) or "meta"
    if not os.path.exists(kmeta):
        kmeta = "." + kmeta

    pathprefix = "export PATH=%s:%s; " % (d.getVar('PATH', True), "${S}/scripts/util/")

    cmd = d.expand("scc --configs -o ${S}/.kernel-meta")
    ret, configs = oe.utils.getstatusoutput("%s%s" % (pathprefix, cmd))

    cmd = d.expand("cd ${S}; kconf_check --report -o ${S}/%s/cfg/ ${B}/.config ${S} %s" % (kmeta,configs))
    ret, result = oe.utils.getstatusoutput("%s%s" % (pathprefix, cmd))

    config_check_visibility = int(d.getVar( "KCONF_AUDIT_LEVEL", True ) or 0)
    bsp_check_visibility = int(d.getVar( "KCONF_BSP_AUDIT_LEVEL", True ) or 0)

    # if config check visibility is non-zero, report dropped configuration values
    mismatch_file = d.expand("${S}/%s/cfg/mismatch.txt" % kmeta)
    if os.path.exists(mismatch_file):
        if config_check_visibility:
            with open (mismatch_file, "r") as myfile:
                results = myfile.read()
                bb.warn( "[kernel config]: specified values did not make it into the kernel's final configuration:\n\n%s" % results)
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
		bbnote "SRCREV validation is not required for AUTOREV"
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
		fi
	fi
}

OE_TERMINAL_EXPORTS += "KBUILD_OUTPUT"
KBUILD_OUTPUT = "${B}"

python () {
    # If diffconfig is available, ensure it runs after kernel_configme
    if 'do_diffconfig' in d:
        bb.build.addtask('do_diffconfig', None, 'do_kernel_configme', d)
}
