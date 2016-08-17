# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# This program is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by the Free
# Software Foundation; version 2 of the License
#
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
# for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc., 59
# Temple Place - Suite 330, Boston, MA 02111-1307, USA.
#
# AUTHOR
# Adrian Freihofer <adrian.freihofer (at] neratec.com>


import re
from wic import msger


def serial_console_form_kargs(kernel_args):
    """
    Create SERIAL... line from kernel parameters

    syslinux needs a line SERIAL port [baudrate [flowcontrol]]
    in the syslinux.cfg file. The config line is generated based
    on kernel boot parameters. The the parameters of the first
    ttyS console are considered for syslinux config.
    @param kernel_args kernel command line
    @return line for syslinux config file e.g. "SERIAL 0 115200"
    """
    syslinux_conf = ""
    for param in kernel_args.split():
        param_match = re.match("console=ttyS([0-9]+),?([0-9]*)([noe]?)([0-9]?)(r?)", param)
        if param_match:
            syslinux_conf += "SERIAL " + param_match.group(1)
            # baudrate
            if param_match.group(2):
                syslinux_conf += " " + param_match.group(2)
            # parity
            if param_match.group(3) and param_match.group(3) != 'n':
                msger.warning("syslinux does not support parity for console. {} is ignored."
                              .format(param_match.group(3)))
            # number of bits
            if param_match.group(4) and param_match.group(4) != '8':
                msger.warning("syslinux supports 8 bit console configuration only. {} is ignored."
                              .format(param_match.group(4)))
            # flow control
            if param_match.group(5) and param_match.group(5) != '':
                msger.warning("syslinux console flowcontrol configuration. {} is ignored."
                              .format(param_match.group(5)))
            break

    return syslinux_conf
