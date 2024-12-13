#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

from abc import ABCMeta, abstractmethod
import os
import glob
import subprocess
import shutil
import re
import collections
import bb
import tempfile
import oe.utils
import oe.path
import string
from oe.gpg_sign import get_signer
import hashlib
import fnmatch

# this can be used by all PM backends to create the index files in parallel
def create_index(arg):
    index_cmd = arg

    bb.note("Executing '%s' ..." % index_cmd)
    result = subprocess.check_output(index_cmd, stderr=subprocess.STDOUT, shell=True).decode("utf-8")
    if result:
        bb.note(result)

def opkg_query(cmd_output):
    """
    This method parse the output from the package managerand return
    a dictionary with the information of the packages. This is used
    when the packages are in deb or ipk format.
    """
    verregex = re.compile(r' \([=<>]* [^ )]*\)')
    output = dict()
    pkg = ""
    arch = ""
    ver = ""
    filename = ""
    dep = []
    prov = []
    pkgarch = ""
    for line in cmd_output.splitlines()+['']:
        line = line.rstrip()
        if ':' in line:
            if line.startswith("Package: "):
                pkg = line.split(": ")[1]
            elif line.startswith("Architecture: "):
                arch = line.split(": ")[1]
            elif line.startswith("Version: "):
                ver = line.split(": ")[1]
            elif line.startswith("File: ") or line.startswith("Filename:"):
                filename = line.split(": ")[1]
                if "/" in filename:
                    filename = os.path.basename(filename)
            elif line.startswith("Depends: "):
                depends = verregex.sub('', line.split(": ")[1])
                for depend in depends.split(", "):
                    dep.append(depend)
            elif line.startswith("Recommends: "):
                recommends = verregex.sub('', line.split(": ")[1])
                for recommend in recommends.split(", "):
                    dep.append("%s [REC]" % recommend)
            elif line.startswith("PackageArch: "):
                pkgarch = line.split(": ")[1]
            elif line.startswith("Provides: "):
                provides = verregex.sub('', line.split(": ")[1])
                for provide in provides.split(", "):
                    prov.append(provide)

        # When there is a blank line save the package information
        elif not line:
            # IPK doesn't include the filename
            if not filename:
                filename = "%s_%s_%s.ipk" % (pkg, ver, arch)
            if pkg:
                output[pkg] = {"arch":arch, "ver":ver,
                        "filename":filename, "deps": dep, "pkgarch":pkgarch, "provs": prov}
            pkg = ""
            arch = ""
            ver = ""
            filename = ""
            dep = []
            prov = []
            pkgarch = ""

    return output

def failed_postinsts_abort(pkgs, log_path):
    bb.fatal("""Postinstall scriptlets of %s have failed. If the intention is to defer them to first boot,
then please place them into pkg_postinst_ontarget:${PN} ().
Deferring to first boot via 'exit 1' is no longer supported.
Details of the failure are in %s.""" %(pkgs, log_path))

def generate_locale_archive(d, rootfs, target_arch, localedir):
    # Pretty sure we don't need this for locale archive generation but
    # keeping it to be safe...
    locale_arch_options = { \
        "arc": ["--uint32-align=4", "--little-endian"],
        "arceb": ["--uint32-align=4", "--big-endian"],
        "arm": ["--uint32-align=4", "--little-endian"],
        "armeb": ["--uint32-align=4", "--big-endian"],
        "aarch64": ["--uint32-align=4", "--little-endian"],
        "aarch64_be": ["--uint32-align=4", "--big-endian"],
        "sh4": ["--uint32-align=4", "--big-endian"],
        "powerpc": ["--uint32-align=4", "--big-endian"],
        "powerpc64": ["--uint32-align=4", "--big-endian"],
        "powerpc64le": ["--uint32-align=4", "--little-endian"],
        "mips": ["--uint32-align=4", "--big-endian"],
        "mipsisa32r6": ["--uint32-align=4", "--big-endian"],
        "mips64": ["--uint32-align=4", "--big-endian"],
        "mipsisa64r6": ["--uint32-align=4", "--big-endian"],
        "mipsel": ["--uint32-align=4", "--little-endian"],
        "mipsisa32r6el": ["--uint32-align=4", "--little-endian"],
        "mips64el": ["--uint32-align=4", "--little-endian"],
        "mipsisa64r6el": ["--uint32-align=4", "--little-endian"],
        "riscv64": ["--uint32-align=4", "--little-endian"],
        "riscv32": ["--uint32-align=4", "--little-endian"],
        "i586": ["--uint32-align=4", "--little-endian"],
        "i686": ["--uint32-align=4", "--little-endian"],
        "x86_64": ["--uint32-align=4", "--little-endian"],
        "loongarch64": ["--uint32-align=4", "--little-endian"]
    }
    if target_arch in locale_arch_options:
        arch_options = locale_arch_options[target_arch]
    else:
        bb.error("locale_arch_options not found for target_arch=" + target_arch)
        bb.fatal("unknown arch:" + target_arch + " for locale_arch_options")

    # Need to set this so cross-localedef knows where the archive is
    env = dict(os.environ)
    env["LOCALEARCHIVE"] = oe.path.join(localedir, "locale-archive")

    for name in sorted(os.listdir(localedir)):
        path = os.path.join(localedir, name)
        if os.path.isdir(path):
            cmd = ["cross-localedef", "--verbose"]
            cmd += arch_options
            cmd += ["--add-to-archive", path]
            subprocess.check_output(cmd, env=env, stderr=subprocess.STDOUT)

class Indexer(object, metaclass=ABCMeta):
    def __init__(self, d, deploy_dir):
        self.d = d
        self.deploy_dir = deploy_dir

    @abstractmethod
    def write_index(self):
        pass

class PkgsList(object, metaclass=ABCMeta):
    def __init__(self, d, rootfs_dir):
        self.d = d
        self.rootfs_dir = rootfs_dir

    @abstractmethod
    def list_pkgs(self):
        pass

class PackageManager(object, metaclass=ABCMeta):
    """
    This is an abstract class. Do not instantiate this directly.
    """

    def __init__(self, d, target_rootfs):
        self.d = d
        self.target_rootfs = target_rootfs
        self.deploy_dir = None
        self.deploy_lock = None
        self._initialize_intercepts()

    def _initialize_intercepts(self):
        bb.note("Initializing intercept dir for %s" % self.target_rootfs)
        # As there might be more than one instance of PackageManager operating at the same time
        # we need to isolate the intercept_scripts directories from each other,
        # hence the ugly hash digest in dir name.
        self.intercepts_dir = os.path.join(self.d.getVar('WORKDIR'), "intercept_scripts-%s" %
                                           (hashlib.sha256(self.target_rootfs.encode()).hexdigest()))

        postinst_intercepts = (self.d.getVar("POSTINST_INTERCEPTS") or "").split()
        if not postinst_intercepts:
            postinst_intercepts_path = self.d.getVar("POSTINST_INTERCEPTS_PATH")
            if not postinst_intercepts_path:
                postinst_intercepts_path = self.d.getVar("POSTINST_INTERCEPTS_DIR") or self.d.expand("${COREBASE}/scripts/postinst-intercepts")
            postinst_intercepts = oe.path.which_wild('*', postinst_intercepts_path)

        bb.debug(1, 'Collected intercepts:\n%s' % ''.join('  %s\n' % i for i in postinst_intercepts))
        bb.utils.remove(self.intercepts_dir, True)
        bb.utils.mkdirhier(self.intercepts_dir)
        for intercept in postinst_intercepts:
            shutil.copy(intercept, os.path.join(self.intercepts_dir, os.path.basename(intercept)))

    @abstractmethod
    def _handle_intercept_failure(self, failed_script):
        pass

    def _postpone_to_first_boot(self, postinst_intercept_hook):
        with open(postinst_intercept_hook) as intercept:
            registered_pkgs = None
            for line in intercept.read().split("\n"):
                m = re.match(r"^##PKGS:(.*)", line)
                if m is not None:
                    registered_pkgs = m.group(1).strip()
                    break

            if registered_pkgs is not None:
                bb.note("If an image is being built, the postinstalls for the following packages "
                        "will be postponed for first boot: %s" %
                        registered_pkgs)

                # call the backend dependent handler
                self._handle_intercept_failure(registered_pkgs)


    def run_intercepts(self, populate_sdk=None):
        intercepts_dir = self.intercepts_dir

        bb.note("Running intercept scripts:")
        os.environ['D'] = self.target_rootfs
        os.environ['STAGING_DIR_NATIVE'] = self.d.getVar('STAGING_DIR_NATIVE')
        for script in os.listdir(intercepts_dir):
            script_full = os.path.join(intercepts_dir, script)

            if script == "postinst_intercept" or not os.access(script_full, os.X_OK):
                continue

            # we do not want to run any multilib variant of this
            if script.startswith("delay_to_first_boot"):
                self._postpone_to_first_boot(script_full)
                continue

            if populate_sdk == 'host' and self.d.getVar('SDK_OS') == 'mingw32':
                bb.note("The postinstall intercept hook '%s' could not be executed due to missing wine support, details in %s/log.do_%s"
                                % (script, self.d.getVar('T'), self.d.getVar('BB_CURRENTTASK')))
                continue

            bb.note("> Executing %s intercept ..." % script)

            try:
                output = subprocess.check_output(script_full, stderr=subprocess.STDOUT)
                if output: bb.note(output.decode("utf-8"))
            except subprocess.CalledProcessError as e:
                bb.note("Exit code %d. Output:\n%s" % (e.returncode, e.output.decode("utf-8")))
                if populate_sdk == 'host':
                    bb.fatal("The postinstall intercept hook '%s' failed, details in %s/log.do_%s" % (script, self.d.getVar('T'), self.d.getVar('BB_CURRENTTASK')))
                elif populate_sdk == 'target':
                    if "qemuwrapper: qemu usermode is not supported" in e.output.decode("utf-8"):
                        bb.note("The postinstall intercept hook '%s' could not be executed due to missing qemu usermode support, details in %s/log.do_%s"
                                % (script, self.d.getVar('T'), self.d.getVar('BB_CURRENTTASK')))
                    else:
                        bb.fatal("The postinstall intercept hook '%s' failed, details in %s/log.do_%s" % (script, self.d.getVar('T'), self.d.getVar('BB_CURRENTTASK')))
                else:
                    if "qemuwrapper: qemu usermode is not supported" in e.output.decode("utf-8"):
                        bb.note("The postinstall intercept hook '%s' could not be executed due to missing qemu usermode support, details in %s/log.do_%s"
                                % (script, self.d.getVar('T'), self.d.getVar('BB_CURRENTTASK')))
                        self._postpone_to_first_boot(script_full)
                    else:
                        bb.fatal("The postinstall intercept hook '%s' failed, details in %s/log.do_%s" % (script, self.d.getVar('T'), self.d.getVar('BB_CURRENTTASK')))

    @abstractmethod
    def update(self):
        """
        Update the package manager package database.
        """
        pass

    @abstractmethod
    def install(self, pkgs, attempt_only=False, hard_depends_only=False):
        """
        Install a list of packages. 'pkgs' is a list object. If 'attempt_only' is
        True, installation failures are ignored.
        """
        pass

    @abstractmethod
    def remove(self, pkgs, with_dependencies=True):
        """
        Remove a list of packages. 'pkgs' is a list object. If 'with_dependencies'
        is False, then any dependencies are left in place.
        """
        pass

    @abstractmethod
    def write_index(self):
        """
        This function creates the index files
        """
        pass

    @abstractmethod
    def remove_packaging_data(self):
        pass

    @abstractmethod
    def list_installed(self):
        pass

    @abstractmethod
    def extract(self, pkg):
        """
        Returns the path to a tmpdir where resides the contents of a package.
        Deleting the tmpdir is responsability of the caller.
        """
        pass

    @abstractmethod
    def insert_feeds_uris(self, feed_uris, feed_base_paths, feed_archs):
        """
        Add remote package feeds into repository manager configuration. The parameters
        for the feeds are set by feed_uris, feed_base_paths and feed_archs.
        See http://www.yoctoproject.org/docs/current/ref-manual/ref-manual.html#var-PACKAGE_FEED_URIS
        for their description.
        """
        pass

    def install_glob(self, globs, sdk=False):
        """
        Install all packages that match a glob.
        """
        # TODO don't have sdk here but have a property on the superclass
        # (and respect in install_complementary)
        if sdk:
            pkgdatadir = self.d.getVar("PKGDATA_DIR_SDK")
        else:
            pkgdatadir = self.d.getVar("PKGDATA_DIR")

        try:
            bb.note("Installing globbed packages...")
            cmd = ["oe-pkgdata-util", "-p", pkgdatadir, "list-pkgs", globs]
            bb.note('Running %s' % cmd)
            proc = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            stdout, stderr = proc.communicate()
            if stderr: bb.note(stderr.decode("utf-8"))
            pkgs = stdout.decode("utf-8")
            self.install(pkgs.split(), attempt_only=True)
        except subprocess.CalledProcessError as e:
            # Return code 1 means no packages matched
            if e.returncode != 1:
                bb.fatal("Could not compute globbed packages list. Command "
                         "'%s' returned %d:\n%s" %
                         (' '.join(cmd), e.returncode, e.output.decode("utf-8")))

    def install_complementary(self, globs=None):
        """
        Install complementary packages based upon the list of currently installed
        packages e.g. locales, *-dev, *-dbg, etc. Note: every backend needs to
        call this function explicitly after the normal package installation.
        """
        if globs is None:
            globs = self.d.getVar('IMAGE_INSTALL_COMPLEMENTARY')
            split_linguas = set()

            for translation in self.d.getVar('IMAGE_LINGUAS').split():
                split_linguas.add(translation)
                split_linguas.add(translation.split('-')[0])

            split_linguas = sorted(split_linguas)

            for lang in split_linguas:
                globs += " *-locale-%s" % lang
                for complementary_linguas in (self.d.getVar('IMAGE_LINGUAS_COMPLEMENTARY') or "").split():
                    globs += (" " + complementary_linguas) % lang

        if globs:
            # we need to write the list of installed packages to a file because the
            # oe-pkgdata-util reads it from a file
            with tempfile.NamedTemporaryFile(mode="w+", prefix="installed-pkgs") as installed_pkgs:
                pkgs = self.list_installed()

                provided_pkgs = set()
                for pkg in pkgs.values():
                    provided_pkgs |= set(pkg.get('provs', []))

                output = oe.utils.format_pkg_list(pkgs, "arch")
                installed_pkgs.write(output)
                installed_pkgs.flush()

                cmd = ["oe-pkgdata-util",
                    "-p", self.d.getVar('PKGDATA_DIR'), "glob", installed_pkgs.name,
                    globs]
                exclude = self.d.getVar('PACKAGE_EXCLUDE_COMPLEMENTARY')
                if exclude:
                    cmd.extend(['--exclude=' + '|'.join(exclude.split())])
                try:
                    bb.note('Running %s' % cmd)
                    proc = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
                    stdout, stderr = proc.communicate()
                    if stderr: bb.note(stderr.decode("utf-8"))
                    complementary_pkgs = stdout.decode("utf-8")
                    complementary_pkgs = set(complementary_pkgs.split())
                    skip_pkgs = sorted(complementary_pkgs & provided_pkgs)
                    install_pkgs = sorted(complementary_pkgs - provided_pkgs)
                    bb.note("Installing complementary packages ... %s (skipped already provided packages %s)" % (
                        ' '.join(install_pkgs),
                        ' '.join(skip_pkgs)))
                    self.install(install_pkgs, hard_depends_only=True)
                except subprocess.CalledProcessError as e:
                    bb.fatal("Could not compute complementary packages list. Command "
                            "'%s' returned %d:\n%s" %
                            (' '.join(cmd), e.returncode, e.output.decode("utf-8")))

        if self.d.getVar('IMAGE_LOCALES_ARCHIVE') == '1':
            target_arch = self.d.getVar('TARGET_ARCH')
            localedir = oe.path.join(self.target_rootfs, self.d.getVar("libdir"), "locale")
            if os.path.exists(localedir) and os.listdir(localedir):
                generate_locale_archive(self.d, self.target_rootfs, target_arch, localedir)
                # And now delete the binary locales
                self.remove(fnmatch.filter(self.list_installed(), "glibc-binary-localedata-*"), False)

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

    def construct_uris(self, uris, base_paths):
        """
        Construct URIs based on the following pattern: uri/base_path where 'uri'
        and 'base_path' correspond to each element of the corresponding array
        argument leading to len(uris) x len(base_paths) elements on the returned
        array
        """
        def _append(arr1, arr2, sep='/'):
            res = []
            narr1 = [a.rstrip(sep) for a in arr1]
            narr2 = [a.rstrip(sep).lstrip(sep) for a in arr2]
            for a1 in narr1:
                if arr2:
                    for a2 in narr2:
                        res.append("%s%s%s" % (a1, sep, a2))
                else:
                    res.append(a1)
            return res
        return _append(uris, base_paths)

def create_packages_dir(d, subrepo_dir, deploydir, taskname, filterbydependencies, include_self=False):
    """
    Go through our do_package_write_X dependencies and hardlink the packages we depend
    upon into the repo directory. This prevents us seeing other packages that may
    have been built that we don't depend upon and also packages for architectures we don't
    support.
    """
    import errno

    taskdepdata = d.getVar("BB_TASKDEPDATA", False)
    mytaskname = d.getVar("BB_RUNTASK")
    pn = d.getVar("PN")
    seendirs = set()
    multilibs = {}

    bb.utils.remove(subrepo_dir, recurse=True)
    bb.utils.mkdirhier(subrepo_dir)

    # Detect bitbake -b usage
    nodeps = d.getVar("BB_LIMITEDDEPS") or False
    if nodeps or not filterbydependencies:
        for arch in d.getVar("ALL_MULTILIB_PACKAGE_ARCHS").split() + d.getVar("ALL_MULTILIB_PACKAGE_ARCHS").replace("-", "_").split():
            target = os.path.join(deploydir + "/" + arch)
            if os.path.exists(target):
                oe.path.symlink(target, subrepo_dir + "/" + arch, True)
        return

    start = None
    for dep in taskdepdata:
        data = taskdepdata[dep]
        if data[1] == mytaskname and data[0] == pn:
            start = dep
            break
    if start is None:
        bb.fatal("Couldn't find ourself in BB_TASKDEPDATA?")
    pkgdeps = set()
    start = [start]
    if include_self:
        seen = set()
    else:
        seen = set(start)
    # Support direct dependencies (do_rootfs -> do_package_write_X)
    # or indirect dependencies within PN (do_populate_sdk_ext -> do_rootfs -> do_package_write_X)
    while start:
        next = []
        for dep2 in start:
            for dep in taskdepdata[dep2][3]:
                if include_self or taskdepdata[dep][0] != pn:
                    if "do_" + taskname in dep:
                        pkgdeps.add(dep)
                elif dep not in seen:
                    next.append(dep)
                    seen.add(dep)
        start = next

    for dep in pkgdeps:
        c = taskdepdata[dep][0]
        manifest, d2 = oe.sstatesig.find_sstate_manifest(c, taskdepdata[dep][2], taskname, d, multilibs)
        if not manifest:
            bb.fatal("No manifest generated from: %s in %s" % (c, taskdepdata[dep][2]))
        if not os.path.exists(manifest):
            continue
        with open(manifest, "r") as f:
            for l in f:
                l = l.strip()
                deploydir = os.path.normpath(deploydir)
                if bb.data.inherits_class('packagefeed-stability', d):
                    dest = l.replace(deploydir + "-prediff", "")
                else:
                    dest = l.replace(deploydir, "")
                dest = subrepo_dir + dest
                if l.endswith("/"):
                    if dest not in seendirs:
                        bb.utils.mkdirhier(dest)
                        seendirs.add(dest)
                    continue
                # Try to hardlink the file, copy if that fails
                destdir = os.path.dirname(dest)
                if destdir not in seendirs:
                    bb.utils.mkdirhier(destdir)
                    seendirs.add(destdir)
                try:
                    os.link(l, dest)
                except OSError as err:
                    if err.errno == errno.EXDEV:
                        bb.utils.copyfile(l, dest)
                    else:
                        raise


def generate_index_files(d):
    from oe.package_manager.rpm import RpmSubdirIndexer
    from oe.package_manager.ipk import OpkgIndexer
    from oe.package_manager.deb import DpkgIndexer

    classes = d.getVar('PACKAGE_CLASSES').replace("package_", "").split()

    indexer_map = {
        "rpm": (RpmSubdirIndexer, d.getVar('DEPLOY_DIR_RPM')),
        "ipk": (OpkgIndexer, d.getVar('DEPLOY_DIR_IPK')),
        "deb": (DpkgIndexer, d.getVar('DEPLOY_DIR_DEB'))
    }

    result = None

    for pkg_class in classes:
        if not pkg_class in indexer_map:
            continue

        if os.path.exists(indexer_map[pkg_class][1]):
            result = indexer_map[pkg_class][0](d, indexer_map[pkg_class][1]).write_index()

            if result is not None:
                bb.fatal(result)
