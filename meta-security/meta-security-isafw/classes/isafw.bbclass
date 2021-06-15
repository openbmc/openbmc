# Security scanning class
#
# Based in part on buildhistory.bbclass which was in turn based on
# testlab.bbclass and packagehistory.bbclass
#
# Copyright (C) 2011-2015 Intel Corporation
# Copyright (C) 2007-2011 Koen Kooi <koen@openembedded.org>
#

LICENSE = "MIT"

require conf/distro/include/distro_alias.inc

ISAFW_WORKDIR = "${WORKDIR}/isafw"
ISAFW_REPORTDIR ?= "${LOG_DIR}/isafw-report"
ISAFW_LOGDIR ?= "${LOG_DIR}/isafw-logs"

ISAFW_PLUGINS_WHITELIST ?= ""
ISAFW_PLUGINS_BLACKLIST ?= ""

ISAFW_LA_PLUGIN_IMAGE_WHITELIST ?= ""
ISAFW_LA_PLUGIN_IMAGE_BLACKLIST ?= ""

# First, code to handle scanning each recipe that goes into the build

do_analysesource[nostamp] = "1"
do_analysesource[cleandirs] = "${ISAFW_WORKDIR}"

python do_analysesource() {
    from isafw import isafw

    imageSecurityAnalyser = isafw_init(isafw, d)

    if not d.getVar('SRC_URI', True):
        # Recipe didn't fetch any sources, nothing to do here I assume?
        return

    recipe = isafw.ISA_package()
    recipe.name = d.getVar('BPN', True)
    recipe.version = d.getVar('PV', True)
    recipe.version = recipe.version.split('+git', 1)[0]

    for p in d.getVar('PACKAGES', True).split():
        license = str(d.getVar('LICENSE_' + p, True))
        if license == "None":
            license = d.getVar('LICENSE', True)
        license = license.replace("(", "")
        license = license.replace(")", "")
        licenses = license.split()
        while '|' in licenses:
            licenses.remove('|')
        while '&' in licenses:
            licenses.remove('&')
        for l in licenses:
            recipe.licenses.append(p + ":" + canonical_license(d, l))

    aliases = d.getVar('DISTRO_PN_ALIAS', True)
    if aliases:
        recipe.aliases = aliases.split()
        faliases = []
        for a in recipe.aliases:
            if (a != "OSPDT") and (not (a.startswith("upstream="))):
                faliases.append(a.split('=', 1)[-1])
        # remove possible duplicates in pkg names
        faliases = list(set(faliases))
        recipe.aliases = faliases

    for patch in src_patches(d):
        _,_,local,_,_,_=bb.fetch.decodeurl(patch)
        recipe.patch_files.append(os.path.basename(local))
    if (not recipe.patch_files) :
        recipe.patch_files.append("None")

    # Pass the recipe object to the security framework
    bb.debug(1, '%s: analyse sources' % (d.getVar('PN', True)))
    imageSecurityAnalyser.process_package(recipe)

    return
}

addtask do_analysesource before do_build

# This task intended to be called after default task to process reports

PR_ORIG_TASK := "${BB_DEFAULT_TASK}"
addhandler process_reports_handler
process_reports_handler[eventmask] = "bb.event.BuildCompleted"

python process_reports_handler() {
    from isafw import isafw

    dd = d.createCopy()
    target_sysroot = dd.expand("${STAGING_DIR}/${MACHINE}")
    native_sysroot = dd.expand("${STAGING_DIR}/${BUILD_ARCH}")
    staging_populate_sysroot_dir(target_sysroot, native_sysroot, True, dd)
 
    dd.setVar("STAGING_DIR_NATIVE", native_sysroot)
    savedenv = os.environ.copy()
    os.environ["PATH"] = dd.getVar("PATH", True)

    imageSecurityAnalyser = isafw_init(isafw, dd)
    bb.debug(1, 'isafw: process reports')
    imageSecurityAnalyser.process_report()

    os.environ["PATH"] = savedenv["PATH"]
}

do_build[depends] += "cve-update-db-native:do_populate_cve_db ca-certificates-native:do_populate_sysroot"
do_build[depends] += "python3-lxml-native:do_populate_sysroot"

# These tasks are intended to be called directly by the user (e.g. bitbake -c)

addtask do_analyse_sources after do_analysesource
do_analyse_sources[doc] = "Produce ISAFW reports based on given package without building it"
do_analyse_sources[nostamp] = "1"
do_analyse_sources() {
	:
}

addtask do_analyse_sources_all after do_analysesource
do_analyse_sources_all[doc] = "Produce ISAFW reports for all packages in given target without building them"
do_analyse_sources_all[recrdeptask] = "do_analyse_sources_all do_analysesource"
do_analyse_sources_all[recideptask] = "do_${PR_ORIG_TASK}"
do_analyse_sources_all[nostamp] = "1"
do_analyse_sources_all() {
	:
}

python() {
    # We probably don't need to scan these
    if bb.data.inherits_class('native', d) or \
       bb.data.inherits_class('nativesdk', d) or \
       bb.data.inherits_class('cross', d) or \
       bb.data.inherits_class('crosssdk', d) or \
       bb.data.inherits_class('cross-canadian', d) or \
       bb.data.inherits_class('packagegroup', d) or \
       bb.data.inherits_class('image', d):
        bb.build.deltask('do_analysesource', d)
}

fakeroot python do_analyse_image() {

    from isafw import isafw

    imageSecurityAnalyser = isafw_init(isafw, d)

    # Directory where the image's entire contents can be examined
    rootfsdir = d.getVar('IMAGE_ROOTFS', True)

    imagebasename = d.getVar('IMAGE_BASENAME', True)

    kernelconf = d.getVar('STAGING_KERNEL_BUILDDIR', True) + "/.config"
    if os.path.exists(kernelconf):
        kernel = isafw.ISA_kernel()
        kernel.img_name = imagebasename
        kernel.path_to_config = kernelconf
        bb.debug(1, 'do kernel conf analysis on %s' % kernelconf)
        imageSecurityAnalyser.process_kernel(kernel)
    else:
        bb.debug(1, 'Kernel configuration file is missing. Not performing analysis on %s' % kernelconf)

    pkglist = manifest2pkglist(d)

    imagebasename = d.getVar('IMAGE_BASENAME', True)

    if (pkglist):
        pkg_list = isafw.ISA_pkg_list()
        pkg_list.img_name = imagebasename
        pkg_list.path_to_list = pkglist
        bb.debug(1, 'do pkg list analysis on %s' % pkglist)
        imageSecurityAnalyser.process_pkg_list(pkg_list)

    fs = isafw.ISA_filesystem()
    fs.img_name = imagebasename
    fs.path_to_fs = rootfsdir

    bb.debug(1, 'do image analysis on %s' % rootfsdir)
    imageSecurityAnalyser.process_filesystem(fs)
}

do_rootfs[depends] += "checksec-native:do_populate_sysroot ca-certificates-native:do_populate_sysroot"
do_rootfs[depends] += "prelink-native:do_populate_sysroot"
do_rootfs[depends] += "python3-lxml-native:do_populate_sysroot"

isafw_init[vardepsexclude] = "DATETIME"
def isafw_init(isafw, d):
    import re, errno

    isafw_config = isafw.ISA_config()
    # Override the builtin default in curl-native (used by cve-update-db-nativ)
    # because that default is a path that may not be valid: when curl-native gets
    # installed from sstate, we end up with the sysroot path as it was on the
    # original build host, which is not necessarily the same path used now
    # (see https://bugzilla.yoctoproject.org/show_bug.cgi?id=9883).
    #
    # Can't use ${sysconfdir} here, it already includes ${STAGING_DIR_NATIVE}
    # when the current recipe is native.
    isafw_config.cacert = d.expand('${STAGING_DIR_NATIVE}/etc/ssl/certs/ca-certificates.crt')

    bb.utils.export_proxies(d)

    isafw_config.machine = d.getVar('MACHINE', True)
    isafw_config.timestamp = d.getVar('DATETIME', True)
    isafw_config.reportdir = d.getVar('ISAFW_REPORTDIR', True) + "_" + isafw_config.timestamp
    if not os.path.exists(os.path.dirname(isafw_config.reportdir + "/test")):
        try:
            os.makedirs(os.path.dirname(isafw_config.reportdir + "/test"))
        except OSError as exc:
            if exc.errno == errno.EEXIST and os.path.isdir(isafw_config.reportdir):
                pass
            else: raise
    isafw_config.logdir = d.getVar('ISAFW_LOGDIR', True)
    # Adding support for arm
    # TODO: Add support for other platforms
    isafw_config.arch =  d.getVar('TARGET_ARCH', True)
    if ( isafw_config.arch != "arm" ):
        isafw_config.arch = "x86"

    whitelist = d.getVar('ISAFW_PLUGINS_WHITELIST', True)
    blacklist = d.getVar('ISAFW_PLUGINS_BLACKLIST', True)
    if whitelist:
        isafw_config.plugin_whitelist = re.split(r'[,\s]*', whitelist)
    if blacklist:
        isafw_config.plugin_blacklist = re.split(r'[,\s]*', blacklist)

    la_image_whitelist = d.getVar('ISAFW_LA_PLUGIN_IMAGE_WHITELIST', True)
    la_image_blacklist = d.getVar('ISAFW_LA_PLUGIN_IMAGE_BLACKLIST', True)
    if la_image_whitelist:
        isafw_config.la_plugin_image_whitelist = re.split(r'[,\s]*', la_image_whitelist)
    if la_image_blacklist:
        isafw_config.la_plugin_image_blacklist = re.split(r'[,\s]*', la_image_blacklist)

    return isafw.ISA(isafw_config)

# based on toaster.bbclass _toaster_load_pkgdatafile function
def binary2source(dirpath, filepath):
    import re
    originPkg = ""
    with open(os.path.join(dirpath, filepath), "r") as fin:
        for line in fin:
            try:
                kn, kv = line.strip().split(": ", 1)
                m = re.match(r"^PKG_([^A-Z:]*)", kn)
                if m:
                    originPkg = str(m.group(1))
            except ValueError:
                pass    # ignore lines without valid key: value pairs:
    if not originPkg:
        originPkg = "UNKNOWN"
    return originPkg

manifest2pkglist[vardepsexclude] = "DATETIME"
def manifest2pkglist(d):
    import glob

    manifest_file = d.getVar('IMAGE_MANIFEST', True)
    imagebasename = d.getVar('IMAGE_BASENAME', True)
    reportdir = d.getVar('ISAFW_REPORTDIR', True) + "_" + d.getVar('DATETIME', True)
    pkgdata_dir = d.getVar("PKGDATA_DIR", True)
    rr_dir = "%s/runtime-reverse/" % pkgdata_dir
    pkglist = reportdir + "/pkglist"

    with open(pkglist, 'a') as foutput:
        foutput.write("Packages for image " + imagebasename + "\n")
        try:
            with open(manifest_file, 'r') as finput:
                for line in finput:
                    items = line.split()
                    if items and (len(items) >= 3):
                        pkgnames = map(os.path.basename, glob.glob(os.path.join(rr_dir, items[0])))
                        for pkgname in pkgnames:
                            originPkg = binary2source(rr_dir, pkgname)
                            version = items[2]
                            if not version:
                                version = "undetermined"
                            foutput.write(pkgname + " " + version + " " + originPkg + "\n")
        except IOError:
            bb.debug(1, 'isafw: manifest file not found. Skip pkg list analysis')
            return "";


    return pkglist

# NOTE: by the time IMAGE_POSTPROCESS_COMMAND items are called, the image
# has been stripped of the package manager database (if runtime package management
# is not enabled, i.e. 'package-management' is not in IMAGE_FEATURES). If you
# do want to be using the package manager to operate on the image contents, you'll
# need to call your function from ROOTFS_POSTINSTALL_COMMAND or
# ROOTFS_POSTUNINSTALL_COMMAND instead - however if you do that you should then be
# aware that what you'll be looking at isn't exactly what you will see in the image
# at runtime (there will be other postprocessing functions called after yours).
#
# do_analyse_image does not need the package manager database. Making it
# a separate task instead of a IMAGE_POSTPROCESS_COMMAND has several
# advantages:
# - all other image commands are guaranteed to have completed
# - it can run in parallel to other tasks which depend on the complete
#   image, instead of blocking those other tasks
# - meta-swupd helper images do not need to be analysed and won't be
#   because nothing depends on their "do_build" task, only on
#   do_image_complete
python () {
    if bb.data.inherits_class('image', d):
        bb.build.addtask('do_analyse_image', 'do_build', 'do_image_complete', d)
}

python isafwreport_handler () {

    import shutil

    logdir = e.data.getVar('ISAFW_LOGDIR', True)
    if os.path.exists(os.path.dirname(logdir+"/test")):
        shutil.rmtree(logdir)
    os.makedirs(os.path.dirname(logdir+"/test"))

}
addhandler isafwreport_handler
isafwreport_handler[eventmask] = "bb.event.BuildStarted"
