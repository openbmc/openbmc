#
# SPDX-License-Identifier: GPL-2.0-only
#

import shutil
import subprocess
from oe.package_manager import *

class RpmIndexer(Indexer):
    def write_index(self):
        self.do_write_index(self.deploy_dir)

    def do_write_index(self, deploy_dir):
        if self.d.getVar('PACKAGE_FEED_SIGN') == '1':
            signer = get_signer(self.d, self.d.getVar('PACKAGE_FEED_GPG_BACKEND'))
        else:
            signer = None

        createrepo_c = bb.utils.which(os.environ['PATH'], "createrepo_c")
        result = create_index("%s --update -q %s" % (createrepo_c, deploy_dir))
        if result:
            bb.fatal(result)

        # Sign repomd
        if signer:
            sig_type = self.d.getVar('PACKAGE_FEED_GPG_SIGNATURE_TYPE')
            is_ascii_sig = (sig_type.upper() != "BIN")
            signer.detach_sign(os.path.join(deploy_dir, 'repodata', 'repomd.xml'),
                               self.d.getVar('PACKAGE_FEED_GPG_NAME'),
                               self.d.getVar('PACKAGE_FEED_GPG_PASSPHRASE_FILE'),
                               armor=is_ascii_sig)

class RpmSubdirIndexer(RpmIndexer):
    def write_index(self):
        bb.note("Generating package index for %s" %(self.deploy_dir))
        # Remove the existing repodata to ensure that we re-generate it no matter what
        bb.utils.remove(os.path.join(self.deploy_dir, "repodata"), recurse=True)

        self.do_write_index(self.deploy_dir)
        for entry in os.walk(self.deploy_dir):
            if os.path.samefile(self.deploy_dir, entry[0]):
                for dir in entry[1]:
                    if dir != 'repodata':
                        dir_path = oe.path.join(self.deploy_dir, dir)
                        bb.note("Generating package index for %s" %(dir_path))
                        self.do_write_index(dir_path)


class PMPkgsList(PkgsList):
    def list_pkgs(self):
        return RpmPM(self.d, self.rootfs_dir, self.d.getVar('TARGET_VENDOR'), needfeed=False).list_installed()

class RpmPM(PackageManager):
    def __init__(self,
                 d,
                 target_rootfs,
                 target_vendor,
                 task_name='target',
                 arch_var=None,
                 os_var=None,
                 rpm_repo_workdir="oe-rootfs-repo",
                 filterbydependencies=True,
                 needfeed=True):
        super(RpmPM, self).__init__(d, target_rootfs)
        self.target_vendor = target_vendor
        self.task_name = task_name
        if arch_var == None:
            self.archs = self.d.getVar('ALL_MULTILIB_PACKAGE_ARCHS').replace("-","_")
        else:
            self.archs = self.d.getVar(arch_var).replace("-","_")
        if task_name == "host":
            self.primary_arch = self.d.getVar('SDK_ARCH')
        else:
            self.primary_arch = self.d.getVar('MACHINE_ARCH')

        if needfeed:
            self.rpm_repo_dir = oe.path.join(self.d.getVar('WORKDIR'), rpm_repo_workdir)
            create_packages_dir(self.d, oe.path.join(self.rpm_repo_dir, "rpm"), d.getVar("DEPLOY_DIR_RPM"), "package_write_rpm", filterbydependencies)

        self.saved_packaging_data = self.d.expand('${T}/saved_packaging_data/%s' % self.task_name)
        if not os.path.exists(self.d.expand('${T}/saved_packaging_data')):
            bb.utils.mkdirhier(self.d.expand('${T}/saved_packaging_data'))
        self.packaging_data_dirs = ['etc/rpm', 'etc/rpmrc', 'etc/dnf', 'var/lib/rpm', 'var/lib/dnf', 'var/cache/dnf']
        self.solution_manifest = self.d.expand('${T}/saved/%s_solution' %
                                               self.task_name)
        if not os.path.exists(self.d.expand('${T}/saved')):
            bb.utils.mkdirhier(self.d.expand('${T}/saved'))

    def _configure_dnf(self):
        # libsolv handles 'noarch' internally, we don't need to specify it explicitly
        archs = [i for i in reversed(self.archs.split()) if i not in ["any", "all", "noarch"]]
        # This prevents accidental matching against libsolv's built-in policies
        if len(archs) <= 1:
            archs = archs + ["bogusarch"]
        # This architecture needs to be upfront so that packages using it are properly prioritized
        archs = ["sdk_provides_dummy_target"] + archs
        confdir = "%s/%s" %(self.target_rootfs, "etc/dnf/vars/")
        bb.utils.mkdirhier(confdir)
        open(confdir + "arch", 'w').write(":".join(archs))
        distro_codename = self.d.getVar('DISTRO_CODENAME')
        open(confdir + "releasever", 'w').write(distro_codename if distro_codename is not None else '')

        open(oe.path.join(self.target_rootfs, "etc/dnf/dnf.conf"), 'w').write("")


    def _configure_rpm(self):
        # We need to configure rpm to use our primary package architecture as the installation architecture,
        # and to make it compatible with other package architectures that we use.
        # Otherwise it will refuse to proceed with packages installation.
        platformconfdir = "%s/%s" %(self.target_rootfs, "etc/rpm/")
        rpmrcconfdir = "%s/%s" %(self.target_rootfs, "etc/")
        bb.utils.mkdirhier(platformconfdir)
        open(platformconfdir + "platform", 'w').write("%s-pc-linux" % self.primary_arch)
        with open(rpmrcconfdir + "rpmrc", 'w') as f:
            f.write("arch_compat: %s: %s\n" % (self.primary_arch, self.archs if len(self.archs) > 0 else self.primary_arch))
            f.write("buildarch_compat: %s: noarch\n" % self.primary_arch)

        open(platformconfdir + "macros", 'w').write("%_transaction_color 7\n")
        if self.d.getVar('RPM_PREFER_ELF_ARCH'):
            open(platformconfdir + "macros", 'a').write("%%_prefer_color %s" % (self.d.getVar('RPM_PREFER_ELF_ARCH')))

        if self.d.getVar('RPM_SIGN_PACKAGES') == '1':
            signer = get_signer(self.d, self.d.getVar('RPM_GPG_BACKEND'))
            pubkey_path = oe.path.join(self.d.getVar('B'), 'rpm-key')
            signer.export_pubkey(pubkey_path, self.d.getVar('RPM_GPG_NAME'))
            rpm_bin = bb.utils.which(os.getenv('PATH'), "rpmkeys")
            cmd = [rpm_bin, '--root=%s' % self.target_rootfs, '--import', pubkey_path]
            try:
                subprocess.check_output(cmd, stderr=subprocess.STDOUT)
            except subprocess.CalledProcessError as e:
                bb.fatal("Importing GPG key failed. Command '%s' "
                        "returned %d:\n%s" % (' '.join(cmd), e.returncode, e.output.decode("utf-8")))

    def create_configs(self):
        self._configure_dnf()
        self._configure_rpm()

    def write_index(self):
        lockfilename = self.d.getVar('DEPLOY_DIR_RPM') + "/rpm.lock"
        lf = bb.utils.lockfile(lockfilename, False)
        RpmIndexer(self.d, self.rpm_repo_dir).write_index()
        bb.utils.unlockfile(lf)

    def insert_feeds_uris(self, feed_uris, feed_base_paths, feed_archs):
        from urllib.parse import urlparse

        if feed_uris == "":
            return

        gpg_opts = ''
        if self.d.getVar('PACKAGE_FEED_SIGN') == '1':
            gpg_opts += 'repo_gpgcheck=1\n'
            gpg_opts += 'gpgkey=file://%s/pki/packagefeed-gpg/PACKAGEFEED-GPG-KEY-%s-%s\n' % (self.d.getVar('sysconfdir'), self.d.getVar('DISTRO'), self.d.getVar('DISTRO_CODENAME'))

        if self.d.getVar('RPM_SIGN_PACKAGES') != '1':
            gpg_opts += 'gpgcheck=0\n'

        bb.utils.mkdirhier(oe.path.join(self.target_rootfs, "etc", "yum.repos.d"))
        remote_uris = self.construct_uris(feed_uris.split(), feed_base_paths.split())
        for uri in remote_uris:
            repo_base = "oe-remote-repo" + "-".join(urlparse(uri).path.split("/"))
            if feed_archs is not None:
                for arch in feed_archs.split():
                    repo_uri = uri + "/" + arch
                    repo_id   = "oe-remote-repo"  + "-".join(urlparse(repo_uri).path.split("/"))
                    repo_name = "OE Remote Repo:" + " ".join(urlparse(repo_uri).path.split("/"))
                    open(oe.path.join(self.target_rootfs, "etc", "yum.repos.d", repo_base + ".repo"), 'a').write(
                             "[%s]\nname=%s\nbaseurl=%s\n%s\n" % (repo_id, repo_name, repo_uri, gpg_opts))
            else:
                repo_name = "OE Remote Repo:" + " ".join(urlparse(uri).path.split("/"))
                repo_uri = uri
                open(oe.path.join(self.target_rootfs, "etc", "yum.repos.d", repo_base + ".repo"), 'w').write(
                             "[%s]\nname=%s\nbaseurl=%s\n%s" % (repo_base, repo_name, repo_uri, gpg_opts))

    def _prepare_pkg_transaction(self):
        os.environ['D'] = self.target_rootfs
        os.environ['OFFLINE_ROOT'] = self.target_rootfs
        os.environ['IPKG_OFFLINE_ROOT'] = self.target_rootfs
        os.environ['OPKG_OFFLINE_ROOT'] = self.target_rootfs
        os.environ['INTERCEPT_DIR'] = self.intercepts_dir
        os.environ['NATIVE_ROOT'] = self.d.getVar('STAGING_DIR_NATIVE')


    def install(self, pkgs, attempt_only = False):
        if len(pkgs) == 0:
            return
        self._prepare_pkg_transaction()

        bad_recommendations = self.d.getVar('BAD_RECOMMENDATIONS')
        package_exclude = self.d.getVar('PACKAGE_EXCLUDE')
        exclude_pkgs = (bad_recommendations.split() if bad_recommendations else []) + (package_exclude.split() if package_exclude else [])

        output = self._invoke_dnf((["--skip-broken"] if attempt_only else []) +
                         (["-x", ",".join(exclude_pkgs)] if len(exclude_pkgs) > 0 else []) +
                         (["--setopt=install_weak_deps=False"] if self.d.getVar('NO_RECOMMENDATIONS') == "1" else []) +
                         (["--nogpgcheck"] if self.d.getVar('RPM_SIGN_PACKAGES') != '1' else ["--setopt=gpgcheck=True"]) +
                         ["install"] +
                         pkgs)

        failed_scriptlets_pkgnames = collections.OrderedDict()
        for line in output.splitlines():
            if line.startswith("Error in POSTIN scriptlet in rpm package"):
                failed_scriptlets_pkgnames[line.split()[-1]] = True

        if len(failed_scriptlets_pkgnames) > 0:
            failed_postinsts_abort(list(failed_scriptlets_pkgnames.keys()), self.d.expand("${T}/log.do_${BB_CURRENTTASK}"))

    def remove(self, pkgs, with_dependencies = True):
        if not pkgs:
            return

        self._prepare_pkg_transaction()

        if with_dependencies:
            self._invoke_dnf(["remove"] + pkgs)
        else:
            cmd = bb.utils.which(os.getenv('PATH'), "rpm")
            args = ["-e", "-v", "--nodeps", "--root=%s" %self.target_rootfs]

            try:
                bb.note("Running %s" % ' '.join([cmd] + args + pkgs))
                output = subprocess.check_output([cmd] + args + pkgs, stderr=subprocess.STDOUT).decode("utf-8")
                bb.note(output)
            except subprocess.CalledProcessError as e:
                bb.fatal("Could not invoke rpm. Command "
                     "'%s' returned %d:\n%s" % (' '.join([cmd] + args + pkgs), e.returncode, e.output.decode("utf-8")))

    def upgrade(self):
        self._prepare_pkg_transaction()
        self._invoke_dnf(["upgrade"])

    def autoremove(self):
        self._prepare_pkg_transaction()
        self._invoke_dnf(["autoremove"])

    def remove_packaging_data(self):
        self._invoke_dnf(["clean", "all"])
        for dir in self.packaging_data_dirs:
            bb.utils.remove(oe.path.join(self.target_rootfs, dir), True)

    def backup_packaging_data(self):
        # Save the packaging dirs for increment rpm image generation
        if os.path.exists(self.saved_packaging_data):
            bb.utils.remove(self.saved_packaging_data, True)
        for i in self.packaging_data_dirs:
            source_dir = oe.path.join(self.target_rootfs, i)
            target_dir = oe.path.join(self.saved_packaging_data, i)
            if os.path.isdir(source_dir):
                shutil.copytree(source_dir, target_dir, symlinks=True)
            elif os.path.isfile(source_dir):
                shutil.copy2(source_dir, target_dir)

    def recovery_packaging_data(self):
        # Move the rpmlib back
        if os.path.exists(self.saved_packaging_data):
            for i in self.packaging_data_dirs:
                target_dir = oe.path.join(self.target_rootfs, i)
                if os.path.exists(target_dir):
                    bb.utils.remove(target_dir, True)
                source_dir = oe.path.join(self.saved_packaging_data, i)
                if os.path.isdir(source_dir):
                    shutil.copytree(source_dir, target_dir, symlinks=True)
                elif os.path.isfile(source_dir):
                    shutil.copy2(source_dir, target_dir)

    def list_installed(self):
        output = self._invoke_dnf(["repoquery", "--installed", "--queryformat", "Package: %{name} %{arch} %{version} %{name}-%{version}-%{release}.%{arch}.rpm\nDependencies:\n%{requires}\nRecommendations:\n%{recommends}\nDependenciesEndHere:\n"],
                                  print_output = False)
        packages = {}
        current_package = None
        current_deps = None
        current_state = "initial"
        for line in output.splitlines():
            if line.startswith("Package:"):
                package_info = line.split(" ")[1:]
                current_package = package_info[0]
                package_arch = package_info[1]
                package_version = package_info[2]
                package_rpm = package_info[3]
                packages[current_package] = {"arch":package_arch, "ver":package_version, "filename":package_rpm}
                current_deps = []
            elif line.startswith("Dependencies:"):
                current_state = "dependencies"
            elif line.startswith("Recommendations"):
                current_state = "recommendations"
            elif line.startswith("DependenciesEndHere:"):
                current_state = "initial"
                packages[current_package]["deps"] = current_deps
            elif len(line) > 0:
                if current_state == "dependencies":
                    current_deps.append(line)
                elif current_state == "recommendations":
                    current_deps.append("%s [REC]" % line)

        return packages

    def update(self):
        self._invoke_dnf(["makecache", "--refresh"])

    def _invoke_dnf(self, dnf_args, fatal = True, print_output = True ):
        os.environ['RPM_ETCCONFIGDIR'] = self.target_rootfs

        dnf_cmd = bb.utils.which(os.getenv('PATH'), "dnf")
        standard_dnf_args = ["-v", "--rpmverbosity=info", "-y",
                             "-c", oe.path.join(self.target_rootfs, "etc/dnf/dnf.conf"),
                             "--setopt=reposdir=%s" %(oe.path.join(self.target_rootfs, "etc/yum.repos.d")),
                             "--installroot=%s" % (self.target_rootfs),
                             "--setopt=logdir=%s" % (self.d.getVar('T'))
                            ]
        if hasattr(self, "rpm_repo_dir"):
            standard_dnf_args.append("--repofrompath=oe-repo,%s" % (self.rpm_repo_dir))
        cmd = [dnf_cmd] + standard_dnf_args + dnf_args
        bb.note('Running %s' % ' '.join(cmd))
        try:
            output = subprocess.check_output(cmd,stderr=subprocess.STDOUT).decode("utf-8")
            if print_output:
                bb.debug(1, output)
            return output
        except subprocess.CalledProcessError as e:
            if print_output:
                (bb.note, bb.fatal)[fatal]("Could not invoke dnf. Command "
                     "'%s' returned %d:\n%s" % (' '.join(cmd), e.returncode, e.output.decode("utf-8")))
            else:
                (bb.note, bb.fatal)[fatal]("Could not invoke dnf. Command "
                     "'%s' returned %d:" % (' '.join(cmd), e.returncode))
            return e.output.decode("utf-8")

    def dump_install_solution(self, pkgs):
        open(self.solution_manifest, 'w').write(" ".join(pkgs))
        return pkgs

    def load_old_install_solution(self):
        if not os.path.exists(self.solution_manifest):
            return []
        with open(self.solution_manifest, 'r') as fd:
            return fd.read().split()

    def _script_num_prefix(self, path):
        files = os.listdir(path)
        numbers = set()
        numbers.add(99)
        for f in files:
            numbers.add(int(f.split("-")[0]))
        return max(numbers) + 1

    def save_rpmpostinst(self, pkg):
        bb.note("Saving postinstall script of %s" % (pkg))
        cmd = bb.utils.which(os.getenv('PATH'), "rpm")
        args = ["-q", "--root=%s" % self.target_rootfs, "--queryformat", "%{postin}", pkg]

        try:
            output = subprocess.check_output([cmd] + args,stderr=subprocess.STDOUT).decode("utf-8")
        except subprocess.CalledProcessError as e:
            bb.fatal("Could not invoke rpm. Command "
                     "'%s' returned %d:\n%s" % (' '.join([cmd] + args), e.returncode, e.output.decode("utf-8")))

        # may need to prepend #!/bin/sh to output

        target_path = oe.path.join(self.target_rootfs, self.d.expand('${sysconfdir}/rpm-postinsts/'))
        bb.utils.mkdirhier(target_path)
        num = self._script_num_prefix(target_path)
        saved_script_name = oe.path.join(target_path, "%d-%s" % (num, pkg))
        open(saved_script_name, 'w').write(output)
        os.chmod(saved_script_name, 0o755)

    def _handle_intercept_failure(self, registered_pkgs):
        rpm_postinsts_dir = self.target_rootfs + self.d.expand('${sysconfdir}/rpm-postinsts/')
        bb.utils.mkdirhier(rpm_postinsts_dir)

        # Save the package postinstalls in /etc/rpm-postinsts
        for pkg in registered_pkgs.split():
            self.save_rpmpostinst(pkg)

    def extract(self, pkg):
        output = self._invoke_dnf(["repoquery", "--queryformat", "%{location}", pkg])
        pkg_name = output.splitlines()[-1]
        if not pkg_name.endswith(".rpm"):
            bb.fatal("dnf could not find package %s in repository: %s" %(pkg, output))
        pkg_path = oe.path.join(self.rpm_repo_dir, pkg_name)

        cpio_cmd = bb.utils.which(os.getenv("PATH"), "cpio")
        rpm2cpio_cmd = bb.utils.which(os.getenv("PATH"), "rpm2cpio")

        if not os.path.isfile(pkg_path):
            bb.fatal("Unable to extract package for '%s'."
                     "File %s doesn't exists" % (pkg, pkg_path))

        tmp_dir = tempfile.mkdtemp()
        current_dir = os.getcwd()
        os.chdir(tmp_dir)

        try:
            cmd = "%s %s | %s -idmv" % (rpm2cpio_cmd, pkg_path, cpio_cmd)
            output = subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
        except subprocess.CalledProcessError as e:
            bb.utils.remove(tmp_dir, recurse=True)
            bb.fatal("Unable to extract %s package. Command '%s' "
                     "returned %d:\n%s" % (pkg_path, cmd, e.returncode, e.output.decode("utf-8")))
        except OSError as e:
            bb.utils.remove(tmp_dir, recurse=True)
            bb.fatal("Unable to extract %s package. Command '%s' "
                     "returned %d:\n%s at %s" % (pkg_path, cmd, e.errno, e.strerror, e.filename))

        bb.note("Extracted %s to %s" % (pkg_path, tmp_dir))
        os.chdir(current_dir)

        return tmp_dir
