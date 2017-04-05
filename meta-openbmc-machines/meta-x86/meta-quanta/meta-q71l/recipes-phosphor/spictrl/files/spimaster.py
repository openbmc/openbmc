# Copyright (C) 2017 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#!/usr/bin/env python
"""Basic utility for reading and writing registers on an ASPEED BMC.

The values hardcoded are for the ASPEED AST2400.
"""

import subprocess
import sys

IO_TOOL = 'devmem'
PROTECTION_KEY = '0x1E6E2000'
HWTRAP = '0x1E6E2070'
# The AST2400 SCU Password (as int for write_register)
SCU_PASSWORD = 0x1688A8A8

# Bits 13:12
SPI_MASK = 0xffffcfff
# Enable SPI Master
SPI_MASTER = 0x1000
# Enable SPI Master and SPI Slave to AHB Bridge
SPI_MASTER_SLAVE = 0x2000
# Enable SPI Pass-through
SPI_BYPASS = 0x3000


def read_register(address):
    """Run a tool to read a register value.

    This will convert it to an integer.
    """

    output = subprocess.check_output([IO_TOOL, address]).replace('\n', '')
    value = int(output, 16)
    return value


def write_register(address, value):
    """Run a tool to write the 32-bit register value."""

    subprocess.check_output([IO_TOOL, address, '32', '0x%x' % value])
    return


def set_master(value):
    """Set Hardware Strapping to SPI Master."""

    print 'Setting to "SPI Master"'

    masked = value & SPI_MASK
    masked |= SPI_MASTER

    print 'setting: 0x%x' % masked
    write_register(HWTRAP, masked)


def set_ahb_bridge(value):
    """Set hardware strapping to spi master and spi-slave to ahb."""

    print 'Setting to "SPI Master and SPI Slave to AHB Bridge"'
    masked = value & SPI_MASK
    masked |= SPI_MASTER_SLAVE

    print 'setting: 0x%x' % masked
    write_register(HWTRAP, masked)


def set_bypass(value):
    """Set hardware strappign to spi bypass."""

    print 'Setting to "Enable SPI Pass-through"'
    masked = value & SPI_MASK
    masked |= SPI_BYPASS

    print 'setting: 0x%x' % masked
    write_register(HWTRAP, masked)


def usage():
    """Print usage string."""

    print 'usage: %s master|bridge|bypass' % sys.argv[0]
    print 'master sets the BMC SPI to Master.'
    print ('bridge sets the BMC SPI to Master-Slave bridge to AHB (for BIOS ' +
        'Update).')
    print 'bypass sets the BMC SPI to Bypass (default).'


def main():
    """Main entry point for execution."""

    if len(sys.argv) != 2:
        usage()
        sys.exit(-1)

    ctrl = sys.argv[1].lower()
    if ctrl not in ('master', 'bridge', 'bypass'):
        usage()
        sys.exit(-1)

    locked = False
    # Check if locked
    value = read_register(PROTECTION_KEY)
    if value == 1:
        print 'Presently unlocked'
    else:
        print 'Presently locked'
        locked = True

    # if Locked we need to unlock it.
    if locked:
        write_register(PROTECTION_KEY, SCU_PASSWORD)

    # Read the value.
    value = read_register(HWTRAP)
    if value & SPI_BYPASS == SPI_BYPASS:
        print 'Presently set to bypass'
    elif value & SPI_MASTER == SPI_MASTER:
        print 'Presently set to master'
    elif value & SPI_MASTER_SLAVE == SPI_MASTER_SLAVE:
        print 'Presently set to master-slave'

    if ctrl == 'master':
        set_master(value)
    elif ctrl == 'bridge':
        set_ahb_bridge(value)
    elif ctrl == 'bypass':
        set_bypass(value)

    # We leave it unlocked in case it was locked.
    # we could lock it.
    # TODO(venture): lock it by writing any non-password value.

if __name__ == '__main__':
    main()
