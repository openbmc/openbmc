#!/usr/bin/env python3

# perf pmu-events sorting tool
#
# Copyright (C) 2021 Bruce Ashfield
#
# SPDX-License-Identifier: MIT
#

import sys
import os
import re
from collections import OrderedDict

if len(sys.argv) < 2:
    print( "[ERROR]: input and output pmu files missing" )
    sys.exit(1)

if len(sys.argv) < 3:
    print( "[ERROR]: output pmu file missing" )
    sys.exit(1)

infile = sys.argv[1]
outfile = sys.argv[2]

if not os.path.exists(infile):
    print( "ERROR. input file does not exist: %s" % infile )
    sys.exit(1)

if os.path.exists(outfile):
    print( "WARNING. output file will be overwritten: %s" % infile )

with open(infile, 'r') as file:
    data = file.read()

preamble_regex = re.compile( '^(.*?)^(struct|const struct|static struct|static const struct)', re.MULTILINE | re.DOTALL )

preamble = re.search( preamble_regex, data )
struct_block_regex = re.compile(r'^(struct|const struct|static struct|static const struct).*?(\w+) (.*?)\[\] = {(.*?)^};', re.MULTILINE | re.DOTALL )
field_regex =  re.compile(r'{.*?},', re.MULTILINE | re.DOTALL )
cpuid_regex = re.compile(r'\.cpuid = (.*?),', re.MULTILINE | re.DOTALL )
name_regex = re.compile(r'\.name = (.*?),', re.MULTILINE | re.DOTALL )

# create a dictionary structure to store all the structs, their
# types and then their fields.
entry_dict = {}
for struct in re.findall( struct_block_regex, data ):
    # print( "struct: %s %s %s" % (struct[0],struct[1],struct[2]) )
    entry_dict[struct[2]] = {}
    entry_dict[struct[2]]['type_prefix'] = struct[0]
    entry_dict[struct[2]]['type'] = struct[1]
    entry_dict[struct[2]]['fields'] = {}
    for entry in re.findall( field_regex, struct[3] ):
        #print( "    entry: %s" % entry )
        cpuid = re.search( cpuid_regex, entry )
        if cpuid:
            #print( "    cpuid found: %s" % cpuid.group(1) )
            entry_dict[struct[2]]['fields'][cpuid.group(1)] = entry

        name = re.search( name_regex, entry )
        if name:
            #print( "    name found: %s" % name.group(1) )
            entry_dict[struct[2]]['fields'][name.group(1)] = entry

        # unmatched entries are most likely array terminators and
        # should end up as the last element in the sorted list, which
        # is achieved by using '0' as the key
        if not cpuid and not name:
            entry_dict[struct[2]]['fields']['0'] = entry

# created ordered dictionaries from the captured values. These are ordered by
# a sorted() iteration of the keys. We don't care about the order we read
# things, just the sorted order. Hency why we couldn't create these during
# reading.
#
# yes, there's a more concise way to do this, but our nested dictionaries of
# fields make it complex enough that it becomes unreadable.
entry_dict_sorted = OrderedDict()
for i in sorted(entry_dict.keys()):
    entry_dict_sorted[i] = {}
    entry_dict_sorted[i]['type_prefix'] = entry_dict[i]['type_prefix']
    entry_dict_sorted[i]['type'] = entry_dict[i]['type']
    entry_dict_sorted[i]['fields'] = {}
    for f in sorted(entry_dict[i]['fields'].keys()):
        entry_dict_sorted[i]['fields'][f] = entry_dict[i]['fields'][f] 

# dump the sorted elements to the outfile
outf = open( outfile, 'w' )

print( preamble.group(1) )
outf.write( preamble.group(1) )
for d in entry_dict_sorted:
    outf.write( "%s %s %s[] = {\n" % (entry_dict_sorted[d]['type_prefix'], entry_dict_sorted[d]['type'],d) )
    for f in entry_dict_sorted[d]['fields']:
        outf.write( entry_dict_sorted[d]['fields'][f] + '\n' )

    outf.write( "};\n" )

outf.close()
    
