import os
import json
import pathlib
import subprocess
import tempfile
import unittest.mock

from oeqa.selftest.case import OESelftestTestCase
from oeqa.core.decorator import OETestTag

runfvp = pathlib.Path(__file__).parents[5] / "scripts" / "runfvp"
testdir = pathlib.Path(__file__).parent / "tests"

@OETestTag("meta-arm")
class RunFVPTests(OESelftestTestCase):
    def setUpLocal(self):
        self.assertTrue(runfvp.exists())

    def run_fvp(self, *args, env=None, should_succeed=True):
        """
        Call runfvp passing any arguments. If check is True verify return stdout
        on exit code 0 or fail the test, otherwise return the CompletedProcess
        instance.
        """
        cli = [runfvp,] + list(args)
        print(f"Calling {cli}")
        # Set cwd to testdir so that any mock FVPs are found
        ret = subprocess.run(cli, cwd=testdir, env=env, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True)
        if should_succeed:
            self.assertEqual(ret.returncode, 0, f"runfvp exit {ret.returncode}, output: {ret.stdout}")
            return ret.stdout
        else:
            self.assertNotEqual(ret.returncode, 0, f"runfvp exit {ret.returncode}, output: {ret.stdout}")
            return ret.stdout

    def test_help(self):
        output = self.run_fvp("--help")
        self.assertIn("Run images in a FVP", output)

    def test_bad_options(self):
        self.run_fvp("--this-is-an-invalid-option", should_succeed=False)

    def test_run_auto_tests(self):
        cases = list(testdir.glob("auto-*.json"))
        if not cases:
            self.fail("No tests found")
        for case in cases:
            with self.subTest(case=case.stem):
                self.run_fvp(case)

    def test_fvp_options(self):
        # test-parameter sets one argument, add another manually
        self.run_fvp(testdir / "test-parameter.json", "--", "--parameter", "board.dog=woof")

    def test_fvp_environment(self):
        output = self.run_fvp(testdir / "test-environment.json", env={"DISPLAY": "test_fvp_environment:42"})
        self.assertEqual(output.strip(), "Found expected DISPLAY")

@OETestTag("meta-arm")
class ConfFileTests(OESelftestTestCase):
    def test_no_exe(self):
        from fvp import conffile
        with tempfile.NamedTemporaryFile('w') as tf:
            tf.write('{}')
            tf.flush()

            with self.assertRaises(ValueError):
                conffile.load(tf.name)

    def test_minimal(self):
        from fvp import conffile
        with tempfile.NamedTemporaryFile('w') as tf:
            tf.write('{"exe": "FVP_Binary"}')
            tf.flush()

            conf = conffile.load(tf.name)
            self.assertTrue('fvp-bindir' in conf)
            self.assertTrue('fvp-bindir' in conf)
            self.assertTrue("exe" in conf)
            self.assertTrue("parameters" in conf)
            self.assertTrue("data" in conf)
            self.assertTrue("applications" in conf)
            self.assertTrue("terminals" in conf)
            self.assertTrue("args" in conf)
            self.assertTrue("consoles" in conf)
            self.assertTrue("env" in conf)


@OETestTag("meta-arm")
class RunnerTests(OESelftestTestCase):
    def create_mock(self):
        return unittest.mock.patch("subprocess.Popen")

    @unittest.mock.patch.dict(os.environ, {"PATH": "/path-42:/usr/sbin:/usr/bin:/sbin:/bin"})
    def test_start(self):
        from fvp import runner
        with self.create_mock() as m:
            fvp = runner.FVPRunner(self.logger)
            config = {"fvp-bindir": "/usr/bin",
                    "exe": "FVP_Binary",
                    "parameters": {'foo': 'bar'},
                    "data": ['data1'],
                    "applications": {'a1': 'file'},
                    "terminals": {},
                    "args": ['--extra-arg'],
                    "env": {"FOO": "BAR"}
                     }

            with tempfile.NamedTemporaryFile('w') as fvpconf:
                json.dump(config, fvpconf)
                fvpconf.flush()
                cwd_mock = os.path.dirname(fvpconf.name)
                fvp.start(fvpconf.name)

            m.assert_called_once_with(['/usr/bin/FVP_Binary',
                '--parameter', 'foo=bar',
                '--data', 'data1',
                '--application', 'a1=file',
                '--extra-arg'],
                stdin=unittest.mock.ANY,
                stdout=unittest.mock.ANY,
                stderr=unittest.mock.ANY,
                env={"FOO":"BAR", "PATH": "/path-42:/usr/sbin:/usr/bin:/sbin:/bin"},
                cwd=cwd_mock)

    @unittest.mock.patch.dict(os.environ, {"DISPLAY": ":42", "WAYLAND_DISPLAY": "wayland-42", "PATH": "/path-42:/usr/sbin:/usr/bin:/sbin:/bin"})
    def test_env_passthrough(self):
        from fvp import runner
        with self.create_mock() as m:
            fvp = runner.FVPRunner(self.logger)
            config = {"fvp-bindir": "/usr/bin",
                      "exe": "FVP_Binary",
                      "parameters": {},
                      "data": [],
                      "applications": {},
                      "terminals": {},
                      "args": [],
                      "env": {"FOO": "BAR"}
                     }

            with tempfile.NamedTemporaryFile('w') as fvpconf:
                json.dump(config, fvpconf)
                fvpconf.flush()
                cwd_mock = os.path.dirname(fvpconf.name)
                fvp.start(fvpconf.name)

            m.assert_called_once_with(['/usr/bin/FVP_Binary'],
                stdin=unittest.mock.ANY,
                stdout=unittest.mock.ANY,
                stderr=unittest.mock.ANY,
                env={"DISPLAY":":42", "FOO": "BAR", "WAYLAND_DISPLAY": "wayland-42", "PATH": "/path-42:/usr/sbin:/usr/bin:/sbin:/bin"},
                cwd=cwd_mock)
