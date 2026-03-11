#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import re
import subprocess
from oe.package_manager import *
from oe.package_manager.common_deb_ipk import OpkgDpkgPM

class DpkgIndexer(Indexer):
    def _create_configs(self):
        bb.utils.mkdirhier(self.apt_conf_dir)
        bb.utils.mkdirhier(os.path.join(self.apt_conf_dir, "lists", "partial"))
        bb.utils.mkdirhier(os.path.join(self.apt_conf_dir, "apt.conf.d"))
        bb.utils.mkdirhier(os.path.join(self.apt_conf_dir, "preferences.d"))

        with open(os.path.join(self.apt_conf_dir, "preferences"),
                "w") as prefs_file:
            pass
        with open(os.path.join(self.apt_conf_dir, "sources.list"),
                "w+") as sources_file:
            pass

        with open(self.apt_conf_file, "w") as apt_conf:
            with open(os.path.join(self.d.expand("${STAGING_ETCDIR_NATIVE}"),
                "apt", "apt.conf.sample")) as apt_conf_sample:
                for line in apt_conf_sample.read().split("\n"):
                    line = re.sub(r"#ROOTFS#", "/dev/null", line)
                    line = re.sub(r"#APTCONF#", self.apt_conf_dir, line)
                    apt_conf.write(line + "\n")

    def write_index(self):
        self.apt_conf_dir = os.path.join(self.d.expand("${APTCONF_TARGET}"),
                "apt-ftparchive")
        self.apt_conf_file = os.path.join(self.apt_conf_dir, "apt.conf")
        self._create_configs()

        os.environ['APT_CONFIG'] = self.apt_conf_file

        pkg_archs = self.d.getVar('PACKAGE_ARCHS')
        if pkg_archs is not None:
            arch_list = pkg_archs.split()
        sdk_pkg_archs = self.d.getVar('SDK_PACKAGE_ARCHS')
        if sdk_pkg_archs is not None:
            for a in sdk_pkg_archs.split():
                if a not in pkg_archs:
                    arch_list.append(a)

        all_mlb_pkg_arch_list = (self.d.getVar('ALL_MULTILIB_PACKAGE_ARCHS') or "").split()
        arch_list.extend(arch for arch in all_mlb_pkg_arch_list if arch not in arch_list)

        apt_ftparchive = bb.utils.which(os.getenv('PATH'), "apt-ftparchive")
        gzip = bb.utils.which(os.getenv('PATH'), "gzip")

        index_cmds = []
        deb_dirs_found = False
        index_sign_files = set()
        for arch in arch_list:
            arch_dir = os.path.join(self.deploy_dir, arch)
            if not os.path.isdir(arch_dir):
                continue

            cmd = "cd %s; PSEUDO_UNLOAD=1 %s packages . > Packages;" % (arch_dir, apt_ftparchive)

            cmd += "%s -fcn Packages > Packages.gz;" % gzip

            release_file = os.path.join(arch_dir, "Release")
            index_sign_files.add(release_file)

            with open(release_file, "w+") as release:
                release.write("Label: %s\n" % arch)

            cmd += "PSEUDO_UNLOAD=1 %s release . >> Release" % apt_ftparchive

            index_cmds.append(cmd)

            deb_dirs_found = True

        if not deb_dirs_found:
            bb.note("There are no packages in %s" % self.deploy_dir)
            return

        oe.utils.multiprocess_launch(create_index, index_cmds, self.d)
        if self.d.getVar('PACKAGE_FEED_SIGN') == '1':
            signer = get_signer(self.d, self.d.getVar('PACKAGE_FEED_GPG_BACKEND'))
        else:
            signer = None
        if signer:
            for f in index_sign_files:
                signer.detach_sign(f,
                                   self.d.getVar('PACKAGE_FEED_GPG_NAME'),
                                   self.d.getVar('PACKAGE_FEED_GPG_PASSPHRASE_FILE'),
                                   output_suffix="gpg",
                                   use_sha256=True)

class PMPkgsList(PkgsList):

    def list_pkgs(self):
        cmd = [bb.utils.which(os.getenv('PATH'), "dpkg-query"),
               "--admindir=%s/var/lib/dpkg" % self.rootfs_dir,
               "-W"]

        cmd.append("-f=Package: ${Package}\nArchitecture: ${PackageArch}\nVersion: ${Version}\nFile: ${Package}_${Version}_${Architecture}.deb\nDepends: ${Depends}\nRecommends: ${Recommends}\nProvides: ${Provides}\n\n")

        try:
            cmd_output = subprocess.check_output(cmd, stderr=subprocess.STDOUT).strip().decode("utf-8")
        except subprocess.CalledProcessError as e:
            bb.fatal("Cannot get the installed packages list. Command '%s' "
                     "returned %d:\n%s" % (' '.join(cmd), e.returncode, e.output.decode("utf-8")))

        return opkg_query(cmd_output)


class DpkgPM(OpkgDpkgPM):
    def __init__(self, d, target_rootfs, archs, base_archs, apt_conf_dir=None, deb_repo_workdir="oe-rootfs-repo", filterbydependencies=True):
        super(DpkgPM, self).__init__(d, target_rootfs)
        self.deploy_dir = oe.path.join(self.d.getVar('WORKDIR'), deb_repo_workdir)

        create_packages_dir(self.d, self.deploy_dir, d.getVar("DEPLOY_DIR_DEB"), "package_write_deb", filterbydependencies)

        if apt_conf_dir is None:
            self.apt_conf_dir = self.d.expand("${APTCONF_TARGET}/apt")
        else:
            self.apt_conf_dir = apt_conf_dir
        self.apt_conf_file = os.path.join(self.apt_conf_dir, "apt.conf")
        self.apt_get_cmd = bb.utils.which(os.getenv('PATH'), "apt-get")
        self.apt_cache_cmd = bb.utils.which(os.getenv('PATH'), "apt-cache")

        self.apt_args = d.getVar("APT_ARGS")

        self.all_arch_list = archs.split()
        all_mlb_pkg_arch_list = (self.d.getVar('ALL_MULTILIB_PACKAGE_ARCHS') or "").split()
        self.all_arch_list.extend(arch for arch in all_mlb_pkg_arch_list if arch not in self.all_arch_list)

        self._create_configs(archs, base_archs)

        self.indexer = DpkgIndexer(self.d, self.deploy_dir)

    def mark_packages(self, status_tag, packages=None):
        """
        This function will change a package's status in /var/lib/dpkg/status file.
        If 'packages' is None then the new_status will be applied to all
        packages
        """
        status_file = self.target_rootfs + "/var/lib/dpkg/status"

        with open(status_file, "r") as sf:
            with open(status_file + ".tmp", "w+") as tmp_sf:
                if packages is None:
                    tmp_sf.write(re.sub(r"Package: (.*?)\n((?:[^\n]+\n)*?)Status: (.*)(?:unpacked|installed)",
                                        r"Package: \1\n\2Status: \3%s" % status_tag,
                                        sf.read()))
                else:
                    if type(packages).__name__ != "list":
                        raise TypeError("'packages' should be a list object")

                    status = sf.read()
                    for pkg in packages:
                        status = re.sub(r"Package: %s\n((?:[^\n]+\n)*?)Status: (.*)(?:unpacked|installed)" % pkg,
                                        r"Package: %s\n\1Status: \2%s" % (pkg, status_tag),
                                        status)

                    tmp_sf.write(status)

        bb.utils.rename(status_file + ".tmp", status_file)

    def run_pre_post_installs(self, package_name=None):
        """
        Run the pre/post installs for package "package_name". If package_name is
        None, then run all pre/post install scriptlets.
        """
        info_dir = self.target_rootfs + "/var/lib/dpkg/info"
        ControlScript = collections.namedtuple("ControlScript", ["suffix", "name", "argument"])
        control_scripts = [
                ControlScript(".preinst", "Preinstall", "install"),
                ControlScript(".postinst", "Postinstall", "configure")]
        status_file = self.target_rootfs + "/var/lib/dpkg/status"
        installed_pkgs = []

        with open(status_file, "r") as status:
            for line in status.read().split('\n'):
                m = re.match(r"^Package: (.*)", line)
                if m is not None:
                    installed_pkgs.append(m.group(1))

        if package_name is not None and not package_name in installed_pkgs:
            return

        os.environ['D'] = self.target_rootfs
        os.environ['OFFLINE_ROOT'] = self.target_rootfs
        os.environ['IPKG_OFFLINE_ROOT'] = self.target_rootfs
        os.environ['OPKG_OFFLINE_ROOT'] = self.target_rootfs
        os.environ['INTERCEPT_DIR'] = self.intercepts_dir
        os.environ['NATIVE_ROOT'] = self.d.getVar('STAGING_DIR_NATIVE')

        for pkg_name in installed_pkgs:
            for control_script in control_scripts:
                p_full = os.path.join(info_dir, pkg_name + control_script.suffix)
                if os.path.exists(p_full):
                    try:
                        bb.note("Executing %s for package: %s ..." %
                                 (control_script.name.lower(), pkg_name))
                        output = subprocess.check_output([p_full, control_script.argument],
                                stderr=subprocess.STDOUT).decode("utf-8")
                        bb.note(output)
                    except subprocess.CalledProcessError as e:
                        bb.warn("%s for package %s failed with %d:\n%s" %
                                (control_script.name, pkg_name, e.returncode,
                                    e.output.decode("utf-8")))
                        failed_postinsts_abort([pkg_name], self.d.expand("${T}/log.do_${BB_CURRENTTASK}"))

    def update(self):
        os.environ['APT_CONFIG'] = self.apt_conf_file

        self.deploy_dir_lock()

        cmd = "%s update" % self.apt_get_cmd

        try:
            subprocess.check_output(cmd.split(), stderr=subprocess.STDOUT)
        except subprocess.CalledProcessError as e:
            bb.fatal("Unable to update the package index files. Command '%s' "
                     "returned %d:\n%s" % (e.cmd, e.returncode, e.output.decode("utf-8")))

        self.deploy_dir_unlock()

    def install(self, pkgs, attempt_only=False, hard_depends_only=False):
        if attempt_only and len(pkgs) == 0:
            return

        os.environ['APT_CONFIG'] = self.apt_conf_file

        extra_args = ""
        if hard_depends_only:
            extra_args = "--no-install-recommends"

        cmd = "%s %s install --allow-downgrades --allow-remove-essential --allow-change-held-packages --allow-unauthenticated --no-remove %s %s" % \
              (self.apt_get_cmd, self.apt_args, extra_args, ' '.join(pkgs))

        try:
            bb.note("Installing the following packages: %s" % ' '.join(pkgs))
            output = subprocess.check_output(cmd.split(), stderr=subprocess.STDOUT)
            bb.note(output.decode("utf-8"))
        except subprocess.CalledProcessError as e:
            (bb.fatal, bb.warn)[attempt_only]("Unable to install packages. "
                                              "Command '%s' returned %d:\n%s" %
                                              (cmd, e.returncode, e.output.decode("utf-8")))

        # rename *.dpkg-new files/dirs
        for root, dirs, files in os.walk(self.target_rootfs):
            for dir in dirs:
                new_dir = re.sub(r"\.dpkg-new", "", dir)
                if dir != new_dir:
                    bb.utils.rename(os.path.join(root, dir),
                              os.path.join(root, new_dir))

            for file in files:
                new_file = re.sub(r"\.dpkg-new", "", file)
                if file != new_file:
                    bb.utils.rename(os.path.join(root, file),
                              os.path.join(root, new_file))


    def remove(self, pkgs, with_dependencies=True):
        if not pkgs:
            return

        os.environ['D'] = self.target_rootfs
        os.environ['OFFLINE_ROOT'] = self.target_rootfs
        os.environ['IPKG_OFFLINE_ROOT'] = self.target_rootfs
        os.environ['OPKG_OFFLINE_ROOT'] = self.target_rootfs
        os.environ['INTERCEPT_DIR'] = self.intercepts_dir

        if with_dependencies:
            os.environ['APT_CONFIG'] = self.apt_conf_file
            cmd = "%s purge %s" % (self.apt_get_cmd, ' '.join(pkgs))
        else:
            cmd = "%s --admindir=%s/var/lib/dpkg --instdir=%s" \
                  " -P --force-depends %s" % \
                  (bb.utils.which(os.getenv('PATH'), "dpkg"),
                   self.target_rootfs, self.target_rootfs, ' '.join(pkgs))

        try:
            subprocess.check_output(cmd.split(), stderr=subprocess.STDOUT)
        except subprocess.CalledProcessError as e:
            bb.fatal("Unable to remove packages. Command '%s' "
                     "returned %d:\n%s" % (e.cmd, e.returncode, e.output.decode("utf-8")))

    def write_index(self):
        self.deploy_dir_lock()

        result = self.indexer.write_index()

        self.deploy_dir_unlock()

        if result is not None:
            bb.fatal(result)

    def insert_feeds_uris(self, feed_uris, feed_base_paths, feed_archs):
        if feed_uris == "":
            return


        sources_conf = os.path.join("%s/etc/apt/sources.list"
                                    % self.target_rootfs)
        if not os.path.exists(os.path.dirname(sources_conf)):
            return

        arch_list = []

        if feed_archs is None:
            for arch in self.all_arch_list:
                if not os.path.exists(os.path.join(self.deploy_dir, arch)):
                    continue
                arch_list.append(arch)
        else:
            arch_list = feed_archs.split()

        feed_uris = self.construct_uris(feed_uris.split(), feed_base_paths.split())

        with open(sources_conf, "w+") as sources_file:
            for uri in feed_uris:
                if arch_list:
                    for arch in arch_list:
                        bb.note('Adding dpkg channel at (%s)' % uri)
                        sources_file.write("deb [trusted=yes] %s/%s ./\n" %
                                           (uri, arch))
                else:
                    bb.note('Adding dpkg channel at (%s)' % uri)
                    sources_file.write("deb [trusted=yes] %s ./\n" % uri)

    def _create_configs(self, archs, base_archs):
        base_archs = re.sub(r"_", r"-", base_archs)

        if os.path.exists(self.apt_conf_dir):
            bb.utils.remove(self.apt_conf_dir, True)

        bb.utils.mkdirhier(self.apt_conf_dir)
        bb.utils.mkdirhier(self.apt_conf_dir + "/lists/partial/")
        bb.utils.mkdirhier(self.apt_conf_dir + "/apt.conf.d/")
        bb.utils.mkdirhier(self.apt_conf_dir + "/preferences.d/")

        arch_list = []
        for arch in self.all_arch_list:
            if not os.path.exists(os.path.join(self.deploy_dir, arch)):
                continue
            arch_list.append(arch)

        with open(os.path.join(self.apt_conf_dir, "preferences"), "w+") as prefs_file:
            priority = 801
            for arch in arch_list:
                prefs_file.write(
                    "Package: *\n"
                    "Pin: release l=%s\n"
                    "Pin-Priority: %d\n\n" % (arch, priority))

                priority += 5

            pkg_exclude = self.d.getVar('PACKAGE_EXCLUDE') or ""
            for pkg in pkg_exclude.split():
                prefs_file.write(
                    "Package: %s\n"
                    "Pin: release *\n"
                    "Pin-Priority: -1\n\n" % pkg)

        arch_list.reverse()

        with open(os.path.join(self.apt_conf_dir, "sources.list"), "w+") as sources_file:
            for arch in arch_list:
                sources_file.write("deb [trusted=yes] file:%s/ ./\n" %
                                   os.path.join(self.deploy_dir, arch))

        base_arch_list = base_archs.split()
        multilib_variants = self.d.getVar("MULTILIB_VARIANTS");
        for variant in multilib_variants.split():
            localdata = bb.data.createCopy(self.d)
            variant_tune = localdata.getVar("DEFAULTTUNE:virtclass-multilib-" + variant, False)
            orig_arch = localdata.getVar("DPKG_ARCH")
            localdata.setVar("DEFAULTTUNE", variant_tune)
            variant_arch = localdata.getVar("DPKG_ARCH")
            if variant_arch not in base_arch_list:
                base_arch_list.append(variant_arch)

        with open(self.apt_conf_file, "w+") as apt_conf:
            with open(self.d.expand("${STAGING_ETCDIR_NATIVE}/apt/apt.conf.sample")) as apt_conf_sample:
                for line in apt_conf_sample.read().split("\n"):
                    match_arch = re.match(r"  Architecture \".*\";$", line)
                    architectures = ""
                    if match_arch:
                        for base_arch in base_arch_list:
                            architectures += "\"%s\";" % base_arch
                        apt_conf.write("  Architectures {%s};\n" % architectures);
                        apt_conf.write("  Architecture \"%s\";\n" % base_archs)
                    else:
                        line = re.sub(r"#ROOTFS#", self.target_rootfs, line)
                        line = re.sub(r"#APTCONF#", self.apt_conf_dir, line)
                        apt_conf.write(line + "\n")

        target_dpkg_dir = "%s/var/lib/dpkg" % self.target_rootfs
        bb.utils.mkdirhier(os.path.join(target_dpkg_dir, "info"))

        bb.utils.mkdirhier(os.path.join(target_dpkg_dir, "updates"))

        if not os.path.exists(os.path.join(target_dpkg_dir, "status")):
            open(os.path.join(target_dpkg_dir, "status"), "w+").close()
        if not os.path.exists(os.path.join(target_dpkg_dir, "available")):
            open(os.path.join(target_dpkg_dir, "available"), "w+").close()

    def remove_packaging_data(self):
        bb.utils.remove(self.target_rootfs + self.d.getVar('opkglibdir'), True)
        bb.utils.remove(self.target_rootfs + "/var/lib/dpkg/", True)

    def fix_broken_dependencies(self):
        os.environ['APT_CONFIG'] = self.apt_conf_file

        cmd = "%s %s --allow-unauthenticated -f install" % (self.apt_get_cmd, self.apt_args)

        try:
            subprocess.check_output(cmd.split(), stderr=subprocess.STDOUT)
        except subprocess.CalledProcessError as e:
            bb.fatal("Cannot fix broken dependencies. Command '%s' "
                     "returned %d:\n%s" % (cmd, e.returncode, e.output.decode("utf-8")))

    def list_installed(self):
        return PMPkgsList(self.d, self.target_rootfs).list_pkgs()

    def package_info(self, pkg):
        """
        Returns a dictionary with the package info.
        """
        cmd = "%s show %s" % (self.apt_cache_cmd, pkg)
        pkg_info = self._common_package_info(cmd)

        pkg_arch = pkg_info[pkg]["pkgarch"]
        pkg_filename = pkg_info[pkg]["filename"]
        pkg_info[pkg]["filepath"] = \
                os.path.join(self.deploy_dir, pkg_arch, pkg_filename)

        return pkg_info
