#! /usr/bin/env python3

# template.py (and other filenames)
# By Max Eliaser (max.eliaser@intel.com)

# Copyright (c) 2014 Intel Corp.

# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:

# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.

# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

# This program acts like a dummy version of the texinfo utilities, creating
# the right output files but leaving them blank. It will parse out the name
# of the executable from argv[0] and emulate the corresponding program, so
# multiple copies of this script will exist under different names.

import sys, argparse

this_binary = sys.argv[0].split("/")[-1]

# To be outputted if functionality that hasn't been stubbed yet is invoked.
stub_msg = """
This stand-in version of %s is not yet fully capable of emulating
the real version from the GNU texinfo suite. If you see this message, file a
bug report with details on the recipe that failed.
Invoked as %s
""" % (this_binary, sys.argv)

# Autotools setups query the version, so this is actually necessary. Some of
# them (lookin' at you, glibc) actually look for the substring "GNU texinfo,"
# so we put that substring in there without actually telling a lie.
version_str = """%s (fake texinfo, emulating GNU texinfo) 5.2

Super amazing version which is totally not fake in any way whatsoever.
Copyright (C) 2014 Intel Corp. Distributed under the terms of the MIT
license.
""" % this_binary

simple_binaries = "pod2texi texi2dvi pdftexi2dvi texindex texi2pdf \
                   txixml2texi install-info ginstall-info \
                   update-info-dir".split()

# These utilities use a slightly different set of options and flags.
complex_binaries = "makeinfo texi2any".split()

valid_binaries = simple_binaries + complex_binaries

assert this_binary in valid_binaries, \
       this_binary + " is not one of " + ', '.join(valid_binaries)

# For debugging
log_interceptions = False
if log_interceptions:
    with open("/tmp/intercepted_" + this_binary, "a") as f:
        f.write(' '.join([this_binary] + sys.argv[1:]) + '\n')

# Look through the options and flags, and if necessary, touch any output
# files.
p = argparse.ArgumentParser()
if this_binary in complex_binaries:
    p.add_argument('-E', '--macro-expand', metavar='FILE')
p.add_argument('-o', '--output', metavar='DEST')
p.add_argument('--version', action='store_true')

args, unknown = p.parse_known_args()

if args.version:
    print(version_str)
    sys.exit(0)

# Check for functionality that isn't implemented yet.
assert not getattr(args, 'macro_expand', None), \
    "-E/--macro-expand option not yet supported" + stub_msg

# Check if -o or --output is specified.
if args.output:
    with open(args.output, 'w'):
        pass
    sys.exit(0)

# The -o/--output option overrides the default. For makeinfo and texi2any,
# that default is to look for a @setfilename command in the input file.
# Otherwise, printing nothing to stdout and then exiting should suffice.
assert this_binary in simple_binaries, \
       "Don't know how to get default output file name from input file!" + \
       stub_msg
