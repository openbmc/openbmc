from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.data import skipIfQemu
import threading
import time

class Suspend_Test(OERuntimeTestCase):

    def test_date(self): 
        (status, output) = self.target.run('date')
        self.assertEqual(status, 0,  msg = 'Failed to run date command, output : %s' % output)
        
    def test_ping(self):
        t_thread = threading.Thread(target=self.target.run, args=("ping 8.8.8.8",))
        t_thread.start()
        time.sleep(2)
        
        status, output = self.target.run('pidof ping')
        self.target.run('kill -9 %s' % output)
        self.assertEqual(status, 0, msg = 'Not able to find process that runs ping, output : %s' % output)  
        
    def set_suspend(self): 
        (status, output) = self.target.run('sudo rtcwake -m mem -s 10')
        self.assertEqual(status, 0,  msg = 'Failed to suspends your system to RAM, output : %s' % output)
    
    @skipIfQemu()
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_suspend(self):
        self.test_date()
        self.test_ping()
        self.set_suspend()
        self.test_date()
        self.test_ping()
