import os

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID
from oeqa.runtime.decorator.package import OEHasPackage

class PythonTest(OERuntimeTestCase):

    @classmethod
    def setUpClass(cls):
        src = os.path.join(cls.tc.files_dir, 'test.py')
        dst = '/tmp/test.py'
        cls.tc.target.copyTo(src, dst)

    @classmethod
    def tearDownClass(cls):
        dst = '/tmp/test.py'
        cls.tc.target.run('rm %s' % dst)

    @OETestID(1145)
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['python-core'])
    def test_python_exists(self):
        status, output = self.target.run('which python')
        msg = 'Python binary not in PATH or not on target.'
        self.assertEqual(status, 0, msg=msg)

    @OETestID(965)
    @OETestDepends(['python.PythonTest.test_python_exists'])
    def test_python_stdout(self):
        status, output = self.target.run('python /tmp/test.py')
        msg = 'Exit status was not 0. Output: %s' % output
        self.assertEqual(status, 0, msg=msg)

        msg = 'Incorrect output: %s' % output
        self.assertEqual(output, "the value of a is 0.01", msg=msg)

    @OETestID(1146)
    @OETestDepends(['python.PythonTest.test_python_stdout'])
    def test_python_testfile(self):
        status, output = self.target.run('ls /tmp/testfile.python')
        self.assertEqual(status, 0, msg='Python test file generate failed.')
