#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import re
import shutil
import subprocess
from oe.package_manager import *
from oe.package_manager.common_deb_ipk import OpkgDpkgPM

class OpkgIndexer(Indexer):
    def write_index(self):
        arch_vars = ["ALL_MULTILIB_PACKAGE_ARCHS",
                     "SDK_PACKAGE_ARCHS",
                     ]

        opkg_index_cmd = bb.utils.which(os.getenv('PATH'), "opkg-make-index")
        opkg_index_cmd_extra_params = self.d.getVar('OPKG_MAKE_INDEX_EXTRA_PARAMS') or ""
        if self.d.getVar('PACKAGE_FEED_SIGN') == '1':
            signer = get_signer(self.d, self.d.getVar('PACKAGE_FEED_GPG_BACKEND'))
        else:
            signer = None

        if not os.path.exists(os.path.join(self.deploy_dir, "Packages")):
            open(os.path.join(self.deploy_dir, "Packages"), "w").close()

        index_cmds = set()
        index_sign_files = set()
        for arch_var in arch_vars:
            archs = self.d.getVar(arch_var)
            if archs is None:
                continue

            for arch in archs.split():
                pkgs_dir = os.path.join(self.deploy_dir, arch)
                pkgs_file = os.path.join(pkgs_dir, "Packages")

                if not os.path.isdir(pkgs_dir):
                    continue

                if not os.path.exists(pkgs_file):
                    open(pkgs_file, "w").close()

                index_cmds.add('%s --checksum md5 --checksum sha256 -r %s -p %s -m %s %s' %
                                  (opkg_index_cmd, pkgs_file, pkgs_file, pkgs_dir, opkg_index_cmd_extra_params))

                index_sign_files.add(pkgs_file)

        if len(index_cmds) == 0:
            bb.note("There are no packages in %s!" % self.deploy_dir)
            return

        oe.utils.multiprocess_launch(create_index, index_cmds, self.d)

        if signer:
            feed_sig_type = self.d.getVar('PACKAGE_FEED_GPG_SIGNATURE_TYPE')
            is_ascii_sig = (feed_sig_type.upper() != "BIN")
            for f in index_sign_files:
                signer.detach_sign(f,
                                   self.d.getVar('PACKAGE_FEED_GPG_NAME'),
                                   self.d.getVar('PACKAGE_FEED_GPG_PASSPHRASE_FILE'),
                                   armor=is_ascii_sig)

class PMPkgsList(PkgsList):
    def __init__(self, d, rootfs_dir):
        super(PMPkgsList, self).__init__(d, rootfs_dir)
        config_file = d.getVar("IPKGCONF_TARGET")

        self.opkg_cmd = bb.utils.which(os.getenv('PATH'), "opkg")
        self.opkg_args = "-f %s -o %s " % (config_file, rootfs_dir)
        self.opkg_args += self.d.getVar("OPKG_ARGS")

    def list_pkgs(self, format=None):
        cmd = "%s %s status" % (self.opkg_cmd, self.opkg_args)

        # opkg returns success even when it printed some
        # "Collected errors:" report to stderr. Mixing stderr into
        # stdout then leads to random failures later on when
        # parsing the output. To avoid this we need to collect both
        # output streams separately and check for empty stderr.
        p = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        cmd_output, cmd_stderr = p.communicate()
        cmd_output = cmd_output.decode("utf-8")
        cmd_stderr = cmd_stderr.decode("utf-8")
        if p.returncode or cmd_stderr:
            bb.fatal("Cannot get the installed packages list. Command '%s' "
                     "returned %d and stderr:\n%s" % (cmd, p.returncode, cmd_stderr))

        return opkg_query(cmd_output)


class OpkgPM(OpkgDpkgPM):
    def __init__(self, d, target_rootfs, config_file, archs, task_name='target', ipk_repo_workdir="oe-rootfs-repo", filterbydependencies=True, prepare_index=True):
        super(OpkgPM, self).__init__(d, target_rootfs)

        self.config_file = config_file
        self.pkg_archs = archs
        self.task_name = task_name

        self.deploy_dir = oe.path.join(self.d.getVar('WORKDIR'), ipk_repo_workdir)
        self.deploy_lock_file = os.path.join(self.deploy_dir, "deploy.lock")
        self.opkg_cmd = bb.utils.which(os.getenv('PATH'), "opkg")
        self.opkg_args = "--volatile-cache -f %s -t %s -o %s " % (self.config_file, self.d.expand('${T}/ipktemp/') ,target_rootfs)
        self.opkg_args += self.d.getVar("OPKG_ARGS")

        if prepare_index:
            create_packages_dir(self.d, self.deploy_dir, d.getVar("DEPLOY_DIR_IPK"), "package_write_ipk", filterbydependencies)

        self.opkg_dir = oe.path.join(target_rootfs, self.d.getVar('OPKGLIBDIR'), "opkg")
        bb.utils.mkdirhier(self.opkg_dir)

        self.saved_opkg_dir = self.d.expand('${T}/saved/%s' % self.task_name)
        if not os.path.exists(self.d.expand('${T}/saved')):
            bb.utils.mkdirhier(self.d.expand('${T}/saved'))

        self.from_feeds = (self.d.getVar('BUILD_IMAGES_FROM_FEEDS') or "") == "1"
        if self.from_feeds:
            self._create_custom_config()
        else:
            self._create_config()

        self.indexer = OpkgIndexer(self.d, self.deploy_dir)

    def mark_packages(self, status_tag, packages=None):
        """
        This function will change a package's status in /var/lib/opkg/status file.
        If 'packages' is None then the new_status will be applied to all
        packages
        """
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

        bb.utils.rename(status_file + ".tmp", status_file)

    def _create_custom_config(self):
        bb.note("Building from feeds activated!")

        with open(self.config_file, "w+") as config_file:
            priority = 1
            for arch in self.pkg_archs.split():
                config_file.write("arch %s %d\n" % (arch, priority))
                priority += 5

            for line in (self.d.getVar('IPK_FEED_URIS') or "").split():
                feed_match = re.match(r"^[ \t]*(.*)##([^ \t]*)[ \t]*$", line)

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
            if (self.d.getVar('FEED_DEPLOYDIR_BASE_URI') or "") != "":
                for arch in self.pkg_archs.split():
                    cfg_file_name = oe.path.join(self.target_rootfs,
                                                 self.d.getVar("sysconfdir"),
                                                 "opkg",
                                                 "local-%s-feed.conf" % arch)

                    with open(cfg_file_name, "w+") as cfg_file:
                        cfg_file.write("src/gz local-%s %s/%s" %
                                       (arch,
                                        self.d.getVar('FEED_DEPLOYDIR_BASE_URI'),
                                        arch))

                        if self.d.getVar('OPKGLIBDIR') != '/var/lib':
                            # There is no command line option for this anymore, we need to add
                            # info_dir and status_file to config file, if OPKGLIBDIR doesn't have
                            # the default value of "/var/lib" as defined in opkg:
                            # libopkg/opkg_conf.h:#define OPKG_CONF_DEFAULT_LISTS_DIR     VARDIR "/lib/opkg/lists"
                            # libopkg/opkg_conf.h:#define OPKG_CONF_DEFAULT_INFO_DIR      VARDIR "/lib/opkg/info"
                            # libopkg/opkg_conf.h:#define OPKG_CONF_DEFAULT_STATUS_FILE   VARDIR "/lib/opkg/status"
                            cfg_file.write("option info_dir     %s\n" % os.path.join(self.d.getVar('OPKGLIBDIR'), 'opkg', 'info'))
                            cfg_file.write("option lists_dir    %s\n" % os.path.join(self.d.getVar('OPKGLIBDIR'), 'opkg', 'lists'))
                            cfg_file.write("option status_file  %s\n" % os.path.join(self.d.getVar('OPKGLIBDIR'), 'opkg', 'status'))


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

            if self.d.getVar('OPKGLIBDIR') != '/var/lib':
                # There is no command line option for this anymore, we need to add
                # info_dir and status_file to config file, if OPKGLIBDIR doesn't have
                # the default value of "/var/lib" as defined in opkg:
                # libopkg/opkg_conf.h:#define OPKG_CONF_DEFAULT_LISTS_DIR     VARDIR "/lib/opkg/lists"
                # libopkg/opkg_conf.h:#define OPKG_CONF_DEFAULT_INFO_DIR      VARDIR "/lib/opkg/info"
                # libopkg/opkg_conf.h:#define OPKG_CONF_DEFAULT_STATUS_FILE   VARDIR "/lib/opkg/status"
                config_file.write("option info_dir     %s\n" % os.path.join(self.d.getVar('OPKGLIBDIR'), 'opkg', 'info'))
                config_file.write("option lists_dir    %s\n" % os.path.join(self.d.getVar('OPKGLIBDIR'), 'opkg', 'lists'))
                config_file.write("option status_file  %s\n" % os.path.join(self.d.getVar('OPKGLIBDIR'), 'opkg', 'status'))

    def insert_feeds_uris(self, feed_uris, feed_base_paths, feed_archs):
        if feed_uris == "":
            return

        rootfs_config = os.path.join('%s/etc/opkg/base-feeds.conf'
                                  % self.target_rootfs)

        os.makedirs('%s/etc/opkg' % self.target_rootfs, exist_ok=True)

        feed_uris = self.construct_uris(feed_uris.split(), feed_base_paths.split())
        archs = self.pkg_archs.split() if feed_archs is None else feed_archs.split()

        with open(rootfs_config, "w+") as config_file:
            uri_iterator = 0
            for uri in feed_uris:
                if archs:
                    for arch in archs:
                        if (feed_archs is None) and (not os.path.exists(oe.path.join(self.deploy_dir, arch))):
                            continue
                        bb.note('Adding opkg feed url-%s-%d (%s)' %
                            (arch, uri_iterator, uri))
                        config_file.write("src/gz uri-%s-%d %s/%s\n" %
                                          (arch, uri_iterator, uri, arch))
                else:
                    bb.note('Adding opkg feed url-%d (%s)' %
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
                     "returned %d:\n%s" % (cmd, e.returncode, e.output.decode("utf-8")))

        self.deploy_dir_unlock()

    def install(self, pkgs, attempt_only=False, hard_depends_only=False):
        if not pkgs:
            return

        cmd = "%s %s" % (self.opkg_cmd, self.opkg_args)
        for exclude in (self.d.getVar("PACKAGE_EXCLUDE") or "").split():
            cmd += " --add-exclude %s" % exclude
        for bad_recommendation in (self.d.getVar("BAD_RECOMMENDATIONS") or "").split():
            cmd += " --add-ignore-recommends %s" % bad_recommendation
        if hard_depends_only:
            cmd += " --no-install-recommends"
        cmd += " install "
        cmd += " ".join(pkgs)

        os.environ['D'] = self.target_rootfs
        os.environ['OFFLINE_ROOT'] = self.target_rootfs
        os.environ['IPKG_OFFLINE_ROOT'] = self.target_rootfs
        os.environ['OPKG_OFFLINE_ROOT'] = self.target_rootfs
        os.environ['INTERCEPT_DIR'] = self.intercepts_dir
        os.environ['NATIVE_ROOT'] = self.d.getVar('STAGING_DIR_NATIVE')

        try:
            bb.note("Installing the following packages: %s" % ' '.join(pkgs))
            bb.note(cmd)
            output = subprocess.check_output(cmd.split(), stderr=subprocess.STDOUT).decode("utf-8")
            bb.note(output)
            failed_pkgs = []
            for line in output.split('\n'):
                if line.endswith("configuration required on target."):
                    bb.warn(line)
                    failed_pkgs.append(line.split(".")[0])
            if failed_pkgs:
                failed_postinsts_abort(failed_pkgs, self.d.expand("${T}/log.do_${BB_CURRENTTASK}"))
        except subprocess.CalledProcessError as e:
            (bb.fatal, bb.warn)[attempt_only]("Unable to install packages. "
                                              "Command '%s' returned %d:\n%s" %
                                              (cmd, e.returncode, e.output.decode("utf-8")))

    def remove(self, pkgs, with_dependencies=True):
        if not pkgs:
            return

        if with_dependencies:
            cmd = "%s %s --force-remove --force-removal-of-dependent-packages remove %s" % \
                (self.opkg_cmd, self.opkg_args, ' '.join(pkgs))
        else:
            cmd = "%s %s --force-depends remove %s" % \
                (self.opkg_cmd, self.opkg_args, ' '.join(pkgs))

        try:
            bb.note(cmd)
            output = subprocess.check_output(cmd.split(), stderr=subprocess.STDOUT).decode("utf-8")
            bb.note(output)
        except subprocess.CalledProcessError as e:
            bb.fatal("Unable to remove packages. Command '%s' "
                     "returned %d:\n%s" % (e.cmd, e.returncode, e.output.decode("utf-8")))

    def write_index(self):
        self.deploy_dir_lock()

        result = self.indexer.write_index()

        self.deploy_dir_unlock()

        if result is not None:
            bb.fatal(result)

    def remove_packaging_data(self):
        cachedir = oe.path.join(self.target_rootfs, self.d.getVar("localstatedir"), "cache", "opkg")
        bb.utils.remove(self.opkg_dir, True)
        bb.utils.remove(cachedir, True)

    def remove_lists(self):
        if not self.from_feeds:
            bb.utils.remove(os.path.join(self.opkg_dir, "lists"), True)

    def list_installed(self):
        return PMPkgsList(self.d, self.target_rootfs).list_pkgs()

    def dummy_install(self, pkgs):
        """
        The following function dummy installs pkgs and returns the log of output.
        """
        if len(pkgs) == 0:
            return

        # Create an temp dir as opkg root for dummy installation
        temp_rootfs = self.d.expand('${T}/opkg')
        opkg_lib_dir = self.d.getVar('OPKGLIBDIR')
        if opkg_lib_dir[0] == "/":
            opkg_lib_dir = opkg_lib_dir[1:]
        temp_opkg_dir = os.path.join(temp_rootfs, opkg_lib_dir, 'opkg')
        bb.utils.mkdirhier(temp_opkg_dir)

        opkg_args = "-f %s -o %s " % (self.config_file, temp_rootfs)
        opkg_args += self.d.getVar("OPKG_ARGS")

        cmd = "%s %s update" % (self.opkg_cmd, opkg_args)
        try:
            subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
        except subprocess.CalledProcessError as e:
            bb.fatal("Unable to update. Command '%s' "
                     "returned %d:\n%s" % (cmd, e.returncode, e.output.decode("utf-8")))

        # Dummy installation
        cmd = "%s %s --noaction install %s " % (self.opkg_cmd,
                                                opkg_args,
                                                ' '.join(pkgs))
        proc = subprocess.run(cmd, capture_output=True, encoding="utf-8", shell=True)
        if proc.returncode:
            bb.fatal("Unable to dummy install packages. Command '%s' "
                     "returned %d:\n%s" % (cmd, proc.returncode, proc.stderr))
        elif proc.stderr:
            bb.note("Command '%s' returned stderr: %s" % (cmd, proc.stderr))

        bb.utils.remove(temp_rootfs, True)

        return proc.stdout

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

    def package_info(self, pkg):
        """
        Returns a dictionary with the package info.
        """
        cmd = "%s %s info %s" % (self.opkg_cmd, self.opkg_args, pkg)
        pkg_info = self._common_package_info(cmd)

        pkg_arch = pkg_info[pkg]["arch"]
        pkg_filename = pkg_info[pkg]["filename"]
        pkg_info[pkg]["filepath"] = \
                os.path.join(self.deploy_dir, pkg_arch, pkg_filename)

        return pkg_info
