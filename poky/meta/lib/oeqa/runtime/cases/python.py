from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID

class PythonTest(OERuntimeTestCase):
    @classmethod
    def setUpClass(cls):
        import unittest
        if "python3-core" not in cls.tc.image_packages:
            raise unittest.SkipTest("Python3 not on target")

    @OETestID(965)
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_python3(self):
        cmd = "python3 -c \"import codecs; print(codecs.encode('Uryyb, jbeyq', 'rot13'))\""
        status, output = self.target.run(cmd)
        msg = 'Exit status was not 0. Output: %s' % output
        self.assertEqual(status, 0, msg=msg)

        msg = 'Incorrect output: %s' % output
        self.assertEqual(output, "Hello, world", msg=msg)
