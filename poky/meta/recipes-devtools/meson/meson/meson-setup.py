#!/usr/bin/env python3

import os
import sys

def bail(msg):
    print(msg, file=sys.stderr)
    sys.exit(1)

_MARKER = '@@'
def transform_line(line):
    # Substitute any special markers of this form:
    # @@ENV@@
    # with the value of ENV, split into meson array syntax.
    start = line.find(_MARKER)
    if start == -1:
        return line

    end = line.rfind(_MARKER)
    if end == start:
        return line

    # Lookup value of the env var.
    var = line[start+len(_MARKER):end]
    try:
        val = os.environ[var]
    except KeyError:
        bail('cannot generate meson.cross; env var %s not set' % var)

    # Transform into meson array.
    val = ["'%s'" % x for x in val.split()]
    val = ', '.join(val)
    val = '[%s]' % val

    before = line[:start]
    after = line[end+len(_MARKER):]

    return '%s%s%s' % (before, val, after)

# Make sure this is really an SDK extraction environment.
try:
    sysroot = os.environ['OECORE_NATIVE_SYSROOT']
except KeyError:
    bail('OECORE_NATIVE_SYSROOT env var must be set')

cross_file = os.path.join(sysroot, 'usr/share/meson/meson.cross')
tmp_cross_file = '%s.tmp' % cross_file

# Read through and transform the current meson.cross.
lines = []
with open(cross_file, 'r') as f:
    for line in f:
        lines.append(transform_line(line))

# Write the transformed result to a tmp file and atomically rename it. In case
# we crash during the file write, we don't want an invalid meson.cross file.
with open(tmp_cross_file, 'w') as f:
    for line in lines:
        f.write(line)
    f.flush()
    os.fdatasync(f.fileno())
os.rename(tmp_cross_file, cross_file)
