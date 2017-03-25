from abc import ABCMeta, abstractmethod
import os
import re
import bb


class Manifest(object, metaclass=ABCMeta):
    """
    This is an abstract class. Do not instantiate this directly.
    """

    PKG_TYPE_MUST_INSTALL = "mip"
    PKG_TYPE_MULTILIB = "mlp"
    PKG_TYPE_LANGUAGE = "lgp"
    PKG_TYPE_ATTEMPT_ONLY = "aop"

    MANIFEST_TYPE_IMAGE = "image"
    MANIFEST_TYPE_SDK_HOST = "sdk_host"
    MANIFEST_TYPE_SDK_TARGET = "sdk_target"

    var_maps = {
        MANIFEST_TYPE_IMAGE: {
            "PACKAGE_INSTALL": PKG_TYPE_MUST_INSTALL,
            "PACKAGE_INSTALL_ATTEMPTONLY": PKG_TYPE_ATTEMPT_ONLY,
            "LINGUAS_INSTALL": PKG_TYPE_LANGUAGE
        },
        MANIFEST_TYPE_SDK_HOST: {
            "TOOLCHAIN_HOST_TASK": PKG_TYPE_MUST_INSTALL,
            "TOOLCHAIN_HOST_TASK_ATTEMPTONLY": PKG_TYPE_ATTEMPT_ONLY
        },
        MANIFEST_TYPE_SDK_TARGET: {
            "TOOLCHAIN_TARGET_TASK": PKG_TYPE_MUST_INSTALL,
            "TOOLCHAIN_TARGET_TASK_ATTEMPTONLY": PKG_TYPE_ATTEMPT_ONLY
        }
    }

    INSTALL_ORDER = [
        PKG_TYPE_LANGUAGE,
        PKG_TYPE_MUST_INSTALL,
        PKG_TYPE_ATTEMPT_ONLY,
        PKG_TYPE_MULTILIB
    ]

    initial_manifest_file_header = \
        "# This file was generated automatically and contains the packages\n" \
        "# passed on to the package manager in order to create the rootfs.\n\n" \
        "# Format:\n" \
        "#  <package_type>,<package_name>\n" \
        "# where:\n" \
        "#   <package_type> can be:\n" \
        "#      'mip' = must install package\n" \
        "#      'aop' = attempt only package\n" \
        "#      'mlp' = multilib package\n" \
        "#      'lgp' = language package\n\n"

    def __init__(self, d, manifest_dir=None, manifest_type=MANIFEST_TYPE_IMAGE):
        self.d = d
        self.manifest_type = manifest_type

        if manifest_dir is None:
            if manifest_type != self.MANIFEST_TYPE_IMAGE:
                self.manifest_dir = self.d.getVar('SDK_DIR', True)
            else:
                self.manifest_dir = self.d.getVar('WORKDIR', True)
        else:
            self.manifest_dir = manifest_dir

        bb.utils.mkdirhier(self.manifest_dir)

        self.initial_manifest = os.path.join(self.manifest_dir, "%s_initial_manifest" % manifest_type)
        self.final_manifest = os.path.join(self.manifest_dir, "%s_final_manifest" % manifest_type)
        self.full_manifest = os.path.join(self.manifest_dir, "%s_full_manifest" % manifest_type)

        # packages in the following vars will be split in 'must install' and
        # 'multilib'
        self.vars_to_split = ["PACKAGE_INSTALL",
                              "TOOLCHAIN_HOST_TASK",
                              "TOOLCHAIN_TARGET_TASK"]

    """
    This creates a standard initial manifest for core-image-(minimal|sato|sato-sdk).
    This will be used for testing until the class is implemented properly!
    """
    def _create_dummy_initial(self):
        image_rootfs = self.d.getVar('IMAGE_ROOTFS', True)
        pkg_list = dict()
        if image_rootfs.find("core-image-sato-sdk") > 0:
            pkg_list[self.PKG_TYPE_MUST_INSTALL] = \
                "packagegroup-core-x11-sato-games packagegroup-base-extended " \
                "packagegroup-core-x11-sato packagegroup-core-x11-base " \
                "packagegroup-core-sdk packagegroup-core-tools-debug " \
                "packagegroup-core-boot packagegroup-core-tools-testapps " \
                "packagegroup-core-eclipse-debug packagegroup-core-qt-demoapps " \
                "apt packagegroup-core-tools-profile psplash " \
                "packagegroup-core-standalone-sdk-target " \
                "packagegroup-core-ssh-openssh dpkg kernel-dev"
            pkg_list[self.PKG_TYPE_LANGUAGE] = \
                "locale-base-en-us locale-base-en-gb"
        elif image_rootfs.find("core-image-sato") > 0:
            pkg_list[self.PKG_TYPE_MUST_INSTALL] = \
                "packagegroup-core-ssh-dropbear packagegroup-core-x11-sato-games " \
                "packagegroup-core-x11-base psplash apt dpkg packagegroup-base-extended " \
                "packagegroup-core-x11-sato packagegroup-core-boot"
            pkg_list['lgp'] = \
                "locale-base-en-us locale-base-en-gb"
        elif image_rootfs.find("core-image-minimal") > 0:
            pkg_list[self.PKG_TYPE_MUST_INSTALL] = "run-postinsts packagegroup-core-boot"

        with open(self.initial_manifest, "w+") as manifest:
            manifest.write(self.initial_manifest_file_header)

            for pkg_type in pkg_list:
                for pkg in pkg_list[pkg_type].split():
                    manifest.write("%s,%s\n" % (pkg_type, pkg))

    """
    This will create the initial manifest which will be used by Rootfs class to
    generate the rootfs
    """
    @abstractmethod
    def create_initial(self):
        pass

    """
    This creates the manifest after everything has been installed.
    """
    @abstractmethod
    def create_final(self):
        pass

    """
    This creates the manifest after the package in initial manifest has been
    dummy installed. It lists all *to be installed* packages. There is no real
    installation, just a test.
    """
    @abstractmethod
    def create_full(self, pm):
        pass

    """
    The following function parses an initial manifest and returns a dictionary
    object with the must install, attempt only, multilib and language packages.
    """
    def parse_initial_manifest(self):
        pkgs = dict()

        with open(self.initial_manifest) as manifest:
            for line in manifest.read().split('\n'):
                comment = re.match("^#.*", line)
                pattern = "^(%s|%s|%s|%s),(.*)$" % \
                          (self.PKG_TYPE_MUST_INSTALL,
                           self.PKG_TYPE_ATTEMPT_ONLY,
                           self.PKG_TYPE_MULTILIB,
                           self.PKG_TYPE_LANGUAGE)
                pkg = re.match(pattern, line)

                if comment is not None:
                    continue

                if pkg is not None:
                    pkg_type = pkg.group(1)
                    pkg_name = pkg.group(2)

                    if not pkg_type in pkgs:
                        pkgs[pkg_type] = [pkg_name]
                    else:
                        pkgs[pkg_type].append(pkg_name)

        return pkgs

    '''
    This following function parses a full manifest and return a list
    object with packages.
    '''
    def parse_full_manifest(self):
        installed_pkgs = list()
        if not os.path.exists(self.full_manifest):
            bb.note('full manifest not exist')
            return installed_pkgs

        with open(self.full_manifest, 'r') as manifest:
            for pkg in manifest.read().split('\n'):
                installed_pkgs.append(pkg.strip())

        return installed_pkgs


class RpmManifest(Manifest):
    """
    Returns a dictionary object with mip and mlp packages.
    """
    def _split_multilib(self, pkg_list):
        pkgs = dict()

        for pkg in pkg_list.split():
            pkg_type = self.PKG_TYPE_MUST_INSTALL

            ml_variants = self.d.getVar('MULTILIB_VARIANTS', True).split()

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
                    split_pkgs = self._split_multilib(self.d.getVar(var, True))
                    if split_pkgs is not None:
                        pkgs = dict(list(pkgs.items()) + list(split_pkgs.items()))
                else:
                    pkg_list = self.d.getVar(var, True)
                    if pkg_list is not None:
                        pkgs[self.var_maps[self.manifest_type][var]] = self.d.getVar(var, True)

            for pkg_type in pkgs:
                for pkg in pkgs[pkg_type].split():
                    manifest.write("%s,%s\n" % (pkg_type, pkg))

    def create_final(self):
        pass

    def create_full(self, pm):
        pass


class OpkgManifest(Manifest):
    """
    Returns a dictionary object with mip and mlp packages.
    """
    def _split_multilib(self, pkg_list):
        pkgs = dict()

        for pkg in pkg_list.split():
            pkg_type = self.PKG_TYPE_MUST_INSTALL

            ml_variants = self.d.getVar('MULTILIB_VARIANTS', True).split()

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
                    split_pkgs = self._split_multilib(self.d.getVar(var, True))
                    if split_pkgs is not None:
                        pkgs = dict(list(pkgs.items()) + list(split_pkgs.items()))
                else:
                    pkg_list = self.d.getVar(var, True)
                    if pkg_list is not None:
                        pkgs[self.var_maps[self.manifest_type][var]] = self.d.getVar(var, True)

            for pkg_type in pkgs:
                for pkg in pkgs[pkg_type].split():
                    manifest.write("%s,%s\n" % (pkg_type, pkg))

    def create_final(self):
        pass

    def create_full(self, pm):
        if not os.path.exists(self.initial_manifest):
            self.create_initial()

        initial_manifest = self.parse_initial_manifest()
        pkgs_to_install = list()
        for pkg_type in initial_manifest:
            pkgs_to_install += initial_manifest[pkg_type]
        if len(pkgs_to_install) == 0:
            return

        output = pm.dummy_install(pkgs_to_install)

        with open(self.full_manifest, 'w+') as manifest:
            pkg_re = re.compile('^Installing ([^ ]+) [^ ].*')
            for line in set(output.split('\n')):
                m = pkg_re.match(line)
                if m:
                    manifest.write(m.group(1) + '\n')

        return


class DpkgManifest(Manifest):
    def create_initial(self):
        with open(self.initial_manifest, "w+") as manifest:
            manifest.write(self.initial_manifest_file_header)

            for var in self.var_maps[self.manifest_type]:
                pkg_list = self.d.getVar(var, True)

                if pkg_list is None:
                    continue

                for pkg in pkg_list.split():
                    manifest.write("%s,%s\n" %
                                   (self.var_maps[self.manifest_type][var], pkg))

    def create_final(self):
        pass

    def create_full(self, pm):
        pass


def create_manifest(d, final_manifest=False, manifest_dir=None,
                    manifest_type=Manifest.MANIFEST_TYPE_IMAGE):
    manifest_map = {'rpm': RpmManifest,
                    'ipk': OpkgManifest,
                    'deb': DpkgManifest}

    manifest = manifest_map[d.getVar('IMAGE_PKGTYPE', True)](d, manifest_dir, manifest_type)

    if final_manifest:
        manifest.create_final()
    else:
        manifest.create_initial()


if __name__ == "__main__":
    pass
