# Populates LICENSE_DIRECTORY as set in distro config with the license files as set by
# LIC_FILES_CHKSUM.
# TODO:
# - There is a real issue revolving around license naming standards.

LICENSE_DIRECTORY ??= "${DEPLOY_DIR}/licenses"
LICSSTATEDIR = "${WORKDIR}/license-destdir/"

# Create extra package with license texts and add it to RRECOMMENDS_${PN}
LICENSE_CREATE_PACKAGE[type] = "boolean"
LICENSE_CREATE_PACKAGE ??= "0"
LICENSE_PACKAGE_SUFFIX ??= "-lic"
LICENSE_FILES_DIRECTORY ??= "${datadir}/licenses/"

addtask populate_lic after do_patch before do_build
do_populate_lic[dirs] = "${LICSSTATEDIR}/${PN}"
do_populate_lic[cleandirs] = "${LICSSTATEDIR}"

python write_package_manifest() {
    # Get list of installed packages
    license_image_dir = d.expand('${LICENSE_DIRECTORY}/${IMAGE_NAME}')
    bb.utils.mkdirhier(license_image_dir)
    from oe.rootfs import image_list_installed_packages
    from oe.utils import format_pkg_list

    pkgs = image_list_installed_packages(d)
    output = format_pkg_list(pkgs)
    open(os.path.join(license_image_dir, 'package.manifest'),
        'w+').write(output)
}

python write_deploy_manifest() {
    license_deployed_manifest(d)
}

python license_create_manifest() {
    import oe.packagedata
    from oe.rootfs import image_list_installed_packages

    build_images_from_feeds = d.getVar('BUILD_IMAGES_FROM_FEEDS')
    if build_images_from_feeds == "1":
        return 0

    pkg_dic = {}
    for pkg in sorted(image_list_installed_packages(d)):
        pkg_info = os.path.join(d.getVar('PKGDATA_DIR'),
                                'runtime-reverse', pkg)
        pkg_name = os.path.basename(os.readlink(pkg_info))

        pkg_dic[pkg_name] = oe.packagedata.read_pkgdatafile(pkg_info)
        if not "LICENSE" in pkg_dic[pkg_name].keys():
            pkg_lic_name = "LICENSE_" + pkg_name
            pkg_dic[pkg_name]["LICENSE"] = pkg_dic[pkg_name][pkg_lic_name]

    rootfs_license_manifest = os.path.join(d.getVar('LICENSE_DIRECTORY'),
                        d.getVar('IMAGE_NAME'), 'license.manifest')
    write_license_files(d, rootfs_license_manifest, pkg_dic)
}

def write_license_files(d, license_manifest, pkg_dic):
    import re

    bad_licenses = (d.getVar("INCOMPATIBLE_LICENSE") or "").split()
    bad_licenses = map(lambda l: canonical_license(d, l), bad_licenses)
    bad_licenses = expand_wildcard_licenses(d, bad_licenses)

    with open(license_manifest, "w") as license_file:
        for pkg in sorted(pkg_dic):
            if bad_licenses:
                try:
                    (pkg_dic[pkg]["LICENSE"], pkg_dic[pkg]["LICENSES"]) = \
                        oe.license.manifest_licenses(pkg_dic[pkg]["LICENSE"],
                        bad_licenses, canonical_license, d)
                except oe.license.LicenseError as exc:
                    bb.fatal('%s: %s' % (d.getVar('P'), exc))
            else:
                pkg_dic[pkg]["LICENSES"] = re.sub('[|&()*]', ' ', pkg_dic[pkg]["LICENSE"])
                pkg_dic[pkg]["LICENSES"] = re.sub('  *', ' ', pkg_dic[pkg]["LICENSES"])
                pkg_dic[pkg]["LICENSES"] = pkg_dic[pkg]["LICENSES"].split()

            if not "IMAGE_MANIFEST" in pkg_dic[pkg]:
                # Rootfs manifest
                license_file.write("PACKAGE NAME: %s\n" % pkg)
                license_file.write("PACKAGE VERSION: %s\n" % pkg_dic[pkg]["PV"])
                license_file.write("RECIPE NAME: %s\n" % pkg_dic[pkg]["PN"])
                license_file.write("LICENSE: %s\n\n" % pkg_dic[pkg]["LICENSE"])

                # If the package doesn't contain any file, that is, its size is 0, the license
                # isn't relevant as far as the final image is concerned. So doing license check
                # doesn't make much sense, skip it.
                if pkg_dic[pkg]["PKGSIZE_%s" % pkg] == "0":
                    continue
            else:
                # Image manifest
                license_file.write("RECIPE NAME: %s\n" % pkg_dic[pkg]["PN"])
                license_file.write("VERSION: %s\n" % pkg_dic[pkg]["PV"])
                license_file.write("LICENSE: %s\n" % pkg_dic[pkg]["LICENSE"])
                license_file.write("FILES: %s\n\n" % pkg_dic[pkg]["FILES"])

            for lic in pkg_dic[pkg]["LICENSES"]:
                lic_file = os.path.join(d.getVar('LICENSE_DIRECTORY'),
                                        pkg_dic[pkg]["PN"], "generic_%s" % 
                                        re.sub('\+', '', lic))
                # add explicity avoid of CLOSED license because isn't generic
                if lic == "CLOSED":
                   continue

                if not os.path.exists(lic_file):
                   bb.warn("The license listed %s was not in the "\ 
                            "licenses collected for recipe %s" 
                            % (lic, pkg_dic[pkg]["PN"]))

    # Two options here:
    # - Just copy the manifest
    # - Copy the manifest and the license directories
    # With both options set we see a .5 M increase in core-image-minimal
    copy_lic_manifest = d.getVar('COPY_LIC_MANIFEST')
    copy_lic_dirs = d.getVar('COPY_LIC_DIRS')
    if copy_lic_manifest == "1":
        rootfs_license_dir = os.path.join(d.getVar('IMAGE_ROOTFS'), 
                                'usr', 'share', 'common-licenses')
        bb.utils.mkdirhier(rootfs_license_dir)
        rootfs_license_manifest = os.path.join(rootfs_license_dir,
                os.path.split(license_manifest)[1])
        if not os.path.exists(rootfs_license_manifest):
            os.link(license_manifest, rootfs_license_manifest)

        if copy_lic_dirs == "1":
            for pkg in sorted(pkg_dic):
                pkg_rootfs_license_dir = os.path.join(rootfs_license_dir, pkg)
                bb.utils.mkdirhier(pkg_rootfs_license_dir)
                pkg_license_dir = os.path.join(d.getVar('LICENSE_DIRECTORY'),
                                            pkg_dic[pkg]["PN"]) 

                pkg_manifest_licenses = [canonical_license(d, lic) \
                        for lic in pkg_dic[pkg]["LICENSES"]]

                licenses = os.listdir(pkg_license_dir)
                for lic in licenses:
                    rootfs_license = os.path.join(rootfs_license_dir, lic)
                    pkg_license = os.path.join(pkg_license_dir, lic)
                    pkg_rootfs_license = os.path.join(pkg_rootfs_license_dir, lic)

                    if re.match("^generic_.*$", lic):
                        generic_lic = canonical_license(d,
                                re.search("^generic_(.*)$", lic).group(1))

                        # Do not copy generic license into package if isn't
                        # declared into LICENSES of the package.
                        if not re.sub('\+$', '', generic_lic) in \
                                [re.sub('\+', '', lic) for lic in \
                                 pkg_manifest_licenses]:
                            continue

                        if oe.license.license_ok(generic_lic,
                                bad_licenses) == False:
                            continue

                        if not os.path.exists(rootfs_license):
                            os.link(pkg_license, rootfs_license)

                        if not os.path.exists(pkg_rootfs_license):
                            os.symlink(os.path.join('..', lic), pkg_rootfs_license)
                    else:
                        if (oe.license.license_ok(canonical_license(d,
                                lic), bad_licenses) == False or
                                os.path.exists(pkg_rootfs_license)):
                            continue

                        os.link(pkg_license, pkg_rootfs_license)


def license_deployed_manifest(d):
    """
    Write the license manifest for the deployed recipes.
    The deployed recipes usually includes the bootloader
    and extra files to boot the target.
    """

    dep_dic = {}
    man_dic = {}
    lic_dir = d.getVar("LICENSE_DIRECTORY")

    dep_dic = get_deployed_dependencies(d)
    for dep in dep_dic.keys():
        man_dic[dep] = {}
        # It is necessary to mark this will be used for image manifest
        man_dic[dep]["IMAGE_MANIFEST"] = True
        man_dic[dep]["PN"] = dep
        man_dic[dep]["FILES"] = \
            " ".join(get_deployed_files(dep_dic[dep]))
        with open(os.path.join(lic_dir, dep, "recipeinfo"), "r") as f:
            for line in f.readlines():
                key,val = line.split(": ", 1)
                man_dic[dep][key] = val[:-1]

    lic_manifest_dir = os.path.join(d.getVar('LICENSE_DIRECTORY'),
                                    d.getVar('IMAGE_NAME'))
    bb.utils.mkdirhier(lic_manifest_dir)
    image_license_manifest = os.path.join(lic_manifest_dir, 'image_license.manifest')
    write_license_files(d, image_license_manifest, man_dic)

def get_deployed_dependencies(d):
    """
    Get all the deployed dependencies of an image
    """

    deploy = {}
    # Get all the dependencies for the current task (rootfs).
    # Also get EXTRA_IMAGEDEPENDS because the bootloader is
    # usually in this var and not listed in rootfs.
    # At last, get the dependencies from boot classes because
    # it might contain the bootloader.
    taskdata = d.getVar("BB_TASKDEPDATA", False)
    depends = list(set([dep[0] for dep
                    in list(taskdata.values())
                    if not dep[0].endswith("-native")]))
    extra_depends = d.getVar("EXTRA_IMAGEDEPENDS")
    boot_depends = get_boot_dependencies(d)
    depends.extend(extra_depends.split())
    depends.extend(boot_depends)
    depends = list(set(depends))

    # To verify what was deployed it checks the rootfs dependencies against
    # the SSTATE_MANIFESTS for "deploy" task.
    # The manifest file name contains the arch. Because we are not running
    # in the recipe context it is necessary to check every arch used.
    sstate_manifest_dir = d.getVar("SSTATE_MANIFESTS")
    sstate_archs = d.getVar("SSTATE_ARCHS")
    extra_archs = d.getVar("PACKAGE_EXTRA_ARCHS")
    archs = list(set(("%s %s" % (sstate_archs, extra_archs)).split()))
    for dep in depends:
        # Some recipes have an arch on their own, so we try that first.
        special_arch = d.getVar("PACKAGE_ARCH_pn-%s" % dep)
        if special_arch:
            sstate_manifest_file = os.path.join(sstate_manifest_dir,
                    "manifest-%s-%s.deploy" % (special_arch, dep))
            if os.path.exists(sstate_manifest_file):
                deploy[dep] = sstate_manifest_file
                continue

        for arch in archs:
            sstate_manifest_file = os.path.join(sstate_manifest_dir,
                    "manifest-%s-%s.deploy" % (arch, dep))
            if os.path.exists(sstate_manifest_file):
                deploy[dep] = sstate_manifest_file
                break

    return deploy
get_deployed_dependencies[vardepsexclude] = "BB_TASKDEPDATA"

def get_boot_dependencies(d):
    """
    Return the dependencies from boot tasks
    """

    depends = []
    taskdepdata = d.getVar("BB_TASKDEPDATA", False)
    # Only bootimg includes the depends flag
    boot_depends_string = d.getVarFlag("do_bootimg", "depends") or ""
    boot_depends = [dep.split(":")[0] for dep
                in boot_depends_string.split()
                if not dep.split(":")[0].endswith("-native")]
    for dep in boot_depends:
        info_file = os.path.join(d.getVar("LICENSE_DIRECTORY"),
                dep, "recipeinfo")
        # If the recipe and dependency name is the same
        if os.path.exists(info_file):
            depends.append(dep)
        # We need to search for the provider of the dependency
        else:
            for taskdep in taskdepdata.values():
                # The fifth field contains what the task provides
                if dep in taskdep[4]:
                    info_file = os.path.join(
                            d.getVar("LICENSE_DIRECTORY"),
                            taskdep[0], "recipeinfo")
                    if os.path.exists(info_file):
                        depends.append(taskdep[0])
                        break
    return depends
get_boot_dependencies[vardepsexclude] = "BB_TASKDEPDATA"

def get_deployed_files(man_file):
    """
    Get the files deployed from the sstate manifest
    """

    dep_files = []
    excluded_files = []
    with open(man_file, "r") as manifest:
        all_files = manifest.read()
    for f in all_files.splitlines():
        if ((not (os.path.islink(f) or os.path.isdir(f))) and
                not os.path.basename(f) in excluded_files):
            dep_files.append(os.path.basename(f))
    return dep_files

python do_populate_lic() {
    """
    Populate LICENSE_DIRECTORY with licenses.
    """
    lic_files_paths = find_license_files(d)

    # The base directory we wrangle licenses to
    destdir = os.path.join(d.getVar('LICSSTATEDIR'), d.getVar('PN'))
    copy_license_files(lic_files_paths, destdir)
    info = get_recipe_info(d)
    with open(os.path.join(destdir, "recipeinfo"), "w") as f:
        for key in sorted(info.keys()):
            f.write("%s: %s\n" % (key, info[key]))
}

# it would be better to copy them in do_install_append, but find_license_filesa is python
python perform_packagecopy_prepend () {
    enabled = oe.data.typed_value('LICENSE_CREATE_PACKAGE', d)
    if d.getVar('CLASSOVERRIDE') == 'class-target' and enabled:
        lic_files_paths = find_license_files(d)

        # LICENSE_FILES_DIRECTORY starts with '/' so os.path.join cannot be used to join D and LICENSE_FILES_DIRECTORY
        destdir = d.getVar('D') + os.path.join(d.getVar('LICENSE_FILES_DIRECTORY'), d.getVar('PN'))
        copy_license_files(lic_files_paths, destdir)
        add_package_and_files(d)
}
perform_packagecopy[vardeps] += "LICENSE_CREATE_PACKAGE"

def get_recipe_info(d):
    info = {}
    info["PV"] = d.getVar("PV")
    info["PR"] = d.getVar("PR")
    info["LICENSE"] = d.getVar("LICENSE")
    return info

def add_package_and_files(d):
    packages = d.getVar('PACKAGES')
    files = d.getVar('LICENSE_FILES_DIRECTORY')
    pn = d.getVar('PN')
    pn_lic = "%s%s" % (pn, d.getVar('LICENSE_PACKAGE_SUFFIX', False))
    if pn_lic in packages:
        bb.warn("%s package already existed in %s." % (pn_lic, pn))
    else:
        # first in PACKAGES to be sure that nothing else gets LICENSE_FILES_DIRECTORY
        d.setVar('PACKAGES', "%s %s" % (pn_lic, packages))
        d.setVar('FILES_' + pn_lic, files)
        rrecommends_pn = d.getVar('RRECOMMENDS_' + pn)
        if rrecommends_pn:
            d.setVar('RRECOMMENDS_' + pn, "%s %s" % (pn_lic, rrecommends_pn))
        else:
            d.setVar('RRECOMMENDS_' + pn, "%s" % (pn_lic))

def copy_license_files(lic_files_paths, destdir):
    import shutil
    import errno

    bb.utils.mkdirhier(destdir)
    for (basename, path, beginline, endline) in lic_files_paths:
        try:
            src = path
            dst = os.path.join(destdir, basename)
            if os.path.exists(dst):
                os.remove(dst)
            if os.path.islink(src):
                src = os.path.realpath(src)
            canlink = os.access(src, os.W_OK) and (os.stat(src).st_dev == os.stat(destdir).st_dev) and beginline is None and endline is None
            if canlink:
                try:
                    os.link(src, dst)
                except OSError as err:
                    if err.errno == errno.EXDEV:
                        # Copy license files if hard-link is not possible even if st_dev is the
                        # same on source and destination (docker container with device-mapper?)
                        canlink = False
                    else:
                        raise
                # Only chown if we did hardling, and, we're running under pseudo
                if canlink and os.environ.get('PSEUDO_DISABLED') == '0':
                    os.chown(dst,0,0)
            if not canlink:
                begin_idx = int(beginline)-1 if beginline is not None else None
                end_idx = int(endline) if endline is not None else None
                if begin_idx is None and end_idx is None:
                    shutil.copyfile(src, dst)
                else:
                    with open(src, 'rb') as src_f:
                        with open(dst, 'wb') as dst_f:
                            dst_f.write(b''.join(src_f.readlines()[begin_idx:end_idx]))

        except Exception as e:
            bb.warn("Could not copy license file %s to %s: %s" % (src, dst, e))

def find_license_files(d):
    """
    Creates list of files used in LIC_FILES_CHKSUM and generic LICENSE files.
    """
    import shutil
    import oe.license
    from collections import defaultdict, OrderedDict

    # All the license files for the package
    lic_files = d.getVar('LIC_FILES_CHKSUM') or ""
    pn = d.getVar('PN')
    # The license files are located in S/LIC_FILE_CHECKSUM.
    srcdir = d.getVar('S')
    # Directory we store the generic licenses as set in the distro configuration
    generic_directory = d.getVar('COMMON_LICENSE_DIR')
    # List of basename, path tuples
    lic_files_paths = []
    # hash for keep track generic lics mappings
    non_generic_lics = {}
    # Entries from LIC_FILES_CHKSUM
    lic_chksums = {}
    license_source_dirs = []
    license_source_dirs.append(generic_directory)
    try:
        additional_lic_dirs = d.getVar('LICENSE_PATH').split()
        for lic_dir in additional_lic_dirs:
            license_source_dirs.append(lic_dir)
    except:
        pass

    class FindVisitor(oe.license.LicenseVisitor):
        def visit_Str(self, node):
            #
            # Until I figure out what to do with
            # the two modifiers I support (or greater = +
            # and "with exceptions" being *
            # we'll just strip out the modifier and put
            # the base license.
            find_license(node.s.replace("+", "").replace("*", ""))
            self.generic_visit(node)

    def find_license(license_type):
        try:
            bb.utils.mkdirhier(gen_lic_dest)
        except:
            pass
        spdx_generic = None
        license_source = None
        # If the generic does not exist we need to check to see if there is an SPDX mapping to it,
        # unless NO_GENERIC_LICENSE is set.
        for lic_dir in license_source_dirs:
            if not os.path.isfile(os.path.join(lic_dir, license_type)):
                if d.getVarFlag('SPDXLICENSEMAP', license_type) != None:
                    # Great, there is an SPDXLICENSEMAP. We can copy!
                    bb.debug(1, "We need to use a SPDXLICENSEMAP for %s" % (license_type))
                    spdx_generic = d.getVarFlag('SPDXLICENSEMAP', license_type)
                    license_source = lic_dir
                    break
            elif os.path.isfile(os.path.join(lic_dir, license_type)):
                spdx_generic = license_type
                license_source = lic_dir
                break

        non_generic_lic = d.getVarFlag('NO_GENERIC_LICENSE', license_type)
        if spdx_generic and license_source:
            # we really should copy to generic_ + spdx_generic, however, that ends up messing the manifest
            # audit up. This should be fixed in emit_pkgdata (or, we actually got and fix all the recipes)

            lic_files_paths.append(("generic_" + license_type, os.path.join(license_source, spdx_generic),
                                    None, None))

            # The user may attempt to use NO_GENERIC_LICENSE for a generic license which doesn't make sense
            # and should not be allowed, warn the user in this case.
            if d.getVarFlag('NO_GENERIC_LICENSE', license_type):
                bb.warn("%s: %s is a generic license, please don't use NO_GENERIC_LICENSE for it." % (pn, license_type))

        elif non_generic_lic and non_generic_lic in lic_chksums:
            # if NO_GENERIC_LICENSE is set, we copy the license files from the fetched source
            # of the package rather than the license_source_dirs.
            lic_files_paths.append(("generic_" + license_type,
                                    os.path.join(srcdir, non_generic_lic), None, None))
            non_generic_lics[non_generic_lic] = license_type
        else:
            # Add explicity avoid of CLOSED license because this isn't generic
            if license_type != 'CLOSED':
                # And here is where we warn people that their licenses are lousy
                bb.warn("%s: No generic license file exists for: %s in any provider" % (pn, license_type))
            pass

    if not generic_directory:
        bb.fatal("COMMON_LICENSE_DIR is unset. Please set this in your distro config")

    for url in lic_files.split():
        try:
            (type, host, path, user, pswd, parm) = bb.fetch.decodeurl(url)
        except bb.fetch.MalformedUrl:
            bb.fatal("%s: LIC_FILES_CHKSUM contains an invalid URL:  %s" % (d.getVar('PF'), url))
        # We want the license filename and path
        chksum = parm.get('md5', None)
        beginline = parm.get('beginline')
        endline = parm.get('endline')
        lic_chksums[path] = (chksum, beginline, endline)

    v = FindVisitor()
    try:
        v.visit_string(d.getVar('LICENSE'))
    except oe.license.InvalidLicense as exc:
        bb.fatal('%s: %s' % (d.getVar('PF'), exc))
    except SyntaxError:
        bb.warn("%s: Failed to parse it's LICENSE field." % (d.getVar('PF')))
    # Add files from LIC_FILES_CHKSUM to list of license files
    lic_chksum_paths = defaultdict(OrderedDict)
    for path, data in sorted(lic_chksums.items()):
        lic_chksum_paths[os.path.basename(path)][data] = (os.path.join(srcdir, path), data[1], data[2])
    for basename, files in lic_chksum_paths.items():
        if len(files) == 1:
            # Don't copy again a LICENSE already handled as non-generic
            if basename in non_generic_lics:
                continue
            data = list(files.values())[0]
            lic_files_paths.append(tuple([basename] + list(data)))
        else:
            # If there are multiple different license files with identical
            # basenames we rename them to <file>.0, <file>.1, ...
            for i, data in enumerate(files.values()):
                lic_files_paths.append(tuple(["%s.%d" % (basename, i)] + list(data)))

    return lic_files_paths

def return_spdx(d, license):
    """
    This function returns the spdx mapping of a license if it exists.
     """
    return d.getVarFlag('SPDXLICENSEMAP', license)

def canonical_license(d, license):
    """
    Return the canonical (SPDX) form of the license if available (so GPLv3
    becomes GPL-3.0), for the license named 'X+', return canonical form of
    'X' if availabel and the tailing '+' (so GPLv3+ becomes GPL-3.0+), 
    or the passed license if there is no canonical form.
    """
    lic = d.getVarFlag('SPDXLICENSEMAP', license) or ""
    if not lic and license.endswith('+'):
        lic = d.getVarFlag('SPDXLICENSEMAP', license.rstrip('+'))
        if lic:
            lic += '+'
    return lic or license

def expand_wildcard_licenses(d, wildcard_licenses):
    """
    Return actual spdx format license names if wildcard used. We expand
    wildcards from SPDXLICENSEMAP flags and SRC_DISTRIBUTE_LICENSES values.
    """
    import fnmatch
    licenses = []
    spdxmapkeys = d.getVarFlags('SPDXLICENSEMAP').keys()
    for wld_lic in wildcard_licenses:
        spdxflags = fnmatch.filter(spdxmapkeys, wld_lic)
        licenses += [d.getVarFlag('SPDXLICENSEMAP', flag) for flag in spdxflags]

    spdx_lics = (d.getVar('SRC_DISTRIBUTE_LICENSES', False) or '').split()
    for wld_lic in wildcard_licenses:
        licenses += fnmatch.filter(spdx_lics, wld_lic)

    licenses = list(set(licenses))
    return licenses

def incompatible_license_contains(license, truevalue, falsevalue, d):
    license = canonical_license(d, license)
    bad_licenses = (d.getVar('INCOMPATIBLE_LICENSE') or "").split()
    bad_licenses = expand_wildcard_licenses(d, bad_licenses)
    return truevalue if license in bad_licenses else falsevalue

def incompatible_license(d, dont_want_licenses, package=None):
    """
    This function checks if a recipe has only incompatible licenses. It also
    take into consideration 'or' operand.  dont_want_licenses should be passed
    as canonical (SPDX) names.
    """
    import oe.license
    license = d.getVar("LICENSE_%s" % package) if package else None
    if not license:
        license = d.getVar('LICENSE')

    # Handles an "or" or two license sets provided by
    # flattened_licenses(), pick one that works if possible.
    def choose_lic_set(a, b):
        return a if all(oe.license.license_ok(canonical_license(d, lic), 
                            dont_want_licenses) for lic in a) else b

    try:
        licenses = oe.license.flattened_licenses(license, choose_lic_set)
    except oe.license.LicenseError as exc:
        bb.fatal('%s: %s' % (d.getVar('P'), exc))
    return any(not oe.license.license_ok(canonical_license(d, l), \
		dont_want_licenses) for l in licenses)

def check_license_flags(d):
    """
    This function checks if a recipe has any LICENSE_FLAGS that
    aren't whitelisted.

    If it does, it returns the first LICENSE_FLAGS item missing from the
    whitelist, or all of the LICENSE_FLAGS if there is no whitelist.

    If everything is is properly whitelisted, it returns None.
    """

    def license_flag_matches(flag, whitelist, pn):
        """
        Return True if flag matches something in whitelist, None if not.

        Before we test a flag against the whitelist, we append _${PN}
        to it.  We then try to match that string against the
        whitelist.  This covers the normal case, where we expect
        LICENSE_FLAGS to be a simple string like 'commercial', which
        the user typically matches exactly in the whitelist by
        explicitly appending the package name e.g 'commercial_foo'.
        If we fail the match however, we then split the flag across
        '_' and append each fragment and test until we either match or
        run out of fragments.
        """
        flag_pn = ("%s_%s" % (flag, pn))
        for candidate in whitelist:
            if flag_pn == candidate:
                    return True

        flag_cur = ""
        flagments = flag_pn.split("_")
        flagments.pop() # we've already tested the full string
        for flagment in flagments:
            if flag_cur:
                flag_cur += "_"
            flag_cur += flagment
            for candidate in whitelist:
                if flag_cur == candidate:
                    return True
        return False

    def all_license_flags_match(license_flags, whitelist):
        """ Return first unmatched flag, None if all flags match """
        pn = d.getVar('PN')
        split_whitelist = whitelist.split()
        for flag in license_flags.split():
            if not license_flag_matches(flag, split_whitelist, pn):
                return flag
        return None

    license_flags = d.getVar('LICENSE_FLAGS')
    if license_flags:
        whitelist = d.getVar('LICENSE_FLAGS_WHITELIST')
        if not whitelist:
            return license_flags
        unmatched_flag = all_license_flags_match(license_flags, whitelist)
        if unmatched_flag:
            return unmatched_flag
    return None

def check_license_format(d):
    """
    This function checks if LICENSE is well defined,
        Validate operators in LICENSES.
        No spaces are allowed between LICENSES.
    """
    pn = d.getVar('PN')
    licenses = d.getVar('LICENSE')
    from oe.license import license_operator, license_operator_chars, license_pattern

    elements = list(filter(lambda x: x.strip(), license_operator.split(licenses)))
    for pos, element in enumerate(elements):
        if license_pattern.match(element):
            if pos > 0 and license_pattern.match(elements[pos - 1]):
                bb.warn('%s: LICENSE value "%s" has an invalid format - license names ' \
                        'must be separated by the following characters to indicate ' \
                        'the license selection: %s' %
                        (pn, licenses, license_operator_chars))
        elif not license_operator.match(element):
            bb.warn('%s: LICENSE value "%s" has an invalid separator "%s" that is not ' \
                    'in the valid list of separators (%s)' %
                    (pn, licenses, element, license_operator_chars))

SSTATETASKS += "do_populate_lic"
do_populate_lic[sstate-inputdirs] = "${LICSSTATEDIR}"
do_populate_lic[sstate-outputdirs] = "${LICENSE_DIRECTORY}/"

ROOTFS_POSTPROCESS_COMMAND_prepend = "write_package_manifest; license_create_manifest; "
do_rootfs[recrdeptask] += "do_populate_lic"

IMAGE_POSTPROCESS_COMMAND_prepend = "write_deploy_manifest; "
do_image[recrdeptask] += "do_populate_lic"

python do_populate_lic_setscene () {
    sstate_setscene(d)
}
addtask do_populate_lic_setscene
