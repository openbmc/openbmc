#
# Records history of build output in order to detect regressions
#
# Based in part on testlab.bbclass and packagehistory.bbclass
#
# Copyright (C) 2011-2016 Intel Corporation
# Copyright (C) 2007-2011 Koen Kooi <koen@openembedded.org>
#

BUILDHISTORY_FEATURES ?= "image package sdk"
BUILDHISTORY_DIR ?= "${TOPDIR}/buildhistory"
BUILDHISTORY_DIR_IMAGE = "${BUILDHISTORY_DIR}/images/${MACHINE_ARCH}/${TCLIBC}/${IMAGE_BASENAME}"
BUILDHISTORY_DIR_PACKAGE = "${BUILDHISTORY_DIR}/packages/${MULTIMACH_TARGET_SYS}/${PN}"

# Setting this to non-empty will remove the old content of the buildhistory as part of
# the current bitbake invocation and replace it with information about what was built
# during the build.
#
# This is meant to be used in continuous integration (CI) systems when invoking bitbake
# for full world builds. The effect in that case is that information about packages
# that no longer get build also gets removed from the buildhistory, which is not
# the case otherwise.
#
# The advantage over manually cleaning the buildhistory outside of bitbake is that
# the "version-going-backwards" check still works. When relying on that, be careful
# about failed world builds: they will lead to incomplete information in the
# buildhistory because information about packages that could not be built will
# also get removed. A CI system should handle that by discarding the buildhistory
# of failed builds.
#
# The expected usage is via auto.conf, but passing via the command line also works
# with: BB_ENV_EXTRAWHITE=BUILDHISTORY_RESET BUILDHISTORY_RESET=1
BUILDHISTORY_RESET ?= ""

BUILDHISTORY_OLD_DIR = "${BUILDHISTORY_DIR}/${@ "old" if "${BUILDHISTORY_RESET}" else ""}"
BUILDHISTORY_OLD_DIR_PACKAGE = "${BUILDHISTORY_OLD_DIR}/packages/${MULTIMACH_TARGET_SYS}/${PN}"
BUILDHISTORY_DIR_SDK = "${BUILDHISTORY_DIR}/sdk/${SDK_NAME}${SDK_EXT}/${IMAGE_BASENAME}"
BUILDHISTORY_IMAGE_FILES ?= "/etc/passwd /etc/group"
BUILDHISTORY_SDK_FILES ?= "conf/local.conf conf/bblayers.conf conf/auto.conf conf/locked-sigs.inc conf/devtool.conf"
BUILDHISTORY_COMMIT ?= "1"
BUILDHISTORY_COMMIT_AUTHOR ?= "buildhistory <buildhistory@${DISTRO}>"
BUILDHISTORY_PUSH_REPO ?= ""

SSTATEPOSTINSTFUNCS_append = " buildhistory_emit_pkghistory"
# We want to avoid influencing the signatures of sstate tasks - first the function itself:
sstate_install[vardepsexclude] += "buildhistory_emit_pkghistory"
# then the value added to SSTATEPOSTINSTFUNCS:
SSTATEPOSTINSTFUNCS[vardepvalueexclude] .= "| buildhistory_emit_pkghistory"

# Similarly for our function that gets the output signatures
SSTATEPOSTUNPACKFUNCS_append = " buildhistory_emit_outputsigs"
sstate_installpkgdir[vardepsexclude] += "buildhistory_emit_outputsigs"
SSTATEPOSTUNPACKFUNCS[vardepvalueexclude] .= "| buildhistory_emit_outputsigs"

# All items excepts those listed here will be removed from a recipe's
# build history directory by buildhistory_emit_pkghistory(). This is
# necessary because some of these items (package directories, files that
# we no longer emit) might be obsolete.
#
# When extending build history, derive your class from buildhistory.bbclass
# and extend this list here with the additional files created by the derived
# class.
BUILDHISTORY_PRESERVE = "latest latest_srcrev"

PATCH_GIT_USER_EMAIL ?= "buildhistory@oe"
PATCH_GIT_USER_NAME ?= "OpenEmbedded"

#
# Write out metadata about this package for comparison when writing future packages
#
python buildhistory_emit_pkghistory() {
    if not d.getVar('BB_CURRENTTASK') in ['packagedata', 'packagedata_setscene']:
        return 0

    if not "package" in (d.getVar('BUILDHISTORY_FEATURES') or "").split():
        return 0

    import re
    import json
    import errno

    pkghistdir = d.getVar('BUILDHISTORY_DIR_PACKAGE')
    oldpkghistdir = d.getVar('BUILDHISTORY_OLD_DIR_PACKAGE')

    class RecipeInfo:
        def __init__(self, name):
            self.name = name
            self.pe = "0"
            self.pv = "0"
            self.pr = "r0"
            self.depends = ""
            self.packages = ""
            self.srcrev = ""
            self.layer = ""


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
                lns = line.split('=', 1)
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
                    pkginfo.size = int(value)
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
            histfile = os.path.join(oldpkghistdir, pkg, "latest")
            return readPackageInfo(pkg, histfile)
        except EnvironmentError:
            return None

    def sortpkglist(string):
        pkgiter = re.finditer(r'[a-zA-Z0-9.+-]+( \([><=]+[^)]+\))?', string, 0)
        pkglist = [p.group(0) for p in pkgiter]
        pkglist.sort()
        return ' '.join(pkglist)

    def sortlist(string):
        items = string.split(' ')
        items.sort()
        return ' '.join(items)

    pn = d.getVar('PN')
    pe = d.getVar('PE') or "0"
    pv = d.getVar('PV')
    pr = d.getVar('PR')
    layer = bb.utils.get_file_layer(d.getVar('FILE'), d)

    pkgdata_dir = d.getVar('PKGDATA_DIR')
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
    preserve = d.getVar('BUILDHISTORY_PRESERVE').split()
    if not os.path.exists(pkghistdir):
        bb.utils.mkdirhier(pkghistdir)
    else:
        # Remove files for packages that no longer exist
        for item in os.listdir(pkghistdir):
            if item not in preserve:
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
    rcpinfo.depends = sortlist(oe.utils.squashspaces(d.getVar('DEPENDS') or ""))
    rcpinfo.packages = packages
    rcpinfo.layer = layer
    write_recipehistory(rcpinfo, d)

    pkgdest = d.getVar('PKGDEST')
    for pkg in packagelist:
        pkgdata = {}
        with open(os.path.join(pkgdata_dir, 'runtime', pkg)) as f:
            for line in f.readlines():
                item = line.rstrip('\n').split(': ', 1)
                key = item[0]
                if key.endswith('_' + pkg):
                    key = key[:-len(pkg)-1]
                pkgdata[key] = item[1]

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
        filelist = list(dictval.keys())
        filelist.sort()
        pkginfo.filelist = " ".join(filelist)

        pkginfo.size = int(pkgdata['PKGSIZE'])

        write_pkghistory(pkginfo, d)

    # Create files-in-<package-name>.txt files containing a list of files of each recipe's package
    bb.build.exec_func("buildhistory_list_pkg_files", d)
}

python buildhistory_emit_outputsigs() {
    if not "task" in (d.getVar('BUILDHISTORY_FEATURES') or "").split():
        return

    import hashlib

    taskoutdir = os.path.join(d.getVar('BUILDHISTORY_DIR'), 'task', 'output')
    bb.utils.mkdirhier(taskoutdir)
    currenttask = d.getVar('BB_CURRENTTASK')
    pn = d.getVar('PN')
    taskfile = os.path.join(taskoutdir, '%s.%s' % (pn, currenttask))

    cwd = os.getcwd()
    filesigs = {}
    for root, _, files in os.walk(cwd):
        for fname in files:
            if fname == 'fixmepath':
                continue
            fullpath = os.path.join(root, fname)
            try:
                if os.path.islink(fullpath):
                    sha256 = hashlib.sha256(os.readlink(fullpath).encode('utf-8')).hexdigest()
                elif os.path.isfile(fullpath):
                    sha256 = bb.utils.sha256_file(fullpath)
                else:
                    continue
            except OSError:
                bb.warn('buildhistory: unable to read %s to get output signature' % fullpath)
                continue
            filesigs[os.path.relpath(fullpath, cwd)] = sha256
    with open(taskfile, 'w') as f:
        for fpath, fsig in sorted(filesigs.items(), key=lambda item: item[0]):
            f.write('%s %s\n' % (fpath, fsig))
}


def write_recipehistory(rcpinfo, d):
    bb.debug(2, "Writing recipe history")

    pkghistdir = d.getVar('BUILDHISTORY_DIR_PACKAGE')

    infofile = os.path.join(pkghistdir, "latest")
    with open(infofile, "w") as f:
        if rcpinfo.pe != "0":
            f.write(u"PE = %s\n" %  rcpinfo.pe)
        f.write(u"PV = %s\n" %  rcpinfo.pv)
        f.write(u"PR = %s\n" %  rcpinfo.pr)
        f.write(u"DEPENDS = %s\n" %  rcpinfo.depends)
        f.write(u"PACKAGES = %s\n" %  rcpinfo.packages)
        f.write(u"LAYER = %s\n" %  rcpinfo.layer)

    write_latest_srcrev(d, pkghistdir)

def write_pkghistory(pkginfo, d):
    bb.debug(2, "Writing package history for package %s" % pkginfo.name)

    pkghistdir = d.getVar('BUILDHISTORY_DIR_PACKAGE')

    pkgpath = os.path.join(pkghistdir, pkginfo.name)
    if not os.path.exists(pkgpath):
        bb.utils.mkdirhier(pkgpath)

    infofile = os.path.join(pkgpath, "latest")
    with open(infofile, "w") as f:
        if pkginfo.pe != "0":
            f.write(u"PE = %s\n" %  pkginfo.pe)
        f.write(u"PV = %s\n" %  pkginfo.pv)
        f.write(u"PR = %s\n" %  pkginfo.pr)

        if pkginfo.pkg != pkginfo.name:
            f.write(u"PKG = %s\n" % pkginfo.pkg)
        if pkginfo.pkge != pkginfo.pe:
            f.write(u"PKGE = %s\n" % pkginfo.pkge)
        if pkginfo.pkgv != pkginfo.pv:
            f.write(u"PKGV = %s\n" % pkginfo.pkgv)
        if pkginfo.pkgr != pkginfo.pr:
            f.write(u"PKGR = %s\n" % pkginfo.pkgr)
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
            with open(filevarpath, "w") as f:
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
    from oe.utils import format_pkg_list

    process_list = [('file', 'bh_installed_pkgs.txt'),\
                    ('deps', 'bh_installed_pkgs_deps.txt')]

    if rootfs_type == "image":
        pkgs = image_list_installed_packages(d)
    else:
        pkgs = sdk_list_installed_packages(d, rootfs_type == "sdk_target")

    for output_type, output_file in process_list:
        output_file_full = os.path.join(d.getVar('WORKDIR'), output_file)

        with open(output_file_full, 'w') as output:
            output.write(format_pkg_list(pkgs, output_type))

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
	sed 's:\([^| ]*\):"\1":g' ${WORKDIR}/bh_installed_pkgs_deps.txt > $1/depends.tmp &&
		rm ${WORKDIR}/bh_installed_pkgs_deps.txt
	# Remove lines with rpmlib(...) and config(...) dependencies, change the
	# delimiter from pipe to "->", set the style for recommend lines and
	# turn versioned dependencies into edge labels.
	sed -i -e '/rpmlib(/d' \
	       -e '/config(/d' \
	       -e 's:|: -> :' \
	       -e 's:"\[REC\]":[style=dotted]:' \
	       -e 's:"\([<>=]\+\)" "\([^"]*\)":[label="\1 \2"]:' \
		$1/depends.tmp
	# Add header, sorted and de-duped contents and footer and then delete the temp file
	printf "digraph depends {\n    node [shape=plaintext]\n" > $1/depends.dot
	cat $1/depends.tmp | sort -u >> $1/depends.dot
	echo "}" >>  $1/depends.dot
	rm $1/depends.tmp

	# Produce installed package sizes list
	oe-pkgdata-util -p ${PKGDATA_DIR} read-value "PKGSIZE" -n -f $pkgcache > $1/installed-package-sizes.tmp
	cat $1/installed-package-sizes.tmp | awk '{print $2 "\tKiB\t" $1}' | sort -n -r > $1/installed-package-sizes.txt
	rm $1/installed-package-sizes.tmp

	# We're now done with the cache, delete it
	rm $pkgcache

	if [ "$2" != "sdk" ] ; then
		# Produce some cut-down graphs (for readability)
		grep -v kernel-image $1/depends.dot | grep -v kernel-3 | grep -v kernel-4 > $1/depends-nokernel.dot
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

        mkdir -p ${BUILDHISTORY_DIR_IMAGE}
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

	# Collect files requested in BUILDHISTORY_SDK_FILES
	rm -rf ${BUILDHISTORY_DIR_SDK}/sdk-files
	for f in ${BUILDHISTORY_SDK_FILES}; do
		if [ -f ${SDK_OUTPUT}/${SDKPATH}/$f ] ; then
			mkdir -p ${BUILDHISTORY_DIR_SDK}/sdk-files/`dirname $f`
			cp ${SDK_OUTPUT}/${SDKPATH}/$f ${BUILDHISTORY_DIR_SDK}/sdk-files/$f
		fi
	done

	# Record some machine-readable meta-information about the SDK
	printf ""  > ${BUILDHISTORY_DIR_SDK}/sdk-info.txt
	cat >> ${BUILDHISTORY_DIR_SDK}/sdk-info.txt <<END
${@buildhistory_get_sdkvars(d)}
END
	sdksize=`du -ks ${SDK_OUTPUT} | awk '{ print $1 }'`
	echo "SDKSIZE = $sdksize" >> ${BUILDHISTORY_DIR_SDK}/sdk-info.txt
}

python buildhistory_get_extra_sdkinfo() {
    import operator
    from oe.sdk import get_extra_sdkinfo

    sstate_dir = d.expand('${SDK_OUTPUT}/${SDKPATH}/sstate-cache')
    extra_info = get_extra_sdkinfo(sstate_dir)

    if d.getVar('BB_CURRENTTASK') == 'populate_sdk_ext' and \
            "sdk" in (d.getVar('BUILDHISTORY_FEATURES') or "").split():
        with open(d.expand('${BUILDHISTORY_DIR_SDK}/sstate-package-sizes.txt'), 'w') as f:
            filesizes_sorted = sorted(extra_info['filesizes'].items(), key=operator.itemgetter(1, 0), reverse=True)
            for fn, size in filesizes_sorted:
                f.write('%10d KiB %s\n' % (size, fn))
        with open(d.expand('${BUILDHISTORY_DIR_SDK}/sstate-task-sizes.txt'), 'w') as f:
            tasksizes_sorted = sorted(extra_info['tasksizes'].items(), key=operator.itemgetter(1, 0), reverse=True)
            for task, size in tasksizes_sorted:
                f.write('%10d KiB %s\n' % (size, task))
}

# By using ROOTFS_POSTUNINSTALL_COMMAND we get in after uninstallation of
# unneeded packages but before the removal of packaging files
ROOTFS_POSTUNINSTALL_COMMAND += "buildhistory_list_installed_image ;"
ROOTFS_POSTUNINSTALL_COMMAND += "buildhistory_get_image_installed ;"
ROOTFS_POSTUNINSTALL_COMMAND[vardepvalueexclude] .= "| buildhistory_list_installed_image ;| buildhistory_get_image_installed ;"
ROOTFS_POSTUNINSTALL_COMMAND[vardepsexclude] += "buildhistory_list_installed_image buildhistory_get_image_installed"

IMAGE_POSTPROCESS_COMMAND += "buildhistory_get_imageinfo ;"
IMAGE_POSTPROCESS_COMMAND[vardepvalueexclude] .= "| buildhistory_get_imageinfo ;"
IMAGE_POSTPROCESS_COMMAND[vardepsexclude] += "buildhistory_get_imageinfo"

# We want these to be the last run so that we get called after complementary package installation
POPULATE_SDK_POST_TARGET_COMMAND_append = " buildhistory_list_installed_sdk_target;"
POPULATE_SDK_POST_TARGET_COMMAND_append = " buildhistory_get_sdk_installed_target;"
POPULATE_SDK_POST_TARGET_COMMAND[vardepvalueexclude] .= "| buildhistory_list_installed_sdk_target;| buildhistory_get_sdk_installed_target;"

POPULATE_SDK_POST_HOST_COMMAND_append = " buildhistory_list_installed_sdk_host;"
POPULATE_SDK_POST_HOST_COMMAND_append = " buildhistory_get_sdk_installed_host;"
POPULATE_SDK_POST_HOST_COMMAND[vardepvalueexclude] .= "| buildhistory_list_installed_sdk_host;| buildhistory_get_sdk_installed_host;"

SDK_POSTPROCESS_COMMAND_append = " buildhistory_get_sdkinfo ; buildhistory_get_extra_sdkinfo; "
SDK_POSTPROCESS_COMMAND[vardepvalueexclude] .= "| buildhistory_get_sdkinfo ; buildhistory_get_extra_sdkinfo; "

python buildhistory_write_sigs() {
    if not "task" in (d.getVar('BUILDHISTORY_FEATURES') or "").split():
        return

    # Create sigs file
    if hasattr(bb.parse.siggen, 'dump_siglist'):
        taskoutdir = os.path.join(d.getVar('BUILDHISTORY_DIR'), 'task')
        bb.utils.mkdirhier(taskoutdir)
        bb.parse.siggen.dump_siglist(os.path.join(taskoutdir, 'tasksigs.txt'))
}

def buildhistory_get_build_id(d):
    if d.getVar('BB_WORKERCONTEXT') != '1':
        return ""
    localdata = bb.data.createCopy(d)
    statuslines = []
    for func in oe.data.typed_value('BUILDCFG_FUNCS', localdata):
        g = globals()
        if func not in g:
            bb.warn("Build configuration function '%s' does not exist" % func)
        else:
            flines = g[func](localdata)
            if flines:
                statuslines.extend(flines)

    statusheader = d.getVar('BUILDCFG_HEADER')
    return('\n%s\n%s\n' % (statusheader, '\n'.join(statuslines)))

def buildhistory_get_metadata_revs(d):
    # We want an easily machine-readable format here, so get_layers_branch_rev isn't quite what we want
    layers = (d.getVar("BBLAYERS") or "").split()
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
        value = d.getVar(var) or ""
        if var in listvars:
            # Squash out spaces
            value = oe.utils.squashspaces(value)
        ret += "%s = %s\n" % (var, value)
    return ret.rstrip('\n')

def buildhistory_get_imagevars(d):
    if d.getVar('BB_WORKERCONTEXT') != '1':
        return ""
    imagevars = "DISTRO DISTRO_VERSION USER_CLASSES IMAGE_CLASSES IMAGE_FEATURES IMAGE_LINGUAS IMAGE_INSTALL BAD_RECOMMENDATIONS NO_RECOMMENDATIONS PACKAGE_EXCLUDE ROOTFS_POSTPROCESS_COMMAND IMAGE_POSTPROCESS_COMMAND"
    listvars = "USER_CLASSES IMAGE_CLASSES IMAGE_FEATURES IMAGE_LINGUAS IMAGE_INSTALL BAD_RECOMMENDATIONS PACKAGE_EXCLUDE"
    return outputvars(imagevars, listvars, d)

def buildhistory_get_sdkvars(d):
    if d.getVar('BB_WORKERCONTEXT') != '1':
        return ""
    sdkvars = "DISTRO DISTRO_VERSION SDK_NAME SDK_VERSION SDKMACHINE SDKIMAGE_FEATURES BAD_RECOMMENDATIONS NO_RECOMMENDATIONS PACKAGE_EXCLUDE"
    if d.getVar('BB_CURRENTTASK') == 'populate_sdk_ext':
        # Extensible SDK uses some additional variables
        sdkvars += " SDK_LOCAL_CONF_WHITELIST SDK_LOCAL_CONF_BLACKLIST SDK_INHERIT_BLACKLIST SDK_UPDATE_URL SDK_EXT_TYPE SDK_RECRDEP_TASKS SDK_INCLUDE_PKGDATA SDK_INCLUDE_TOOLCHAIN"
    listvars = "SDKIMAGE_FEATURES BAD_RECOMMENDATIONS PACKAGE_EXCLUDE SDK_LOCAL_CONF_WHITELIST SDK_LOCAL_CONF_BLACKLIST SDK_INHERIT_BLACKLIST"
    return outputvars(sdkvars, listvars, d)


def buildhistory_get_cmdline(d):
    argv = d.getVar('BB_CMDLINE', False)
    if argv:
        if argv[0].endswith('bin/bitbake'):
            bincmd = 'bitbake'
        else:
            bincmd = argv[0]
        return '%s %s' % (bincmd, ' '.join(argv[1:]))
    return ''


buildhistory_single_commit() {
	if [ "$3" = "" ] ; then
		commitopts="${BUILDHISTORY_DIR}/ --allow-empty"
		shortlogprefix="No changes: "
	else
		commitopts=""
		shortlogprefix=""
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
${shortlogprefix}Build ${BUILDNAME} of ${DISTRO} ${DISTRO_VERSION} for machine ${MACHINE} on $2

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
		if [ ! -e .git ] ; then
			git init -q
		else
			git tag -f build-minus-3 build-minus-2 > /dev/null 2>&1 || true
			git tag -f build-minus-2 build-minus-1 > /dev/null 2>&1 || true
			git tag -f build-minus-1 > /dev/null 2>&1 || true
		fi

		check_git_config

		# Check if there are new/changed files to commit (other than metadata-revs)
		repostatus=`git status --porcelain | grep -v " metadata-revs$"`
		HOSTNAME=`hostname 2>/dev/null || echo unknown`
		CMDLINE="${@buildhistory_get_cmdline(d)}"
		if [ "$repostatus" != "" ] ; then
			git add -A .
			# porcelain output looks like "?? packages/foo/bar"
			# Ensure we commit metadata-revs with the first commit
			buildhistory_single_commit "$CMDLINE" "$HOSTNAME" dummy
			git gc --auto --quiet
		else
			buildhistory_single_commit "$CMDLINE" "$HOSTNAME"
		fi
		if [ "${BUILDHISTORY_PUSH_REPO}" != "" ] ; then
			git push -q ${BUILDHISTORY_PUSH_REPO}
		fi) || true
}

python buildhistory_eventhandler() {
    if e.data.getVar('BUILDHISTORY_FEATURES').strip():
        reset = e.data.getVar("BUILDHISTORY_RESET")
        olddir = e.data.getVar("BUILDHISTORY_OLD_DIR")
        if isinstance(e, bb.event.BuildStarted):
            if reset:
                import shutil
                # Clean up after potentially interrupted build.
                if os.path.isdir(olddir):
                    shutil.rmtree(olddir)
                rootdir = e.data.getVar("BUILDHISTORY_DIR")
                entries = [ x for x in os.listdir(rootdir) if not x.startswith('.') ]
                bb.utils.mkdirhier(olddir)
                for entry in entries:
                    os.rename(os.path.join(rootdir, entry),
                              os.path.join(olddir, entry))
        elif isinstance(e, bb.event.BuildCompleted):
            if reset:
                import shutil
                shutil.rmtree(olddir)
            if e.data.getVar("BUILDHISTORY_COMMIT") == "1":
                bb.note("Writing buildhistory")
                bb.build.exec_func("buildhistory_write_sigs", d)
                localdata = bb.data.createCopy(e.data)
                localdata.setVar('BUILDHISTORY_BUILD_FAILURES', str(e._failures))
                interrupted = getattr(e, '_interrupted', 0)
                localdata.setVar('BUILDHISTORY_BUILD_INTERRUPTED', str(interrupted))
                bb.build.exec_func("buildhistory_commit", localdata)
            else:
                bb.note("No commit since BUILDHISTORY_COMMIT != '1'")
}

addhandler buildhistory_eventhandler
buildhistory_eventhandler[eventmask] = "bb.event.BuildCompleted bb.event.BuildStarted"


# FIXME this ought to be moved into the fetcher
def _get_srcrev_values(d):
    """
    Return the version strings for the current recipe
    """

    scms = []
    fetcher = bb.fetch.Fetch(d.getVar('SRC_URI').split(), d)
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
    write_latest_srcrev(d, d.getVar('BUILDHISTORY_DIR_PACKAGE'))
}

def write_latest_srcrev(d, pkghistdir):
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
                f.write('SRCREV = "%s"\n' % next(iter(srcrevs.values())))
            if len(tag_srcrevs) > 0:
                for name, srcrev in tag_srcrevs.items():
                    f.write('# tag_%s = "%s"\n' % (name, srcrev))
                    if name in old_tag_srcrevs and old_tag_srcrevs[name] != srcrev:
                        pkg = d.getVar('PN')
                        bb.warn("Revision for tag %s in package %s was changed since last build (from %s to %s)" % (name, pkg, old_tag_srcrevs[name], srcrev))

    else:
        if os.path.exists(srcrevfile):
            os.remove(srcrevfile)

do_testimage[postfuncs] += "write_ptest_result"
do_testimage[vardepsexclude] += "write_ptest_result"

python write_ptest_result() {
    write_latest_ptest_result(d, d.getVar('BUILDHISTORY_DIR'))
}

def write_latest_ptest_result(d, histdir):
    import glob
    import subprocess
    test_log_dir = d.getVar('TEST_LOG_DIR')
    input_ptest = os.path.join(test_log_dir, 'ptest_log')
    output_ptest = os.path.join(histdir, 'ptest')
    if os.path.exists(input_ptest):
        try:
            # Lock it avoid race issue
            lock = bb.utils.lockfile(output_ptest + "/ptest.lock")
            bb.utils.mkdirhier(output_ptest)
            oe.path.copytree(input_ptest, output_ptest)
            # Sort test result
            for result in glob.glob('%s/pass.fail.*' % output_ptest):
                bb.debug(1, 'Processing %s' % result)
                cmd = ['sort', result, '-o', result]
                bb.debug(1, 'Running %s' % cmd)
                ret = subprocess.call(cmd)
                if ret != 0:
                    bb.error('Failed to run %s!' % cmd)
        finally:
            bb.utils.unlockfile(lock)
