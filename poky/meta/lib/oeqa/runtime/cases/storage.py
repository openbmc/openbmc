#
# SPDX-License-Identifier: MIT
#

import re
import time

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.data import skipIfQemu

class StorageBase(OERuntimeTestCase):
    def storage_mount(cls, tmo=1):

        (status, output) = cls.target.run('mkdir -p %s' % cls.mount_point)
        (status, output) = cls.target.run('mount %s %s' % (cls.device, cls.mount_point))
        msg = ('Mount failed: %s.' % status)
        cls.assertFalse(output, msg = msg)
        time.sleep(tmo)
        (status, output) = cls.target.run('cat /proc/mounts')
        match = re.search('%s' % cls.device, output)
        if match:
            msg = ('Device %s not mounted.' % cls.device)
            cls.assertTrue(match, msg = msg)

        (status, output) = cls.target.run('mkdir -p %s' % cls.test_dir)

        (status, output) = cls.target.run('rm -f %s/*' % cls.test_dir)
        msg = ('Failed to cleanup files @ %s/*' % cls.test_dir)
        cls.assertFalse(output, msg = msg)


    def storage_basic(cls):
        # create file on device
        (status, output) = cls.target.run('touch %s/%s' % (cls.test_dir, cls.test_file))
        msg = ('File %s not created on %s' % (cls.test_file, cls.device))
        cls.assertFalse(status, msg = msg)
        # move file
        (status, output) = cls.target.run('mv %s/%s %s/%s1' %  
                (cls.test_dir, cls.test_file, cls.test_dir, cls.test_file))
        msg = ('File %s not moved to %s' % (cls.test_file, cls.device))
        cls.assertFalse(status, msg = msg)
        # remove file
        (status, output) = cls.target.run('rm %s/%s1' % (cls.test_dir, cls.test_file))
        msg = ('File %s not removed on %s' % (cls.test_file, cls.device))
        cls.assertFalse(status, msg = msg)

    def storage_read(cls):
        # check if message is in file
        (status, output) = cls.target.run('cat  %s/%s' % 
                (cls.test_dir, cls.test_file))

        match = re.search('%s' % cls.test_msg, output)
        msg = ('Test message %s not in file %s.' % (cls.test_msg, cls.test_file))
        cls.assertEqual(status, 0,  msg = msg)

    def storage_write(cls):
        # create test message in file on device
        (status, output) = cls.target.run('echo "%s" >  %s/%s' % 
                (cls.test_msg, cls.test_dir, cls.test_file))
        msg = ('File %s not create test message on %s' % (cls.test_file, cls.device))
        cls.assertEqual(status, 0,  msg = msg)

    def storage_umount(cls, tmo=1):
        time.sleep(tmo)
        (status, output) = cls.target.run('umount %s' % cls.mount_point)

        if status == 32:
            # already unmounted, should it fail?
            return
        else:
            msg = ('Device not unmount %s' % cls.mount_point)
            cls.assertEqual(status, 0,  msg = msg)

        (status, output) = cls.target.run('cat /proc/mounts')
        match = re.search('%s' % cls.device, output)
        if match:
            msg = ('Device %s still mounted.' % cls.device)
            cls.assertTrue(match, msg = msg)


class UsbTest(StorageBase):
    '''
        This is to mimic the usb test previously done in manual bsp-hw.json
    '''
    @classmethod
    def setUpClass(self):
        self.test_msg = "Hello World - USB"
        self.mount_point = "/media/usb"
        self.device = "/dev/sda1"
        self.test_file = "usb.tst"
        self.test_dir = os.path.join(self.mount_point, "oeqa")

    @skipIfQemu('qemuall', 'Test only runs on real hardware')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_usb_mount(self):
        self.storage_umount(2)
        self.storage_mount(5)

    @skipIfQemu('qemuall', 'Test only runs on real hardware')
    @OETestDepends(['storage.UsbTest.test_usb_mount'])
    def test_usb_basic_operations(self):
        self.storage_basic()

    @skipIfQemu('qemuall', 'Test only runs on real hardware')
    @OETestDepends(['storage.UsbTest.test_usb_basic_operations'])
    def test_usb_basic_rw(self):
        self.storage_write()
        self.storage_read()

    @skipIfQemu('qemuall', 'Test only runs on real hardware')
    @OETestDepends(['storage.UsbTest.test_usb_mount'])
    def test_usb_umount(self):
        self.storage_umount(2)


class MMCTest(StorageBase):
    '''
        This is to mimic the usb test previously done in manual bsp-hw.json
    '''
    @classmethod
    def setUpClass(self):
        self.test_msg = "Hello World - MMC"
        self.mount_point = "/media/mmc"
        self.device = "/dev/mmcblk1p1"
        self.test_file = "mmc.tst"
        self.test_dir = os.path.join(self.mount_point, "oeqa")

    @skipIfQemu('qemuall', 'Test only runs on real hardware')
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_mmc_mount(self):
        self.storage_umount(2)
        self.storage_mount()

    @skipIfQemu('qemuall', 'Test only runs on real hardware')
    @OETestDepends(['storage.MMCTest.test_mmc_mount'])
    def test_mmc_basic_operations(self):
        self.storage_basic()

    @skipIfQemu('qemuall', 'Test only runs on real hardware')
    @OETestDepends(['storage.MMCTest.test_mmc_basic_operations'])
    def test_mmc_basic_rw(self):
        self.storage_write()
        self.storage_read()

    @skipIfQemu('qemuall', 'Test only runs on real hardware')
    @OETestDepends(['storage.MMCTest.test_mmc_mount'])
    def test_mmc_umount(self):
        self.storage_umount(2)
