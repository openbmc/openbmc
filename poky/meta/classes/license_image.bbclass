ROOTFS_LICENSE_DIR = "${IMAGE_ROOTFS}/usr/share/common-licenses"

# This requires LICENSE_CREATE_PACKAGE=1 to work too
COMPLEMENTARY_GLOB[lic-pkgs] = "*-lic"

python() {
    if not oe.data.typed_value('LICENSE_CREATE_PACKAGE', d):
        features = set(oe.data.typed_value('IMAGE_FEATURES', d))
        if 'lic-pkgs' in features:
            bb.error("'lic-pkgs' in IMAGE_FEATURES but LICENSE_CREATE_PACKAGE not enabled to generate -lic packages")
}

python write_package_manifest() {
    # Get list of installed packages
    license_image_dir = d.expand('${LICENSE_DIRECTORY}/${IMAGE_NAME}')
    bb.utils.mkdirhier(license_image_dir)
    from oe.rootfs import image_list_installed_packages
    from oe.utils import format_pkg_list

    pkgs = image_list_installed_packages(d)
    output = format_pkg_list(pkgs)
    with open(os.path.join(license_image_dir, 'package.manifest'), "w+") as package_manifest:
        package_manifest.write(output)
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
            pkg_lic_name = "LICENSE:" + pkg_name
            pkg_dic[pkg_name]["LICENSE"] = pkg_dic[pkg_name][pkg_lic_name]

    rootfs_license_manifest = os.path.join(d.getVar('LICENSE_DIRECTORY'),
                        d.getVar('IMAGE_NAME'), 'license.manifest')
    write_license_files(d, rootfs_license_manifest, pkg_dic, rootfs=True)
}

def write_license_files(d, license_manifest, pkg_dic, rootfs=True):
    import re
    import stat

    bad_licenses = (d.getVar("INCOMPATIBLE_LICENSE") or "").split()
    bad_licenses = expand_wildcard_licenses(d, bad_licenses)

    exceptions = (d.getVar("INCOMPATIBLE_LICENSE_EXCEPTIONS") or "").split()
    with open(license_manifest, "w") as license_file:
        for pkg in sorted(pkg_dic):
            remaining_bad_licenses = oe.license.apply_pkg_license_exception(pkg, bad_licenses, exceptions)
            incompatible_licenses = incompatible_pkg_license(d, remaining_bad_licenses, pkg_dic[pkg]["LICENSE"])
            if incompatible_licenses:
                bb.fatal("Package %s cannot be installed into the image because it has incompatible license(s): %s" %(pkg, ' '.join(incompatible_licenses)))
            else:
                incompatible_licenses = incompatible_pkg_license(d, bad_licenses, pkg_dic[pkg]["LICENSE"])
                if incompatible_licenses:
                    oe.qa.handle_error('license-incompatible', "Including %s with incompatible license(s) %s into the image, because it has been allowed by exception list." %(pkg, ' '.join(incompatible_licenses)), d)
            try:
                (pkg_dic[pkg]["LICENSE"], pkg_dic[pkg]["LICENSES"]) = \
                    oe.license.manifest_licenses(pkg_dic[pkg]["LICENSE"],
                    remaining_bad_licenses, canonical_license, d)
            except oe.license.LicenseError as exc:
                bb.fatal('%s: %s' % (d.getVar('P'), exc))

            if not "IMAGE_MANIFEST" in pkg_dic[pkg]:
                # Rootfs manifest
                license_file.write("PACKAGE NAME: %s\n" % pkg)
                license_file.write("PACKAGE VERSION: %s\n" % pkg_dic[pkg]["PV"])
                license_file.write("RECIPE NAME: %s\n" % pkg_dic[pkg]["PN"])
                license_file.write("LICENSE: %s\n\n" % pkg_dic[pkg]["LICENSE"])

                # If the package doesn't contain any file, that is, its size is 0, the license
                # isn't relevant as far as the final image is concerned. So doing license check
                # doesn't make much sense, skip it.
                if pkg_dic[pkg]["PKGSIZE:%s" % pkg] == "0":
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
                    oe.qa.handle_error('license-file-missing',
                                       "The license listed %s was not in the "\
                                       "licenses collected for recipe %s"
                                       % (lic, pkg_dic[pkg]["PN"]), d)
    oe.qa.exit_if_errors(d)

    # Two options here:
    # - Just copy the manifest
    # - Copy the manifest and the license directories
    # With both options set we see a .5 M increase in core-image-minimal
    copy_lic_manifest = d.getVar('COPY_LIC_MANIFEST')
    copy_lic_dirs = d.getVar('COPY_LIC_DIRS')
    if rootfs and copy_lic_manifest == "1":
        rootfs_license_dir = d.getVar('ROOTFS_LICENSE_DIR')
        bb.utils.mkdirhier(rootfs_license_dir)
        rootfs_license_manifest = os.path.join(rootfs_license_dir,
                os.path.split(license_manifest)[1])
        if not os.path.exists(rootfs_license_manifest):
            oe.path.copyhardlink(license_manifest, rootfs_license_manifest)

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

                        # Make sure we use only canonical name for the license file
                        generic_lic_file = "generic_%s" % generic_lic
                        rootfs_license = os.path.join(rootfs_license_dir, generic_lic_file)
                        if not os.path.exists(rootfs_license):
                            oe.path.copyhardlink(pkg_license, rootfs_license)

                        if not os.path.exists(pkg_rootfs_license):
                            os.symlink(os.path.join('..', generic_lic_file), pkg_rootfs_license)
                    else:
                        if (oe.license.license_ok(canonical_license(d,
                                lic), bad_licenses) == False or
                                os.path.exists(pkg_rootfs_license)):
                            continue

                        oe.path.copyhardlink(pkg_license, pkg_rootfs_license)
            # Fixup file ownership and permissions
            for walkroot, dirs, files in os.walk(rootfs_license_dir):
                for f in files:
                    p = os.path.join(walkroot, f)
                    os.lchown(p, 0, 0)
                    if not os.path.islink(p):
                        os.chmod(p, stat.S_IRUSR | stat.S_IWUSR | stat.S_IRGRP | stat.S_IROTH)
                for dir in dirs:
                    p = os.path.join(walkroot, dir)
                    os.lchown(p, 0, 0)
                    os.chmod(p, stat.S_IRWXU | stat.S_IRGRP | stat.S_IXGRP | stat.S_IROTH | stat.S_IXOTH)



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
    write_license_files(d, image_license_manifest, man_dic, rootfs=False)

    link_name = d.getVar('IMAGE_LINK_NAME')
    if link_name:
        lic_manifest_symlink_dir = os.path.join(d.getVar('LICENSE_DIRECTORY'),
                                    link_name)
        # remove old symlink
        if os.path.islink(lic_manifest_symlink_dir):
            os.unlink(lic_manifest_symlink_dir)

        # create the image dir symlink
        if lic_manifest_dir != lic_manifest_symlink_dir:
            os.symlink(lic_manifest_dir, lic_manifest_symlink_dir)

def get_deployed_dependencies(d):
    """
    Get all the deployed dependencies of an image
    """

    deploy = {}
    # Get all the dependencies for the current task (rootfs).
    taskdata = d.getVar("BB_TASKDEPDATA", False)
    pn = d.getVar("PN")
    depends = list(set([dep[0] for dep
                    in list(taskdata.values())
                    if not dep[0].endswith("-native") and not dep[0] == pn]))

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

ROOTFS_POSTPROCESS_COMMAND:prepend = "write_package_manifest; license_create_manifest; "
do_rootfs[recrdeptask] += "do_populate_lic"

python do_populate_lic_deploy() {
    license_deployed_manifest(d)
    oe.qa.exit_if_errors(d)
}

addtask populate_lic_deploy before do_build after do_image_complete
do_populate_lic_deploy[recrdeptask] += "do_populate_lic do_deploy"

python license_qa_dead_symlink() {
    import os

    for root, dirs, files in os.walk(d.getVar('ROOTFS_LICENSE_DIR')):
        for file in files:
            full_path = root + "/" + file
            if os.path.islink(full_path) and not os.path.exists(full_path):
                bb.error("broken symlink: " + full_path)
}
IMAGE_QA_COMMANDS += "license_qa_dead_symlink"
