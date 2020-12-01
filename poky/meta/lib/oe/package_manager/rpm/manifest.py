#
# SPDX-License-Identifier: GPL-2.0-only
#

from oe.manifest import Manifest

class PkgManifest(Manifest):
    """
    Returns a dictionary object with mip and mlp packages.
    """
    def _split_multilib(self, pkg_list):
        pkgs = dict()

        for pkg in pkg_list.split():
            pkg_type = self.PKG_TYPE_MUST_INSTALL

            ml_variants = self.d.getVar('MULTILIB_VARIANTS').split()

            for ml_variant in ml_variants:
                if pkg.startswith(ml_variant + '-'):
                    pkg_type = self.PKG_TYPE_MULTILIB

            if not pkg_type in pkgs:
                pkgs[pkg_type] = pkg
            else:
                pkgs[pkg_type] += " " + pkg

        return pkgs

    def create_initial(self):
        pkgs = dict()

        with open(self.initial_manifest, "w+") as manifest:
            manifest.write(self.initial_manifest_file_header)

            for var in self.var_maps[self.manifest_type]:
                if var in self.vars_to_split:
                    split_pkgs = self._split_multilib(self.d.getVar(var))
                    if split_pkgs is not None:
                        pkgs = dict(list(pkgs.items()) + list(split_pkgs.items()))
                else:
                    pkg_list = self.d.getVar(var)
                    if pkg_list is not None:
                        pkgs[self.var_maps[self.manifest_type][var]] = self.d.getVar(var)

            for pkg_type in pkgs:
                for pkg in pkgs[pkg_type].split():
                    manifest.write("%s,%s\n" % (pkg_type, pkg))

    def create_final(self):
        pass

    def create_full(self, pm):
        pass
