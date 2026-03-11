#!/usr/bin/env python3
import sys
logfile = open(sys.argv[1]).read()

necessary_bits = logfile.find("The necessary bits to build these optional modules were not found")
to_find_bits = logfile.find("To find the necessary bits, look in setup.py in detect_modules() for the module's name.")
if necessary_bits != -1:
    print("%s" %(logfile[necessary_bits:to_find_bits]))

failed_to_build = logfile.find("Failed to build these modules:")
if failed_to_build != -1:
    failed_to_build_end = logfile.find("\n\n", failed_to_build)
    print("%s" %(logfile[failed_to_build:failed_to_build_end]))

if necessary_bits != -1 or failed_to_build != -1:
    sys.exit(1)

