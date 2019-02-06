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
                pkg_dic[pkg]["LICENSES"] = re.sub(r'[|&()*]', ' ', pkg_dic[pkg]["LICENSE"])
                pkg_dic[pkg]["LICENSES"] = re.sub(r'  *', ' ', pkg_dic[pkg]["LICENSES"])
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
                                        re.sub(r'\+', '', lic))
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

                    if re.match(r"^generic_.*$", lic):
                        generic_lic = canonical_license(d,
                                re.search(r"^generic_(.*)$", lic).group(1))

                        # Do not copy generic license into package if isn't
                        # declared into LICENSES of the package.
                        if not re.sub(r'\+$', '', generic_lic) in \
                                [re.sub(r'\+', '', lic) for lic in \
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

    # To verify what was deployed it checks the rootfs dependencies against
    # the SSTATE_MANIFESTS for "deploy" task.
    # The manifest file name contains the arch. Because we are not running
    # in the recipe context it is necessary to check every arch used.
    sstate_manifest_dir = d.getVar("SSTATE_MANIFESTS")
    archs = list(set(d.getVar("SSTATE_ARCHS").split()))
    for dep in depends:
        for arch in archs:
            sstate_manifest_file = os.path.join(sstate_manifest_dir,
                    "manifest-%s-%s.deploy" % (arch, dep))
            if os.path.exists(sstate_manifest_file):
                deploy[dep] = sstate_manifest_file
                break

    return deploy
get_deployed_dependencies[vardepsexclude] = "BB_TASKDEPDATA"

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

ROOTFS_POSTPROCESS_COMMAND_prepend = "write_package_manifest; license_create_manifest; "
do_rootfs[recrdeptask] += "do_populate_lic"

python do_populate_lic_deploy() {
    license_deployed_manifest(d)
}

addtask populate_lic_deploy before do_build after do_image_complete
do_populate_lic_deploy[recrdeptask] += "do_populate_lic do_deploy"

