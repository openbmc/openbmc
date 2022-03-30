#
# SPDX-License-Identifier: GPL-2.0-only
#
"""Code for parsing OpenEmbedded license strings"""

import ast
import re
from fnmatch import fnmatchcase as fnmatch

def license_ok(license, dont_want_licenses):
    """ Return False if License exist in dont_want_licenses else True """
    for dwl in dont_want_licenses:
        if fnmatch(license, dwl):
            return False
    return True

def obsolete_license_list():
    return ["AGPL-3", "AGPL-3+", "AGPLv3", "AGPLv3+", "AGPLv3.0", "AGPLv3.0+", "AGPL-3.0", "AGPL-3.0+", "BSD-0-Clause",
            "GPL-1", "GPL-1+", "GPLv1", "GPLv1+", "GPLv1.0", "GPLv1.0+", "GPL-1.0", "GPL-1.0+", "GPL-2", "GPL-2+", "GPLv2",
            "GPLv2+", "GPLv2.0", "GPLv2.0+", "GPL-2.0", "GPL-2.0+", "GPL-3", "GPL-3+", "GPLv3", "GPLv3+", "GPLv3.0", "GPLv3.0+",
            "GPL-3.0", "GPL-3.0+", "LGPLv2", "LGPLv2+", "LGPLv2.0", "LGPLv2.0+", "LGPL-2.0", "LGPL-2.0+", "LGPL2.1", "LGPL2.1+",
            "LGPLv2.1", "LGPLv2.1+", "LGPL-2.1", "LGPL-2.1+", "LGPLv3", "LGPLv3+", "LGPL-3.0", "LGPL-3.0+", "MPL-1", "MPLv1",
            "MPLv1.1", "MPLv2", "MIT-X", "MIT-style", "openssl", "PSF", "PSFv2", "Python-2", "Apachev2", "Apache-2", "Artisticv1",
            "Artistic-1", "AFL-2", "AFL-1", "AFLv2", "AFLv1", "CDDLv1", "CDDL-1", "EPLv1.0", "FreeType", "Nauman",
            "tcl", "vim", "SGIv1"]

class LicenseError(Exception):
    pass

class LicenseSyntaxError(LicenseError):
    def __init__(self, licensestr, exc):
        self.licensestr = licensestr
        self.exc = exc
        LicenseError.__init__(self)

    def __str__(self):
        return "error in '%s': %s" % (self.licensestr, self.exc)

class InvalidLicense(LicenseError):
    def __init__(self, license):
        self.license = license
        LicenseError.__init__(self)

    def __str__(self):
        return "invalid characters in license '%s'" % self.license

license_operator_chars = '&|() '
license_operator = re.compile(r'([' + license_operator_chars + '])')
license_pattern = re.compile(r'[a-zA-Z0-9.+_\-]+$')

class LicenseVisitor(ast.NodeVisitor):
    """Get elements based on OpenEmbedded license strings"""
    def get_elements(self, licensestr):
        new_elements = []
        elements = list([x for x in license_operator.split(licensestr) if x.strip()])
        for pos, element in enumerate(elements):
            if license_pattern.match(element):
                if pos > 0 and license_pattern.match(elements[pos-1]):
                    new_elements.append('&')
                element = '"' + element + '"'
            elif not license_operator.match(element):
                raise InvalidLicense(element)
            new_elements.append(element)

        return new_elements

    """Syntax tree visitor which can accept elements previously generated with
    OpenEmbedded license string"""
    def visit_elements(self, elements):
        self.visit(ast.parse(' '.join(elements)))

    """Syntax tree visitor which can accept OpenEmbedded license strings"""
    def visit_string(self, licensestr):
        self.visit_elements(self.get_elements(licensestr))

class FlattenVisitor(LicenseVisitor):
    """Flatten a license tree (parsed from a string) by selecting one of each
    set of OR options, in the way the user specifies"""
    def __init__(self, choose_licenses):
        self.choose_licenses = choose_licenses
        self.licenses = []
        LicenseVisitor.__init__(self)

    def visit_Str(self, node):
        self.licenses.append(node.s)

    def visit_Constant(self, node):
        self.licenses.append(node.value)

    def visit_BinOp(self, node):
        if isinstance(node.op, ast.BitOr):
            left = FlattenVisitor(self.choose_licenses)
            left.visit(node.left)

            right = FlattenVisitor(self.choose_licenses)
            right.visit(node.right)

            selected = self.choose_licenses(left.licenses, right.licenses)
            self.licenses.extend(selected)
        else:
            self.generic_visit(node)

def flattened_licenses(licensestr, choose_licenses):
    """Given a license string and choose_licenses function, return a flat list of licenses"""
    flatten = FlattenVisitor(choose_licenses)
    try:
        flatten.visit_string(licensestr)
    except SyntaxError as exc:
        raise LicenseSyntaxError(licensestr, exc)
    return flatten.licenses

def is_included(licensestr, include_licenses=None, exclude_licenses=None):
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

    licenses = flattened_licenses(licensestr, choose_licenses)
    excluded = [lic for lic in licenses if exclude_license(lic)]
    included = [lic for lic in licenses if include_license(lic)]
    if excluded:
        return False, excluded
    else:
        return True, included

class ManifestVisitor(LicenseVisitor):
    """Walk license tree (parsed from a string) removing the incompatible
    licenses specified"""
    def __init__(self, dont_want_licenses, canonical_license, d):
        self._dont_want_licenses = dont_want_licenses
        self._canonical_license = canonical_license
        self._d = d
        self._operators = []

        self.licenses = []
        self.licensestr = ''

        LicenseVisitor.__init__(self)

    def visit(self, node):
        if isinstance(node, ast.Str):
            lic = node.s

            if license_ok(self._canonical_license(self._d, lic),
                    self._dont_want_licenses) == True:
                if self._operators:
                    ops = []
                    for op in self._operators:
                        if op == '[':
                            ops.append(op)
                        elif op == ']':
                            ops.append(op)
                        else:
                            if not ops:
                                ops.append(op)
                            elif ops[-1] in ['[', ']']:
                                ops.append(op)
                            else:
                                ops[-1] = op 

                    for op in ops:
                        if op == '[' or op == ']':
                            self.licensestr += op
                        elif self.licenses:
                            self.licensestr += ' ' + op + ' '

                    self._operators = []

                self.licensestr += lic
                self.licenses.append(lic)
        elif isinstance(node, ast.BitAnd):
            self._operators.append("&")
        elif isinstance(node, ast.BitOr):
            self._operators.append("|")
        elif isinstance(node, ast.List):
            self._operators.append("[")
        elif isinstance(node, ast.Load):
            self.licensestr += "]"

        self.generic_visit(node)

def manifest_licenses(licensestr, dont_want_licenses, canonical_license, d):
    """Given a license string and dont_want_licenses list,
       return license string filtered and a list of licenses"""
    manifest = ManifestVisitor(dont_want_licenses, canonical_license, d)

    try:
        elements = manifest.get_elements(licensestr)

        # Replace '()' to '[]' for handle in ast as List and Load types.
        elements = ['[' if e == '(' else e for e in elements]
        elements = [']' if e == ')' else e for e in elements]

        manifest.visit_elements(elements)
    except SyntaxError as exc:
        raise LicenseSyntaxError(licensestr, exc)

    # Replace '[]' to '()' for output correct license.
    manifest.licensestr = manifest.licensestr.replace('[', '(').replace(']', ')')

    return (manifest.licensestr, manifest.licenses)

class ListVisitor(LicenseVisitor):
    """Record all different licenses found in the license string"""
    def __init__(self):
        self.licenses = set()

    def visit_Str(self, node):
        self.licenses.add(node.s)

    def visit_Constant(self, node):
        self.licenses.add(node.value)

def list_licenses(licensestr):
    """Simply get a list of all licenses mentioned in a license string.
       Binary operators are not applied or taken into account in any way"""
    visitor = ListVisitor()
    try:
        visitor.visit_string(licensestr)
    except SyntaxError as exc:
        raise LicenseSyntaxError(licensestr, exc)
    return visitor.licenses

def apply_pkg_license_exception(pkg, bad_licenses, exceptions):
    """Return remaining bad licenses after removing any package exceptions"""

    return [lic for lic in bad_licenses if pkg + ':' + lic not in exceptions]
