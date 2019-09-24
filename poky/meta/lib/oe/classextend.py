#
# SPDX-License-Identifier: GPL-2.0-only
#

import collections

class ClassExtender(object):
    def __init__(self, extname, d):
        self.extname = extname
        self.d = d
        self.pkgs_mapping = []

    def extend_name(self, name):
        if name.startswith("kernel-") or name == "virtual/kernel":
            return name
        if name.startswith("rtld"):
            return name
        if name.endswith("-crosssdk"):
            return name
        if name.endswith("-" + self.extname):
            name = name.replace("-" + self.extname, "")
        if name.startswith("virtual/"):
            subs = name.split("/", 1)[1]
            if not subs.startswith(self.extname):
                return "virtual/" + self.extname + "-" + subs
            return name
        if name.startswith("/"):
            return name
        if not name.startswith(self.extname):
            return self.extname + "-" + name
        return name

    def map_variable(self, varname, setvar = True):
        var = self.d.getVar(varname)
        if not var:
            return ""
        var = var.split()
        newvar = []
        for v in var:
            newvar.append(self.extend_name(v))
        newdata =  " ".join(newvar)
        if setvar:
            self.d.setVar(varname, newdata)
        return newdata

    def map_regexp_variable(self, varname, setvar = True):
        var = self.d.getVar(varname)
        if not var:
            return ""
        var = var.split()
        newvar = []
        for v in var:
            if v.startswith("^" + self.extname):
                newvar.append(v)
            elif v.startswith("^"):
                newvar.append("^" + self.extname + "-" + v[1:])
            else:
                newvar.append(self.extend_name(v))
        newdata =  " ".join(newvar)
        if setvar:
            self.d.setVar(varname, newdata)
        return newdata

    def map_depends(self, dep):
        if dep.endswith(("-native", "-native-runtime")) or ('nativesdk-' in dep) or ('cross-canadian' in dep) or ('-crosssdk-' in dep):
            return dep
        else:
            # Do not extend for that already have multilib prefix
            var = self.d.getVar("MULTILIB_VARIANTS")
            if var:
                var = var.split()
                for v in var:
                    if dep.startswith(v):
                        return dep
            return self.extend_name(dep)

    def map_depends_variable(self, varname, suffix = ""):
        # We need to preserve EXTENDPKGV so it can be expanded correctly later
        if suffix:
            varname = varname + "_" + suffix
        orig = self.d.getVar("EXTENDPKGV", False)
        self.d.setVar("EXTENDPKGV", "EXTENDPKGV")
        deps = self.d.getVar(varname)
        if not deps:
            self.d.setVar("EXTENDPKGV", orig)
            return
        deps = bb.utils.explode_dep_versions2(deps)
        newdeps = collections.OrderedDict()
        for dep in deps:
            newdeps[self.map_depends(dep)] = deps[dep]

        self.d.setVar(varname, bb.utils.join_deps(newdeps, False).replace("EXTENDPKGV", "${EXTENDPKGV}"))
        self.d.setVar("EXTENDPKGV", orig)

    def map_packagevars(self):
        for pkg in (self.d.getVar("PACKAGES").split() + [""]):
            self.map_depends_variable("RDEPENDS", pkg)
            self.map_depends_variable("RRECOMMENDS", pkg)
            self.map_depends_variable("RSUGGESTS", pkg)
            self.map_depends_variable("RPROVIDES", pkg)
            self.map_depends_variable("RREPLACES", pkg)
            self.map_depends_variable("RCONFLICTS", pkg)
            self.map_depends_variable("PKG", pkg)

    def rename_packages(self):
        for pkg in (self.d.getVar("PACKAGES") or "").split():
            if pkg.startswith(self.extname):
               self.pkgs_mapping.append([pkg.split(self.extname + "-")[1], pkg])
               continue
            self.pkgs_mapping.append([pkg, self.extend_name(pkg)])

        self.d.setVar("PACKAGES", " ".join([row[1] for row in self.pkgs_mapping]))

    def rename_package_variables(self, variables):
        for pkg_mapping in self.pkgs_mapping:
            for subs in variables:
                self.d.renameVar("%s_%s" % (subs, pkg_mapping[0]), "%s_%s" % (subs, pkg_mapping[1]))

class NativesdkClassExtender(ClassExtender):
    def map_depends(self, dep):
        if dep.startswith(self.extname):
            return dep
        if dep.endswith(("-gcc", "-g++")):
            return dep + "-crosssdk"
        elif dep.endswith(("-native", "-native-runtime")) or ('nativesdk-' in dep) or ('-cross-' in dep) or ('-crosssdk-' in dep):
            return dep
        else:
            return self.extend_name(dep)
