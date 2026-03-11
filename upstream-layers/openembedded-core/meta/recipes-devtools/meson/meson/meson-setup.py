#!/usr/bin/env python3

import os
import string
import sys

class Template(string.Template):
    delimiter = "@"

class Environ():
    def __getitem__(self, name):
        val = os.environ[name]
        val = val.split()
        if len(val) > 1:
            val = ["'%s'" % x for x in val]
            val = ', '.join(val)
            val = '[%s]' % val
        elif val:
            val = "'%s'" % val.pop()
        return val

try:
    sysroot = os.environ['OECORE_NATIVE_SYSROOT']
except KeyError:
    print("Not in environment setup, bailing")
    sys.exit(1)

template_file = os.path.join(sysroot, 'usr/share/meson/meson.cross.template')
cross_file = os.path.join(sysroot, 'usr/share/meson/%smeson.cross' % os.environ["TARGET_PREFIX"])
native_template_file = os.path.join(sysroot, 'usr/share/meson/meson.native.template')
native_file = os.path.join(sysroot, 'usr/share/meson/meson.native')

with open(template_file) as in_file:
    template = in_file.read()
    output = Template(template).substitute(Environ())
    with open(cross_file, "w") as out_file:
        out_file.write(output)

with open(native_template_file) as in_file:
    template = in_file.read()
    output = Template(template).substitute({'OECORE_NATIVE_SYSROOT': os.environ['OECORE_NATIVE_SYSROOT']})
    with open(native_file, "w") as out_file:
        out_file.write(output)
