#!/usr/bin/env python

# This script is used for verify HOMEPAGE.
# The result is influenced by network environment, since the timeout of connect url is 5 seconds as default.

import sys
import os
import subprocess
import urllib2

def search_bitbakepath():
    bitbakepath = ""

    # Search path to bitbake lib dir in order to load bb modules
    if os.path.exists(os.path.join(os.path.dirname(sys.argv[0]), '../../bitbake/lib/bb')):
        bitbakepath = os.path.join(os.path.dirname(sys.argv[0]), '../../bitbake/lib')
        bitbakepath = os.path.abspath(bitbakepath)
    else:
        # Look for bitbake/bin dir in PATH
        for pth in os.environ['PATH'].split(':'):
            if os.path.exists(os.path.join(pth, '../lib/bb')):
                bitbakepath = os.path.abspath(os.path.join(pth, '../lib'))
                break
        if not bitbakepath:
            sys.stderr.write("Unable to find bitbake by searching parent directory of this script or PATH\n")
            sys.exit(1)
    return bitbakepath

# For importing the following modules
sys.path.insert(0, search_bitbakepath())
import bb.tinfoil

def wgetHomepage(pn, homepage):
    result = subprocess.call('wget ' + '-q -T 5 -t 1 --spider ' + homepage, shell = True)
    if result:
        bb.warn("Failed to verify HOMEPAGE (%s) of %s" % (homepage, pn))
        return 1
    else:
        return 0

def verifyHomepage(bbhandler):
    pkg_pn = bbhandler.cooker.recipecache.pkg_pn
    pnlist = sorted(pkg_pn)
    count = 0
    for pn in pnlist:
        fn = pkg_pn[pn].pop()
        data = bb.cache.Cache.loadDataFull(fn, bbhandler.cooker.collection.get_file_appends(fn), bbhandler.config_data)
        homepage = data.getVar("HOMEPAGE")
        if homepage:
            try:
                urllib2.urlopen(homepage, timeout=5)
            except Exception:
                count = count + wgetHomepage(pn, homepage)
    return count

if __name__=='__main__':
    failcount = 0
    bbhandler = bb.tinfoil.Tinfoil()
    bbhandler.prepare()
    print "Start to verify HOMEPAGE:"
    failcount = verifyHomepage(bbhandler)
    print "finish to verify HOMEPAGE."
    print "Summary: %s failed" % failcount
