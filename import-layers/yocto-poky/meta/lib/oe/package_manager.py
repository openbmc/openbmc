from abc import ABCMeta, abstractmethod
import os
import glob
import subprocess
import shutil
import multiprocessing
import re
import bb
import tempfile
import oe.utils
import string
from oe.gpg_sign import get_signer

# this can be used by all PM backends to create the index files in parallel
def create_index(arg):
    index_cmd = arg

    try:
        bb.note("Executing '%s' ..." % index_cmd)
        result = subprocess.check_output(index_cmd, stderr=subprocess.STDOUT, shell=True)
    except subprocess.CalledProcessError as e:
        return("Index creation command '%s' failed with return code %d:\n%s" %
               (e.cmd, e.returncode, e.output))

    if result:
        bb.note(result)

    return None


class Indexer(object):
    __metaclass__ = ABCMeta

    def __init__(self, d, deploy_dir):
        self.d = d
        self.deploy_dir = deploy_dir

    @abstractmethod
    def write_index(self):
        pass


class RpmIndexer(Indexer):
    def get_ml_prefix_and_os_list(self, arch_var=None, os_var=None):
        package_archs = {
            'default': [],
        }

        target_os = {
            'default': "",
        }

        if arch_var is not None and os_var is not None:
            package_archs['default'] = self.d.getVar(arch_var, True).split()
            package_archs['default'].reverse()
            target_os['default'] = self.d.getVar(os_var, True).strip()
        else:
            package_archs['default'] = self.d.getVar("PACKAGE_ARCHS", True).split()
            # arch order is reversed.  This ensures the -best- match is
            # listed first!
            package_archs['default'].reverse()
            target_os['default'] = self.d.getVar("TARGET_OS", True).strip()
            multilibs = self.d.getVar('MULTILIBS', True) or ""
            for ext in multilibs.split():
                eext = ext.split(':')
                if len(eext) > 1 and eext[0] == 'multilib':
                    localdata = bb.data.createCopy(self.d)
                    default_tune_key = "DEFAULTTUNE_virtclass-multilib-" + eext[1]
                    default_tune = localdata.getVar(default_tune_key, False)
                    if default_tune is None:
                        default_tune_key = "DEFAULTTUNE_ML_" + eext[1]
                        default_tune = localdata.getVar(default_tune_key, False)
                    if default_tune:
                        localdata.setVar("DEFAULTTUNE", default_tune)
                        bb.data.update_data(localdata)
                        package_archs[eext[1]] = localdata.getVar('PACKAGE_ARCHS',
                                                                  True).split()
                        package_archs[eext[1]].reverse()
                        target_os[eext[1]] = localdata.getVar("TARGET_OS",
                                                              True).strip()

        ml_prefix_list = dict()
        for mlib in package_archs:
            if mlib == 'default':
                ml_prefix_list[mlib] = package_archs[mlib]
            else:
                ml_prefix_list[mlib] = list()
                for arch in package_archs[mlib]:
                    if arch in ['all', 'noarch', 'any']:
                        ml_prefix_list[mlib].append(arch)
                    else:
                        ml_prefix_list[mlib].append(mlib + "_" + arch)

        return (ml_prefix_list, target_os)

    def write_index(self):
        sdk_pkg_archs = (self.d.getVar('SDK_PACKAGE_ARCHS', True) or "").replace('-', '_').split()
        all_mlb_pkg_archs = (self.d.getVar('ALL_MULTILIB_PACKAGE_ARCHS', True) or "").replace('-', '_').split()

        mlb_prefix_list = self.get_ml_prefix_and_os_list()[0]

        archs = set()
        for item in mlb_prefix_list:
            archs = archs.union(set(i.replace('-', '_') for i in mlb_prefix_list[item]))

        if len(archs) == 0:
            archs = archs.union(set(all_mlb_pkg_archs))

        archs = archs.union(set(sdk_pkg_archs))

        rpm_createrepo = bb.utils.which(os.getenv('PATH'), "createrepo")
        if self.d.getVar('PACKAGE_FEED_SIGN', True) == '1':
            signer = get_signer(self.d, self.d.getVar('PACKAGE_FEED_GPG_BACKEND', True))
        else:
            signer = None
        index_cmds = []
        repomd_files = []
        rpm_dirs_found = False
        for arch in archs:
            dbpath = os.path.join(self.d.getVar('WORKDIR', True), 'rpmdb', arch)
            if os.path.exists(dbpath):
                bb.utils.remove(dbpath, True)
            arch_dir = os.path.join(self.deploy_dir, arch)
            if not os.path.isdir(arch_dir):
                continue

            index_cmds.append("%s --dbpath %s --update -q %s" % \
                             (rpm_createrepo, dbpath, arch_dir))
            repomd_files.append(os.path.join(arch_dir, 'repodata', 'repomd.xml'))

            rpm_dirs_found = True

        if not rpm_dirs_found:
            bb.note("There are no packages in %s" % self.deploy_dir)
            return

        # Create repodata
        result = oe.utils.multiprocess_exec(index_cmds, create_index)
        if result:
            bb.fatal('%s' % ('\n'.join(result)))
        # Sign repomd
        if signer:
            for repomd in repomd_files:
                feed_sig_type = self.d.getVar('PACKAGE_FEED_GPG_SIGNATURE_TYPE', True)
                is_ascii_sig = (feed_sig_type.upper() != "BIN")
                signer.detach_sign(repomd,
                                   self.d.getVar('PACKAGE_FEED_GPG_NAME', True),
                                   self.d.getVar('PACKAGE_FEED_GPG_PASSPHRASE_FILE', True),
                                   armor=is_ascii_sig)


class OpkgIndexer(Indexer):
    def write_index(self):
        arch_vars = ["ALL_MULTILIB_PACKAGE_ARCHS",
                     "SDK_PACKAGE_ARCHS",
                     "MULTILIB_ARCHS"]

        opkg_index_cmd = bb.utils.which(os.getenv('PATH'), "opkg-make-index")
        if self.d.getVar('PACKAGE_FEED_SIGN', True) == '1':
            signer = get_signer(self.d, self.d.getVar('PACKAGE_FEED_GPG_BACKEND', True))
        else:
            signer = None

        if not os.path.exists(os.path.join(self.deploy_dir, "Packages")):
            open(os.path.join(self.deploy_dir, "Packages"), "w").close()

        index_cmds = set()
        index_sign_files = set()
        for arch_var in arch_vars:
            archs = self.d.getVar(arch_var, True)
            if archs is None:
                continue

            for arch in archs.split():
                pkgs_dir = os.path.join(self.deploy_dir, arch)
                pkgs_file = os.path.join(pkgs_dir, "Packages")

                if not os.path.isdir(pkgs_dir):
                    continue

                if not os.path.exists(pkgs_file):
                    open(pkgs_file, "w").close()

                index_cmds.add('%s -r %s -p %s -m %s' %
                                  (opkg_index_cmd, pkgs_file, pkgs_file, pkgs_dir))

                index_sign_files.add(pkgs_file)

        if len(index_cmds) == 0:
            bb.note("There are no packages in %s!" % self.deploy_dir)
            return

        result = oe.utils.multiprocess_exec(index_cmds, create_index)
        if result:
            bb.fatal('%s' % ('\n'.join(result)))

        if signer:
            feed_sig_type = self.d.getVar('PACKAGE_FEED_GPG_SIGNATURE_TYPE', True)
            is_ascii_sig = (feed_sig_type.upper() != "BIN")
            for f in index_sign_files:
                signer.detach_sign(f,
                                   self.d.getVar('PACKAGE_FEED_GPG_NAME', True),
                                   self.d.getVar('PACKAGE_FEED_GPG_PASSPHRASE_FILE', True),
                                   armor=is_ascii_sig)


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
                    line = re.sub("#ROOTFS#", "/dev/null", line)
                    line = re.sub("#APTCONF#", self.apt_conf_dir, line)
                    apt_conf.write(line + "\n")

    def write_index(self):
        self.apt_conf_dir = os.path.join(self.d.expand("${APTCONF_TARGET}"),
                "apt-ftparchive")
        self.apt_conf_file = os.path.join(self.apt_conf_dir, "apt.conf")
        self._create_configs()

        os.environ['APT_CONFIG'] = self.apt_conf_file

        pkg_archs = self.d.getVar('PACKAGE_ARCHS', True)
        if pkg_archs is not None:
            arch_list = pkg_archs.split()
        sdk_pkg_archs = self.d.getVar('SDK_PACKAGE_ARCHS', True)
        if sdk_pkg_archs is not None:
            for a in sdk_pkg_archs.split():
                if a not in pkg_archs:
                    arch_list.append(a)

        all_mlb_pkg_arch_list = (self.d.getVar('ALL_MULTILIB_PACKAGE_ARCHS', True) or "").split()
        arch_list.extend(arch for arch in all_mlb_pkg_arch_list if arch not in arch_list)

        apt_ftparchive = bb.utils.which(os.getenv('PATH'), "apt-ftparchive")
        gzip = bb.utils.which(os.getenv('PATH'), "gzip")

        index_cmds = []
        deb_dirs_found = False
        for arch in arch_list:
            arch_dir = os.path.join(self.deploy_dir, arch)
            if not os.path.isdir(arch_dir):
                continue

            cmd = "cd %s; PSEUDO_UNLOAD=1 %s packages . > Packages;" % (arch_dir, apt_ftparchive)

            cmd += "%s -fc Packages > Packages.gz;" % gzip

            with open(os.path.join(arch_dir, "Release"), "w+") as release:
                release.write("Label: %s\n" % arch)

            cmd += "PSEUDO_UNLOAD=1 %s release . >> Release" % apt_ftparchive
            
            index_cmds.append(cmd)

            deb_dirs_found = True

        if not deb_dirs_found:
            bb.note("There are no packages in %s" % self.deploy_dir)
            return

        result = oe.utils.multiprocess_exec(index_cmds, create_index)
        if result:
            bb.fatal('%s' % ('\n'.join(result)))
        if self.d.getVar('PACKAGE_FEED_SIGN', True) == '1':
            raise NotImplementedError('Package feed signing not implementd for dpkg')



class PkgsList(object):
    __metaclass__ = ABCMeta

    def __init__(self, d, rootfs_dir):
        self.d = d
        self.rootfs_dir = rootfs_dir

    @abstractmethod
    def list_pkgs(self):
        pass


    """
    This method parse the output from the package manager
    and return a dictionary with the information of the
    installed packages. This is used whne the packages are
    in deb or ipk format
    """
    def opkg_query(self, cmd_output):
        verregex = re.compile(' \([=<>]* [^ )]*\)')
        output = dict()
        filename = ""
        dep = []
        pkg = ""
        for line in cmd_output.splitlines():
            line = line.rstrip()
            if ':' in line:
                if line.startswith("Package: "):
                    pkg = line.split(": ")[1]
                elif line.startswith("Architecture: "):
                    arch = line.split(": ")[1]
                elif line.startswith("Version: "):
                    ver = line.split(": ")[1]
                elif line.startswith("File: "):
                    filename = line.split(": ")[1]
                elif line.startswith("Depends: "):
                    depends = verregex.sub('', line.split(": ")[1])
                    for depend in depends.split(", "):
                        dep.append(depend)
                elif line.startswith("Recommends: "):
                    recommends = verregex.sub('', line.split(": ")[1])
                    for recommend in recommends.split(", "):
                        dep.append("%s [REC]" % recommend)
            else:
                # IPK doesn't include the filename
                if not filename:
                    filename = "%s_%s_%s.ipk" % (pkg, ver, arch)
                if pkg:
                    output[pkg] = {"arch":arch, "ver":ver,
                            "filename":filename, "deps": dep }
                pkg = ""
                filename = ""
                dep = []

        if pkg:
            if not filename:
                filename = "%s_%s_%s.ipk" % (pkg, ver, arch)
            output[pkg] = {"arch":arch, "ver":ver,
                    "filename":filename, "deps": dep }

        return output


class RpmPkgsList(PkgsList):
    def __init__(self, d, rootfs_dir, arch_var=None, os_var=None):
        super(RpmPkgsList, self).__init__(d, rootfs_dir)

        self.rpm_cmd = bb.utils.which(os.getenv('PATH'), "rpm")
        self.image_rpmlib = os.path.join(self.rootfs_dir, 'var/lib/rpm')

        self.ml_prefix_list, self.ml_os_list = \
            RpmIndexer(d, rootfs_dir).get_ml_prefix_and_os_list(arch_var, os_var)

        # Determine rpm version
        cmd = "%s --version" % self.rpm_cmd
        try:
            output = subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
        except subprocess.CalledProcessError as e:
            bb.fatal("Getting rpm version failed. Command '%s' "
                     "returned %d:\n%s" % (cmd, e.returncode, e.output))

    '''
    Translate the RPM/Smart format names to the OE multilib format names
    '''
    def _pkg_translate_smart_to_oe(self, pkg, arch):
        new_pkg = pkg
        new_arch = arch
        fixed_arch = arch.replace('_', '-')
        found = 0
        for mlib in self.ml_prefix_list:
            for cmp_arch in self.ml_prefix_list[mlib]:
                fixed_cmp_arch = cmp_arch.replace('_', '-')
                if fixed_arch == fixed_cmp_arch:
                    if mlib == 'default':
                        new_pkg = pkg
                        new_arch = cmp_arch
                    else:
                        new_pkg = mlib + '-' + pkg
                        # We need to strip off the ${mlib}_ prefix on the arch
                        new_arch = cmp_arch.replace(mlib + '_', '')

                    # Workaround for bug 3565. Simply look to see if we
                    # know of a package with that name, if not try again!
                    filename = os.path.join(self.d.getVar('PKGDATA_DIR', True),
                                            'runtime-reverse',
                                            new_pkg)
                    if os.path.exists(filename):
                        found = 1
                        break

            if found == 1 and fixed_arch == fixed_cmp_arch:
                break
        #bb.note('%s, %s -> %s, %s' % (pkg, arch, new_pkg, new_arch))
        return new_pkg, new_arch

    def _list_pkg_deps(self):
        cmd = [bb.utils.which(os.getenv('PATH'), "rpmresolve"),
               "-t", self.image_rpmlib]

        try:
            output = subprocess.check_output(cmd, stderr=subprocess.STDOUT).strip()
        except subprocess.CalledProcessError as e:
            bb.fatal("Cannot get the package dependencies. Command '%s' "
                     "returned %d:\n%s" % (' '.join(cmd), e.returncode, e.output))

        return output

    def list_pkgs(self):
        cmd = self.rpm_cmd + ' --root ' + self.rootfs_dir
        cmd += ' -D "_dbpath /var/lib/rpm" -qa'
        cmd += " --qf '[%{NAME} %{ARCH} %{VERSION} %{PACKAGEORIGIN}\n]'"

        try:
            # bb.note(cmd)
            tmp_output = subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True).strip()
        except subprocess.CalledProcessError as e:
            bb.fatal("Cannot get the installed packages list. Command '%s' "
                     "returned %d:\n%s" % (cmd, e.returncode, e.output))

        output = dict()
        deps = dict()
        dependencies = self._list_pkg_deps()

        # Populate deps dictionary for better manipulation
        for line in dependencies.splitlines():
            try:
                pkg, dep = line.split("|")
                if not pkg in deps:
                    deps[pkg] = list()
                if not dep in deps[pkg]:
                    deps[pkg].append(dep)
            except:
                # Ignore any other lines they're debug or errors
                pass

        for line in tmp_output.split('\n'):
            if len(line.strip()) == 0:
                continue
            pkg = line.split()[0]
            arch = line.split()[1]
            ver = line.split()[2]
            dep = deps.get(pkg, [])

            # Skip GPG keys
            if pkg == 'gpg-pubkey':
                continue

            pkgorigin = line.split()[3]
            new_pkg, new_arch = self._pkg_translate_smart_to_oe(pkg, arch)

            output[new_pkg] = {"arch":new_arch, "ver":ver,
                        "filename":pkgorigin, "deps":dep}

        return output


class OpkgPkgsList(PkgsList):
    def __init__(self, d, rootfs_dir, config_file):
        super(OpkgPkgsList, self).__init__(d, rootfs_dir)

        self.opkg_cmd = bb.utils.which(os.getenv('PATH'), "opkg")
        self.opkg_args = "-f %s -o %s " % (config_file, rootfs_dir)
        self.opkg_args += self.d.getVar("OPKG_ARGS", True)

    def list_pkgs(self, format=None):
        cmd = "%s %s status" % (self.opkg_cmd, self.opkg_args)

        # opkg returns success even when it printed some
        # "Collected errors:" report to stderr. Mixing stderr into
        # stdout then leads to random failures later on when
        # parsing the output. To avoid this we need to collect both
        # output streams separately and check for empty stderr.
        p = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        cmd_output, cmd_stderr = p.communicate()
        if p.returncode or cmd_stderr:
            bb.fatal("Cannot get the installed packages list. Command '%s' "
                     "returned %d and stderr:\n%s" % (cmd, p.returncode, cmd_stderr))

        return self.opkg_query(cmd_output)


class DpkgPkgsList(PkgsList):

    def list_pkgs(self):
        cmd = [bb.utils.which(os.getenv('PATH'), "dpkg-query"),
               "--admindir=%s/var/lib/dpkg" % self.rootfs_dir,
               "-W"]

        cmd.append("-f=Package: ${Package}\nArchitecture: ${PackageArch}\nVersion: ${Version}\nFile: ${Package}_${Version}_${Architecture}.deb\nDepends: ${Depends}\nRecommends: ${Recommends}\n\n")

        try:
            cmd_output = subprocess.check_output(cmd, stderr=subprocess.STDOUT).strip()
        except subprocess.CalledProcessError as e:
            bb.fatal("Cannot get the installed packages list. Command '%s' "
                     "returned %d:\n%s" % (' '.join(cmd), e.returncode, e.output))

        return self.opkg_query(cmd_output)


class PackageManager(object):
    """
    This is an abstract class. Do not instantiate this directly.
    """
    __metaclass__ = ABCMeta

    def __init__(self, d):
        self.d = d
        self.deploy_dir = None
        self.deploy_lock = None
        self.feed_uris = self.d.getVar('PACKAGE_FEED_URIS', True) or ""
        self.feed_base_paths = self.d.getVar('PACKAGE_FEED_BASE_PATHS', True) or ""
        self.feed_archs = self.d.getVar('PACKAGE_FEED_ARCHS', True)

    """
    Update the package manager package database.
    """
    @abstractmethod
    def update(self):
        pass

    """
    Install a list of packages. 'pkgs' is a list object. If 'attempt_only' is
    True, installation failures are ignored.
    """
    @abstractmethod
    def install(self, pkgs, attempt_only=False):
        pass

    """
    Remove a list of packages. 'pkgs' is a list object. If 'with_dependencies'
    is False, the any dependencies are left in place.
    """
    @abstractmethod
    def remove(self, pkgs, with_dependencies=True):
        pass

    """
    This function creates the index files
    """
    @abstractmethod
    def write_index(self):
        pass

    @abstractmethod
    def remove_packaging_data(self):
        pass

    @abstractmethod
    def list_installed(self):
        pass

    @abstractmethod
    def insert_feeds_uris(self):
        pass

    """
    Install complementary packages based upon the list of currently installed
    packages e.g. locales, *-dev, *-dbg, etc. This will only attempt to install
    these packages, if they don't exist then no error will occur.  Note: every
    backend needs to call this function explicitly after the normal package
    installation
    """
    def install_complementary(self, globs=None):
        # we need to write the list of installed packages to a file because the
        # oe-pkgdata-util reads it from a file
        installed_pkgs_file = os.path.join(self.d.getVar('WORKDIR', True),
                                           "installed_pkgs.txt")
        with open(installed_pkgs_file, "w+") as installed_pkgs:
            pkgs = self.list_installed()
            output = oe.utils.format_pkg_list(pkgs, "arch")
            installed_pkgs.write(output)

        if globs is None:
            globs = self.d.getVar('IMAGE_INSTALL_COMPLEMENTARY', True)
            split_linguas = set()

            for translation in self.d.getVar('IMAGE_LINGUAS', True).split():
                split_linguas.add(translation)
                split_linguas.add(translation.split('-')[0])

            split_linguas = sorted(split_linguas)

            for lang in split_linguas:
                globs += " *-locale-%s" % lang

        if globs is None:
            return

        cmd = [bb.utils.which(os.getenv('PATH'), "oe-pkgdata-util"),
               "-p", self.d.getVar('PKGDATA_DIR', True), "glob", installed_pkgs_file,
               globs]
        exclude = self.d.getVar('PACKAGE_EXCLUDE_COMPLEMENTARY', True)
        if exclude:
            cmd.extend(['-x', exclude])
        try:
            bb.note("Installing complementary packages ...")
            bb.note('Running %s' % cmd)
            complementary_pkgs = subprocess.check_output(cmd, stderr=subprocess.STDOUT)
        except subprocess.CalledProcessError as e:
            bb.fatal("Could not compute complementary packages list. Command "
                     "'%s' returned %d:\n%s" %
                     (' '.join(cmd), e.returncode, e.output))
        self.install(complementary_pkgs.split(), attempt_only=True)
        os.remove(installed_pkgs_file)

    def deploy_dir_lock(self):
        if self.deploy_dir is None:
            raise RuntimeError("deploy_dir is not set!")

        lock_file_name = os.path.join(self.deploy_dir, "deploy.lock")

        self.deploy_lock = bb.utils.lockfile(lock_file_name)

    def deploy_dir_unlock(self):
        if self.deploy_lock is None:
            return

        bb.utils.unlockfile(self.deploy_lock)

        self.deploy_lock = None

    """
    Construct URIs based on the following pattern: uri/base_path where 'uri'
    and 'base_path' correspond to each element of the corresponding array
    argument leading to len(uris) x len(base_paths) elements on the returned
    array
    """
    def construct_uris(self, uris, base_paths):
        def _append(arr1, arr2, sep='/'):
            res = []
            narr1 = map(lambda a: string.rstrip(a, sep), arr1)
            narr2 = map(lambda a: string.lstrip(string.rstrip(a, sep), sep), arr2)
            for a1 in narr1:
                if arr2:
                    for a2 in narr2:
                        res.append("%s%s%s" % (a1, sep, a2))
                else:
                    res.append(a1)
            return res
        return _append(uris, base_paths)

class RpmPM(PackageManager):
    def __init__(self,
                 d,
                 target_rootfs,
                 target_vendor,
                 task_name='target',
                 providename=None,
                 arch_var=None,
                 os_var=None):
        super(RpmPM, self).__init__(d)
        self.target_rootfs = target_rootfs
        self.target_vendor = target_vendor
        self.task_name = task_name
        self.providename = providename
        self.fullpkglist = list()
        self.deploy_dir = self.d.getVar('DEPLOY_DIR_RPM', True)
        self.etcrpm_dir = os.path.join(self.target_rootfs, "etc/rpm")
        self.install_dir_name = "oe_install"
        self.install_dir_path = os.path.join(self.target_rootfs, self.install_dir_name)
        self.rpm_cmd = bb.utils.which(os.getenv('PATH'), "rpm")
        self.smart_cmd = bb.utils.which(os.getenv('PATH'), "smart")
        # 0 = default, only warnings
        # 1 = --log-level=info (includes information about executing scriptlets and their output)
        # 2 = --log-level=debug
        # 3 = --log-level=debug plus dumps of scriplet content and command invocation
        self.debug_level = int(d.getVar('ROOTFS_RPM_DEBUG', True) or "0")
        self.smart_opt = "--log-level=%s --data-dir=%s" % \
                         ("warning" if self.debug_level == 0 else
                          "info" if self.debug_level == 1 else
                          "debug",
                          os.path.join(target_rootfs, 'var/lib/smart'))
        self.scriptlet_wrapper = self.d.expand('${WORKDIR}/scriptlet_wrapper')
        self.solution_manifest = self.d.expand('${T}/saved/%s_solution' %
                                               self.task_name)
        self.saved_rpmlib = self.d.expand('${T}/saved/%s' % self.task_name)
        self.image_rpmlib = os.path.join(self.target_rootfs, 'var/lib/rpm')

        if not os.path.exists(self.d.expand('${T}/saved')):
            bb.utils.mkdirhier(self.d.expand('${T}/saved'))

        self.indexer = RpmIndexer(self.d, self.deploy_dir)
        self.pkgs_list = RpmPkgsList(self.d, self.target_rootfs, arch_var, os_var)

        self.ml_prefix_list, self.ml_os_list = self.indexer.get_ml_prefix_and_os_list(arch_var, os_var)

    def insert_feeds_uris(self):
        if self.feed_uris == "":
            return

        arch_list = []
        if self.feed_archs is not None:
            # User define feed architectures
            arch_list = self.feed_archs.split()
        else:
            # List must be prefered to least preferred order
            default_platform_extra = set()
            platform_extra = set()
            bbextendvariant = self.d.getVar('BBEXTENDVARIANT', True) or ""
            for mlib in self.ml_os_list:
                for arch in self.ml_prefix_list[mlib]:
                    plt = arch.replace('-', '_') + '-.*-' + self.ml_os_list[mlib]
                    if mlib == bbextendvariant:
                            default_platform_extra.add(plt)
                    else:
                            platform_extra.add(plt)

            platform_extra = platform_extra.union(default_platform_extra)

            for canonical_arch in platform_extra:
                arch = canonical_arch.split('-')[0]
                if not os.path.exists(os.path.join(self.deploy_dir, arch)):
                    continue
                arch_list.append(arch)

        feed_uris = self.construct_uris(self.feed_uris.split(), self.feed_base_paths.split())

        uri_iterator = 0
        channel_priority = 10 + 5 * len(feed_uris) * (len(arch_list) if arch_list else 1)

        for uri in feed_uris:
            if arch_list:
                for arch in arch_list:
                    bb.note('Note: adding Smart channel url%d%s (%s)' %
                            (uri_iterator, arch, channel_priority))
                    self._invoke_smart('channel --add url%d-%s type=rpm-md baseurl=%s/%s -y'
                                       % (uri_iterator, arch, uri, arch))
                    self._invoke_smart('channel --set url%d-%s priority=%d' %
                                       (uri_iterator, arch, channel_priority))
                    channel_priority -= 5
            else:
                bb.note('Note: adding Smart channel url%d (%s)' %
                        (uri_iterator, channel_priority))
                self._invoke_smart('channel --add url%d type=rpm-md baseurl=%s -y'
                                   % (uri_iterator, uri))
                self._invoke_smart('channel --set url%d priority=%d' %
                                   (uri_iterator, channel_priority))
                channel_priority -= 5

            uri_iterator += 1

    '''
    Create configs for rpm and smart, and multilib is supported
    '''
    def create_configs(self):
        target_arch = self.d.getVar('TARGET_ARCH', True)
        platform = '%s%s-%s' % (target_arch.replace('-', '_'),
                                self.target_vendor,
                                self.ml_os_list['default'])

        # List must be prefered to least preferred order
        default_platform_extra = list()
        platform_extra = list()
        bbextendvariant = self.d.getVar('BBEXTENDVARIANT', True) or ""
        for mlib in self.ml_os_list:
            for arch in self.ml_prefix_list[mlib]:
                plt = arch.replace('-', '_') + '-.*-' + self.ml_os_list[mlib]
                if mlib == bbextendvariant:
                    if plt not in default_platform_extra:
                        default_platform_extra.append(plt)
                else:
                    if plt not in platform_extra:
                        platform_extra.append(plt)
        platform_extra = default_platform_extra + platform_extra

        self._create_configs(platform, platform_extra)

    def _invoke_smart(self, args):
        cmd = "%s %s %s" % (self.smart_cmd, self.smart_opt, args)
        # bb.note(cmd)
        try:
            complementary_pkgs = subprocess.check_output(cmd,
                                                         stderr=subprocess.STDOUT,
                                                         shell=True)
            # bb.note(complementary_pkgs)
            return complementary_pkgs
        except subprocess.CalledProcessError as e:
            bb.fatal("Could not invoke smart. Command "
                     "'%s' returned %d:\n%s" % (cmd, e.returncode, e.output))

    def _search_pkg_name_in_feeds(self, pkg, feed_archs):
        for arch in feed_archs:
            arch = arch.replace('-', '_')
            regex_match = re.compile(r"^%s-[^-]*-[^-]*@%s$" % \
                (re.escape(pkg), re.escape(arch)))
            for p in self.fullpkglist:
                if regex_match.match(p) is not None:
                    # First found is best match
                    # bb.note('%s -> %s' % (pkg, pkg + '@' + arch))
                    return pkg + '@' + arch

        # Search provides if not found by pkgname.
        bb.note('Not found %s by name, searching provides ...' % pkg)
        cmd = "%s %s query --provides %s --show-format='$name-$version'" % \
                (self.smart_cmd, self.smart_opt, pkg)
        cmd += " | sed -ne 's/ *Provides://p'"
        bb.note('cmd: %s' % cmd)
        output = subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
        # Found a provider
        if output:
            bb.note('Found providers for %s: %s' % (pkg, output))
            for p in output.split():
                for arch in feed_archs:
                    arch = arch.replace('-', '_')
                    if p.rstrip().endswith('@' + arch):
                        return p

        return ""

    '''
    Translate the OE multilib format names to the RPM/Smart format names
    It searched the RPM/Smart format names in probable multilib feeds first,
    and then searched the default base feed.
    '''
    def _pkg_translate_oe_to_smart(self, pkgs, attempt_only=False):
        new_pkgs = list()

        for pkg in pkgs:
            new_pkg = pkg
            # Search new_pkg in probable multilibs first
            for mlib in self.ml_prefix_list:
                # Jump the default archs
                if mlib == 'default':
                    continue

                subst = pkg.replace(mlib + '-', '')
                # if the pkg in this multilib feed
                if subst != pkg:
                    feed_archs = self.ml_prefix_list[mlib]
                    new_pkg = self._search_pkg_name_in_feeds(subst, feed_archs)
                    if not new_pkg:
                        # Failed to translate, package not found!
                        err_msg = '%s not found in the %s feeds (%s).\n' % \
                                  (pkg, mlib, " ".join(feed_archs))
                        if not attempt_only:
                            err_msg += " ".join(self.fullpkglist)
                            bb.fatal(err_msg)
                        bb.warn(err_msg)
                    else:
                        new_pkgs.append(new_pkg)

                    break

            # Apparently not a multilib package...
            if pkg == new_pkg:
                # Search new_pkg in default archs
                default_archs = self.ml_prefix_list['default']
                new_pkg = self._search_pkg_name_in_feeds(pkg, default_archs)
                if not new_pkg:
                    err_msg = '%s not found in the base feeds (%s).\n' % \
                              (pkg, ' '.join(default_archs))
                    if not attempt_only:
                        err_msg += " ".join(self.fullpkglist)
                        bb.fatal(err_msg)
                    bb.warn(err_msg)
                else:
                    new_pkgs.append(new_pkg)

        return new_pkgs

    def _create_configs(self, platform, platform_extra):
        # Setup base system configuration
        bb.note("configuring RPM platform settings")

        # Configure internal RPM environment when using Smart
        os.environ['RPM_ETCRPM'] = self.etcrpm_dir
        bb.utils.mkdirhier(self.etcrpm_dir)

        # Setup temporary directory -- install...
        if os.path.exists(self.install_dir_path):
            bb.utils.remove(self.install_dir_path, True)
        bb.utils.mkdirhier(os.path.join(self.install_dir_path, 'tmp'))

        channel_priority = 5
        platform_dir = os.path.join(self.etcrpm_dir, "platform")
        sdkos = self.d.getVar("SDK_OS", True)
        with open(platform_dir, "w+") as platform_fd:
            platform_fd.write(platform + '\n')
            for pt in platform_extra:
                channel_priority += 5
                if sdkos:
                    tmp = re.sub("-%s$" % sdkos, "-%s\n" % sdkos, pt)
                tmp = re.sub("-linux.*$", "-linux.*\n", tmp)
                platform_fd.write(tmp)

        # Tell RPM that the "/" directory exist and is available
        bb.note("configuring RPM system provides")
        sysinfo_dir = os.path.join(self.etcrpm_dir, "sysinfo")
        bb.utils.mkdirhier(sysinfo_dir)
        with open(os.path.join(sysinfo_dir, "Dirnames"), "w+") as dirnames:
            dirnames.write("/\n")

        if self.providename:
            providename_dir = os.path.join(sysinfo_dir, "Providename")
            if not os.path.exists(providename_dir):
                providename_content = '\n'.join(self.providename)
                providename_content += '\n'
                open(providename_dir, "w+").write(providename_content)

        # Configure RPM... we enforce these settings!
        bb.note("configuring RPM DB settings")
        # After change the __db.* cache size, log file will not be
        # generated automatically, that will raise some warnings,
        # so touch a bare log for rpm write into it.
        rpmlib_log = os.path.join(self.image_rpmlib, 'log', 'log.0000000001')
        if not os.path.exists(rpmlib_log):
            bb.utils.mkdirhier(os.path.join(self.image_rpmlib, 'log'))
            open(rpmlib_log, 'w+').close()

        DB_CONFIG_CONTENT = "# ================ Environment\n" \
            "set_data_dir .\n" \
            "set_create_dir .\n" \
            "set_lg_dir ./log\n" \
            "set_tmp_dir ./tmp\n" \
            "set_flags db_log_autoremove on\n" \
            "\n" \
            "# -- thread_count must be >= 8\n" \
            "set_thread_count 64\n" \
            "\n" \
            "# ================ Logging\n" \
            "\n" \
            "# ================ Memory Pool\n" \
            "set_cachesize 0 1048576 0\n" \
            "set_mp_mmapsize 268435456\n" \
            "\n" \
            "# ================ Locking\n" \
            "set_lk_max_locks 16384\n" \
            "set_lk_max_lockers 16384\n" \
            "set_lk_max_objects 16384\n" \
            "mutex_set_max 163840\n" \
            "\n" \
            "# ================ Replication\n"

        db_config_dir = os.path.join(self.image_rpmlib, 'DB_CONFIG')
        if not os.path.exists(db_config_dir):
            open(db_config_dir, 'w+').write(DB_CONFIG_CONTENT)

        # Create database so that smart doesn't complain (lazy init)
        opt = "-qa"
        cmd = "%s --root %s --dbpath /var/lib/rpm %s > /dev/null" % (
              self.rpm_cmd, self.target_rootfs, opt)
        try:
            subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
        except subprocess.CalledProcessError as e:
            bb.fatal("Create rpm database failed. Command '%s' "
                     "returned %d:\n%s" % (cmd, e.returncode, e.output))
        # Import GPG key to RPM database of the target system
        if self.d.getVar('RPM_SIGN_PACKAGES', True) == '1':
            pubkey_path = self.d.getVar('RPM_GPG_PUBKEY', True)
            cmd = "%s --root %s --dbpath /var/lib/rpm --import %s > /dev/null" % (
                  self.rpm_cmd, self.target_rootfs, pubkey_path)
            subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)

        # Configure smart
        bb.note("configuring Smart settings")
        bb.utils.remove(os.path.join(self.target_rootfs, 'var/lib/smart'),
                        True)
        self._invoke_smart('config --set rpm-root=%s' % self.target_rootfs)
        self._invoke_smart('config --set rpm-dbpath=/var/lib/rpm')
        self._invoke_smart('config --set rpm-extra-macros._var=%s' %
                           self.d.getVar('localstatedir', True))
        cmd = "config --set rpm-extra-macros._tmppath=/%s/tmp" % (self.install_dir_name)

        prefer_color = self.d.getVar('RPM_PREFER_ELF_ARCH', True)
        if prefer_color:
            if prefer_color not in ['0', '1', '2', '4']:
                bb.fatal("Invalid RPM_PREFER_ELF_ARCH: %s, it should be one of:\n"
                        "\t1: ELF32 wins\n"
                        "\t2: ELF64 wins\n"
                        "\t4: ELF64 N32 wins (mips64 or mips64el only)" %
                        prefer_color)
            if prefer_color == "4" and self.d.getVar("TUNE_ARCH", True) not in \
                                    ['mips64', 'mips64el']:
                bb.fatal("RPM_PREFER_ELF_ARCH = \"4\" is for mips64 or mips64el "
                         "only.")
            self._invoke_smart('config --set rpm-extra-macros._prefer_color=%s'
                        % prefer_color)

        self._invoke_smart(cmd)
        self._invoke_smart('config --set rpm-ignoresize=1')

        # Write common configuration for host and target usage
        self._invoke_smart('config --set rpm-nolinktos=1')
        self._invoke_smart('config --set rpm-noparentdirs=1')
        check_signature = self.d.getVar('RPM_CHECK_SIGNATURES', True)
        if check_signature and check_signature.strip() == "0":
            self._invoke_smart('config --set rpm-check-signatures=false')
        for i in self.d.getVar('BAD_RECOMMENDATIONS', True).split():
            self._invoke_smart('flag --set ignore-recommends %s' % i)

        # Do the following configurations here, to avoid them being
        # saved for field upgrade
        if self.d.getVar('NO_RECOMMENDATIONS', True).strip() == "1":
            self._invoke_smart('config --set ignore-all-recommends=1')
        pkg_exclude = self.d.getVar('PACKAGE_EXCLUDE', True) or ""
        for i in pkg_exclude.split():
            self._invoke_smart('flag --set exclude-packages %s' % i)

        # Optional debugging
        # self._invoke_smart('config --set rpm-log-level=debug')
        # cmd = 'config --set rpm-log-file=/tmp/smart-debug-logfile'
        # self._invoke_smart(cmd)
        ch_already_added = []
        for canonical_arch in platform_extra:
            arch = canonical_arch.split('-')[0]
            arch_channel = os.path.join(self.deploy_dir, arch)
            if os.path.exists(arch_channel) and not arch in ch_already_added:
                bb.note('Note: adding Smart channel %s (%s)' %
                        (arch, channel_priority))
                self._invoke_smart('channel --add %s type=rpm-md baseurl=%s -y'
                                   % (arch, arch_channel))
                self._invoke_smart('channel --set %s priority=%d' %
                                   (arch, channel_priority))
                channel_priority -= 5

                ch_already_added.append(arch)

        bb.note('adding Smart RPM DB channel')
        self._invoke_smart('channel --add rpmsys type=rpm-sys -y')

        # Construct install scriptlet wrapper.
        # Scripts need to be ordered when executed, this ensures numeric order.
        # If we ever run into needing more the 899 scripts, we'll have to.
        # change num to start with 1000.
        #
        scriptletcmd = "$2 $1/$3 $4\n"
        scriptpath = "$1/$3"

        # When self.debug_level >= 3, also dump the content of the
        # executed scriptlets and how they get invoked.  We have to
        # replace "exit 1" and "ERR" because printing those as-is
        # would trigger a log analysis failure.
        if self.debug_level >= 3:
            dump_invocation = 'echo "Executing ${name} ${kind} with: ' + scriptletcmd + '"\n'
            dump_script = 'cat ' + scriptpath + '| sed -e "s/exit 1/exxxit 1/g" -e "s/ERR/IRR/g"; echo\n'
        else:
            dump_invocation = 'echo "Executing ${name} ${kind}"\n'
            dump_script = ''

        SCRIPTLET_FORMAT = "#!/bin/bash\n" \
            "\n" \
            "export PATH=%s\n" \
            "export D=%s\n" \
            'export OFFLINE_ROOT="$D"\n' \
            'export IPKG_OFFLINE_ROOT="$D"\n' \
            'export OPKG_OFFLINE_ROOT="$D"\n' \
            "export INTERCEPT_DIR=%s\n" \
            "export NATIVE_ROOT=%s\n" \
            "\n" \
            "name=`head -1 " + scriptpath + " | cut -d\' \' -f 2`\n" \
            "kind=`head -1 " + scriptpath + " | cut -d\' \' -f 4`\n" \
            + dump_invocation \
            + dump_script \
            + scriptletcmd + \
            "ret=$?\n" \
            "echo Result of ${name} ${kind}: ${ret}\n" \
            "if [ ${ret} -ne 0 ]; then\n" \
            "  if [ $4 -eq 1 ]; then\n" \
            "    mkdir -p $1/etc/rpm-postinsts\n" \
            "    num=100\n" \
            "    while [ -e $1/etc/rpm-postinsts/${num}-* ]; do num=$((num + 1)); done\n" \
            '    echo "#!$2" > $1/etc/rpm-postinsts/${num}-${name}\n' \
            '    echo "# Arg: $4" >> $1/etc/rpm-postinsts/${num}-${name}\n' \
            "    cat " + scriptpath + " >> $1/etc/rpm-postinsts/${num}-${name}\n" \
            "    chmod +x $1/etc/rpm-postinsts/${num}-${name}\n" \
            '    echo "Info: deferring ${name} ${kind} install scriptlet to first boot"\n' \
            "  else\n" \
            '    echo "Error: ${name} ${kind} remove scriptlet failed"\n' \
            "  fi\n" \
            "fi\n"

        intercept_dir = self.d.expand('${WORKDIR}/intercept_scripts')
        native_root = self.d.getVar('STAGING_DIR_NATIVE', True)
        scriptlet_content = SCRIPTLET_FORMAT % (os.environ['PATH'],
                                                self.target_rootfs,
                                                intercept_dir,
                                                native_root)
        open(self.scriptlet_wrapper, 'w+').write(scriptlet_content)

        bb.note("Note: configuring RPM cross-install scriptlet_wrapper")
        os.chmod(self.scriptlet_wrapper, 0755)
        cmd = 'config --set rpm-extra-macros._cross_scriptlet_wrapper=%s' % \
              self.scriptlet_wrapper
        self._invoke_smart(cmd)

        # Debug to show smart config info
        # bb.note(self._invoke_smart('config --show'))

    def update(self):
        self._invoke_smart('update rpmsys')

    def get_rdepends_recursively(self, pkgs):
        # pkgs will be changed during the loop, so use [:] to make a copy.
        for pkg in pkgs[:]:
            sub_data = oe.packagedata.read_subpkgdata(pkg, self.d)
            sub_rdep = sub_data.get("RDEPENDS_" + pkg)
            if not sub_rdep:
                continue
            done = bb.utils.explode_dep_versions2(sub_rdep).keys()
            next = done
            # Find all the rdepends on dependency chain
            while next:
                new = []
                for sub_pkg in next:
                    sub_data = oe.packagedata.read_subpkgdata(sub_pkg, self.d)
                    sub_pkg_rdep = sub_data.get("RDEPENDS_" + sub_pkg)
                    if not sub_pkg_rdep:
                        continue
                    for p in bb.utils.explode_dep_versions2(sub_pkg_rdep):
                        # Already handled, skip it.
                        if p in done or p in pkgs:
                            continue
                        # It's a new dep
                        if oe.packagedata.has_subpkgdata(p, self.d):
                            done.append(p)
                            new.append(p)
                next = new
            pkgs.extend(done)
        return pkgs

    '''
    Install pkgs with smart, the pkg name is oe format
    '''
    def install(self, pkgs, attempt_only=False):

        if not pkgs:
            bb.note("There are no packages to install")
            return
        bb.note("Installing the following packages: %s" % ' '.join(pkgs))
        if not attempt_only:
            # Pull in multilib requires since rpm may not pull in them
            # correctly, for example,
            # lib32-packagegroup-core-standalone-sdk-target requires
            # lib32-libc6, but rpm may pull in libc6 rather than lib32-libc6
            # since it doesn't know mlprefix (lib32-), bitbake knows it and
            # can handle it well, find out the RDEPENDS on the chain will
            # fix the problem. Both do_rootfs and do_populate_sdk have this
            # issue.
            # The attempt_only packages don't need this since they are
            # based on the installed ones.
            #
            # Separate pkgs into two lists, one is multilib, the other one
            # is non-multilib.
            ml_pkgs = []
            non_ml_pkgs = pkgs[:]
            for pkg in pkgs:
                for mlib in (self.d.getVar("MULTILIB_VARIANTS", True) or "").split():
                    if pkg.startswith(mlib + '-'):
                        ml_pkgs.append(pkg)
                        non_ml_pkgs.remove(pkg)

            if len(ml_pkgs) > 0 and len(non_ml_pkgs) > 0:
                # Found both foo and lib-foo
                ml_pkgs = self.get_rdepends_recursively(ml_pkgs)
                non_ml_pkgs = self.get_rdepends_recursively(non_ml_pkgs)
                # Longer list makes smart slower, so only keep the pkgs
                # which have the same BPN, and smart can handle others
                # correctly.
                pkgs_new = []
                for pkg in non_ml_pkgs:
                    for mlib in (self.d.getVar("MULTILIB_VARIANTS", True) or "").split():
                        mlib_pkg = mlib + "-" + pkg
                        if mlib_pkg in ml_pkgs:
                            pkgs_new.append(pkg)
                            pkgs_new.append(mlib_pkg)
                for pkg in pkgs:
                    if pkg not in pkgs_new:
                        pkgs_new.append(pkg)
                pkgs = pkgs_new
                new_depends = {}
                deps = bb.utils.explode_dep_versions2(" ".join(pkgs))
                for depend in deps:
                    data = oe.packagedata.read_subpkgdata(depend, self.d)
                    key = "PKG_%s" % depend
                    if key in data:
                        new_depend = data[key]
                    else:
                        new_depend = depend
                    new_depends[new_depend] = deps[depend]
                pkgs = bb.utils.join_deps(new_depends, commasep=True).split(', ')
        pkgs = self._pkg_translate_oe_to_smart(pkgs, attempt_only)
        if not attempt_only:
            bb.note('to be installed: %s' % ' '.join(pkgs))
            cmd = "%s %s install -y %s" % \
                  (self.smart_cmd, self.smart_opt, ' '.join(pkgs))
            bb.note(cmd)
        else:
            bb.note('installing attempt only packages...')
            bb.note('Attempting %s' % ' '.join(pkgs))
            cmd = "%s %s install --attempt -y %s" % \
                  (self.smart_cmd, self.smart_opt, ' '.join(pkgs))
        try:
            output = subprocess.check_output(cmd.split(), stderr=subprocess.STDOUT)
            bb.note(output)
        except subprocess.CalledProcessError as e:
            bb.fatal("Unable to install packages. Command '%s' "
                     "returned %d:\n%s" % (cmd, e.returncode, e.output))

    '''
    Remove pkgs with smart, the pkg name is smart/rpm format
    '''
    def remove(self, pkgs, with_dependencies=True):
        bb.note('to be removed: ' + ' '.join(pkgs))

        if not with_dependencies:
            cmd = "%s -e --nodeps " % self.rpm_cmd
            cmd += "--root=%s " % self.target_rootfs
            cmd += "--dbpath=/var/lib/rpm "
            cmd += "--define='_cross_scriptlet_wrapper %s' " % \
                   self.scriptlet_wrapper
            cmd += "--define='_tmppath /%s/tmp' %s" % (self.install_dir_name, ' '.join(pkgs))
        else:
            # for pkg in pkgs:
            #   bb.note('Debug: What required: %s' % pkg)
            #   bb.note(self._invoke_smart('query %s --show-requiredby' % pkg))

            cmd = "%s %s remove -y %s" % (self.smart_cmd,
                                          self.smart_opt,
                                          ' '.join(pkgs))

        try:
            bb.note(cmd)
            output = subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
            bb.note(output)
        except subprocess.CalledProcessError as e:
            bb.note("Unable to remove packages. Command '%s' "
                    "returned %d:\n%s" % (cmd, e.returncode, e.output))

    def upgrade(self):
        bb.note('smart upgrade')
        self._invoke_smart('upgrade')

    def write_index(self):
        result = self.indexer.write_index()

        if result is not None:
            bb.fatal(result)

    def remove_packaging_data(self):
        bb.utils.remove(self.image_rpmlib, True)
        bb.utils.remove(os.path.join(self.target_rootfs, 'var/lib/smart'),
                        True)
        bb.utils.remove(os.path.join(self.target_rootfs, 'var/lib/opkg'), True)

        # remove temp directory
        bb.utils.remove(self.install_dir_path, True)

    def backup_packaging_data(self):
        # Save the rpmlib for increment rpm image generation
        if os.path.exists(self.saved_rpmlib):
            bb.utils.remove(self.saved_rpmlib, True)
        shutil.copytree(self.image_rpmlib,
                        self.saved_rpmlib,
                        symlinks=True)

    def recovery_packaging_data(self):
        # Move the rpmlib back
        if os.path.exists(self.saved_rpmlib):
            if os.path.exists(self.image_rpmlib):
                bb.utils.remove(self.image_rpmlib, True)

            bb.note('Recovery packaging data')
            shutil.copytree(self.saved_rpmlib,
                            self.image_rpmlib,
                            symlinks=True)

    def list_installed(self):
        return self.pkgs_list.list_pkgs()

    '''
    If incremental install, we need to determine what we've got,
    what we need to add, and what to remove...
    The dump_install_solution will dump and save the new install
    solution.
    '''
    def dump_install_solution(self, pkgs):
        bb.note('creating new install solution for incremental install')
        if len(pkgs) == 0:
            return

        pkgs = self._pkg_translate_oe_to_smart(pkgs, False)
        install_pkgs = list()

        cmd = "%s %s install -y --dump %s 2>%s" %  \
              (self.smart_cmd,
               self.smart_opt,
               ' '.join(pkgs),
               self.solution_manifest)
        try:
            # Disable rpmsys channel for the fake install
            self._invoke_smart('channel --disable rpmsys')

            subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
            with open(self.solution_manifest, 'r') as manifest:
                for pkg in manifest.read().split('\n'):
                    if '@' in pkg:
                        install_pkgs.append(pkg)
        except subprocess.CalledProcessError as e:
            bb.note("Unable to dump install packages. Command '%s' "
                    "returned %d:\n%s" % (cmd, e.returncode, e.output))
        # Recovery rpmsys channel
        self._invoke_smart('channel --enable rpmsys')
        return install_pkgs

    '''
    If incremental install, we need to determine what we've got,
    what we need to add, and what to remove...
    The load_old_install_solution will load the previous install
    solution
    '''
    def load_old_install_solution(self):
        bb.note('load old install solution for incremental install')
        installed_pkgs = list()
        if not os.path.exists(self.solution_manifest):
            bb.note('old install solution not exist')
            return installed_pkgs

        with open(self.solution_manifest, 'r') as manifest:
            for pkg in manifest.read().split('\n'):
                if '@' in pkg:
                    installed_pkgs.append(pkg.strip())

        return installed_pkgs

    '''
    Dump all available packages in feeds, it should be invoked after the
    newest rpm index was created
    '''
    def dump_all_available_pkgs(self):
        available_manifest = self.d.expand('${T}/saved/available_pkgs.txt')
        available_pkgs = list()
        cmd = "%s %s query --output %s" %  \
              (self.smart_cmd, self.smart_opt, available_manifest)
        try:
            subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
            with open(available_manifest, 'r') as manifest:
                for pkg in manifest.read().split('\n'):
                    if '@' in pkg:
                        available_pkgs.append(pkg.strip())
        except subprocess.CalledProcessError as e:
            bb.note("Unable to list all available packages. Command '%s' "
                    "returned %d:\n%s" % (cmd, e.returncode, e.output))

        self.fullpkglist = available_pkgs

        return

    def save_rpmpostinst(self, pkg):
        mlibs = (self.d.getVar('MULTILIB_GLOBAL_VARIANTS', False) or "").split()

        new_pkg = pkg
        # Remove any multilib prefix from the package name
        for mlib in mlibs:
            if mlib in pkg:
                new_pkg = pkg.replace(mlib + '-', '')
                break

        bb.note('  * postponing %s' % new_pkg)
        saved_dir = self.target_rootfs + self.d.expand('${sysconfdir}/rpm-postinsts/') + new_pkg

        cmd = self.rpm_cmd + ' -q --scripts --root ' + self.target_rootfs
        cmd += ' --dbpath=/var/lib/rpm ' + new_pkg
        cmd += ' | sed -n -e "/^postinstall scriptlet (using .*):$/,/^.* scriptlet (using .*):$/ {/.*/p}"'
        cmd += ' | sed -e "/postinstall scriptlet (using \(.*\)):$/d"'
        cmd += ' -e "/^.* scriptlet (using .*):$/d" > %s' % saved_dir

        try:
            bb.note(cmd)
            output = subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True).strip()
            bb.note(output)
            os.chmod(saved_dir, 0755)
        except subprocess.CalledProcessError as e:
            bb.fatal("Invoke save_rpmpostinst failed. Command '%s' "
                     "returned %d:\n%s" % (cmd, e.returncode, e.output))

    '''Write common configuration for target usage'''
    def rpm_setup_smart_target_config(self):
        bb.utils.remove(os.path.join(self.target_rootfs, 'var/lib/smart'),
                        True)

        self._invoke_smart('config --set rpm-nolinktos=1')
        self._invoke_smart('config --set rpm-noparentdirs=1')
        for i in self.d.getVar('BAD_RECOMMENDATIONS', True).split():
            self._invoke_smart('flag --set ignore-recommends %s' % i)
        self._invoke_smart('channel --add rpmsys type=rpm-sys -y')

    '''
    The rpm db lock files were produced after invoking rpm to query on
    build system, and they caused the rpm on target didn't work, so we
    need to unlock the rpm db by removing the lock files.
    '''
    def unlock_rpm_db(self):
        # Remove rpm db lock files
        rpm_db_locks = glob.glob('%s/var/lib/rpm/__db.*' % self.target_rootfs)
        for f in rpm_db_locks:
            bb.utils.remove(f, True)


class OpkgPM(PackageManager):
    def __init__(self, d, target_rootfs, config_file, archs, task_name='target'):
        super(OpkgPM, self).__init__(d)

        self.target_rootfs = target_rootfs
        self.config_file = config_file
        self.pkg_archs = archs
        self.task_name = task_name

        self.deploy_dir = self.d.getVar("DEPLOY_DIR_IPK", True)
        self.deploy_lock_file = os.path.join(self.deploy_dir, "deploy.lock")
        self.opkg_cmd = bb.utils.which(os.getenv('PATH'), "opkg")
        self.opkg_args = "--volatile-cache -f %s -o %s " % (self.config_file, target_rootfs)
        self.opkg_args += self.d.getVar("OPKG_ARGS", True)

        opkg_lib_dir = self.d.getVar('OPKGLIBDIR', True)
        if opkg_lib_dir[0] == "/":
            opkg_lib_dir = opkg_lib_dir[1:]

        self.opkg_dir = os.path.join(target_rootfs, opkg_lib_dir, "opkg")

        bb.utils.mkdirhier(self.opkg_dir)

        self.saved_opkg_dir = self.d.expand('${T}/saved/%s' % self.task_name)
        if not os.path.exists(self.d.expand('${T}/saved')):
            bb.utils.mkdirhier(self.d.expand('${T}/saved'))

        self.from_feeds = (self.d.getVar('BUILD_IMAGES_FROM_FEEDS', True) or "") == "1"
        if self.from_feeds:
            self._create_custom_config()
        else:
            self._create_config()

        self.indexer = OpkgIndexer(self.d, self.deploy_dir)

    """
    This function will change a package's status in /var/lib/opkg/status file.
    If 'packages' is None then the new_status will be applied to all
    packages
    """
    def mark_packages(self, status_tag, packages=None):
        status_file = os.path.join(self.opkg_dir, "status")

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

        os.rename(status_file + ".tmp", status_file)

    def _create_custom_config(self):
        bb.note("Building from feeds activated!")

        with open(self.config_file, "w+") as config_file:
            priority = 1
            for arch in self.pkg_archs.split():
                config_file.write("arch %s %d\n" % (arch, priority))
                priority += 5

            for line in (self.d.getVar('IPK_FEED_URIS', True) or "").split():
                feed_match = re.match("^[ \t]*(.*)##([^ \t]*)[ \t]*$", line)

                if feed_match is not None:
                    feed_name = feed_match.group(1)
                    feed_uri = feed_match.group(2)

                    bb.note("Add %s feed with URL %s" % (feed_name, feed_uri))

                    config_file.write("src/gz %s %s\n" % (feed_name, feed_uri))

            """
            Allow to use package deploy directory contents as quick devel-testing
            feed. This creates individual feed configs for each arch subdir of those
            specified as compatible for the current machine.
            NOTE: Development-helper feature, NOT a full-fledged feed.
            """
            if (self.d.getVar('FEED_DEPLOYDIR_BASE_URI', True) or "") != "":
                for arch in self.pkg_archs.split():
                    cfg_file_name = os.path.join(self.target_rootfs,
                                                 self.d.getVar("sysconfdir", True),
                                                 "opkg",
                                                 "local-%s-feed.conf" % arch)

                    with open(cfg_file_name, "w+") as cfg_file:
                        cfg_file.write("src/gz local-%s %s/%s" %
                                       (arch,
                                        self.d.getVar('FEED_DEPLOYDIR_BASE_URI', True),
                                        arch))

                        if self.opkg_dir != '/var/lib/opkg':
                            # There is no command line option for this anymore, we need to add
                            # info_dir and status_file to config file, if OPKGLIBDIR doesn't have
                            # the default value of "/var/lib" as defined in opkg:
                            # libopkg/opkg_conf.h:#define OPKG_CONF_DEFAULT_INFO_DIR      "/var/lib/opkg/info"
                            # libopkg/opkg_conf.h:#define OPKG_CONF_DEFAULT_STATUS_FILE   "/var/lib/opkg/status"
                            cfg_file.write("option info_dir     %s\n" % os.path.join(self.d.getVar('OPKGLIBDIR', True), 'opkg', 'info'))
                            cfg_file.write("option status_file  %s\n" % os.path.join(self.d.getVar('OPKGLIBDIR', True), 'opkg', 'status'))


    def _create_config(self):
        with open(self.config_file, "w+") as config_file:
            priority = 1
            for arch in self.pkg_archs.split():
                config_file.write("arch %s %d\n" % (arch, priority))
                priority += 5

            config_file.write("src oe file:%s\n" % self.deploy_dir)

            for arch in self.pkg_archs.split():
                pkgs_dir = os.path.join(self.deploy_dir, arch)
                if os.path.isdir(pkgs_dir):
                    config_file.write("src oe-%s file:%s\n" %
                                      (arch, pkgs_dir))

            if self.opkg_dir != '/var/lib/opkg':
                # There is no command line option for this anymore, we need to add
                # info_dir and status_file to config file, if OPKGLIBDIR doesn't have
                # the default value of "/var/lib" as defined in opkg:
                # libopkg/opkg_conf.h:#define OPKG_CONF_DEFAULT_INFO_DIR      "/var/lib/opkg/info"
                # libopkg/opkg_conf.h:#define OPKG_CONF_DEFAULT_STATUS_FILE   "/var/lib/opkg/status"
                config_file.write("option info_dir     %s\n" % os.path.join(self.d.getVar('OPKGLIBDIR', True), 'opkg', 'info'))
                config_file.write("option status_file  %s\n" % os.path.join(self.d.getVar('OPKGLIBDIR', True), 'opkg', 'status'))

    def insert_feeds_uris(self):
        if self.feed_uris == "":
            return

        rootfs_config = os.path.join('%s/etc/opkg/base-feeds.conf'
                                  % self.target_rootfs)

        feed_uris = self.construct_uris(self.feed_uris.split(), self.feed_base_paths.split())
        archs = self.pkg_archs.split() if self.feed_archs is None else self.feed_archs.split()

        with open(rootfs_config, "w+") as config_file:
            uri_iterator = 0
            for uri in feed_uris:
                if archs:
                    for arch in archs:
                        if (self.feed_archs is None) and (not os.path.exists(os.path.join(self.deploy_dir, arch))):
                            continue
                        bb.note('Note: adding opkg feed url-%s-%d (%s)' %
                            (arch, uri_iterator, uri))
                        config_file.write("src/gz uri-%s-%d %s/%s\n" %
                                          (arch, uri_iterator, uri, arch))
                else:
                    bb.note('Note: adding opkg feed url-%d (%s)' %
                        (uri_iterator, uri))
                    config_file.write("src/gz uri-%d %s\n" %
                                      (uri_iterator, uri))

                uri_iterator += 1

    def update(self):
        self.deploy_dir_lock()

        cmd = "%s %s update" % (self.opkg_cmd, self.opkg_args)

        try:
            subprocess.check_output(cmd.split(), stderr=subprocess.STDOUT)
        except subprocess.CalledProcessError as e:
            self.deploy_dir_unlock()
            bb.fatal("Unable to update the package index files. Command '%s' "
                     "returned %d:\n%s" % (cmd, e.returncode, e.output))

        self.deploy_dir_unlock()

    def install(self, pkgs, attempt_only=False):
        if not pkgs:
            return

        cmd = "%s %s install %s" % (self.opkg_cmd, self.opkg_args, ' '.join(pkgs))

        os.environ['D'] = self.target_rootfs
        os.environ['OFFLINE_ROOT'] = self.target_rootfs
        os.environ['IPKG_OFFLINE_ROOT'] = self.target_rootfs
        os.environ['OPKG_OFFLINE_ROOT'] = self.target_rootfs
        os.environ['INTERCEPT_DIR'] = os.path.join(self.d.getVar('WORKDIR', True),
                                                   "intercept_scripts")
        os.environ['NATIVE_ROOT'] = self.d.getVar('STAGING_DIR_NATIVE', True)

        try:
            bb.note("Installing the following packages: %s" % ' '.join(pkgs))
            bb.note(cmd)
            output = subprocess.check_output(cmd.split(), stderr=subprocess.STDOUT)
            bb.note(output)
        except subprocess.CalledProcessError as e:
            (bb.fatal, bb.note)[attempt_only]("Unable to install packages. "
                                              "Command '%s' returned %d:\n%s" %
                                              (cmd, e.returncode, e.output))

    def remove(self, pkgs, with_dependencies=True):
        if with_dependencies:
            cmd = "%s %s --force-depends --force-remove --force-removal-of-dependent-packages remove %s" % \
                (self.opkg_cmd, self.opkg_args, ' '.join(pkgs))
        else:
            cmd = "%s %s --force-depends remove %s" % \
                (self.opkg_cmd, self.opkg_args, ' '.join(pkgs))

        try:
            bb.note(cmd)
            output = subprocess.check_output(cmd.split(), stderr=subprocess.STDOUT)
            bb.note(output)
        except subprocess.CalledProcessError as e:
            bb.fatal("Unable to remove packages. Command '%s' "
                     "returned %d:\n%s" % (e.cmd, e.returncode, e.output))

    def write_index(self):
        self.deploy_dir_lock()

        result = self.indexer.write_index()

        self.deploy_dir_unlock()

        if result is not None:
            bb.fatal(result)

    def remove_packaging_data(self):
        bb.utils.remove(self.opkg_dir, True)
        # create the directory back, it's needed by PM lock
        bb.utils.mkdirhier(self.opkg_dir)

    def remove_lists(self):
        if not self.from_feeds:
            bb.utils.remove(os.path.join(self.opkg_dir, "lists"), True)

    def list_installed(self):
        return OpkgPkgsList(self.d, self.target_rootfs, self.config_file).list_pkgs()

    def handle_bad_recommendations(self):
        bad_recommendations = self.d.getVar("BAD_RECOMMENDATIONS", True) or ""
        if bad_recommendations.strip() == "":
            return

        status_file = os.path.join(self.opkg_dir, "status")

        # If status file existed, it means the bad recommendations has already
        # been handled
        if os.path.exists(status_file):
            return

        cmd = "%s %s info " % (self.opkg_cmd, self.opkg_args)

        with open(status_file, "w+") as status:
            for pkg in bad_recommendations.split():
                pkg_info = cmd + pkg

                try:
                    output = subprocess.check_output(pkg_info.split(), stderr=subprocess.STDOUT).strip()
                except subprocess.CalledProcessError as e:
                    bb.fatal("Cannot get package info. Command '%s' "
                             "returned %d:\n%s" % (pkg_info, e.returncode, e.output))

                if output == "":
                    bb.note("Ignored bad recommendation: '%s' is "
                            "not a package" % pkg)
                    continue

                for line in output.split('\n'):
                    if line.startswith("Status:"):
                        status.write("Status: deinstall hold not-installed\n")
                    else:
                        status.write(line + "\n")

                # Append a blank line after each package entry to ensure that it
                # is separated from the following entry
                status.write("\n")

    '''
    The following function dummy installs pkgs and returns the log of output.
    '''
    def dummy_install(self, pkgs):
        if len(pkgs) == 0:
            return

        # Create an temp dir as opkg root for dummy installation
        temp_rootfs = self.d.expand('${T}/opkg')
        temp_opkg_dir = os.path.join(temp_rootfs, 'var/lib/opkg')
        bb.utils.mkdirhier(temp_opkg_dir)

        opkg_args = "-f %s -o %s " % (self.config_file, temp_rootfs)
        opkg_args += self.d.getVar("OPKG_ARGS", True)

        cmd = "%s %s update" % (self.opkg_cmd, opkg_args)
        try:
            subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
        except subprocess.CalledProcessError as e:
            bb.fatal("Unable to update. Command '%s' "
                     "returned %d:\n%s" % (cmd, e.returncode, e.output))

        # Dummy installation
        cmd = "%s %s --noaction install %s " % (self.opkg_cmd,
                                                opkg_args,
                                                ' '.join(pkgs))
        try:
            output = subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
        except subprocess.CalledProcessError as e:
            bb.fatal("Unable to dummy install packages. Command '%s' "
                     "returned %d:\n%s" % (cmd, e.returncode, e.output))

        bb.utils.remove(temp_rootfs, True)

        return output

    def backup_packaging_data(self):
        # Save the opkglib for increment ipk image generation
        if os.path.exists(self.saved_opkg_dir):
            bb.utils.remove(self.saved_opkg_dir, True)
        shutil.copytree(self.opkg_dir,
                        self.saved_opkg_dir,
                        symlinks=True)

    def recover_packaging_data(self):
        # Move the opkglib back
        if os.path.exists(self.saved_opkg_dir):
            if os.path.exists(self.opkg_dir):
                bb.utils.remove(self.opkg_dir, True)

            bb.note('Recover packaging data')
            shutil.copytree(self.saved_opkg_dir,
                            self.opkg_dir,
                            symlinks=True)


class DpkgPM(PackageManager):
    def __init__(self, d, target_rootfs, archs, base_archs, apt_conf_dir=None):
        super(DpkgPM, self).__init__(d)
        self.target_rootfs = target_rootfs
        self.deploy_dir = self.d.getVar('DEPLOY_DIR_DEB', True)
        if apt_conf_dir is None:
            self.apt_conf_dir = self.d.expand("${APTCONF_TARGET}/apt")
        else:
            self.apt_conf_dir = apt_conf_dir
        self.apt_conf_file = os.path.join(self.apt_conf_dir, "apt.conf")
        self.apt_get_cmd = bb.utils.which(os.getenv('PATH'), "apt-get")

        self.apt_args = d.getVar("APT_ARGS", True)

        self.all_arch_list = archs.split()
        all_mlb_pkg_arch_list = (self.d.getVar('ALL_MULTILIB_PACKAGE_ARCHS', True) or "").split()
        self.all_arch_list.extend(arch for arch in all_mlb_pkg_arch_list if arch not in self.all_arch_list)

        self._create_configs(archs, base_archs)

        self.indexer = DpkgIndexer(self.d, self.deploy_dir)

    """
    This function will change a package's status in /var/lib/dpkg/status file.
    If 'packages' is None then the new_status will be applied to all
    packages
    """
    def mark_packages(self, status_tag, packages=None):
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

        os.rename(status_file + ".tmp", status_file)

    """
    Run the pre/post installs for package "package_name". If package_name is
    None, then run all pre/post install scriptlets.
    """
    def run_pre_post_installs(self, package_name=None):
        info_dir = self.target_rootfs + "/var/lib/dpkg/info"
        suffixes = [(".preinst", "Preinstall"), (".postinst", "Postinstall")]
        status_file = self.target_rootfs + "/var/lib/dpkg/status"
        installed_pkgs = []

        with open(status_file, "r") as status:
            for line in status.read().split('\n'):
                m = re.match("^Package: (.*)", line)
                if m is not None:
                    installed_pkgs.append(m.group(1))

        if package_name is not None and not package_name in installed_pkgs:
            return

        os.environ['D'] = self.target_rootfs
        os.environ['OFFLINE_ROOT'] = self.target_rootfs
        os.environ['IPKG_OFFLINE_ROOT'] = self.target_rootfs
        os.environ['OPKG_OFFLINE_ROOT'] = self.target_rootfs
        os.environ['INTERCEPT_DIR'] = os.path.join(self.d.getVar('WORKDIR', True),
                                                   "intercept_scripts")
        os.environ['NATIVE_ROOT'] = self.d.getVar('STAGING_DIR_NATIVE', True)

        failed_pkgs = []
        for pkg_name in installed_pkgs:
            for suffix in suffixes:
                p_full = os.path.join(info_dir, pkg_name + suffix[0])
                if os.path.exists(p_full):
                    try:
                        bb.note("Executing %s for package: %s ..." %
                                 (suffix[1].lower(), pkg_name))
                        subprocess.check_output(p_full, stderr=subprocess.STDOUT)
                    except subprocess.CalledProcessError as e:
                        bb.note("%s for package %s failed with %d:\n%s" %
                                (suffix[1], pkg_name, e.returncode, e.output))
                        failed_pkgs.append(pkg_name)
                        break

        if len(failed_pkgs):
            self.mark_packages("unpacked", failed_pkgs)

    def update(self):
        os.environ['APT_CONFIG'] = self.apt_conf_file

        self.deploy_dir_lock()

        cmd = "%s update" % self.apt_get_cmd

        try:
            subprocess.check_output(cmd.split(), stderr=subprocess.STDOUT)
        except subprocess.CalledProcessError as e:
            bb.fatal("Unable to update the package index files. Command '%s' "
                     "returned %d:\n%s" % (e.cmd, e.returncode, e.output))

        self.deploy_dir_unlock()

    def install(self, pkgs, attempt_only=False):
        if attempt_only and len(pkgs) == 0:
            return

        os.environ['APT_CONFIG'] = self.apt_conf_file

        cmd = "%s %s install --force-yes --allow-unauthenticated %s" % \
              (self.apt_get_cmd, self.apt_args, ' '.join(pkgs))

        try:
            bb.note("Installing the following packages: %s" % ' '.join(pkgs))
            subprocess.check_output(cmd.split(), stderr=subprocess.STDOUT)
        except subprocess.CalledProcessError as e:
            (bb.fatal, bb.note)[attempt_only]("Unable to install packages. "
                                              "Command '%s' returned %d:\n%s" %
                                              (cmd, e.returncode, e.output))

        # rename *.dpkg-new files/dirs
        for root, dirs, files in os.walk(self.target_rootfs):
            for dir in dirs:
                new_dir = re.sub("\.dpkg-new", "", dir)
                if dir != new_dir:
                    os.rename(os.path.join(root, dir),
                              os.path.join(root, new_dir))

            for file in files:
                new_file = re.sub("\.dpkg-new", "", file)
                if file != new_file:
                    os.rename(os.path.join(root, file),
                              os.path.join(root, new_file))


    def remove(self, pkgs, with_dependencies=True):
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
                     "returned %d:\n%s" % (e.cmd, e.returncode, e.output))

    def write_index(self):
        self.deploy_dir_lock()

        result = self.indexer.write_index()

        self.deploy_dir_unlock()

        if result is not None:
            bb.fatal(result)

    def insert_feeds_uris(self):
        if self.feed_uris == "":
            return

        sources_conf = os.path.join("%s/etc/apt/sources.list"
                                    % self.target_rootfs)
        arch_list = []

        if self.feed_archs is None:
            for arch in self.all_arch_list:
                if not os.path.exists(os.path.join(self.deploy_dir, arch)):
                    continue
                arch_list.append(arch)
        else:
            arch_list = self.feed_archs.split()

        feed_uris = self.construct_uris(self.feed_uris.split(), self.feed_base_paths.split())

        with open(sources_conf, "w+") as sources_file:
            for uri in feed_uris:
                if arch_list:
                    for arch in arch_list:
                        bb.note('Note: adding dpkg channel at (%s)' % uri)
                        sources_file.write("deb %s/%s ./\n" %
                                           (uri, arch))
                else:
                    bb.note('Note: adding dpkg channel at (%s)' % uri)
                    sources_file.write("deb %s ./\n" % uri)

    def _create_configs(self, archs, base_archs):
        base_archs = re.sub("_", "-", base_archs)

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

            pkg_exclude = self.d.getVar('PACKAGE_EXCLUDE', True) or ""
            for pkg in pkg_exclude.split():
                prefs_file.write(
                    "Package: %s\n"
                    "Pin: release *\n"
                    "Pin-Priority: -1\n\n" % pkg)

        arch_list.reverse()

        with open(os.path.join(self.apt_conf_dir, "sources.list"), "w+") as sources_file:
            for arch in arch_list:
                sources_file.write("deb file:%s/ ./\n" %
                                   os.path.join(self.deploy_dir, arch))

        base_arch_list = base_archs.split()
        multilib_variants = self.d.getVar("MULTILIB_VARIANTS", True);
        for variant in multilib_variants.split():
            localdata = bb.data.createCopy(self.d)
            variant_tune = localdata.getVar("DEFAULTTUNE_virtclass-multilib-" + variant, False)
            orig_arch = localdata.getVar("DPKG_ARCH", True)
            localdata.setVar("DEFAULTTUNE", variant_tune)
            bb.data.update_data(localdata)
            variant_arch = localdata.getVar("DPKG_ARCH", True)
            if variant_arch not in base_arch_list:
                base_arch_list.append(variant_arch)

        with open(self.apt_conf_file, "w+") as apt_conf:
            with open(self.d.expand("${STAGING_ETCDIR_NATIVE}/apt/apt.conf.sample")) as apt_conf_sample:
                for line in apt_conf_sample.read().split("\n"):
                    match_arch = re.match("  Architecture \".*\";$", line)
                    architectures = ""
                    if match_arch:
                        for base_arch in base_arch_list:
                            architectures += "\"%s\";" % base_arch
                        apt_conf.write("  Architectures {%s};\n" % architectures);
                        apt_conf.write("  Architecture \"%s\";\n" % base_archs)
                    else:
                        line = re.sub("#ROOTFS#", self.target_rootfs, line)
                        line = re.sub("#APTCONF#", self.apt_conf_dir, line)
                        apt_conf.write(line + "\n")

        target_dpkg_dir = "%s/var/lib/dpkg" % self.target_rootfs
        bb.utils.mkdirhier(os.path.join(target_dpkg_dir, "info"))

        bb.utils.mkdirhier(os.path.join(target_dpkg_dir, "updates"))

        if not os.path.exists(os.path.join(target_dpkg_dir, "status")):
            open(os.path.join(target_dpkg_dir, "status"), "w+").close()
        if not os.path.exists(os.path.join(target_dpkg_dir, "available")):
            open(os.path.join(target_dpkg_dir, "available"), "w+").close()

    def remove_packaging_data(self):
        bb.utils.remove(os.path.join(self.target_rootfs,
                                     self.d.getVar('opkglibdir', True)), True)
        bb.utils.remove(self.target_rootfs + "/var/lib/dpkg/", True)

    def fix_broken_dependencies(self):
        os.environ['APT_CONFIG'] = self.apt_conf_file

        cmd = "%s %s -f install" % (self.apt_get_cmd, self.apt_args)

        try:
            subprocess.check_output(cmd.split(), stderr=subprocess.STDOUT)
        except subprocess.CalledProcessError as e:
            bb.fatal("Cannot fix broken dependencies. Command '%s' "
                     "returned %d:\n%s" % (cmd, e.returncode, e.output))

    def list_installed(self):
        return DpkgPkgsList(self.d, self.target_rootfs).list_pkgs()


def generate_index_files(d):
    classes = d.getVar('PACKAGE_CLASSES', True).replace("package_", "").split()

    indexer_map = {
        "rpm": (RpmIndexer, d.getVar('DEPLOY_DIR_RPM', True)),
        "ipk": (OpkgIndexer, d.getVar('DEPLOY_DIR_IPK', True)),
        "deb": (DpkgIndexer, d.getVar('DEPLOY_DIR_DEB', True))
    }

    result = None

    for pkg_class in classes:
        if not pkg_class in indexer_map:
            continue

        if os.path.exists(indexer_map[pkg_class][1]):
            result = indexer_map[pkg_class][0](d, indexer_map[pkg_class][1]).write_index()

            if result is not None:
                bb.fatal(result)

if __name__ == "__main__":
    """
    We should be able to run this as a standalone script, from outside bitbake
    environment.
    """
    """
    TBD
    """
