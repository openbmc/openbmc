#!/usr/bin/env python

# This script can be used to verify HOMEPAGE values for all recipes in
# the current configuration.
# The result is influenced by network environment, since the timeout of connect url is 5 seconds as default.

import sys
import os
import subprocess
import urllib2


# Allow importing scripts/lib modules
scripts_path = os.path.abspath(os.path.dirname(os.path.realpath(__file__)) + '/..')
lib_path = scripts_path + '/lib'
sys.path = sys.path + [lib_path]
import scriptpath
import scriptutils

# Allow importing bitbake modules
bitbakepath = scriptpath.add_bitbake_lib_path()

import bb.tinfoil

logger = scriptutils.logger_create('verify_homepage')

def wgetHomepage(pn, homepage):
    result = subprocess.call('wget ' + '-q -T 5 -t 1 --spider ' + homepage, shell = True)
    if result:
        logger.warn("%s: failed to verify HOMEPAGE: %s " % (pn, homepage))
        return 1
    else:
        return 0

def verifyHomepage(bbhandler):
    pkg_pn = bbhandler.cooker.recipecache.pkg_pn
    pnlist = sorted(pkg_pn)
    count = 0
    checked = []
    for pn in pnlist:
        for fn in pkg_pn[pn]:
            # There's no point checking multiple BBCLASSEXTENDed variants of the same recipe
            realfn, _ = bb.cache.Cache.virtualfn2realfn(fn)
            if realfn in checked:
                continue
            data = bb.cache.Cache.loadDataFull(realfn, bbhandler.cooker.collection.get_file_appends(realfn), bbhandler.config_data)
            homepage = data.getVar("HOMEPAGE", True)
            if homepage:
                try:
                    urllib2.urlopen(homepage, timeout=5)
                except Exception:
                    count = count + wgetHomepage(os.path.basename(realfn), homepage)
            checked.append(realfn)
    return count

if __name__=='__main__':
    bbhandler = bb.tinfoil.Tinfoil()
    bbhandler.prepare()
    logger.info("Start verifying HOMEPAGE:")
    failcount = verifyHomepage(bbhandler)
    logger.info("Finished verifying HOMEPAGE.")
    logger.info("Summary: %s failed" % failcount)
