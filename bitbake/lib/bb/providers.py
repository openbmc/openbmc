# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (C) 2003, 2004  Chris Larson
# Copyright (C) 2003, 2004  Phil Blundell
# Copyright (C) 2003 - 2005 Michael 'Mickey' Lauer
# Copyright (C) 2005        Holger Hans Peter Freyther
# Copyright (C) 2005        ROAD GmbH
# Copyright (C) 2006        Richard Purdie
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

import re
import logging
from bb import data, utils
from collections import defaultdict
import bb

logger = logging.getLogger("BitBake.Provider")

class NoProvider(bb.BBHandledException):
    """Exception raised when no provider of a build dependency can be found"""

class NoRProvider(bb.BBHandledException):
    """Exception raised when no provider of a runtime dependency can be found"""

class MultipleRProvider(bb.BBHandledException):
    """Exception raised when multiple providers of a runtime dependency can be found"""

def findProviders(cfgData, dataCache, pkg_pn = None):
    """
    Convenience function to get latest and preferred providers in pkg_pn
    """

    if not pkg_pn:
        pkg_pn = dataCache.pkg_pn

    # Need to ensure data store is expanded
    localdata = data.createCopy(cfgData)
    bb.data.update_data(localdata)
    bb.data.expandKeys(localdata)

    preferred_versions = {}
    latest_versions = {}

    for pn in pkg_pn:
        (last_ver, last_file, pref_ver, pref_file) = findBestProvider(pn, localdata, dataCache, pkg_pn)
        preferred_versions[pn] = (pref_ver, pref_file)
        latest_versions[pn] = (last_ver, last_file)

    return (latest_versions, preferred_versions)


def allProviders(dataCache):
    """
    Find all providers for each pn
    """
    all_providers = defaultdict(list)
    for (fn, pn) in dataCache.pkg_fn.items():
        ver = dataCache.pkg_pepvpr[fn]
        all_providers[pn].append((ver, fn))
    return all_providers


def sortPriorities(pn, dataCache, pkg_pn = None):
    """
    Reorder pkg_pn by file priority and default preference
    """

    if not pkg_pn:
        pkg_pn = dataCache.pkg_pn

    files = pkg_pn[pn]
    priorities = {}
    for f in files:
        priority = dataCache.bbfile_priority[f]
        preference = dataCache.pkg_dp[f]
        if priority not in priorities:
            priorities[priority] = {}
        if preference not in priorities[priority]:
            priorities[priority][preference] = []
        priorities[priority][preference].append(f)
    tmp_pn = []
    for pri in sorted(priorities):
        tmp_pref = []
        for pref in sorted(priorities[pri]):
            tmp_pref.extend(priorities[pri][pref])
        tmp_pn = [tmp_pref] + tmp_pn

    return tmp_pn

def preferredVersionMatch(pe, pv, pr, preferred_e, preferred_v, preferred_r):
    """
    Check if the version pe,pv,pr is the preferred one.
    If there is preferred version defined and ends with '%', then pv has to start with that version after removing the '%'
    """
    if (pr == preferred_r or preferred_r == None):
        if (pe == preferred_e or preferred_e == None):
            if preferred_v == pv:
                return True
            if preferred_v != None and preferred_v.endswith('%') and pv.startswith(preferred_v[:len(preferred_v)-1]):
                return True
    return False

def findPreferredProvider(pn, cfgData, dataCache, pkg_pn = None, item = None):
    """
    Find the first provider in pkg_pn with a PREFERRED_VERSION set.
    """

    preferred_file = None
    preferred_ver = None

    localdata = data.createCopy(cfgData)
    localdata.setVar('OVERRIDES', "%s:pn-%s:%s" % (data.getVar('OVERRIDES', localdata), pn, pn))
    bb.data.update_data(localdata)

    preferred_v = localdata.getVar('PREFERRED_VERSION', True)
    if preferred_v:
        m = re.match('(\d+:)*(.*)(_.*)*', preferred_v)
        if m:
            if m.group(1):
                preferred_e = m.group(1)[:-1]
            else:
                preferred_e = None
            preferred_v = m.group(2)
            if m.group(3):
                preferred_r = m.group(3)[1:]
            else:
                preferred_r = None
        else:
            preferred_e = None
            preferred_r = None

        for file_set in pkg_pn:
            for f in file_set:
                pe, pv, pr = dataCache.pkg_pepvpr[f]
                if preferredVersionMatch(pe, pv, pr, preferred_e, preferred_v, preferred_r):
                    preferred_file = f
                    preferred_ver = (pe, pv, pr)
                    break
            if preferred_file:
                break;
        if preferred_r:
            pv_str = '%s-%s' % (preferred_v, preferred_r)
        else:
            pv_str = preferred_v
        if not (preferred_e is None):
            pv_str = '%s:%s' % (preferred_e, pv_str)
        itemstr = ""
        if item:
            itemstr = " (for item %s)" % item
        if preferred_file is None:
            logger.info("preferred version %s of %s not available%s", pv_str, pn, itemstr)
            available_vers = []
            for file_set in pkg_pn:
                for f in file_set:
                    pe, pv, pr = dataCache.pkg_pepvpr[f]
                    ver_str = pv
                    if pe:
                        ver_str = "%s:%s" % (pe, ver_str)
                    if not ver_str in available_vers:
                        available_vers.append(ver_str)
            if available_vers:
                available_vers.sort()
                logger.info("versions of %s available: %s", pn, ' '.join(available_vers))
        else:
            logger.debug(1, "selecting %s as PREFERRED_VERSION %s of package %s%s", preferred_file, pv_str, pn, itemstr)

    return (preferred_ver, preferred_file)


def findLatestProvider(pn, cfgData, dataCache, file_set):
    """
    Return the highest version of the providers in file_set.
    Take default preferences into account.
    """
    latest = None
    latest_p = 0
    latest_f = None
    for file_name in file_set:
        pe, pv, pr = dataCache.pkg_pepvpr[file_name]
        dp = dataCache.pkg_dp[file_name]

        if (latest is None) or ((latest_p == dp) and (utils.vercmp(latest, (pe, pv, pr)) < 0)) or (dp > latest_p):
            latest = (pe, pv, pr)
            latest_f = file_name
            latest_p = dp

    return (latest, latest_f)


def findBestProvider(pn, cfgData, dataCache, pkg_pn = None, item = None):
    """
    If there is a PREFERRED_VERSION, find the highest-priority bbfile
    providing that version.  If not, find the latest version provided by
    an bbfile in the highest-priority set.
    """

    sortpkg_pn = sortPriorities(pn, dataCache, pkg_pn)
    # Find the highest priority provider with a PREFERRED_VERSION set
    (preferred_ver, preferred_file) = findPreferredProvider(pn, cfgData, dataCache, sortpkg_pn, item)
    # Find the latest version of the highest priority provider
    (latest, latest_f) = findLatestProvider(pn, cfgData, dataCache, sortpkg_pn[0])

    if preferred_file is None:
        preferred_file = latest_f
        preferred_ver = latest

    return (latest, latest_f, preferred_ver, preferred_file)


def _filterProviders(providers, item, cfgData, dataCache):
    """
    Take a list of providers and filter/reorder according to the
    environment variables and previous build results
    """
    eligible = []
    preferred_versions = {}
    sortpkg_pn = {}

    # The order of providers depends on the order of the files on the disk
    # up to here. Sort pkg_pn to make dependency issues reproducible rather
    # than effectively random.
    providers.sort()

    # Collate providers by PN
    pkg_pn = {}
    for p in providers:
        pn = dataCache.pkg_fn[p]
        if pn not in pkg_pn:
            pkg_pn[pn] = []
        pkg_pn[pn].append(p)

    logger.debug(1, "providers for %s are: %s", item, pkg_pn.keys())

    # First add PREFERRED_VERSIONS
    for pn in pkg_pn:
        sortpkg_pn[pn] = sortPriorities(pn, dataCache, pkg_pn)
        preferred_versions[pn] = findPreferredProvider(pn, cfgData, dataCache, sortpkg_pn[pn], item)
        if preferred_versions[pn][1]:
            eligible.append(preferred_versions[pn][1])

    # Now add latest versions
    for pn in sortpkg_pn:
        if pn in preferred_versions and preferred_versions[pn][1]:
            continue
        preferred_versions[pn] = findLatestProvider(pn, cfgData, dataCache, sortpkg_pn[pn][0])
        eligible.append(preferred_versions[pn][1])

    if len(eligible) == 0:
        logger.error("no eligible providers for %s", item)
        return 0

    # If pn == item, give it a slight default preference
    # This means PREFERRED_PROVIDER_foobar defaults to foobar if available
    for p in providers:
        pn = dataCache.pkg_fn[p]
        if pn != item:
            continue
        (newvers, fn) = preferred_versions[pn]
        if not fn in eligible:
            continue
        eligible.remove(fn)
        eligible = [fn] + eligible

    return eligible


def filterProviders(providers, item, cfgData, dataCache):
    """
    Take a list of providers and filter/reorder according to the
    environment variables and previous build results
    Takes a "normal" target item
    """

    eligible = _filterProviders(providers, item, cfgData, dataCache)

    prefervar = cfgData.getVar('PREFERRED_PROVIDER_%s' % item, True)
    if prefervar:
        dataCache.preferred[item] = prefervar

    foundUnique = False
    if item in dataCache.preferred:
        for p in eligible:
            pn = dataCache.pkg_fn[p]
            if dataCache.preferred[item] == pn:
                logger.verbose("selecting %s to satisfy %s due to PREFERRED_PROVIDERS", pn, item)
                eligible.remove(p)
                eligible = [p] + eligible
                foundUnique = True
                break

    logger.debug(1, "sorted providers for %s are: %s", item, eligible)

    return eligible, foundUnique

def filterProvidersRunTime(providers, item, cfgData, dataCache):
    """
    Take a list of providers and filter/reorder according to the
    environment variables and previous build results
    Takes a "runtime" target item
    """

    eligible = _filterProviders(providers, item, cfgData, dataCache)

    # Should use dataCache.preferred here?
    preferred = []
    preferred_vars = []
    pns = {}
    for p in eligible:
        pns[dataCache.pkg_fn[p]] = p
    for p in eligible:
        pn = dataCache.pkg_fn[p]
        provides = dataCache.pn_provides[pn]
        for provide in provides:
            prefervar = cfgData.getVar('PREFERRED_PROVIDER_%s' % provide, True)
            #logger.debug(1, "checking PREFERRED_PROVIDER_%s (value %s) against %s", provide, prefervar, pns.keys())
            if prefervar in pns and pns[prefervar] not in preferred:
                var = "PREFERRED_PROVIDER_%s = %s" % (provide, prefervar)
                logger.verbose("selecting %s to satisfy runtime %s due to %s", prefervar, item, var)
                preferred_vars.append(var)
                pref = pns[prefervar]
                eligible.remove(pref)
                eligible = [pref] + eligible
                preferred.append(pref)
                break

    numberPreferred = len(preferred)

    if numberPreferred > 1:
        logger.error("Trying to resolve runtime dependency %s resulted in conflicting PREFERRED_PROVIDER entries being found.\nThe providers found were: %s\nThe PREFERRED_PROVIDER entries resulting in this conflict were: %s", item, preferred, preferred_vars)

    logger.debug(1, "sorted runtime providers for %s are: %s", item, eligible)

    return eligible, numberPreferred

regexp_cache = {}

def getRuntimeProviders(dataCache, rdepend):
    """
    Return any providers of runtime dependency
    """
    rproviders = []

    if rdepend in dataCache.rproviders:
        rproviders += dataCache.rproviders[rdepend]

    if rdepend in dataCache.packages:
        rproviders += dataCache.packages[rdepend]

    if rproviders:
        return rproviders

    # Only search dynamic packages if we can't find anything in other variables
    for pattern in dataCache.packages_dynamic:
        pattern = pattern.replace('+', "\+")
        if pattern in regexp_cache:
            regexp = regexp_cache[pattern]
        else:
            try:
                regexp = re.compile(pattern)
            except:
                logger.error("Error parsing regular expression '%s'", pattern)
                raise
            regexp_cache[pattern] = regexp
        if regexp.match(rdepend):
            rproviders += dataCache.packages_dynamic[pattern]
            logger.debug(1, "Assuming %s is a dynamic package, but it may not exist" % rdepend)

    return rproviders
