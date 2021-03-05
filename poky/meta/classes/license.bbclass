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

PSEUDO_IGNORE_PATHS .= ",${@','.join(((d.getVar('COMMON_LICENSE_DIR') or '') + ' ' + (d.getVar('LICENSE_PATH') or '')).split())}"
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
    if pn_lic in packages.split():
        bb.warn("%s package already existed in %s." % (pn_lic, pn))
    else:
        # first in PACKAGES to be sure that nothing else gets LICENSE_FILES_DIRECTORY
        d.setVar('PACKAGES', "%s %s" % (pn_lic, packages))
        d.setVar('FILES_' + pn_lic, files)
    for pn in packages.split():
        if pn == pn_lic:
            continue
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
    becomes GPL-3.0) or the passed license if there is no canonical form.
    """
    return d.getVarFlag('SPDXLICENSEMAP', license) or license

def available_licenses(d):
    """
    Return the available licenses by searching the directories specified by
    COMMON_LICENSE_DIR and LICENSE_PATH.
    """
    lic_dirs = ((d.getVar('COMMON_LICENSE_DIR') or '') + ' ' +
                (d.getVar('LICENSE_PATH') or '')).split()

    licenses = []
    for lic_dir in lic_dirs:
        licenses += os.listdir(lic_dir)

    licenses = sorted(licenses)
    return licenses

# Only determine the list of all available licenses once. This assumes that any
# additions to LICENSE_PATH have been done before this file is parsed.
AVAILABLE_LICENSES := "${@' '.join(available_licenses(d))}"

def expand_wildcard_licenses(d, wildcard_licenses):
    """
    Return actual spdx format license names if wildcards are used. We expand
    wildcards from SPDXLICENSEMAP flags and AVAILABLE_LICENSES.
    """
    import fnmatch

    # Assume if we're passed "GPLv3" or "*GPLv3" it means -or-later as well
    for lic in wildcard_licenses[:]:
        if not lic.endswith(("-or-later", "-only", "*")):
            wildcard_licenses.append(lic + "+")

    licenses = wildcard_licenses[:]
    spdxmapkeys = d.getVarFlags('SPDXLICENSEMAP').keys()
    for wld_lic in wildcard_licenses:
        spdxflags = fnmatch.filter(spdxmapkeys, wld_lic)
        licenses += [d.getVarFlag('SPDXLICENSEMAP', flag) for flag in spdxflags]

    spdx_lics = d.getVar('AVAILABLE_LICENSES').split()
    for wld_lic in wildcard_licenses:
        licenses += fnmatch.filter(spdx_lics, wld_lic)

    licenses = list(set(licenses))
    return licenses

def incompatible_license_contains(license, truevalue, falsevalue, d):
    license = canonical_license(d, license)
    bad_licenses = (d.getVar('INCOMPATIBLE_LICENSE') or "").split()
    bad_licenses = expand_wildcard_licenses(d, bad_licenses)
    return truevalue if license in bad_licenses else falsevalue

def incompatible_pkg_license(d, dont_want_licenses, license):
    # Handles an "or" or two license sets provided by
    # flattened_licenses(), pick one that works if possible.
    def choose_lic_set(a, b):
        return a if all(oe.license.license_ok(canonical_license(d, lic),
                            dont_want_licenses) for lic in a) else b

    try:
        licenses = oe.license.flattened_licenses(license, choose_lic_set)
    except oe.license.LicenseError as exc:
        bb.fatal('%s: %s' % (d.getVar('P'), exc))

    incompatible_lic = []
    for l in licenses:
        license = canonical_license(d, l)
        if not oe.license.license_ok(license, dont_want_licenses):
            incompatible_lic.append(license)

    return sorted(incompatible_lic)

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

    return incompatible_pkg_license(d, dont_want_licenses, license)

def check_license_flags(d):
    """
    This function checks if a recipe has any LICENSE_FLAGS that
    aren't whitelisted.

    If it does, it returns the all LICENSE_FLAGS missing from the whitelist, or
    all of the LICENSE_FLAGS if there is no whitelist.

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
        """ Return all unmatched flags, None if all flags match """
        pn = d.getVar('PN')
        split_whitelist = whitelist.split()
        flags = []
        for flag in license_flags.split():
            if not license_flag_matches(flag, split_whitelist, pn):
                flags.append(flag)
        return flags if flags else None

    license_flags = d.getVar('LICENSE_FLAGS')
    if license_flags:
        whitelist = d.getVar('LICENSE_FLAGS_WHITELIST')
        if not whitelist:
            return license_flags.split()
        unmatched_flags = all_license_flags_match(license_flags, whitelist)
        if unmatched_flags:
            return unmatched_flags
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

IMAGE_CLASSES_append = " license_image"

python do_populate_lic_setscene () {
    sstate_setscene(d)
}
addtask do_populate_lic_setscene
