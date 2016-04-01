#
# Records history of build output in order to detect regressions
#
# Based in part on testlab.bbclass and packagehistory.bbclass
#
# Copyright (C) 2011-2014 Intel Corporation
# Copyright (C) 2007-2011 Koen Kooi <koen@openembedded.org>
#

BUILDHISTORY_FEATURES ?= "image package sdk"
BUILDHISTORY_DIR ?= "${TOPDIR}/buildhistory"
BUILDHISTORY_DIR_IMAGE = "${BUILDHISTORY_DIR}/images/${MACHINE_ARCH}/${TCLIBC}/${IMAGE_BASENAME}"
BUILDHISTORY_DIR_PACKAGE = "${BUILDHISTORY_DIR}/packages/${MULTIMACH_TARGET_SYS}/${PN}"
BUILDHISTORY_DIR_SDK = "${BUILDHISTORY_DIR}/sdk/${SDK_NAME}/${IMAGE_BASENAME}"
BUILDHISTORY_IMAGE_FILES ?= "/etc/passwd /etc/group"
BUILDHISTORY_COMMIT ?= "0"
BUILDHISTORY_COMMIT_AUTHOR ?= "buildhistory <buildhistory@${DISTRO}>"
BUILDHISTORY_PUSH_REPO ?= ""

SSTATEPOSTINSTFUNCS_append = " buildhistory_emit_pkghistory"
# We want to avoid influence the signatures of sstate tasks - first the function itself:
sstate_install[vardepsexclude] += "buildhistory_emit_pkghistory"
# then the value added to SSTATEPOSTINSTFUNCS:
SSTATEPOSTINSTFUNCS[vardepvalueexclude] .= "| buildhistory_emit_pkghistory"

#
# Write out metadata about this package for comparision when writing future packages
#
python buildhistory_emit_pkghistory() {
    if not d.getVar('BB_CURRENTTASK', True) in ['packagedata', 'packagedata_setscene']:
        return 0

    if not "package" in (d.getVar('BUILDHISTORY_FEATURES', True) or "").split():
        return 0

    import re
    import json
    import errno

    pkghistdir = d.getVar('BUILDHISTORY_DIR_PACKAGE', True)

    class RecipeInfo:
        def __init__(self, name):
            self.name = name
            self.pe = "0"
            self.pv = "0"
            self.pr = "r0"
            self.depends = ""
            self.packages = ""
            self.srcrev = ""


    class PackageInfo:
        def __init__(self, name):
            self.name = name
            self.pe = "0"
            self.pv = "0"
            self.pr = "r0"
            # pkg/pkge/pkgv/pkgr should be empty because we want to be able to default them
            self.pkg = ""
            self.pkge = ""
            self.pkgv = ""
            self.pkgr = ""
            self.size = 0
            self.depends = ""
            self.rprovides = ""
            self.rdepends = ""
            self.rrecommends = ""
            self.rsuggests = ""
            self.rreplaces = ""
            self.rconflicts = ""
            self.files = ""
            self.filelist = ""
            # Variables that need to be written to their own separate file
            self.filevars = dict.fromkeys(['pkg_preinst', 'pkg_postinst', 'pkg_prerm', 'pkg_postrm'])

    # Should check PACKAGES here to see if anything removed

    def readPackageInfo(pkg, histfile):
        pkginfo = PackageInfo(pkg)
        with open(histfile, "r") as f:
            for line in f:
                lns = line.split('=')
                name = lns[0].strip()
                value = lns[1].strip(" \t\r\n").strip('"')
                if name == "PE":
                    pkginfo.pe = value
                elif name == "PV":
                    pkginfo.pv = value
                elif name == "PR":
                    pkginfo.pr = value
                elif name == "PKG":
                    pkginfo.pkg = value
                elif name == "PKGE":
                    pkginfo.pkge = value
                elif name == "PKGV":
                    pkginfo.pkgv = value
                elif name == "PKGR":
                    pkginfo.pkgr = value
                elif name == "RPROVIDES":
                    pkginfo.rprovides = value
                elif name == "RDEPENDS":
                    pkginfo.rdepends = value
                elif name == "RRECOMMENDS":
                    pkginfo.rrecommends = value
                elif name == "RSUGGESTS":
                    pkginfo.rsuggests = value
                elif name == "RREPLACES":
                    pkginfo.rreplaces = value
                elif name == "RCONFLICTS":
                    pkginfo.rconflicts = value
                elif name == "PKGSIZE":
                    pkginfo.size = long(value)
                elif name == "FILES":
                    pkginfo.files = value
                elif name == "FILELIST":
                    pkginfo.filelist = value
        # Apply defaults
        if not pkginfo.pkg:
            pkginfo.pkg = pkginfo.name
        if not pkginfo.pkge:
            pkginfo.pkge = pkginfo.pe
        if not pkginfo.pkgv:
            pkginfo.pkgv = pkginfo.pv
        if not pkginfo.pkgr:
            pkginfo.pkgr = pkginfo.pr
        return pkginfo

    def getlastpkgversion(pkg):
        try:
            histfile = os.path.join(pkghistdir, pkg, "latest")
            return readPackageInfo(pkg, histfile)
        except EnvironmentError:
            return None

    def sortpkglist(string):
        pkgiter = re.finditer(r'[a-zA-Z0-9.+-]+( \([><=]+ [^ )]+\))?', string, 0)
        pkglist = [p.group(0) for p in pkgiter]
        pkglist.sort()
        return ' '.join(pkglist)

    def sortlist(string):
        items = string.split(' ')
        items.sort()
        return ' '.join(items)

    pn = d.getVar('PN', True)
    pe = d.getVar('PE', True) or "0"
    pv = d.getVar('PV', True)
    pr = d.getVar('PR', True)

    pkgdata_dir = d.getVar('PKGDATA_DIR', True)
    packages = ""
    try:
        with open(os.path.join(pkgdata_dir, pn)) as f:
            for line in f.readlines():
                if line.startswith('PACKAGES: '):
                    packages = oe.utils.squashspaces(line.split(': ', 1)[1])
                    break
    except IOError as e:
        if e.errno == errno.ENOENT:
            # Probably a -cross recipe, just ignore
            return 0
        else:
            raise

    packagelist = packages.split()
    if not os.path.exists(pkghistdir):
        bb.utils.mkdirhier(pkghistdir)
    else:
        # Remove files for packages that no longer exist
        for item in os.listdir(pkghistdir):
            if item != "latest" and item != "latest_srcrev":
                if item not in packagelist:
                    itempath = os.path.join(pkghistdir, item)
                    if os.path.isdir(itempath):
                        for subfile in os.listdir(itempath):
                            os.unlink(os.path.join(itempath, subfile))
                        os.rmdir(itempath)
                    else:
                        os.unlink(itempath)

    rcpinfo = RecipeInfo(pn)
    rcpinfo.pe = pe
    rcpinfo.pv = pv
    rcpinfo.pr = pr
    rcpinfo.depends = sortlist(oe.utils.squashspaces(d.getVar('DEPENDS', True) or ""))
    rcpinfo.packages = packages
    write_recipehistory(rcpinfo, d)

    pkgdest = d.getVar('PKGDEST', True)
    for pkg in packagelist:
        pkgdata = {}
        with open(os.path.join(pkgdata_dir, 'runtime', pkg)) as f:
            for line in f.readlines():
                item = line.rstrip('\n').split(': ', 1)
                key = item[0]
                if key.endswith('_' + pkg):
                    key = key[:-len(pkg)-1]
                pkgdata[key] = item[1].decode('utf-8').decode('string_escape')

        pkge = pkgdata.get('PKGE', '0')
        pkgv = pkgdata['PKGV']
        pkgr = pkgdata['PKGR']
        #
        # Find out what the last version was
        # Make sure the version did not decrease
        #
        lastversion = getlastpkgversion(pkg)
        if lastversion:
            last_pkge = lastversion.pkge
            last_pkgv = lastversion.pkgv
            last_pkgr = lastversion.pkgr
            r = bb.utils.vercmp((pkge, pkgv, pkgr), (last_pkge, last_pkgv, last_pkgr))
            if r < 0:
                msg = "Package version for package %s went backwards which would break package feeds from (%s:%s-%s to %s:%s-%s)" % (pkg, last_pkge, last_pkgv, last_pkgr, pkge, pkgv, pkgr)
                package_qa_handle_error("version-going-backwards", msg, d)

        pkginfo = PackageInfo(pkg)
        # Apparently the version can be different on a per-package basis (see Python)
        pkginfo.pe = pkgdata.get('PE', '0')
        pkginfo.pv = pkgdata['PV']
        pkginfo.pr = pkgdata['PR']
        pkginfo.pkg = pkgdata['PKG']
        pkginfo.pkge = pkge
        pkginfo.pkgv = pkgv
        pkginfo.pkgr = pkgr
        pkginfo.rprovides = sortpkglist(oe.utils.squashspaces(pkgdata.get('RPROVIDES', "")))
        pkginfo.rdepends = sortpkglist(oe.utils.squashspaces(pkgdata.get('RDEPENDS', "")))
        pkginfo.rrecommends = sortpkglist(oe.utils.squashspaces(pkgdata.get('RRECOMMENDS', "")))
        pkginfo.rsuggests = sortpkglist(oe.utils.squashspaces(pkgdata.get('RSUGGESTS', "")))
        pkginfo.rreplaces = sortpkglist(oe.utils.squashspaces(pkgdata.get('RREPLACES', "")))
        pkginfo.rconflicts = sortpkglist(oe.utils.squashspaces(pkgdata.get('RCONFLICTS', "")))
        pkginfo.files = oe.utils.squashspaces(pkgdata.get('FILES', ""))
        for filevar in pkginfo.filevars:
            pkginfo.filevars[filevar] = pkgdata.get(filevar, "")

        # Gather information about packaged files
        val = pkgdata.get('FILES_INFO', '')
        dictval = json.loads(val)
        filelist = dictval.keys()
        filelist.sort()
        pkginfo.filelist = " ".join(filelist)

        pkginfo.size = int(pkgdata['PKGSIZE'])

        write_pkghistory(pkginfo, d)

    # Create files-in-<package-name>.txt files containing a list of files of each recipe's package
    bb.build.exec_func("buildhistory_list_pkg_files", d)
}


def write_recipehistory(rcpinfo, d):
    import codecs

    bb.debug(2, "Writing recipe history")

    pkghistdir = d.getVar('BUILDHISTORY_DIR_PACKAGE', True)

    infofile = os.path.join(pkghistdir, "latest")
    with codecs.open(infofile, "w", encoding='utf8') as f:
        if rcpinfo.pe != "0":
            f.write(u"PE = %s\n" %  rcpinfo.pe)
        f.write(u"PV = %s\n" %  rcpinfo.pv)
        f.write(u"PR = %s\n" %  rcpinfo.pr)
        f.write(u"DEPENDS = %s\n" %  rcpinfo.depends)
        f.write(u"PACKAGES = %s\n" %  rcpinfo.packages)


def write_pkghistory(pkginfo, d):
    import codecs

    bb.debug(2, "Writing package history for package %s" % pkginfo.name)

    pkghistdir = d.getVar('BUILDHISTORY_DIR_PACKAGE', True)

    pkgpath = os.path.join(pkghistdir, pkginfo.name)
    if not os.path.exists(pkgpath):
        bb.utils.mkdirhier(pkgpath)

    infofile = os.path.join(pkgpath, "latest")
    with codecs.open(infofile, "w", encoding='utf8') as f:
        if pkginfo.pe != "0":
            f.write(u"PE = %s\n" %  pkginfo.pe)
        f.write(u"PV = %s\n" %  pkginfo.pv)
        f.write(u"PR = %s\n" %  pkginfo.pr)

        pkgvars = {}
        pkgvars['PKG'] = pkginfo.pkg if pkginfo.pkg != pkginfo.name else ''
        pkgvars['PKGE'] = pkginfo.pkge if pkginfo.pkge != pkginfo.pe else ''
        pkgvars['PKGV'] = pkginfo.pkgv if pkginfo.pkgv != pkginfo.pv else ''
        pkgvars['PKGR'] = pkginfo.pkgr if pkginfo.pkgr != pkginfo.pr else ''
        for pkgvar in pkgvars:
            val = pkgvars[pkgvar]
            if val:
                f.write(u"%s = %s\n" % (pkgvar, val))

        f.write(u"RPROVIDES = %s\n" %  pkginfo.rprovides)
        f.write(u"RDEPENDS = %s\n" %  pkginfo.rdepends)
        f.write(u"RRECOMMENDS = %s\n" %  pkginfo.rrecommends)
        if pkginfo.rsuggests:
            f.write(u"RSUGGESTS = %s\n" %  pkginfo.rsuggests)
        if pkginfo.rreplaces:
            f.write(u"RREPLACES = %s\n" %  pkginfo.rreplaces)
        if pkginfo.rconflicts:
            f.write(u"RCONFLICTS = %s\n" %  pkginfo.rconflicts)
        f.write(u"PKGSIZE = %d\n" %  pkginfo.size)
        f.write(u"FILES = %s\n" %  pkginfo.files)
        f.write(u"FILELIST = %s\n" %  pkginfo.filelist)

    for filevar in pkginfo.filevars:
        filevarpath = os.path.join(pkgpath, "latest.%s" % filevar)
        val = pkginfo.filevars[filevar]
        if val:
            with codecs.open(filevarpath, "w", encoding='utf8') as f:
                f.write(val)
        else:
            if os.path.exists(filevarpath):
                os.unlink(filevarpath)

#
# rootfs_type can be: image, sdk_target, sdk_host
#
def buildhistory_list_installed(d, rootfs_type="image"):
    from oe.rootfs import image_list_installed_packages
    from oe.sdk import sdk_list_installed_packages

    process_list = [('file', 'bh_installed_pkgs.txt'),\
                    ('deps', 'bh_installed_pkgs_deps.txt')]

    for output_type, output_file in process_list:
        output_file_full = os.path.join(d.getVar('WORKDIR', True), output_file)

        with open(output_file_full, 'w') as output:
            if rootfs_type == "image":
                output.write(image_list_installed_packages(d, output_type))
            else:
                output.write(sdk_list_installed_packages(d, rootfs_type == "sdk_target", output_type))

python buildhistory_list_installed_image() {
    buildhistory_list_installed(d)
}

python buildhistory_list_installed_sdk_target() {
    buildhistory_list_installed(d, "sdk_target")
}

python buildhistory_list_installed_sdk_host() {
    buildhistory_list_installed(d, "sdk_host")
}

buildhistory_get_installed() {
	mkdir -p $1

	# Get list of installed packages
	pkgcache="$1/installed-packages.tmp"
	cat ${WORKDIR}/bh_installed_pkgs.txt | sort > $pkgcache && rm ${WORKDIR}/bh_installed_pkgs.txt

	cat $pkgcache | awk '{ print $1 }' > $1/installed-package-names.txt
	if [ -s $pkgcache ] ; then
		cat $pkgcache | awk '{ print $2 }' | xargs -n1 basename > $1/installed-packages.txt
	else
		printf "" > $1/installed-packages.txt
	fi

	# Produce dependency graph
	# First, quote each name to handle characters that cause issues for dot
	sed 's:\([^| ]*\):"\1":g' ${WORKDIR}/bh_installed_pkgs_deps.txt > $1/depends.tmp && \
		rm ${WORKDIR}/bh_installed_pkgs_deps.txt
	# Change delimiter from pipe to -> and set style for recommend lines
	sed -i -e 's:|: -> :' -e 's:"\[REC\]":[style=dotted]:' -e 's:$:;:' $1/depends.tmp
	# Add header, sorted and de-duped contents and footer and then delete the temp file
	printf "digraph depends {\n    node [shape=plaintext]\n" > $1/depends.dot
	cat $1/depends.tmp | sort | uniq >> $1/depends.dot
	echo "}" >>  $1/depends.dot
	rm $1/depends.tmp

	# Produce installed package sizes list
	printf "" > $1/installed-package-sizes.tmp
	cat $pkgcache | while read pkg pkgfile pkgarch
	do
		size=`oe-pkgdata-util -p ${PKGDATA_DIR} read-value "PKGSIZE" ${pkg}_${pkgarch}`
		if [ "$size" != "" ] ; then
			echo "$size $pkg" >> $1/installed-package-sizes.tmp
		fi
	done
	cat $1/installed-package-sizes.tmp | sort -n -r | awk '{print $1 "\tKiB " $2}' > $1/installed-package-sizes.txt
	rm $1/installed-package-sizes.tmp

	# We're now done with the cache, delete it
	rm $pkgcache

	if [ "$2" != "sdk" ] ; then
		# Produce some cut-down graphs (for readability)
		grep -v kernel_image $1/depends.dot | grep -v kernel-2 | grep -v kernel-3 > $1/depends-nokernel.dot
		grep -v libc6 $1/depends-nokernel.dot | grep -v libgcc > $1/depends-nokernel-nolibc.dot
		grep -v update- $1/depends-nokernel-nolibc.dot > $1/depends-nokernel-nolibc-noupdate.dot
		grep -v kernel-module $1/depends-nokernel-nolibc-noupdate.dot > $1/depends-nokernel-nolibc-noupdate-nomodules.dot
	fi

	# add complementary package information
	if [ -e ${WORKDIR}/complementary_pkgs.txt ]; then
		cp ${WORKDIR}/complementary_pkgs.txt $1
	fi
}

buildhistory_get_image_installed() {
	# Anything requiring the use of the packaging system should be done in here
	# in case the packaging files are going to be removed for this image

	if [ "${@bb.utils.contains('BUILDHISTORY_FEATURES', 'image', '1', '0', d)}" = "0" ] ; then
		return
	fi

	buildhistory_get_installed ${BUILDHISTORY_DIR_IMAGE}
}

buildhistory_get_sdk_installed() {
	# Anything requiring the use of the packaging system should be done in here
	# in case the packaging files are going to be removed for this SDK

	if [ "${@bb.utils.contains('BUILDHISTORY_FEATURES', 'sdk', '1', '0', d)}" = "0" ] ; then
		return
	fi

	buildhistory_get_installed ${BUILDHISTORY_DIR_SDK}/$1 sdk
}

buildhistory_get_sdk_installed_host() {
	buildhistory_get_sdk_installed host
}

buildhistory_get_sdk_installed_target() {
	buildhistory_get_sdk_installed target
}

buildhistory_list_files() {
	# List the files in the specified directory, but exclude date/time etc.
	# This awk script is somewhat messy, but handles where the size is not printed for device files under pseudo
	if [ "$3" = "fakeroot" ] ; then
		( cd $1 && ${FAKEROOTENV} ${FAKEROOTCMD} find . ! -path . -printf "%M %-10u %-10g %10s %p -> %l\n" | sort -k5 | sed 's/ * -> $//' > $2 )
	else
		( cd $1 && find . ! -path . -printf "%M %-10u %-10g %10s %p -> %l\n" | sort -k5 | sed 's/ * -> $//' > $2 )
	fi
}

buildhistory_list_pkg_files() {
	# Create individual files-in-package for each recipe's package
	for pkgdir in $(find ${PKGDEST}/* -maxdepth 0 -type d); do
		pkgname=$(basename $pkgdir)
		outfolder="${BUILDHISTORY_DIR_PACKAGE}/$pkgname"
		outfile="$outfolder/files-in-package.txt"
		# Make sure the output folder exists so we can create the file
		if [ ! -d $outfolder ] ; then
			bbdebug 2 "Folder $outfolder does not exist, file $outfile not created"
			continue
		fi
		buildhistory_list_files $pkgdir $outfile fakeroot
	done
}

buildhistory_get_imageinfo() {
	if [ "${@bb.utils.contains('BUILDHISTORY_FEATURES', 'image', '1', '0', d)}" = "0" ] ; then
		return
	fi

	buildhistory_list_files ${IMAGE_ROOTFS} ${BUILDHISTORY_DIR_IMAGE}/files-in-image.txt

	# Collect files requested in BUILDHISTORY_IMAGE_FILES
	rm -rf ${BUILDHISTORY_DIR_IMAGE}/image-files
	for f in ${BUILDHISTORY_IMAGE_FILES}; do
		if [ -f ${IMAGE_ROOTFS}/$f ] ; then
			mkdir -p ${BUILDHISTORY_DIR_IMAGE}/image-files/`dirname $f`
			cp ${IMAGE_ROOTFS}/$f ${BUILDHISTORY_DIR_IMAGE}/image-files/$f
		fi
	done

	# Record some machine-readable meta-information about the image
	printf ""  > ${BUILDHISTORY_DIR_IMAGE}/image-info.txt
	cat >> ${BUILDHISTORY_DIR_IMAGE}/image-info.txt <<END
${@buildhistory_get_imagevars(d)}
END
	imagesize=`du -ks ${IMAGE_ROOTFS} | awk '{ print $1 }'`
	echo "IMAGESIZE = $imagesize" >> ${BUILDHISTORY_DIR_IMAGE}/image-info.txt

	# Add some configuration information
	echo "${MACHINE}: ${IMAGE_BASENAME} configured for ${DISTRO} ${DISTRO_VERSION}" > ${BUILDHISTORY_DIR_IMAGE}/build-id.txt

	cat >> ${BUILDHISTORY_DIR_IMAGE}/build-id.txt <<END
${@buildhistory_get_build_id(d)}
END
}

buildhistory_get_sdkinfo() {
	if [ "${@bb.utils.contains('BUILDHISTORY_FEATURES', 'sdk', '1', '0', d)}" = "0" ] ; then
		return
	fi

	buildhistory_list_files ${SDK_OUTPUT} ${BUILDHISTORY_DIR_SDK}/files-in-sdk.txt

	# Record some machine-readable meta-information about the SDK
	printf ""  > ${BUILDHISTORY_DIR_SDK}/sdk-info.txt
	cat >> ${BUILDHISTORY_DIR_SDK}/sdk-info.txt <<END
${@buildhistory_get_sdkvars(d)}
END
	sdksize=`du -ks ${SDK_OUTPUT} | awk '{ print $1 }'`
	echo "SDKSIZE = $sdksize" >> ${BUILDHISTORY_DIR_SDK}/sdk-info.txt
}

# By using ROOTFS_POSTUNINSTALL_COMMAND we get in after uninstallation of
# unneeded packages but before the removal of packaging files
ROOTFS_POSTUNINSTALL_COMMAND += " buildhistory_list_installed_image ;\
                                buildhistory_get_image_installed ; "

IMAGE_POSTPROCESS_COMMAND += " buildhistory_get_imageinfo ; "

# We want these to be the last run so that we get called after complementary package installation
POPULATE_SDK_POST_TARGET_COMMAND_append = " buildhistory_list_installed_sdk_target ;\
                                            buildhistory_get_sdk_installed_target ; "
POPULATE_SDK_POST_HOST_COMMAND_append = " buildhistory_list_installed_sdk_host ;\
                                          buildhistory_get_sdk_installed_host ; "

SDK_POSTPROCESS_COMMAND_append = " buildhistory_get_sdkinfo ; "

def buildhistory_get_build_id(d):
    if d.getVar('BB_WORKERCONTEXT', True) != '1':
        return ""
    localdata = bb.data.createCopy(d)
    bb.data.update_data(localdata)
    statuslines = []
    for func in oe.data.typed_value('BUILDCFG_FUNCS', localdata):
        g = globals()
        if func not in g:
            bb.warn("Build configuration function '%s' does not exist" % func)
        else:
            flines = g[func](localdata)
            if flines:
                statuslines.extend(flines)

    statusheader = d.getVar('BUILDCFG_HEADER', True)
    return('\n%s\n%s\n' % (statusheader, '\n'.join(statuslines)))

def buildhistory_get_metadata_revs(d):
    # We want an easily machine-readable format here, so get_layers_branch_rev isn't quite what we want
    layers = (d.getVar("BBLAYERS", True) or "").split()
    medadata_revs = ["%-17s = %s:%s" % (os.path.basename(i), \
        base_get_metadata_git_branch(i, None).strip(), \
        base_get_metadata_git_revision(i, None)) \
            for i in layers]
    return '\n'.join(medadata_revs)

def outputvars(vars, listvars, d):
    vars = vars.split()
    listvars = listvars.split()
    ret = ""
    for var in vars:
        value = d.getVar(var, True) or ""
        if var in listvars:
            # Squash out spaces
            value = oe.utils.squashspaces(value)
        ret += "%s = %s\n" % (var, value)
    return ret.rstrip('\n')

def buildhistory_get_imagevars(d):
    if d.getVar('BB_WORKERCONTEXT', True) != '1':
        return ""
    imagevars = "DISTRO DISTRO_VERSION USER_CLASSES IMAGE_CLASSES IMAGE_FEATURES IMAGE_LINGUAS IMAGE_INSTALL BAD_RECOMMENDATIONS NO_RECOMMENDATIONS PACKAGE_EXCLUDE ROOTFS_POSTPROCESS_COMMAND IMAGE_POSTPROCESS_COMMAND"
    listvars = "USER_CLASSES IMAGE_CLASSES IMAGE_FEATURES IMAGE_LINGUAS IMAGE_INSTALL BAD_RECOMMENDATIONS PACKAGE_EXCLUDE"
    return outputvars(imagevars, listvars, d)

def buildhistory_get_sdkvars(d):
    if d.getVar('BB_WORKERCONTEXT', True) != '1':
        return ""
    sdkvars = "DISTRO DISTRO_VERSION SDK_NAME SDK_VERSION SDKMACHINE SDKIMAGE_FEATURES BAD_RECOMMENDATIONS NO_RECOMMENDATIONS PACKAGE_EXCLUDE"
    listvars = "SDKIMAGE_FEATURES BAD_RECOMMENDATIONS PACKAGE_EXCLUDE"
    return outputvars(sdkvars, listvars, d)


def buildhistory_get_cmdline(d):
    if sys.argv[0].endswith('bin/bitbake'):
        bincmd = 'bitbake'
    else:
        bincmd = sys.argv[0]
    return '%s %s' % (bincmd, ' '.join(sys.argv[1:]))


buildhistory_single_commit() {
	if [ "$3" = "" ] ; then
		commitopts="${BUILDHISTORY_DIR}/ --allow-empty"
		item="No changes"
	else
		commitopts="$3 metadata-revs"
		item="$3"
	fi
	if [ "${BUILDHISTORY_BUILD_FAILURES}" = "0" ] ; then
		result="succeeded"
	else
		result="failed"
	fi
	case ${BUILDHISTORY_BUILD_INTERRUPTED} in
		1)
			result="$result (interrupted)"
			;;
		2)
			result="$result (force interrupted)"
			;;
	esac
	commitmsgfile=`mktemp`
	cat > $commitmsgfile << END
$item: Build ${BUILDNAME} of ${DISTRO} ${DISTRO_VERSION} for machine ${MACHINE} on $2

cmd: $1

result: $result

metadata revisions:
END
	cat ${BUILDHISTORY_DIR}/metadata-revs >> $commitmsgfile
	git commit $commitopts -F $commitmsgfile --author "${BUILDHISTORY_COMMIT_AUTHOR}" > /dev/null
	rm $commitmsgfile
}

buildhistory_commit() {
	if [ ! -d ${BUILDHISTORY_DIR} ] ; then
		# Code above that creates this dir never executed, so there can't be anything to commit
		return
	fi

	# Create a machine-readable list of metadata revisions for each layer
	cat > ${BUILDHISTORY_DIR}/metadata-revs <<END
${@buildhistory_get_metadata_revs(d)}
END

	( cd ${BUILDHISTORY_DIR}/
		# Initialise the repo if necessary
		if [ ! -d .git ] ; then
			git init -q
		else
			git tag -f build-minus-3 build-minus-2 > /dev/null 2>&1 || true
			git tag -f build-minus-2 build-minus-1 > /dev/null 2>&1 || true
			git tag -f build-minus-1 > /dev/null 2>&1 || true
		fi
		# If the user hasn't set up their name/email, set some defaults
		# just for this repo (otherwise the commit will fail with older
		# versions of git)
		if ! git config user.email > /dev/null ; then
			git config --local user.email "buildhistory@${DISTRO}"
		fi
		if ! git config user.name > /dev/null ; then
			git config --local user.name "buildhistory"
		fi
		# Check if there are new/changed files to commit (other than metadata-revs)
		repostatus=`git status --porcelain | grep -v " metadata-revs$"`
		HOSTNAME=`hostname 2>/dev/null || echo unknown`
		CMDLINE="${@buildhistory_get_cmdline(d)}"
		if [ "$repostatus" != "" ] ; then
			git add -A .
			# porcelain output looks like "?? packages/foo/bar"
			# Ensure we commit metadata-revs with the first commit
			for entry in `echo "$repostatus" | awk '{print $2}' | awk -F/ '{print $1}' | sort | uniq` ; do
				buildhistory_single_commit "$CMDLINE" "$HOSTNAME" "$entry"
			done
			git gc --auto --quiet
		else
			buildhistory_single_commit "$CMDLINE" "$HOSTNAME"
		fi
		if [ "${BUILDHISTORY_PUSH_REPO}" != "" ] ; then
			git push -q ${BUILDHISTORY_PUSH_REPO}
		fi) || true
}

python buildhistory_eventhandler() {
    if e.data.getVar('BUILDHISTORY_FEATURES', True).strip():
        if e.data.getVar("BUILDHISTORY_COMMIT", True) == "1":
            bb.note("Writing buildhistory")
            localdata = bb.data.createCopy(e.data)
            localdata.setVar('BUILDHISTORY_BUILD_FAILURES', str(e._failures))
            interrupted = getattr(e, '_interrupted', 0)
            localdata.setVar('BUILDHISTORY_BUILD_INTERRUPTED', str(interrupted))
            bb.build.exec_func("buildhistory_commit", localdata)
}

addhandler buildhistory_eventhandler
buildhistory_eventhandler[eventmask] = "bb.event.BuildCompleted"


# FIXME this ought to be moved into the fetcher
def _get_srcrev_values(d):
    """
    Return the version strings for the current recipe
    """

    scms = []
    fetcher = bb.fetch.Fetch(d.getVar('SRC_URI', True).split(), d)
    urldata = fetcher.ud
    for u in urldata:
        if urldata[u].method.supports_srcrev():
            scms.append(u)

    autoinc_templ = 'AUTOINC+'
    dict_srcrevs = {}
    dict_tag_srcrevs = {}
    for scm in scms:
        ud = urldata[scm]
        for name in ud.names:
            try:
                rev = ud.method.sortable_revision(ud, d, name)
            except TypeError:
                # support old bitbake versions
                rev = ud.method.sortable_revision(scm, ud, d, name)
            # Clean this up when we next bump bitbake version
            if type(rev) != str:
                autoinc, rev = rev
            elif rev.startswith(autoinc_templ):
                rev = rev[len(autoinc_templ):]
            dict_srcrevs[name] = rev
            if 'tag' in ud.parm:
                tag = ud.parm['tag'];
                key = name+'_'+tag
                dict_tag_srcrevs[key] = rev
    return (dict_srcrevs, dict_tag_srcrevs)

do_fetch[postfuncs] += "write_srcrev"
do_fetch[vardepsexclude] += "write_srcrev"
python write_srcrev() {
    pkghistdir = d.getVar('BUILDHISTORY_DIR_PACKAGE', True)
    srcrevfile = os.path.join(pkghistdir, 'latest_srcrev')

    srcrevs, tag_srcrevs = _get_srcrev_values(d)
    if srcrevs:
        if not os.path.exists(pkghistdir):
            bb.utils.mkdirhier(pkghistdir)
        old_tag_srcrevs = {}
        if os.path.exists(srcrevfile):
            with open(srcrevfile) as f:
                for line in f:
                    if line.startswith('# tag_'):
                        key, value = line.split("=", 1)
                        key = key.replace('# tag_', '').strip()
                        value = value.replace('"', '').strip()
                        old_tag_srcrevs[key] = value
        with open(srcrevfile, 'w') as f:
            orig_srcrev = d.getVar('SRCREV', False) or 'INVALID'
            if orig_srcrev != 'INVALID':
                f.write('# SRCREV = "%s"\n' % orig_srcrev)
            if len(srcrevs) > 1:
                for name, srcrev in srcrevs.items():
                    orig_srcrev = d.getVar('SRCREV_%s' % name, False)
                    if orig_srcrev:
                        f.write('# SRCREV_%s = "%s"\n' % (name, orig_srcrev))
                    f.write('SRCREV_%s = "%s"\n' % (name, srcrev))
            else:
                f.write('SRCREV = "%s"\n' % srcrevs.itervalues().next())
            if len(tag_srcrevs) > 0:
                for name, srcrev in tag_srcrevs.items():
                    f.write('# tag_%s = "%s"\n' % (name, srcrev))
                    if name in old_tag_srcrevs and old_tag_srcrevs[name] != srcrev:
                        pkg = d.getVar('PN', True)
                        bb.warn("Revision for tag %s in package %s was changed since last build (from %s to %s)" % (name, pkg, old_tag_srcrevs[name], srcrev))

    else:
        if os.path.exists(srcrevfile):
            os.remove(srcrevfile)
}
