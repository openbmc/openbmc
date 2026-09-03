#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

from bb.tests.fetch import FetcherTest
import bb
import bb.process
import glob
import hashlib
import json
import os
import shutil
import stat
import subprocess
import sys
import tempfile
import unittest
import venv
import zipfile
from bb.tests.support.httpserver import HTTPService

class BitbakeSetupTest(FetcherTest):
    def setUp(self):
        super(BitbakeSetupTest, self).setUp()

        self.registrypath = os.path.join(self.tempdir, "bitbake-setup-configurations")

        os.makedirs(self.registrypath)
        self.git_init(cwd=self.registrypath)
        self.git(['commit', '--allow-empty', '-m', "Initial commit"], cwd=self.registrypath)

        self.testrepopath = os.path.join(self.tempdir, "test-repo")
        os.makedirs(self.testrepopath)
        self.git_init(cwd=self.testrepopath)
        self.git(['commit', '--allow-empty', '-m', "Initial commit"], cwd=self.testrepopath)

        oeinitbuildenv = """BBPATH=$1
export BBPATH
PATH={}:$PATH
""".format(os.path.join(self.testrepopath, 'scripts'))
        self.add_file_to_testrepo('oe-init-build-env',oeinitbuildenv, script=True)

        oesetupbuild = """#!/usr/bin/env python3
import getopt
import sys
import os
import shutil
opts, args = getopt.getopt(sys.argv[2:], "c:b:", ["no-shell"])
for option, value in opts:
    if option == '-c':
        template = value
    if option == '-b':
        builddir = value
confdir = os.path.join(builddir, 'conf')
os.makedirs(confdir, exist_ok=True)
with open(os.path.join(confdir, 'conf-summary.txt'), 'w') as f:
    f.write(template)
shutil.copy(os.path.join(os.path.dirname(__file__), 'test-repo/test-file'), confdir)
with open(os.path.join(builddir, 'init-build-env'), 'w') as f:
    f.write("BBPATH={}\\nexport BBPATH\\nPATH={}:$PATH".format(builddir, os.path.join(os.path.dirname(__file__), 'test-repo/scripts')))
"""
        self.add_file_to_testrepo('scripts/oe-setup-build', oesetupbuild, script=True)

        installbuildtools = """#!/usr/bin/env python3
import getopt
import sys
import os

opts, args = getopt.getopt(sys.argv[1:], "d:", ["downloads-directory=", "local-file="])
installdir = None
local_file = None
for option, value in opts:
    if option == '-d':
        installdir = value
    elif option == '--local-file':
        local_file = value
        print("install-buildtools local-file={}".format(value))

print("Buildtools installed into {}".format(installdir))
os.makedirs(installdir, exist_ok=True)
"""
        self.add_file_to_testrepo('scripts/install-buildtools', installbuildtools, script=True)

        # Dummy buildtools installer for bb.fetch testing
        self.buildtools_dir = os.path.join(self.tempdir, "buildtools-dl")
        os.makedirs(self.buildtools_dir)
        self.buildtools_filename = "x86_64-buildtools-nativesdk-standalone-test.sh"
        buildtools_filepath = os.path.join(self.buildtools_dir, self.buildtools_filename)
        with open(buildtools_filepath, 'w') as f:
            f.write("#!/bin/sh\necho dummy\n")
        with open(buildtools_filepath, 'rb') as f:
            self.buildtools_sha256 = hashlib.sha256(f.read()).hexdigest()

        bitbakeconfigbuild = """#!/usr/bin/env python3
import os
import sys
confdir = os.path.join(os.environ['BBPATH'], 'conf')
fragment = sys.argv[2]
with open(os.path.join(confdir, fragment), 'w') as f:
    f.write('')
"""
        self.add_file_to_testrepo('scripts/bitbake-config-build', bitbakeconfigbuild, script=True)

        sometargetexecutable_template = """#!/usr/bin/env python3
import os
print("This is {}")
print("BBPATH is {{}}".format(os.environ["BBPATH"]))
"""
        for e_name in ("some-target-executable-1", "some-target-executable-2"):
            sometargetexecutable = sometargetexecutable_template.format(e_name)
            self.add_file_to_testrepo('scripts/{}'.format(e_name), sometargetexecutable, script=True)

    def runbbsetup(self, cmd):
        bbsetup = os.path.abspath(os.path.dirname(__file__) +  "/../../../bin/bitbake-setup")
        # Set PYTHONPATH so subprocess can find bb module instead of relying on the current directory
        env = os.environ.copy()
        libdir = os.path.abspath(os.path.dirname(__file__) + "/../..")
        env["PYTHONPATH"] = libdir + ":" + env.get("PYTHONPATH", "")
        return bb.process.run([bbsetup, '--global-settings', os.path.join(self.tempdir, 'global-config')] + cmd, env=env, cwd=self.tempdir)


    def _add_json_config_to_registry_helper(self, name, sources):
        config = """
{
    "sources": {
%s
    },
    "description": "Test configuration",
    "bitbake-setup": {
        "configurations": [
            {
                "name": "gadget",
                "description": "Gadget configuration",
                "oe-template": "test-configuration-gadget",
                "oe-fragments": ["test-fragment-1"],
                "install-buildtools": {
                    "url": "file://%s/%s",
                    "sha256sum": "%s"
                }
            },
            {
                "name": "gizmo",
                "description": "Gizmo configuration",
                "oe-template": "test-configuration-gizmo",
                "oe-fragments": ["test-fragment-2"],
                "setup-dir-name": "this-is-a-custom-gizmo-build"
            },
            {
                "name": "gizmo-env-passthrough",
                "description": "Gizmo configuration with environment-passthrough",
                "bb-layers": ["layerC","layerD/meta-layer"],
                "oe-fragments": ["test-fragment-1"],
                "bb-env-passthrough-additions": [
                    "BUILD_ID",
                    "BUILD_DATE",
                    "BUILD_SERVER"
                ]
            },
            {
                "name": "gizmo-no-fragment",
                "description": "Gizmo no-fragment template-only configuration",
                "oe-template": "test-configuration-gizmo"
            },
            {
                "name": "gadget-notemplate",
                "description": "Gadget notemplate configuration",
                "notes": [
                    "Gadget notemplate notes",
                    "Second line"
                ],
                "bb-layers": ["layerA","layerB/meta-layer"],
                "oe-fragments": ["test-fragment-1"]
            },
            {
                "name": "gizmo-notemplate-fragments-one-of",
                "description": "Gizmo notemplate configuration",
                "bb-layers": ["layerC","layerD/meta-layer"],
                "oe-fragments": ["test-fragment-2"],
                "oe-fragments-one-of": {
                    "fragment-desc": {
                        "description": "Test fragments (with description)",
                        "options" : [
                            { "name": "fragment-desc-1", "description": "fragment 1 desc" },
                            { "name": "fragment-desc-2", "description": "fragment 2 desc" }
                        ]
                    },
                    "fragment-nodesc": {
                        "description": "Test fragments (no description)",
                        "options" : [
                            "fragment-nodesc-1",
                            "fragment-nodesc-2"
                        ]
                    }
                }
            },
            {
                "name": "gizmo-notemplate-with-filerelative-layers",
                "description": "Gizmo notemplate configuration using filerelative layers",
                "bb-layers": ["layerC","layerD/meta-layer"],
                "bb-layers-file-relative": ["layerE/meta-layer"],
                "oe-fragments": ["test-fragment-2"]
            }
        ]
    },
    "version": "1.0"
}
""" % (sources, self.buildtools_dir, self.buildtools_filename, self.buildtools_sha256)
        os.makedirs(os.path.join(self.registrypath, os.path.dirname(name)), exist_ok=True)
        with open(os.path.join(self.registrypath, name), 'w') as f:
            f.write(config)
        self.git(['add', name], cwd=self.registrypath)
        self.git(['commit', '-m', "Adding " + name], cwd=self.registrypath)
        return json.loads(config)

    def add_json_config_to_registry(self, name, rev, branch):
        sources = """
        "test-repo": {
            "git-remote": {
                "remotes": {
                    "origin": {
                        "uri": "file://%s"
                    }
                },
                "branch": "%s",
                "rev": "%s"
            }
        }
""" % (self.testrepopath, branch, rev)
        return self._add_json_config_to_registry_helper(name, sources)

    def add_local_json_config_to_registry(self, name, path):
        sources = """
        "test-repo": {
            "local": {
                "path": "%s"
            }
        }
""" % (path)
        return self._add_json_config_to_registry_helper(name, sources)

    def add_file_to_testrepo(self, name, content, script=False):
        fullname = os.path.join(self.testrepopath, name)
        os.makedirs(os.path.join(self.testrepopath, os.path.dirname(name)), exist_ok=True)
        with open(fullname, 'w') as f:
            f.write(content)
        if script:
            st = os.stat(fullname)
            os.chmod(fullname, st.st_mode | stat.S_IEXEC)
        self.git(['add', name], cwd=self.testrepopath)
        self.git(['commit', '-m', "Adding " + name], cwd=self.testrepopath)

    def config_is_unchanged(self, setuppath):
        os.environ['BBPATH'] = os.path.join(setuppath, 'build')
        out = self.runbbsetup(["status"])
        self.assertIn("Configuration in {} has not changed".format(setuppath), out[0])
        out = self.runbbsetup(["update", "--update-bb-conf=yes"])
        self.assertIn("Configuration in {} has not changed".format(setuppath), out[0])
        del os.environ['BBPATH']

    def check_setupdir_files(self, setuppath, test_file_content):
        with open(os.path.join(setuppath, 'config', "config-upstream.json")) as f:
            config_upstream = json.load(f)
        with open(os.path.join(setuppath, 'layers', 'test-repo', 'test-file')) as f:
            self.assertEqual(f.read(), test_file_content)
        bitbake_config = config_upstream["bitbake-config"]
        bb_build_path = os.path.join(setuppath, 'build')
        bb_conf_path = os.path.join(bb_build_path, 'conf')
        self.assertTrue(os.path.exists(os.path.join(bb_build_path, 'init-build-env')))

        with open(os.path.join(setuppath, 'config', "sources-fixed-revisions.json")) as f:
            sources_fixed_revisions = json.load(f)
        self.assertTrue('test-repo' in sources_fixed_revisions['sources'].keys())
        revision = self.git(['rev-parse', 'HEAD'], cwd=self.testrepopath).strip()
        self.assertEqual(revision, sources_fixed_revisions['sources']['test-repo']['git-remote']['rev'])

        if "oe-template" in bitbake_config:
            with open(os.path.join(bb_conf_path, 'conf-summary.txt')) as f:
                self.assertEqual(f.read(), bitbake_config["oe-template"])
            with open(os.path.join(bb_conf_path, 'test-file')) as f:
                self.assertEqual(f.read(), test_file_content)
        else:
            with open(os.path.join(bb_conf_path, 'conf-summary.txt')) as f:
                self.assertIn(bitbake_config["description"], f.read())
            with open(os.path.join(bb_conf_path, 'conf-notes.txt')) as f:
                expected_notes = bitbake_config.get("notes")
                if isinstance(expected_notes, list):
                    expected_notes = "\n".join(expected_notes)
                self.assertEqual(f.read(), expected_notes + "\n" if expected_notes else "")
            with open(os.path.join(bb_conf_path, 'bblayers.conf')) as f:
                bblayers = f.read()
                for l in bitbake_config["bb-layers"]:
                    self.assertIn(os.path.join(setuppath, "layers", l), bblayers)
                for l in bitbake_config.get("bb-layers-file-relative") or []:
                    filerelative_layer = os.path.join(
                            os.path.dirname(config_upstream["path"]),
                            l,
                        )
                    self.assertIn(filerelative_layer, bblayers)

        if 'oe-fragments' in bitbake_config:
            for f in bitbake_config["oe-fragments"]:
                self.assertTrue(os.path.exists(os.path.join(bb_conf_path, f)))

        if 'bb-env-passthrough-additions' in bitbake_config:
            with open(os.path.join(bb_build_path, 'init-build-env'), 'r') as f:
                init_build_env = f.read()
            self.assertTrue('BB_ENV_PASSTHROUGH_ADDITIONS' in init_build_env)
            self.assertTrue('BUILD_ID' in init_build_env)
            self.assertTrue('BUILD_DATE' in init_build_env)
            self.assertTrue('BUILD_SERVER' in init_build_env)
            # a more throrough test could be to initialize a bitbake build-env, export FOO to the shell environment, set the env-passthrough on it and finally check against 'bitbake-getvar FOO'

        self.config_is_unchanged(setuppath)

    def get_setup_path(self, cf, c):
        if c == 'gizmo':
            return os.path.join(self.tempdir, 'bitbake-builds', 'this-is-a-custom-gizmo-build')
        return os.path.join(self.tempdir, 'bitbake-builds', '{}-{}'.format(cf, "-".join(c.split())))

    def test_setup(self):
        # unset BBPATH to ensure tests run in isolation from the existing bitbake environment
        if 'BBPATH' in os.environ:
            del os.environ['BBPATH']

        # check that no arguments works
        self.runbbsetup([])

        # check that --help works
        self.runbbsetup(["--help"])

        # change to self.tempdir to work with cwd-based default settings
        os.chdir(self.tempdir)

        # check that the default top-dir-prefix is cwd (now self.tempdir) with no global settings
        out = self.runbbsetup(["settings", "list"])
        self.assertIn("default top-dir-prefix {}".format(os.getcwd()), out[0])

        # set up global location for dl-dir
        settings_path = "{}/global-config".format(self.tempdir)
        out = self.runbbsetup(["settings", "set", "--global", "default", "dl-dir", os.path.join(self.tempdir, 'downloads')])
        self.assertIn("From section 'default' the setting 'dl-dir' was changed to", out[0])
        self.assertIn("Settings written to".format(settings_path), out[0])

        # check that writing settings works and then adjust them to point to
        # test registry repo
        out = self.runbbsetup(["settings", "set", "default", "registry", "'git://{};protocol=file;branch=master;rev=master'".format(self.registrypath)])
        settings_path = "{}/bitbake-builds/settings.conf".format(self.tempdir)
        self.assertIn(settings_path, out[0])
        self.assertIn("From section 'default' the setting 'registry' was changed to", out[0])
        self.assertIn("Settings written to".format(settings_path), out[0])

        # check that listing settings works
        out = self.runbbsetup(["settings", "list"])
        self.assertIn("default top-dir-prefix {}".format(self.tempdir), out[0])
        self.assertIn("default dl-dir {}".format(os.path.join(self.tempdir, 'downloads')), out[0])
        self.assertIn("default registry {}".format('git://{};protocol=file;branch=master;rev=master'.format(self.registrypath)), out[0])

        # check that 'list' produces correct output with no configs, one config and two configs
        out = self.runbbsetup(["list"])
        self.assertNotIn("test-config-1", out[0])
        self.assertNotIn("test-config-2", out[0])

        json_1 = self.add_json_config_to_registry('test-config-1.conf.json', 'master', 'master')
        out = self.runbbsetup(["list"])
        self.assertIn("test-config-1", out[0])
        self.assertNotIn("test-config-2", out[0])

        json_2 = self.add_json_config_to_registry('config-2/test-config-2.conf.json', 'master', 'master')
        out = self.runbbsetup(["list", "--write-json={}".format(os.path.join(self.tempdir, "test-configs.json"))])
        self.assertIn("test-config-1", out[0])
        self.assertIn("test-config-2", out[0])
        with open(os.path.join(self.tempdir, "test-configs.json")) as f:
            json_configs = json.load(f)
        self.assertIn("test-config-1", json_configs)
        self.assertIn("test-config-2", json_configs)

        # check that init/status/update work
        # (the latter two should do nothing and say that config hasn't changed)
        test_file_content = 'initial\n'
        self.add_file_to_testrepo('test-file', test_file_content)

        # test-config-1 is tested as a registry config and over http, test-config-2 as a local file
        server = HTTPService(self.registrypath, host="127.0.0.1")
        server.start()
        variants = (
            'gadget',
            'gizmo',
            'gizmo-env-passthrough',
            'gizmo-no-fragment',
            'gadget-notemplate',
            'gizmo-notemplate-fragments-one-of fragment-desc-1 fragment-nodesc-1',
        )
        variants_local = variants + ('gizmo-notemplate-with-filerelative-layers',)
        test_configurations = ({'name':'test-config-1','cmdline': 'test-config-1',
                                                 'buildconfigs': variants},
                               {'name':'test-config-2','cmdline': os.path.join(self.registrypath,'config-2/test-config-2.conf.json'),
                                                 'buildconfigs': variants_local},
                               {'name':'test-config-1','cmdline':'http://127.0.0.1:{}/test-config-1.conf.json'.format(server.port),
                                                 'buildconfigs': variants}
                               )
        try:
            for v in test_configurations:
                for c in v['buildconfigs']:
                    out = self.runbbsetup(["init", "--non-interactive", v['cmdline']] + c.split())
                    setuppath = self.get_setup_path(v['name'], c)
                    self.check_setupdir_files(setuppath, test_file_content)
        finally:
            server.stop()

        # install buildtools
        out = self.runbbsetup(["install-buildtools", "--setup-dir", setuppath])
        self.assertIn("Buildtools installed into", out[0])
        self.assertTrue(os.path.exists(os.path.join(setuppath, 'buildtools')))

        # change a file in the test layer repo, make a new commit and
        # test that status/update correctly report the change and update the config
        prev_test_file_content = test_file_content
        test_file_content = 'modified\n'
        self.add_file_to_testrepo('test-file', test_file_content)
        for c in variants:
            setuppath = self.get_setup_path('test-config-1', c)
            os.environ['BBPATH'] = os.path.join(setuppath, 'build')
            out = self.runbbsetup(["status"])
            self.assertIn("Layer repository file://{} checked out into {}/layers/test-repo updated revision master from".format(self.testrepopath, setuppath), out[0])
            out = self.runbbsetup(["update", "--update-bb-conf=yes"])
            if c in ('gadget', 'gizmo'):
                self.assertIn("Leaving the previous configuration in {}/build/conf-backup.".format(setuppath), out[0])
                self.assertIn('-{}+{}'.format(prev_test_file_content, test_file_content), out[0])
            self.check_setupdir_files(setuppath, test_file_content)

        # make a new branch in the test layer repo, change a file on that branch,
        # make a new commit, update the top level json config to refer to that branch,
        # and test that status/update correctly report the change and update the config
        prev_test_file_content = test_file_content
        test_file_content = 'modified-in-branch\n'
        branch = "another-branch"
        self.git(['checkout', '-b', branch], cwd=self.testrepopath)
        self.add_file_to_testrepo('test-file', test_file_content)
        json_1 = self.add_json_config_to_registry('test-config-1.conf.json', branch, branch)
        for c in variants:
            setuppath = self.get_setup_path('test-config-1', c)
            os.environ['BBPATH'] = os.path.join(setuppath, 'build')
            out = self.runbbsetup(["status"])
            self.assertIn("Configuration in {} has changed:".format(setuppath), out[0])
            self.assertIn('-                    "rev": "master"\n+                    "rev": "another-branch"', out[0])
            out = self.runbbsetup(["update", "--update-bb-conf=yes"])
            if c in ('gadget', 'gizmo'):
                self.assertIn("Leaving the previous configuration in {}/build/conf-backup.".format(setuppath), out[0])
                self.assertIn('-{}+{}'.format(prev_test_file_content, test_file_content), out[0])
            self.check_setupdir_files(setuppath, test_file_content)

        # do the same as the previous test, but now without updating the bitbake configuration (--update-bb-conf=no)
        # and check that files have not been modified
        def _conf_chksum(confdir: str) -> list:
            sums = []
            for f in glob.glob(f'{confdir}/*'):
                if os.path.isfile(f) and not os.path.islink(f):
                    with open(f, 'rb') as fd:
                        sha = hashlib.sha256()
                        sha.update(fd.read())
                        sums.append(os.path.basename(f) + '_' + sha.hexdigest())
            return sums

        prev_test_file_content = test_file_content
        test_file_content = 'modified-in-branch-no-bb-conf-update\n'
        branch = "another-branch-no-bb-conf-update"
        self.git(['checkout', '-b', branch], cwd=self.testrepopath)
        self.add_file_to_testrepo('test-file', test_file_content)
        json_1 = self.add_json_config_to_registry('test-config-1.conf.json', branch, branch)
        for c in variants:
            setuppath = self.get_setup_path('test-config-1', c)
            os.environ['BBPATH'] = os.path.join(setuppath, 'build')
            # write something in local.conf and bblayers.conf
            for f in ["local.conf", "bblayers.conf"]:
                with open(f"{setuppath}/build/conf/{f}", "w") as fd:
                    fd.write("deadbeef")
            sums_before = _conf_chksum(f"{setuppath}/build/conf")
            out = self.runbbsetup(["update", "--update-bb-conf=no"])
            sums_after = _conf_chksum(f"{setuppath}/build/conf")
            self.assertEqual(sums_before, sums_after)

        def _check_local_sources(custom_setup_dir):
            custom_setup_path = os.path.join(self.tempdir, 'bitbake-builds', custom_setup_dir)
            custom_layer_path = os.path.join(custom_setup_path, 'layers', 'test-repo')
            self.assertTrue(os.path.islink(custom_layer_path))
            self.assertEqual(self.testrepopath, os.path.realpath(custom_layer_path))
            self.config_is_unchanged(custom_setup_path)

        def _check_layer_backups(layer_path, expected_backups):
            files = os.listdir(layer_path)
            backups = len([f for f in files if 'backup' in f])
            self.assertEqual(backups, expected_backups, msg = "Expected {} layer backups, got {}, directory listing: {}".format(expected_backups, backups, files))

        # Change the configuration to refer to a local source, then to another local source, then back to a git remote
        # Run status/update after each change and verify that nothing breaks
        # Also check that layer backups happen when expected
        c = 'gadget'
        setuppath = self.get_setup_path('test-config-1', c)
        self.config_is_unchanged(setuppath)

        layers_path = os.path.join(setuppath, 'layers')
        layer_path = os.path.join(layers_path, 'test-repo')
        _check_layer_backups(layers_path, 0)

        json_1 = self.add_local_json_config_to_registry('test-config-1.conf.json', self.testrepopath)
        os.environ['BBPATH'] = os.path.join(setuppath, 'build')
        out = self.runbbsetup(["update", "--update-bb-conf=yes"])
        _check_local_sources(setuppath)
        _check_layer_backups(layers_path, 1)

        prev_path = self.testrepopath
        self.testrepopath = prev_path + "-2"
        self.git(['clone', prev_path, self.testrepopath], cwd=self.tempdir)
        json_1 = self.add_local_json_config_to_registry('test-config-1.conf.json', self.testrepopath)
        os.environ['BBPATH'] = os.path.join(setuppath, 'build')
        out = self.runbbsetup(["update", "--update-bb-conf=yes"])
        _check_local_sources(setuppath)
        _check_layer_backups(layers_path, 1)

        self.testrepopath = prev_path
        json_1 = self.add_json_config_to_registry('test-config-1.conf.json', branch, branch)
        os.environ['BBPATH'] = os.path.join(setuppath, 'build')
        out = self.runbbsetup(["update", "--update-bb-conf=yes"])
        self.check_setupdir_files(setuppath, test_file_content)
        _check_layer_backups(layers_path, 1)

        ## edit a file without making a commit
        with open(os.path.join(layer_path, 'local-modification'), 'w') as f:
            f.write('locally-modified\n')
        test_file_content = "modified-again\n"
        self.add_file_to_testrepo('test-file', test_file_content)
        os.environ['BBPATH'] = os.path.join(setuppath, 'build')
        out = self.runbbsetup(["update", "--update-bb-conf=yes"])
        _check_layer_backups(layers_path, 1)

        ## edit a file and make a commit such that no rebase conflicts occur
        with open(os.path.join(layer_path, 'local-modification'), 'w') as f:
            f.write('locally-modified-again\n')
        self.git(['add', '.'], cwd=layer_path)
        self.git(['commit', '-m', 'Adding a local modification'], cwd=layer_path)
        test_file_content = "modified-again-and-again\n"
        self.add_file_to_testrepo('test-file', test_file_content)
        out = self.runbbsetup(["update", "--update-bb-conf=yes"])
        _check_layer_backups(layers_path, 1)

        ## edit a file and make a commit in a way that causes a rebase conflict
        with open(os.path.join(layer_path, 'test-file'), 'w') as f:
            f.write('locally-modified\n')
        self.git(['add', '.'], cwd=layer_path)
        self.git(['commit', '-m', 'Adding a local modification'], cwd=layer_path)
        test_file_content = "remotely-modified\n"
        self.add_file_to_testrepo('test-file', test_file_content)
        with self.assertRaisesRegex(bb.process.ExecutionError, "Merge conflict in test-file"):
            out = self.runbbsetup(["update", "--update-bb-conf=yes"])
        _check_layer_backups(layers_path, 1)

        # check source overrides, local sources provided with symlinks, and custom setup dir name
        source_override_content = """
{
    "sources": {
        "test-repo": {
            "local": {
                "path": "."
            }
        }
    }
}"""
        override_filename = 'source-overrides.json'
        custom_setup_dir = 'special-setup-dir'
        self.add_file_to_testrepo(override_filename, source_override_content)
        out = self.runbbsetup(["init", "--non-interactive", "--source-overrides", os.path.join(self.testrepopath, override_filename), "--setup-dir-name", custom_setup_dir, "test-config-1", "gadget"])
        _check_local_sources(custom_setup_dir)

        # same but use command line options to specify local overrides
        custom_setup_dir = 'special-setup-dir-with-cmdline-overrides'
        out = self.runbbsetup(["init", "--non-interactive", "-L", "test-repo", self.testrepopath, "--setup-dir-name", custom_setup_dir, "test-config-1", "gadget"])
        _check_local_sources(custom_setup_dir)

    def test_vscode(self):
        if 'BBPATH' in os.environ:
            del os.environ['BBPATH']
        os.chdir(self.tempdir)

        self.runbbsetup(["settings", "set", "default", "registry", "'git://{};protocol=file;branch=master;rev=master'".format(self.registrypath)])
        self.add_file_to_testrepo('test-file', 'initial\n')
        self.add_json_config_to_registry('test-config-1.conf.json', 'master', 'master')

        # --init-vscode should create bitbake.code-workspace
        self.runbbsetup(["init", "--non-interactive", "--init-vscode", "test-config-1", "gadget"])
        setuppath = self.get_setup_path('test-config-1', 'gadget')
        workspace_file = os.path.join(setuppath, 'bitbake.code-workspace')
        self.assertTrue(os.path.exists(workspace_file),
                        "bitbake.code-workspace should be created with --init-vscode")

        with open(workspace_file) as f:
            workspace = json.load(f)

        # top-level structure
        self.assertIn('folders', workspace)
        self.assertIn('settings', workspace)
        self.assertIn('extensions', workspace)
        self.assertIn('yocto-project.yocto-bitbake',
                      workspace['extensions']['recommendations'])

        # folders: conf dir + test-repo (symlinks like oe-init-build-env-dir are skipped)
        folder_names = {f['name'] for f in workspace['folders']}
        self.assertIn('conf', folder_names)
        self.assertIn('test-repo', folder_names)

        # folder paths must be relative so the workspace is portable
        for f in workspace['folders']:
            self.assertFalse(os.path.isabs(f['path']),
                             "Folder path should be relative, got: {}".format(f['path']))

        # bitbake extension settings
        settings = workspace['settings']
        self.assertTrue(settings.get('bitbake.disableConfigModification'))
        self.assertEqual(settings['bitbake.pathToBuildFolder'],
                         os.path.join(setuppath, 'build'))
        self.assertEqual(settings['bitbake.pathToEnvScript'],
                         os.path.join(setuppath, 'build', 'init-build-env'))

        # file associations
        self.assertIn('*.conf', settings.get('files.associations', {}))
        self.assertIn('*.inc', settings.get('files.associations', {}))

        # python extra paths: test-repo/scripts/ exists and should be listed
        extra_paths = settings.get('python.analysis.extraPaths', [])
        self.assertTrue(any('scripts' in p for p in extra_paths),
                        "python.analysis.extraPaths should include the scripts dir")
        self.assertEqual(settings.get('python.analysis.extraPaths'),
                         settings.get('python.autoComplete.extraPaths'))

        # --no-init-vscode should NOT create a workspace file
        self.runbbsetup(["init", "--non-interactive", "--no-init-vscode", "test-config-1", "gadget-notemplate"])
        notemplate_path = self.get_setup_path('test-config-1', 'gadget-notemplate')
        self.assertFalse(
            os.path.exists(os.path.join(notemplate_path, 'bitbake.code-workspace')),
            "bitbake.code-workspace should not be created with --no-init-vscode")

        # update with --init-vscode after a layer change should preserve
        # user-added folders and settings while still rewriting managed ones
        workspace['folders'].append({"name": "user-folder", "path": "user/custom"})
        workspace['settings']['my.user.setting'] = 'preserved'
        with open(workspace_file, 'w') as f:
            json.dump(workspace, f, indent=4)

        self.add_file_to_testrepo('test-file', 'updated\n')
        os.environ['BBPATH'] = os.path.join(setuppath, 'build')
        self.runbbsetup(["update", "--update-bb-conf=no"])
        del os.environ['BBPATH']

        with open(workspace_file) as f:
            updated = json.load(f)
        self.assertIn('user/custom', {f['path'] for f in updated['folders']},
                      "User-added folder was removed during update")
        self.assertIn('my.user.setting', updated['settings'],
                      "User-added setting was removed during update")

        # update with a corrupt workspace file should log an error and leave it unchanged
        self.add_file_to_testrepo('test-file', 'updated-again\n')
        with open(workspace_file, 'w') as f:
            f.write('{invalid json')
        os.environ['BBPATH'] = os.path.join(setuppath, 'build')
        self.runbbsetup(["update", "--update-bb-conf=no"])
        del os.environ['BBPATH']
        with open(workspace_file) as f:
            content = f.read()
        self.assertEqual(content, '{invalid json',
                         "Corrupt workspace file should not be modified")

    def _count_layer_backups(self, layers_path):
        return len([f for f in os.listdir(layers_path) if 'backup' in f])

    def test_install_buildtools_fetch(self):
        """Test that install-buildtools uses bb.fetch with sha256 and passes --local-file"""
        import shutil

        if 'BBPATH' in os.environ:
            del os.environ['BBPATH']
        os.chdir(self.tempdir)

        registry_uri = "git://{};protocol=file;branch=master;rev=master".format(
            self.registrypath)
        self.runbbsetup(["settings", "set", "default", "registry", registry_uri])
        self.add_file_to_testrepo('test-file', 'initial\n')
        self.add_json_config_to_registry('test-config-bt.conf.json', 'master', 'master')
        self.runbbsetup(["init", "--non-interactive", "test-config-bt", "gadget"])
        setuppath = self.get_setup_path('test-config-bt', 'gadget')

        # test config-driven install (url + sha256sum from config)
        out = self.runbbsetup(["install-buildtools", "--setup-dir", setuppath])
        self.assertIn("Buildtools installed into", out[0])
        self.assertIn("install-buildtools local-file=", out[0])
        self.assertTrue(os.path.exists(os.path.join(setuppath, 'buildtools')))

        # test CLI overrides (use a different file to prove precedence)
        shutil.rmtree(os.path.join(setuppath, 'buildtools'))
        alt_filename = "alt-buildtools-test.sh"
        alt_filepath = os.path.join(self.buildtools_dir, alt_filename)
        with open(alt_filepath, 'w') as f:
            f.write("#!/bin/sh\necho alt\n")
        with open(alt_filepath, 'rb') as f:
            alt_sha256 = hashlib.sha256(f.read()).hexdigest()
        alt_url = "file://{}/{}".format(self.buildtools_dir, alt_filename)
        out = self.runbbsetup(["install-buildtools", "--setup-dir", setuppath,
                               "--url", alt_url,
                               "--sha256", alt_sha256])
        self.assertIn("Buildtools installed into", out[0])
        self.assertIn("install-buildtools local-file=", out[0])
        self.assertIn(alt_filename, out[0])

        # test missing sha256sum is a hard error
        shutil.rmtree(os.path.join(setuppath, 'buildtools'), ignore_errors=True)
        with open(os.path.join(setuppath, 'config', 'config-upstream.json')) as f:
            config_upstream = json.load(f)
        config_upstream['bitbake-config']['install-buildtools'] = {
            'url': 'file://{}/{}'.format(self.buildtools_dir, self.buildtools_filename)
        }
        with open(os.path.join(setuppath, 'config', 'config-upstream.json'), 'w') as f:
            json.dump(config_upstream, f)
        with self.assertRaises(bb.process.ExecutionError):
            self.runbbsetup(["install-buildtools", "--setup-dir", setuppath, "--force"])

    def test_update_rebase_conflicts_strategy(self):
        """Test the --rebase-conflicts-strategy option for the update command.

        Covers three scenarios not exercised by test_setup:
        1. Uncommitted tracked-file change (LocalModificationsError) + default 'abort'
           strategy → clean error message containing 'has uncommitted changes' and a
           hint at --rebase-conflicts-strategy=backup; no backup directory is created.
        2. Same uncommitted change + 'backup' strategy → directory is renamed to a
           timestamped backup and the layer is re-cloned cleanly.
        3. Committed local change that conflicts with an incoming upstream commit
           (RebaseError):
           a. Default 'abort' strategy → error containing 'Merge conflict' and the
              --rebase-conflicts-strategy=backup hint; no backup directory is created.
           b. 'backup' strategy → backup + re-clone instead of a hard failure.
        """
        if 'BBPATH' in os.environ:
            del os.environ['BBPATH']
        os.chdir(self.tempdir)

        self.runbbsetup(["settings", "set", "default", "registry", "'git://{};protocol=file;branch=master;rev=master'".format(self.registrypath)])
        self.add_file_to_testrepo('test-file', 'initial\n')
        self.add_json_config_to_registry('test-config-1.conf.json', 'master', 'master')
        self.runbbsetup(["init", "--non-interactive", "test-config-1", "gadget"])

        setuppath = self.get_setup_path('test-config-1', 'gadget')
        layer_path = os.path.join(setuppath, 'layers', 'test-repo')
        layers_path = os.path.join(setuppath, 'layers')

        # Scenario 1: uncommitted tracked change, default 'abort' strategy
        # Advance upstream so an update is required.
        self.add_file_to_testrepo('test-file', 'upstream-v2\n')
        # Modify the same tracked file in the layer without committing.
        with open(os.path.join(layer_path, 'test-file'), 'w') as f:
            f.write('locally-modified\n')

        os.environ['BBPATH'] = os.path.join(setuppath, 'build')
        with self.assertRaises(bb.process.ExecutionError) as ctx:
            self.runbbsetup(["update", "--update-bb-conf=no"])
        self.assertIn('has uncommitted changes', str(ctx.exception))
        self.assertIn('--rebase-conflicts-strategy=backup', str(ctx.exception))
        # No backup directory must have been created.
        self.assertEqual(self._count_layer_backups(layers_path), 0,
                         "abort strategy must not create any backup")

        # Scenario 2: same uncommitted change, 'backup' strategy
        out = self.runbbsetup(["update", "--update-bb-conf=no", "--rebase-conflicts-strategy=backup"])
        # One backup directory must now exist.
        self.assertEqual(self._count_layer_backups(layers_path), 1,
                         "backup strategy must create exactly one backup")
        # The re-cloned layer must be clean and at the upstream revision.
        with open(os.path.join(layer_path, 'test-file')) as f:
            self.assertEqual(f.read(), 'upstream-v2\n',
                             "re-cloned layer must contain the upstream content")
        status = self.git(['status', '--porcelain'], cwd=layer_path).strip()
        self.assertEqual(status, '',
                         "re-cloned layer must have no local modifications")
        del os.environ['BBPATH']

        # Scenario 3: committed conflicting change, 'backup' strategy
        # Re-initialise a fresh setup so we start from a clean state.
        self.runbbsetup(["init", "--non-interactive", "--setup-dir-name", "rebase-conflict-setup", "test-config-1", "gadget"])
        conflict_setup = os.path.join(self.tempdir, 'bitbake-builds', 'rebase-conflict-setup')
        conflict_layer = os.path.join(conflict_setup, 'layers', 'test-repo')
        conflict_layers = os.path.join(conflict_setup, 'layers')

        # Commit a local change that touches the same file as the next upstream commit.
        with open(os.path.join(conflict_layer, 'test-file'), 'w') as f:
            f.write('conflicting-local\n')
        self.git(['add', 'test-file'], cwd=conflict_layer)
        self.git(['commit', '-m', 'Local conflicting change'], cwd=conflict_layer)

        # Advance upstream with a conflicting edit.
        self.add_file_to_testrepo('test-file', 'conflicting-upstream\n')

        os.environ['BBPATH'] = os.path.join(conflict_setup, 'build')
        # Default stop strategy must still fail with a conflict error and include
        # the --rebase-conflicts-strategy=backup hint (same handler as LocalModificationsError).
        with self.assertRaises(bb.process.ExecutionError) as ctx:
            self.runbbsetup(["update", "--update-bb-conf=no"])
        self.assertIn('Merge conflict in test-file', str(ctx.exception))
        self.assertIn('--rebase-conflicts-strategy=backup', str(ctx.exception))
        self.assertEqual(self._count_layer_backups(conflict_layers), 0)

        # Backup strategy must succeed: backup the conflicted dir and re-clone.
        self.runbbsetup(["update", "--update-bb-conf=no", "--rebase-conflicts-strategy=backup"])
        self.assertEqual(self._count_layer_backups(conflict_layers), 1,
                         "backup strategy must create exactly one backup after a conflict")
        with open(os.path.join(conflict_layer, 'test-file')) as f:
            self.assertEqual(f.read(), 'conflicting-upstream\n',
                             "re-cloned layer must contain the upstream content after conflict backup")
        del os.environ['BBPATH']

@unittest.skipIf(os.environ.get("BB_SKIP_PYPI_TESTS", "yes") != "no",
                 "PyPI packaging test (set BB_SKIP_PYPI_TESTS=no to run)")
class PyPIPackagingTest(unittest.TestCase):
    """Tests for PyPI packaging of bitbake-setup.

    These tests build a wheel from source, install it in an isolated venv,
    and verify the package works correctly. Skipped by default unless
    BB_SKIP_PYPI_TESTS=no is set.
    """

    wheel_path = None
    build_tempdir = None
    workspace_dir = None

    @classmethod
    def setUpClass(cls):
        """Build wheel once for all tests in this class."""
        # Locate packaging-pypi/bitbake-setup directory
        cls.pypi_dir = os.path.abspath(
            os.path.join(os.path.dirname(__file__), '..', '..', '..', 'packaging-pypi', 'bitbake-setup')
        )

        # Check for required build tools
        cls._check_build_tools()

        # Create temporary directory for packaging workspace
        cls.build_tempdir = tempfile.mkdtemp(prefix="bitbake-pypi-build-")

        # Run package-bitbake-setup.py to create packaging workspace
        cls._create_packaging_workspace()

        # Build the wheel
        cls._build_wheel()

    @classmethod
    def _check_build_tools(cls):
        """Verify build tools are available, skip if not."""
        try:
            result = subprocess.run(
                [sys.executable, "-m", "build", "--version"],
                capture_output=True, check=True
            )
        except (subprocess.CalledProcessError, FileNotFoundError):
            raise unittest.SkipTest("'build' package not installed (pip install build)")

    @classmethod
    def _create_packaging_workspace(cls):
        """Create packaging workspace using package-bitbake-setup.py."""
        script = os.path.join(cls.pypi_dir, 'package-bitbake-setup.py')
        cls.workspace_dir = os.path.join(cls.build_tempdir, 'workspace')
        result = subprocess.run(
            [sys.executable, script, '-d', cls.workspace_dir],
            capture_output=True, text=True
        )
        if result.returncode != 0:
            raise unittest.SkipTest(f"Packaging workspace creation failed: {result.stderr}")

    @classmethod
    def _build_wheel(cls):
        """Build the wheel in the packaging workspace."""
        result = subprocess.run(
            [sys.executable, "-m", "build", "--wheel"],
            cwd=cls.workspace_dir,
            capture_output=True, text=True
        )
        if result.returncode != 0:
            raise unittest.SkipTest(f"Wheel build failed: {result.stderr}")

        # Find the built wheel
        dist_dir = os.path.join(cls.workspace_dir, 'dist')
        wheels = glob.glob(os.path.join(dist_dir, '*.whl'))
        if not wheels:
            raise unittest.SkipTest("No wheel file found after build")
        cls.wheel_path = wheels[0]

    @classmethod
    def tearDownClass(cls):
        """Clean up the shared wheel build artifacts."""
        if cls.build_tempdir and os.environ.get("BB_TMPDIR_NOCLEAN") != "yes":
            shutil.rmtree(cls.build_tempdir, ignore_errors=True)
        elif cls.build_tempdir:
            print(f"Not cleaning up {cls.build_tempdir}. Please remove manually.")

    def setUp(self):
        """Create isolated venv for testing the installed package."""
        self.venv_dir = tempfile.mkdtemp(prefix="bitbake-pypi-venv-")

        # Create venv without pip (faster, no network needed)
        venv.create(self.venv_dir, with_pip=False, symlinks=True)

        # Get paths to venv python and bin directory
        self.venv_python = os.path.join(self.venv_dir, 'bin', 'python')
        self.venv_bin = os.path.join(self.venv_dir, 'bin')

        # Install wheel using pip from the outer environment (offline, no-deps)
        site_packages = self._get_site_packages()
        result = subprocess.run(
            [sys.executable, "-m", "pip", "install",
             "--target", site_packages,
             "--no-deps", "--no-index",
             self.wheel_path],
            capture_output=True, text=True
        )
        if result.returncode != 0:
            self.fail(f"Failed to install wheel: {result.stderr}")

        # Install console script entry point manually
        self._install_console_script()

    def _get_site_packages(self):
        """Get the site-packages directory in the venv."""
        lib_dir = os.path.join(self.venv_dir, 'lib')
        # Find python version directory
        for d in os.listdir(lib_dir):
            if d.startswith('python'):
                return os.path.join(lib_dir, d, 'site-packages')
        raise RuntimeError("Could not find site-packages in venv")

    def _install_console_script(self):
        """Create console script wrapper in venv bin directory."""
        site_packages = self._get_site_packages()
        script_path = os.path.join(self.venv_bin, 'bitbake-setup')
        script_content = f'''#!{self.venv_python}
import sys
sys.path.insert(0, "{site_packages}")
from bitbake_setup.__main__ import main
sys.exit(main())
'''
        with open(script_path, 'w') as f:
            f.write(script_content)
        os.chmod(script_path, 0o755)

    def tearDown(self):
        """Remove venv after test."""
        if os.environ.get("BB_TMPDIR_NOCLEAN") != "yes":
            shutil.rmtree(self.venv_dir, ignore_errors=True)
        else:
            print(f"Not cleaning up {self.venv_dir}. Please remove manually.")

    def test_imports(self):
        """Verify all expected modules can be imported from installed package."""

        site_packages = self._get_site_packages()
        imports = ['bb', 'bitbake_setup']
        for module in imports:
            result = subprocess.run(
                [self.venv_python, '-c', f'import sys; sys.path.insert(0, "{site_packages}"); import {module}'],
                capture_output=True, text=True
            )
            self.assertEqual(result.returncode, 0,
                f"Failed to import {module}: {result.stderr}")

    def test_console_script_help(self):
        """Verify bitbake-setup --help runs successfully."""
        script = os.path.join(self.venv_bin, 'bitbake-setup')
        result = subprocess.run(
            [script, '--help'],
            capture_output=True, text=True
        )
        self.assertEqual(result.returncode, 0,
            f"bitbake-setup --help failed: {result.stderr}")
        self.assertIn('usage:', result.stdout.lower())

    def test_console_script_list(self):
        """Verify bitbake-setup list runs successfully."""
        script = os.path.join(self.venv_bin, 'bitbake-setup')
        result = subprocess.run(
            [script, 'list'],
            capture_output=True, text=True
        )
        # List may return 0 even with no configurations
        self.assertEqual(result.returncode, 0,
            f"bitbake-setup list failed: {result.stderr}")

    def test_package_metadata(self):
        """Verify package metadata is correctly set."""
        site_packages = self._get_site_packages()
        code = '''
import json
import sys
sys.path.insert(0, "{}")
from importlib.metadata import metadata
m = metadata("bitbake-setup")
print(json.dumps({{
    "name": m["Name"],
    "version": m["Version"],
    "requires_python": m.get("Requires-Python", ""),
    "license": m.get("License", ""),
}}))
'''.format(site_packages)
        result = subprocess.run(
            [self.venv_python, '-c', code],
            capture_output=True, text=True
        )
        self.assertEqual(result.returncode, 0,
            f"Failed to get metadata: {result.stderr}")

        meta = json.loads(result.stdout)
        self.assertEqual(meta['name'], 'bitbake-setup')
        self.assertIn('>=3.9', meta['requires_python'])

    def test_vendored_dependencies(self):
        """Verify vendored dependencies (bs4, ply, progressbar, simplediff) are not bundled in package."""
        # Check that vendored packages do not exist in root of wheel
        with zipfile.ZipFile(self.wheel_path, 'r') as whl:
            names = whl.namelist()

            # Check for expected package directories
            expected = ['bs4/', 'ply/', 'progressbar/', 'simplediff/']
            for pkg in expected:
                found = any(n.startswith(pkg) for n in names)
                self.assertFalse(found,
                    f"Unexpected vendored package '{pkg}' found in wheel")

    def test_version_from_wheel(self):
        """Verify version is set correctly (not fallback 0.0.0 unless expected)."""
        import re
        # Extract version from wheel filename
        wheel_name = os.path.basename(self.wheel_path)
        # Wheel format: {name}-{version}(-{build})?-{python}-{abi}-{platform}.whl
        parts = wheel_name.split('-')
        version = parts[1]

        # Check version format (should be semver-like or contain git info)
        version_pattern = r'^\d+\.\d+\.\d+.*$'
        self.assertTrue(re.match(version_pattern, version),
            f"Version '{version}' doesn't match expected pattern")

        print(f"Extracted version from wheel: {version}")

        self.assertNotEqual(version, '0.0.0',
            "Version is fallback 0.0.0 - no git tags found")

    def test_wheel_metadata_file(self):
        """Verify wheel METADATA file contains required fields."""
        with zipfile.ZipFile(self.wheel_path, 'r') as whl:
            # Find METADATA file in dist-info
            metadata_path = None
            for name in whl.namelist():
                if name.endswith('.dist-info/METADATA'):
                    metadata_path = name
                    break

            self.assertIsNotNone(metadata_path, "METADATA file not found in wheel")

            # Parse metadata
            metadata_content = whl.read(metadata_path).decode('utf-8')

            # Check required fields
            self.assertIn('Metadata-Version:', metadata_content)
            self.assertIn('Name: bitbake-setup', metadata_content)
            self.assertIn('Version:', metadata_content)
            self.assertIn('Requires-Python:', metadata_content)

    def test_entry_points(self):
        """Verify console script entry points are correctly defined."""
        with zipfile.ZipFile(self.wheel_path, 'r') as whl:
            # Find entry_points.txt in dist-info
            entry_points_path = None
            for name in whl.namelist():
                if name.endswith('.dist-info/entry_points.txt'):
                    entry_points_path = name
                    break

            self.assertIsNotNone(entry_points_path,
                "entry_points.txt not found in wheel")

            content = whl.read(entry_points_path).decode('utf-8')
            self.assertIn('[console_scripts]', content)
            self.assertIn('bitbake-setup', content)
            self.assertIn('bitbake_setup.__main__:main', content)
