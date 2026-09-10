#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Populates LICENSE_DIRECTORY as set in distro config with the license files as set by
# LIC_FILES_CHKSUM.
# TODO:
# - There is a real issue revolving around license naming standards.

LICENSE_DIRECTORY ??= "${DEPLOY_DIR}/licenses"
LICSSTATEDIR = "${WORKDIR}/license-destdir/"

# Create extra package with license texts and add it to RRECOMMENDS:${PN}
LICENSE_CREATE_PACKAGE[type] = "boolean"
LICENSE_CREATE_PACKAGE ??= "0"
LICENSE_PACKAGE_SUFFIX ??= "-lic"
LICENSE_FILES_DIRECTORY ??= "${datadir}/licenses/"

addtask populate_lic after do_patch before do_build
do_populate_lic[dirs] = "${LICSSTATEDIR}/${PN}"
do_populate_lic[cleandirs] = "${LICSSTATEDIR}"

python do_populate_lic() {
    """
    Populate LICENSE_DIRECTORY with licenses.
    """
    lic_files_paths = find_license_files(d)

    # The base directory we wrangle licenses to
    destdir = os.path.join(d.getVar('LICSSTATEDIR'),  d.getVar('PN'))
    copy_license_files(lic_files_paths, destdir)
    info = get_recipe_info(d)
    with open(os.path.join(destdir, "recipeinfo"), "w") as f:
        for key in sorted(info.keys()):
            f.write("%s: %s\n" % (key, info[key]))
    oe.qa.exit_if_errors(d)
}

# it would be better to copy them in do_install:append, but find_license_files is python
python perform_packagecopy:prepend () {
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
    import oe.license

    info = {}
    info["PV"] = d.getVar("PV")
    info["PR"] = d.getVar("PR")
    info["LICENSE"] = oe.license.convert_legacy_license_to_spdx(d, d.getVar("LICENSE"))
    return info

def add_package_and_files(d):
    packages = d.getVar('PACKAGES')
    files = d.getVar('LICENSE_FILES_DIRECTORY')
    pn = d.getVar('PN')
    pn_lic = "%s%s" % (pn, d.getVar('LICENSE_PACKAGE_SUFFIX', False))
    if pn_lic in packages.split():
        bb.warn("%s package already existed in %s." % (pn_lic, pn))
    else:
        # first in PACKAGES to be sure that nothing else gets LICENSE_FILES_DIRECTORY
        d.setVar('PACKAGES', "%s %s" % (pn_lic, packages))
        d.setVar('FILES:' + pn_lic, files)

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
                        # Copy license files if hardlink is not possible even if st_dev is the
                        # same on source and destination (docker container with device-mapper?)
                        canlink = False
                    else:
                        raise
                # Only chown if we did hardlink and we're running under pseudo
                if canlink and os.environ.get('PSEUDO_DISABLED') == '0':
                    os.chown(dst,0,0)
            if not canlink:
                begin_idx = max(0, int(beginline) - 1) if beginline is not None else None
                end_idx = max(0, int(endline)) if endline is not None else None
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
    import oe.spdx_license
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
    non_generic_lics = set()
    # Entries from LIC_FILES_CHKSUM
    lic_chksums = {}

    if not generic_directory:
        bb.fatal("COMMON_LICENSE_DIR is unset. Please set this in your distro config")

    try:
        bb.utils.mkdirhier(gen_lic_dest)
    except:
        pass

    for url in lic_files.split():
        try:
            (method, host, path, user, pswd, parm) = bb.fetch.decodeurl(url)
            if method != "file" or not path:
                raise bb.fetch.MalformedUrl()
        except bb.fetch.MalformedUrl:
            bb.fatal("%s: LIC_FILES_CHKSUM contains an invalid URL:  %s" % (d.getVar('PF'), url))
        # We want the license filename and path
        chksum = parm.get('md5', None)
        beginline = parm.get('beginline')
        endline = parm.get('endline')
        lic_chksums[path] = (chksum, beginline, endline)

    def walk_license(node):
        if isinstance(node, oe.spdx_license.Identifier):
            p, non_generic_fn = oe.license.get_license_path(d, node)
            if p is not None and (not non_generic_fn or non_generic_fn in lic_chksums):
                lic_files_paths.append(("generic_" + node.ident, p, None, None))
                if non_generic_fn:
                    non_generic_lics.add(non_generic_fn)
            else:
                oe.qa.handle_error("license-exists",
                    "%s: No generic license file exists for: %s in any provider" % (pn, node.ident), d)

        for child in node.children:
            walk_license(child)

    try:
        licensestr = d.getVar("LICENSE")
        if licensestr and licensestr != "CLOSED":
            node = oe.license.parse_legacy_license(d, licensestr)
            walk_license(node)
    except oe.spdx_license.ParseError as e:
        oe.qa.handle_error("license-syntax", e.format(prefix=f"{d.getVar('PF')}: "), d)

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

SSTATETASKS += "do_populate_lic"
do_populate_lic[sstate-inputdirs] = "${LICSSTATEDIR}"
do_populate_lic[sstate-outputdirs] = "${LICENSE_DIRECTORY}/${SSTATE_PKGARCH}/"

IMAGE_CLASSES:append = " license_image"

python do_populate_lic_setscene () {
    sstate_setscene(d)
}
addtask do_populate_lic_setscene
