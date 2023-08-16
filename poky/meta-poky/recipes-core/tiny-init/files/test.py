#!/usr/bin/env python
import os.path
import re
import sys
from ctypes import *
from subprocess import PIPE, Popen

from bios_board import *
from pal import pal_get_fru_list, pal_get_fru_id, pal_is_slot_server, pal_is_fru_prsnt


def bios_main():
    fruname = sys.argv[1]
    command = sys.argv[2]

    if fruname == "all":
        frulist = pal_get_fru_list()
        for fruname in frulist:
            fru = pal_get_fru_id(fruname)
            if fru >= 0:
                if not pal_is_fru_prsnt(fru):
                    continue
                if pal_is_slot_server(fru) == True:
                    print("%s:" % (fruname))
                    bios_main_fru(fru, command)
    else:
        fru = pal_get_fru_id(fruname)
        if fru < 0:
            print("%s is not a known FRU on this platform" % (fruname))
            return
        if pal_is_fru_prsnt(fru) == False:
            print("%s is not present!" % (fruname))
            return
        if pal_is_slotme))
          return
        bios_main_fru(fru, command)


if __name__ == "__main__":
    bios_usage_check = check_bios_util()
    if bios_usage_check.valid is True:
        bios_main()
