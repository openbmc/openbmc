#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

from bb.tests.fetch import FetcherTest
import json
import hashlib
import glob
from bb.tests.support.httpserver import HTTPService

class BitbakeSetupTest(FetcherTest):
    def setUp(self):
        super(BitbakeSetupTest, self).setUp()

        self.registrypath = os.path.join(self.tempdir, "bitbake-setup-configurations")

        os.makedirs(self.registrypath)
        self.git_init(cwd=self.registrypath)
        self.git('commit --allow-empty -m "Initial commit"', cwd=self.registrypath)

        self.testrepopath = os.path.join(self.tempdir, "test-repo")
        os.makedirs(self.testrepopath)
        self.git_init(cwd=self.testrepopath)
        self.git('commit --allow-empty -m "Initial commit"', cwd=self.testrepopath)

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

opts, args = getopt.getopt(sys.argv[1:], "d:", ["downloads-directory="])
for option, value in opts:
    if option == '-d':
        installdir = value

print("Buildtools installed into {}".format(installdir))
os.makedirs(installdir)
"""
        self.add_file_to_testrepo('scripts/install-buildtools', installbuildtools, script=True)

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
        return bb.process.run("{} --global-settings {} {}".format(bbsetup, os.path.join(self.tempdir, 'global-config'), cmd))


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
                "oe-fragments": ["test-fragment-1"]
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
""" % (sources)
        os.makedirs(os.path.join(self.registrypath, os.path.dirname(name)), exist_ok=True)
        with open(os.path.join(self.registrypath, name), 'w') as f:
            f.write(config)
        self.git('add {}'.format(name), cwd=self.registrypath)
        self.git('commit -m "Adding {}"'.format(name), cwd=self.registrypath)
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
            import stat
            st = os.stat(fullname)
            os.chmod(fullname, st.st_mode | stat.S_IEXEC)
        self.git('add {}'.format(name), cwd=self.testrepopath)
        self.git('commit -m "Adding {}"'.format(name), cwd=self.testrepopath)

    def config_is_unchanged(self, setuppath):
        os.environ['BBPATH'] = os.path.join(setuppath, 'build')
        out = self.runbbsetup("status")
        self.assertIn("Configuration in {} has not changed".format(setuppath), out[0])
        out = self.runbbsetup("update --update-bb-conf='yes'")
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
        revision = self.git('rev-parse HEAD', cwd=self.testrepopath).strip()
        self.assertEqual(revision, sources_fixed_revisions['sources']['test-repo']['git-remote']['rev'])

        if "oe-template" in bitbake_config:
            with open(os.path.join(bb_conf_path, 'conf-summary.txt')) as f:
                self.assertEqual(f.read(), bitbake_config["oe-template"])
            with open(os.path.join(bb_conf_path, 'test-file')) as f:
                self.assertEqual(f.read(), test_file_content)
        else:
            with open(os.path.join(bb_conf_path, 'conf-summary.txt')) as f:
                self.assertIn(bitbake_config["description"], f.read())
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

        if 'oe-fragment' in bitbake_config.keys():
            for f in bitbake_config["oe-fragments"]:
                self.assertTrue(os.path.exists(os.path.join(bb_conf_path, f)))

        if 'bb-environment-passthrough' in bitbake_config.keys():
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
        import os
        if 'BBPATH' in os.environ:
            del os.environ['BBPATH']

        # check that no arguments works
        self.runbbsetup("")

        # check that --help works
        self.runbbsetup("--help")

        # change to self.tempdir to work with cwd-based default settings
        os.chdir(self.tempdir)

        # check that the default top-dir-prefix is cwd (now self.tempdir) with no global settings
        out = self.runbbsetup("settings list")
        self.assertIn("default top-dir-prefix {}".format(os.getcwd()), out[0])

        # set up global location for dl-dir
        settings_path = "{}/global-config".format(self.tempdir)
        out = self.runbbsetup("settings set --global default dl-dir {}".format(os.path.join(self.tempdir, 'downloads')))
        self.assertIn("From section 'default' the setting 'dl-dir' was changed to", out[0])
        self.assertIn("Settings written to".format(settings_path), out[0])

        # check that writing settings works and then adjust them to point to
        # test registry repo
        out = self.runbbsetup("settings set default registry 'git://{};protocol=file;branch=master;rev=master'".format(self.registrypath))
        settings_path = "{}/bitbake-builds/settings.conf".format(self.tempdir)
        self.assertIn(settings_path, out[0])
        self.assertIn("From section 'default' the setting 'registry' was changed to", out[0])
        self.assertIn("Settings written to".format(settings_path), out[0])

        # check that listing settings works
        out = self.runbbsetup("settings list")
        self.assertIn("default top-dir-prefix {}".format(self.tempdir), out[0])
        self.assertIn("default dl-dir {}".format(os.path.join(self.tempdir, 'downloads')), out[0])
        self.assertIn("default registry {}".format('git://{};protocol=file;branch=master;rev=master'.format(self.registrypath)), out[0])

        # check that 'list' produces correct output with no configs, one config and two configs
        out = self.runbbsetup("list")
        self.assertNotIn("test-config-1", out[0])
        self.assertNotIn("test-config-2", out[0])

        json_1 = self.add_json_config_to_registry('test-config-1.conf.json', 'master', 'master')
        out = self.runbbsetup("list")
        self.assertIn("test-config-1", out[0])
        self.assertNotIn("test-config-2", out[0])

        json_2 = self.add_json_config_to_registry('config-2/test-config-2.conf.json', 'master', 'master')
        out = self.runbbsetup("list --write-json={}".format(os.path.join(self.tempdir, "test-configs.json")))
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
                    out = self.runbbsetup("init --non-interactive {} {}".format(v['cmdline'], c))
                    setuppath = self.get_setup_path(v['name'], c)
                    self.check_setupdir_files(setuppath, test_file_content)
        finally:
            server.stop()

        # install buildtools
        out = self.runbbsetup("install-buildtools --setup-dir {}".format(setuppath))
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
            out = self.runbbsetup("status")
            self.assertIn("Layer repository file://{} checked out into {}/layers/test-repo updated revision master from".format(self.testrepopath, setuppath), out[0])
            out = self.runbbsetup("update --update-bb-conf='yes'")
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
        self.git('checkout -b {}'.format(branch), cwd=self.testrepopath)
        self.add_file_to_testrepo('test-file', test_file_content)
        json_1 = self.add_json_config_to_registry('test-config-1.conf.json', branch, branch)
        for c in variants:
            setuppath = self.get_setup_path('test-config-1', c)
            os.environ['BBPATH'] = os.path.join(setuppath, 'build')
            out = self.runbbsetup("status")
            self.assertIn("Configuration in {} has changed:".format(setuppath), out[0])
            self.assertIn('-                    "rev": "master"\n+                    "rev": "another-branch"', out[0])
            out = self.runbbsetup("update --update-bb-conf='yes'")
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
        self.git('checkout -b {}'.format(branch), cwd=self.testrepopath)
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
            out = self.runbbsetup("update --update-bb-conf='no'")
            sums_after = _conf_chksum(f"{setuppath}/build/conf")
            self.assertEqual(sums_before, sums_after)

        def _check_local_sources(custom_setup_dir):
            custom_setup_path = os.path.join(self.tempdir, 'bitbake-builds', custom_setup_dir)
            custom_layer_path = os.path.join(custom_setup_path, 'layers', 'test-repo')
            self.assertTrue(os.path.islink(custom_layer_path))
            self.assertEqual(self.testrepopath, os.path.realpath(custom_layer_path))
            self.config_is_unchanged(custom_setup_path)

        # Change the configuration to refer to a local source, then to another local source, then back to a git remote
        # Run status/update after each change and verify that nothing breaks
        c = 'gadget'
        setuppath = self.get_setup_path('test-config-1', c)
        self.config_is_unchanged(setuppath)

        json_1 = self.add_local_json_config_to_registry('test-config-1.conf.json', self.testrepopath)
        os.environ['BBPATH'] = os.path.join(setuppath, 'build')
        out = self.runbbsetup("update --update-bb-conf='yes'")
        _check_local_sources(setuppath)

        prev_path = self.testrepopath
        self.testrepopath = prev_path + "-2"
        self.git("clone {} {}".format(prev_path, self.testrepopath), cwd=self.tempdir)
        json_1 = self.add_local_json_config_to_registry('test-config-1.conf.json', self.testrepopath)
        os.environ['BBPATH'] = os.path.join(setuppath, 'build')
        out = self.runbbsetup("update --update-bb-conf='yes'")
        _check_local_sources(setuppath)

        self.testrepopath = prev_path
        json_1 = self.add_json_config_to_registry('test-config-1.conf.json', branch, branch)
        os.environ['BBPATH'] = os.path.join(setuppath, 'build')
        out = self.runbbsetup("update --update-bb-conf='yes'")
        self.check_setupdir_files(setuppath, test_file_content)

        # Also check that there are no layer backups up to this point, then make a change that should
        # result in a layer backup, and check that it does happen.
        def _check_layer_backups(layer_path, expected_backups):
            files = os.listdir(layer_path)
            backups = len([f for f in files if 'backup' in f])
            self.assertEqual(backups, expected_backups, msg = "Expected {} layer backups, got {}, directory listing: {}".format(expected_backups, backups, files))

        layers_path = os.path.join(setuppath, 'layers')
        layer_path = os.path.join(layers_path, 'test-repo')
        _check_layer_backups(layers_path, 0)

        ## edit a file without making a commit
        with open(os.path.join(layer_path, 'local-modification'), 'w') as f:
            f.write('locally-modified\n')
        test_file_content = "modified-again\n"
        self.add_file_to_testrepo('test-file', test_file_content)
        os.environ['BBPATH'] = os.path.join(setuppath, 'build')
        out = self.runbbsetup("update --update-bb-conf='yes'")
        _check_layer_backups(layers_path, 1)

        ## edit a file and try to make a commit; this should be rejected
        with open(os.path.join(layer_path, 'local-modification'), 'w') as f:
            f.write('locally-modified-again\n')
        self.git('add .', cwd=layer_path)
        with self.assertRaisesRegex(bb.process.ExecutionError, "making commits is restricted"):
            self.git('commit -m "Adding a local modification"', cwd=layer_path)
        test_file_content = "modified-again-and-again\n"
        self.add_file_to_testrepo('test-file', test_file_content)
        out = self.runbbsetup("update --update-bb-conf='yes'")
        _check_layer_backups(layers_path, 2)

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
        out = self.runbbsetup("init --non-interactive --source-overrides {} --setup-dir-name {} test-config-1 gadget".format(os.path.join(self.testrepopath, override_filename), custom_setup_dir))
        _check_local_sources(custom_setup_dir)

        # same but use command line options to specify local overrides
        custom_setup_dir = 'special-setup-dir-with-cmdline-overrides'
        out = self.runbbsetup("init --non-interactive -L test-repo {} --setup-dir-name {} test-config-1 gadget".format(self.testrepopath, custom_setup_dir))
        _check_local_sources(custom_setup_dir)
