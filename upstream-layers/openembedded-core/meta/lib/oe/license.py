#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#
"""Code for parsing OpenEmbedded license strings"""

import ast
import os
import re
import oe.qa
from fnmatch import fnmatchcase as fnmatch
import oe.spdx_license

def license_ok(license, dont_want_licenses):
    """ Return False if License exist in dont_want_licenses else True """
    for dwl in dont_want_licenses:
        if fnmatch(license, dwl):
            return False
    return True

def check_license_list(node, lst):
    """
    Checks a license node against a list if license identifiers. Returns True
    if the node is in the list, and False if not
    """
    result = []
    for i in lst:
        if lic := oe.spdx_license.get_license(i):
            if isinstance(node, oe.spdx_license.Identifier) and node.ident == lic["licenseId"]:
                return True

            if isinstance(node, oe.spdx_license.WithOp) and node.license.ident == lic["licenseId"]:
                return True

        elif lic := oe.spdx_license.get_exception(i):
            if isinstance(node, oe.spdx_license.WithOp) and node.exception.ident == lic["licenseExceptionId"]:
                return True

        else:
            if not i.startswith("LicenseRef-"):
                # For legacy license matching
                lic = "LicenseRef-" + i
            else:
                lic = i

            if isinstance(node, oe.spdx_license.Identifier) and node.ident == lic:
                return True

    return False

def license_allowed(node, disallowed, allowed):
    """
    Filters an SPDX license expression abstract syntax tree based on a list of
    allowed and disallowed licenses, search through AND and OR conjunctions to
    check for any path that allows the license to be allowed. If an individual
    license is in the allowed list, or not in the disallowed list, it is
    allowed. If the expression is not allowed, returns a list of nodes from the
    expression for the licenses that caused the license to not be
    allowed. If the expression is allowed, an empty list is returned.
    """
    def walk(n):
        if isinstance(n, oe.spdx_license.AndOp):
            return walk(n.left) + walk(n.right)

        if isinstance(n, oe.spdx_license.OrOp):
            lst = walk(n.left)
            if not lst:
                return []
            return walk(n.right)

        if isinstance(n, oe.spdx_license.CompoundExpression):
            return walk(n.child)

        if isinstance(n, (oe.spdx_license.WithOp, oe.spdx_license.Identifier)):
            if check_license_list(n, allowed):
                return []
            if check_license_list(n, disallowed):
                return [n]
            return []

        raise Exception(f"Unknown node type {n.__class__.__name__}")

    return walk(node)


def obsolete_license_list():
    return ["AGPL-3", "AGPL-3+", "AGPLv3", "AGPLv3+", "AGPLv3.0", "AGPLv3.0+", "AGPL-3.0", "AGPL-3.0+", "BSD-0-Clause",
            "GPL-1", "GPL-1+", "GPLv1", "GPLv1+", "GPLv1.0", "GPLv1.0+", "GPL-1.0", "GPL-1.0+", "GPL-2", "GPL-2+", "GPLv2",
            "GPLv2+", "GPLv2.0", "GPLv2.0+", "GPL-2.0", "GPL-2.0+", "GPL-3", "GPL-3+", "GPLv3", "GPLv3+", "GPLv3.0", "GPLv3.0+",
            "GPL-3.0", "GPL-3.0+", "LGPLv2", "LGPLv2+", "LGPLv2.0", "LGPLv2.0+", "LGPL-2.0", "LGPL-2.0+", "LGPL2.1", "LGPL2.1+",
            "LGPLv2.1", "LGPLv2.1+", "LGPL-2.1", "LGPL-2.1+", "LGPLv3", "LGPLv3+", "LGPL-3.0", "LGPL-3.0+", "MPL-1", "MPLv1",
            "MPLv1.1", "MPLv2", "MIT-X", "MIT-style", "openssl", "PSF", "PSFv2", "Python-2", "Apachev2", "Apache-2", "Artisticv1",
            "Artistic-1", "AFL-2", "AFL-1", "AFLv2", "AFLv1", "CDDLv1", "CDDL-1", "EPLv1.0", "FreeType", "Nauman",
            "tcl", "vim", "SGIv1"]


LicenseError = oe.spdx_license.ParseError

def flattened_licenses(d, licensestr, choose_licenses):
    """Given a license string and choose_licenses function, return a flat list of licenses"""
    def walk_license(node):
        if isinstance(node, oe.spdx_license.Identifier):
            return [node.ident]

        if isinstance(node, oe.spdx_license.OrOp):
            left = walk_license(node.left)
            right = walk_license(node.right)

            return choose_licenses(left, right)

        ret = []
        for child in node.children:
            ret.extend(walk_license(child))

        return ret

    n = parse_legacy_license(d, licensestr)
    if n is None:
        return []

    return walk_license(n)

def is_included(d, licensestr, include_licenses=None, exclude_licenses=None):
    """Given a license string, a list of licenses to include and a list of
    licenses to exclude, determine if the license string matches the include
    list and does not match the exclude list.

    Returns a tuple holding the boolean state and a list of the applicable
    licenses that were excluded if state is False, or the licenses that were
    included if the state is True."""

    def include_license(license):
        return any(fnmatch(license, pattern) for pattern in include_licenses)

    def exclude_license(license):
        return any(fnmatch(license, pattern) for pattern in exclude_licenses)

    def choose_licenses(alpha, beta):
        """Select the option in an OR which is the 'best' (has the most
        included licenses and no excluded licenses)."""
        # The factor 1000 below is arbitrary, just expected to be much larger
        # than the number of licenses actually specified. That way the weight
        # will be negative if the list of licenses contains an excluded license,
        # but still gives a higher weight to the list with the most included
        # licenses.
        alpha_weight = (len(list(filter(include_license, alpha))) -
                        1000 * (len(list(filter(exclude_license, alpha))) > 0))
        beta_weight = (len(list(filter(include_license, beta))) -
                       1000 * (len(list(filter(exclude_license, beta))) > 0))
        if alpha_weight >= beta_weight:
            return alpha
        else:
            return beta

    if not include_licenses:
        include_licenses = ['*']

    if not exclude_licenses:
        exclude_licenses = []

    licenses = flattened_licenses(d, licensestr, choose_licenses)
    excluded = [lic for lic in licenses if exclude_license(lic)]
    included = [lic for lic in licenses if include_license(lic)]
    if excluded:
        return False, excluded
    else:
        return True, included

def manifest_licenses(d, licensestr, disallowed, allowed):
    """Given a license string and dont_want_licenses list,
       return license string filtered and a list of licenses"""

    node = parse_legacy_license(d, licensestr)
    licenses = []

    def walk_license(node):
        nonlocal licenses
        if isinstance(node, oe.spdx_license.Identifier):
            if not license_allowed(node, disallowed, allowed):
                licenses.append(node.to_string())
                return node
            return None

        if isinstance(node, oe.spdx_license.WithOp):
            if not license_allowed(node, disallowed, allowed):
                licenses.append(node.license.to_string())
                licenses.append(node.exception.to_string())
                return node
            return None

        old_children = node.children

        node.children = []

        for child in old_children:
            node.children.append(walk_license(child))

        # If all children are removed, remove this node also
        if not any(node.children):
            return None

        # Elide a binary operation if it now only has one child
        if isinstance(node, (oe.spdx_license.AndOp, oe.spdx_license.OrOp)):
            if node.left is None:
                return node.right
            if node.right is None:
                return node.left

        # If one of the above cases doesn't catch everything, raise an error
        if not all(node.children):
            node.children = old_children
            pn = d.getVar("PN")
            bb.fatal(f"{pn}: Removing licenses from {node.to_string} results in an invalid license expression")

        return node

    if node:
        node = walk_license(node)

    if not node:
        return ("", licenses)

    return (node.to_string(), licenses)

def list_licenses(licensestr, d):
    """Simply get a set of all licenses mentioned in a license string.
       Binary operators are not applied or taken into account in any way"""
    licenses = []

    def walk_license(node):
        nonlocal licenses
        if isinstance(node, oe.spdx_license.Identifier):
            licenses.append(node.ident)

        for child in node.children:
            walk_license(child)

    node = parse_legacy_license(d, licensestr)
    if node:
        walk_license(node)
    return set(licenses)

def return_spdx(d, license):
    """
    This function returns the spdx mapping of a license if it exists.
     """
    return d.getVarFlag('SPDXLICENSEMAP', license)

def canonical_license(d, license):
    """
    Return the canonical (SPDX) form of the license if available (so GPLv3
    becomes GPL-3.0-only) or the passed license if there is no canonical form.
    """
    return d.getVarFlag('SPDXLICENSEMAP', license) or license

def expand_wildcard_licenses(d, wildcard_licenses):
    """
    There are some common wildcard values users may want to use. Support them
    here.
    """
    licenses = set(wildcard_licenses)
    mapping = {
        "AGPL-3.0*" : ["AGPL-3.0-only", "AGPL-3.0-or-later"],
        "GPL-3.0*" : ["GPL-3.0-only", "GPL-3.0-or-later"],
        "LGPL-3.0*" : ["LGPL-3.0-only", "LGPL-3.0-or-later"],
    }
    for k in mapping:
        if k in wildcard_licenses:
            licenses.remove(k)
            for item in mapping[k]:
                licenses.add(item)

    for l in licenses:
        if l in obsolete_license_list():
            bb.fatal("Error, %s is an obsolete license, please use an SPDX reference in INCOMPATIBLE_LICENSE" % l)
        if "*" in l:
            bb.fatal("Error, %s is an invalid license wildcard entry" % l)

    return list(licenses)

def incompatible_pkg_license(d, dont_want_licenses, license):
    def walk_license(node):
        if isinstance(node, oe.spdx_license.Identifier):
            if license_ok(node.ident, dont_want_licenses):
                return []
            return [node.ident]

        incompatible_lic = []
        for child in node.children:
            c = walk_license(child)
            if not c and isinstance(node, oe.spdx_license.OrOp):
                return []
            incompatible_lic.extend(c)

        return incompatible_lic

    try:
        node = parse_legacy_license(d, license)
    except oe.spdx_license.ParseError as e:
        bb.fatal(e.format(prefix=d.getVar('P') + ":"))
        return []

    if not node:
        return []

    return sorted(walk_license(node))

def incompatible_license(d, dont_want_licenses, package=None):
    """
    This function checks if a recipe has only incompatible licenses. It also
    take into consideration 'or' operand.  dont_want_licenses should be passed
    as canonical (SPDX) names.
    """
    license = d.getVar("LICENSE:%s" % package) if package else None
    if not license:
        license = d.getVar('LICENSE')

    return incompatible_pkg_license(d, dont_want_licenses, license)

def check_license_flags(d):
    """
    This function checks if a recipe has any LICENSE_FLAGS that
    aren't acceptable.

    If it does, it returns the all LICENSE_FLAGS missing from the list
    of acceptable license flags, or all of the LICENSE_FLAGS if there
    is no list of acceptable flags.

    If everything is is acceptable, it returns None.
    """

    def license_flag_matches(flag, acceptlist, pn):
        """
        Return True if flag matches something in acceptlist, None if not.

        Before we test a flag against the acceptlist, we append _${PN}
        to it.  We then try to match that string against the
        acceptlist.  This covers the normal case, where we expect
        LICENSE_FLAGS to be a simple string like 'commercial', which
        the user typically matches exactly in the acceptlist by
        explicitly appending the package name e.g 'commercial_foo'.
        If we fail the match however, we then split the flag across
        '_' and append each fragment and test until we either match or
        run out of fragments.
        """
        flag_pn = ("%s_%s" % (flag, pn))
        for candidate in acceptlist:
            if flag_pn == candidate:
                    return True

        flag_cur = ""
        flagments = flag_pn.split("_")
        flagments.pop() # we've already tested the full string
        for flagment in flagments:
            if flag_cur:
                flag_cur += "_"
            flag_cur += flagment
            for candidate in acceptlist:
                if flag_cur == candidate:
                    return True
        return False

    def all_license_flags_match(license_flags, acceptlist):
        """ Return all unmatched flags, None if all flags match """
        pn = d.getVar('PN')
        split_acceptlist = acceptlist.split()
        flags = []
        for flag in license_flags.split():
            if not license_flag_matches(flag, split_acceptlist, pn):
                flags.append(flag)
        return flags if flags else None

    license_flags = d.getVar('LICENSE_FLAGS')
    if license_flags:
        acceptlist = d.getVar('LICENSE_FLAGS_ACCEPTED')
        if not acceptlist:
            return license_flags.split()
        unmatched_flags = all_license_flags_match(license_flags, acceptlist)
        if unmatched_flags:
            return unmatched_flags
    return None


def _substitute_legacy_license(licensestr):
    if not licensestr or licensestr == "CLOSED":
        return None

    licensestr = licensestr.replace("&", " AND ").replace("|", " OR ")

    return licensestr

def _parse_legacy_license(d, licensestr):
    node = oe.spdx_license.parse(licensestr, allow_unknown=True)

    unknown_found = False

    def convert_unknown(node):
        nonlocal unknown_found

        if isinstance(node, oe.spdx_license.UnknownId):
            if d is not None:
                v = d.getVarFlag("SPDXLICENSEMAP", node.ident)
                if v:
                    return oe.spdx_license.parse(v)

            if node.ident == "Unknown":
                return node

            ident = re.sub(r'[^a-zA-Z0-9\.\-]', '-', node.ident)
            unknown_found = True
            return oe.spdx_license.LicenseRef("LicenseRef-" + ident, node.ident, token=node.token)

        node.children = [convert_unknown(child) for child in node.children]
        return node

    return convert_unknown(node), unknown_found

def parse_legacy_license(d, licensestr):
    """
    Converts a legacy license string to a SPDX license string and parses it,
    returning a SPDX license abstract syntax tree

    This can be removed and replaced with oe.spdx_license.parse(licensestr)
    once legacy licenses are not longer allowed.

    Unknown license IDs in the string are automatically converted to
    LicenseRefs, assuming that they are provided as either a common license
    file or a NO_GENERIC_LICENSE
    """
    licensestr = _substitute_legacy_license(licensestr)
    if licensestr is None:
        return None

    node, _ = _parse_legacy_license(d, licensestr)
    return node

def convert_legacy_license_to_spdx(d, licensestr):
    """
    Converts a legacy license string to an SPDX-compatible license string
    """
    s = parse_legacy_license(d, licensestr)
    if not s:
        return licensestr

    return s.to_string()

def get_license_path(d, node):
    """
    Given an node from an SPDX AST, return the path to the license file
    """
    if isinstance(node, oe.spdx_license.LicenseRef):
        filename = d.getVarFlag("NO_GENERIC_LICENSE", node.name)
        if filename:
            return d.expand("${S}/" + filename), filename

    for lic_dir in [d.getVar("COMMON_LICENSE_DIR")] + (d.getVar("LICENSE_PATH") or "").split():
        p = os.path.join(lic_dir, node.name)
        if os.path.isfile(p):
            return p, None

        # Some license expressions already contain the "LicenseRef-" prefix, so
        # look for a matching license name
        p = os.path.join(lic_dir, node.ident)
        if os.path.isfile(p):
            return p, None

    return None, None

def check_license_format(d):
    """
    This function checks if LICENSE is well defined,
        Validate operators in LICENSES.
        No spaces are allowed between LICENSES.
    """
    pn = d.getVar('PN')
    packages = d.getVar("PACKAGES").split()

    for v in ["LICENSE"] + [f"LICENSE:{p}" for p in packages]:
        licenses = d.getVar(v)

        if licenses == "INVALID":
            continue

        if licenses == "CLOSED":
            bb.warn(f'{pn}: {v} is using "CLOSED", which is deprecated. Convert to using a license ref pointing to an actual license file, e.g.\n{v} = "LicenseRef-{pn}-CLOSED"')
            continue

        if not licenses:
            continue

        try:
            spdx_license = _substitute_legacy_license(licenses)

            s, unknown_found = _parse_legacy_license(d, spdx_license)
            if not s:
                continue

            if spdx_license != licenses or unknown_found:
                bb.warn(f'{pn}: {v} is using an old syntax and should be upgraded to: "{s.sort().to_string()}"')

            if s:
                # Check license refs
                missing_generic_refs = set()
                missing_refs = set()
                deprecated = set()
                def walk_license(node):
                    nonlocal missing_generic_refs
                    nonlocal missing_refs

                    if isinstance(node, oe.spdx_license.LicenseRef):
                        p, non_generic_fn = get_license_path(d, node)
                        if not p:
                            if non_generic_fn:
                                missing_refs.add(node.ident)
                            else:
                                missing_generic_refs.add(node.ident)

                    if isinstance(node, (oe.spdx_license.LicenseId, oe.spdx_license.ExceptionId)):
                        if node.deprecated:
                            deprecated.add(node.ident)

                    for child in node.children:
                        walk_license(child)

                walk_license(s)
                if missing_refs:
                    missing_refs = ', '.join(sorted(missing_refs))
                    oe.qa.handle_error("license-format",
                                        f'{pn}: LICENSE contains a reference to license(s) {missing_refs} which are not defined in NO_GENERIC_LICENSE',
                                        d)

                if missing_generic_refs:
                    missing_generic_refs = ', '.join(sorted(missing_generic_refs))
                    oe.qa.handle_error("license-format",
                                        f'{pn}: LICENSE contains a reference to license(s) {missing_generic_refs} for which no generic license was found',
                                        d)

                if deprecated:
                    deprecated = ", ".join(sorted(deprecated))
                    bb.warn(f'{pn}: {v} contains deprecated licenses {deprecated}')

        except oe.spdx_license.ParseError as e:
            oe.qa.handle_error("license-format",
                            f'{pn}: {v} value has an invalid format:\n{e.format()}\n', d)

def filter_pkg_license_list(pkg, lst):
    """
    Given a list of license IDs and a package, returns the list of license IDs
    that apply to the package by looking for a package prefix
    """
    pkg_lst = []
    for e in lst:
        if ":" not in e:
            pkg_lst.append(e)
            continue

        p, l = e.split(":", 1)
        if pkg == p:
            pkg_lst.append(l)

    return pkg_lst

def skip_incompatible_package_licenses(d, pkgs):
    if not pkgs:
        return {}

    pn = d.getVar("PN")

    check_license = False if pn.startswith("nativesdk-") else True
    for t in ["-native", "-cross-${TARGET_ARCH}", "-cross-initial-${TARGET_ARCH}",
            "-crosssdk-${SDK_SYS}", "-crosssdk-initial-${SDK_SYS}",
            "-cross-canadian-${TRANSLATED_TARGET_ARCH}"]:
        if pn.endswith(d.expand(t)):
            check_license = False
    if pn.startswith("gcc-source-"):
        check_license = False

    bad_licenses = (d.getVar('INCOMPATIBLE_LICENSE') or "").split()
    if not check_license or not bad_licenses:
        return {}

    bad_licenses = expand_wildcard_licenses(d, bad_licenses)

    allow_licenses = (d.getVar("INCOMPATIBLE_LICENSE_EXCEPTIONS") or "").split()

    for lic in allow_licenses:
        if ":" in lic:
            lic = lic.split(":")[1]
        if lic in obsolete_license_list():
            bb.fatal("Obsolete license %s used in INCOMPATIBLE_LICENSE_EXCEPTIONS" % lic)

    skipped_pkgs = {}
    for pkg in pkgs:
        pkg_lic = d.getVar("LICENSE:%s" % pkg) if pkg else None
        if not pkg_lic:
            pkg_lic = d.getVar('LICENSE')

        node = parse_legacy_license(d, pkg_lic)
        if not node:
            continue

        incompatible_lic = license_allowed(node, bad_licenses, filter_pkg_license_list(pkg, allow_licenses))
        if incompatible_lic:
            skipped_pkgs[pkg] = [lic.to_string() for lic in incompatible_lic]

    return skipped_pkgs
