#
# SPDX-License-Identifier: GPL-2.0-only
#

from oe.manifest import Manifest

class PkgManifest(Manifest):
    def create_initial(self):
        with open(self.initial_manifest, "w+") as manifest:
            manifest.write(self.initial_manifest_file_header)

            for var in self.var_maps[self.manifest_type]:
                pkg_list = self.d.getVar(var)

                if pkg_list is None:
                    continue

                for pkg in pkg_list.split():
                    manifest.write("%s,%s\n" %
                                   (self.var_maps[self.manifest_type][var], pkg))

    def create_final(self):
        pass

    def create_full(self, pm):
        pass
