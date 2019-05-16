#
# SPDX-License-Identifier: MIT
#

from unittest.case import TestCase
import oe.qa

class TestElf(TestCase):
    def test_machine_name(self):
        """
        Test elf_machine_to_string()
        """
        self.assertEqual(oe.qa.elf_machine_to_string(0x02), "SPARC")
        self.assertEqual(oe.qa.elf_machine_to_string(0x03), "x86")
        self.assertEqual(oe.qa.elf_machine_to_string(0x08), "MIPS")
        self.assertEqual(oe.qa.elf_machine_to_string(0x14), "PowerPC")
        self.assertEqual(oe.qa.elf_machine_to_string(0x28), "ARM")
        self.assertEqual(oe.qa.elf_machine_to_string(0x2A), "SuperH")
        self.assertEqual(oe.qa.elf_machine_to_string(0x32), "IA-64")
        self.assertEqual(oe.qa.elf_machine_to_string(0x3E), "x86-64")
        self.assertEqual(oe.qa.elf_machine_to_string(0xB7), "AArch64")
        self.assertEqual(oe.qa.elf_machine_to_string(0xF7), "BPF")

        self.assertEqual(oe.qa.elf_machine_to_string(0x00), "Unknown (0)")
        self.assertEqual(oe.qa.elf_machine_to_string(0xDEADBEEF), "Unknown (3735928559)")
        self.assertEqual(oe.qa.elf_machine_to_string("foobar"), "Unknown ('foobar')")
