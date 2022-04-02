#!/usr/bin/env python3

import os
import string
import sys

class Template(string.Template):
    delimiter = "@"

class Environ():
    def __getitem__(self, name):
        if name == "OECORE_SDK_SYS":
            return  os.path.basename(os.environ["OECORE_NATIVE_SYSROOT"])
        elif name == "OECORE_TARGET_SYS":
            return  os.path.basename(os.environ["OECORE_TARGET_SYSROOT"])
        elif name == "OECORE_TARGET_ALIAS":
            return  os.path.basename(os.environ["TARGET_PREFIX"].strip("-"))
        else:
            return  os.environ[name]

try:
    sysroot = os.environ['OECORE_NATIVE_SYSROOT']
except KeyError:
    print("Not in environment setup, bailing")
    sys.exit(1)

template_file = os.path.join(sysroot, 'usr/share/cmake/SDKToolchainConfig.cmake.template')
cross_file = os.path.join(sysroot, 'usr/share/cmake/%s-toolchain.cmake' % (os.path.basename(os.environ["OECORE_TARGET_SYSROOT"])))
with open(template_file) as in_file:
    template = in_file.read()
    output = Template(template).substitute(Environ())
    with open(cross_file, "w") as out_file:
        out_file.write(output)
