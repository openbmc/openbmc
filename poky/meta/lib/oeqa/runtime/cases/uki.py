# SPDX-License-Identifier: MIT
#

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.data import skipIfNotInDataVar

class UkiTest(OERuntimeTestCase):

    @skipIfNotInDataVar('IMAGE_CLASSES', 'uki', 'Test case uki is for images which use uki.bbclass')
    def test_uki(self):
        uki_filename = self.td.get('UKI_FILENAME')
        status, output = self.target.run('ls /boot/EFI/Linux/%s' % uki_filename)
        self.assertEqual(status, 0, output)

        status, output = self.target.run('echo $( cat /sys/firmware/efi/efivars/LoaderEntrySelected-4a67b082-0a4c-41cf-b6c7-440b29bb8c4f  ) | grep %s' % uki_filename)
        self.assertEqual(status, 0, output)
