# vi:sts=4:sw=4:et
"""Code for parsing OpenEmbedded license strings"""

import ast
import re
from fnmatch import fnmatchcase as fnmatch

def license_ok(license, dont_want_licenses):
    """ Return False if License exist in dont_want_licenses else True """
    for dwl in dont_want_licenses:
        # If you want to exclude license named generically 'X', we
        # surely want to exclude 'X+' as well.  In consequence, we
        # will exclude a trailing '+' character from LICENSE in
        # case INCOMPATIBLE_LICENSE is not a 'X+' license.
        lic = license
        if not re.search('\+$', dwl):
            lic = re.sub('\+', '', license)
        if fnmatch(lic, dwl):
            return False
    return True

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
license_operator = re.compile('([' + license_operator_chars + '])')
license_pattern = re.compile('[a-zA-Z0-9.+_\-]+$')

class LicenseVisitor(ast.NodeVisitor):
    """Get elements based on OpenEmbedded license strings"""
    def get_elements(self, licensestr):
        new_elements = []
        elements = filter(lambda x: x.strip(), license_operator.split(licensestr))
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

def is_included(licensestr, whitelist=None, blacklist=None):
    """Given a license string and whitelist and blacklist, determine if the
    license string matches the whitelist and does not match the blacklist.

    Returns a tuple holding the boolean state and a list of the applicable
    licenses which were excluded (or None, if the state is True)
    """

    def include_license(license):
        return any(fnmatch(license, pattern) for pattern in whitelist)

    def exclude_license(license):
        return any(fnmatch(license, pattern) for pattern in blacklist)

    def choose_licenses(alpha, beta):
        """Select the option in an OR which is the 'best' (has the most
        included licenses)."""
        alpha_weight = len(filter(include_license, alpha))
        beta_weight = len(filter(include_license, beta))
        if alpha_weight > beta_weight:
            return alpha
        else:
            return beta

    if not whitelist:
        whitelist = ['*']

    if not blacklist:
        blacklist = []

    licenses = flattened_licenses(licensestr, choose_licenses)
    excluded = filter(lambda lic: exclude_license(lic), licenses)
    included = filter(lambda lic: include_license(lic), licenses)
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
