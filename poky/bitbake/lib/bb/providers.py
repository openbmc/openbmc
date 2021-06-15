#
# Copyright (C) 2003, 2004  Chris Larson
# Copyright (C) 2003, 2004  Phil Blundell
# Copyright (C) 2003 - 2005 Michael 'Mickey' Lauer
# Copyright (C) 2005        Holger Hans Peter Freyther
# Copyright (C) 2005        ROAD GmbH
# Copyright (C) 2006        Richard Purdie
#
# SPDX-License-Identifier: GPL-2.0-only
#

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
    bb.data.expandKeys(localdata)

    required = {}
    preferred_versions = {}
    latest_versions = {}

    for pn in pkg_pn:
        (last_ver, last_file, pref_ver, pref_file, req) = findBestProvider(pn, localdata, dataCache, pkg_pn)
        preferred_versions[pn] = (pref_ver, pref_file)
        latest_versions[pn] = (last_ver, last_file)
        required[pn] = req

    return (latest_versions, preferred_versions, required)

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

def versionVariableMatch(cfgData, keyword, pn):
    """
    Return the value of the <keyword>_VERSION variable if set.
    """

    # pn can contain '_', e.g. gcc-cross-x86_64 and an override cannot
    # hence we do this manually rather than use OVERRIDES
    ver = cfgData.getVar("%s_VERSION_pn-%s" % (keyword, pn))
    if not ver:
        ver = cfgData.getVar("%s_VERSION_%s" % (keyword, pn))
    if not ver:
        ver = cfgData.getVar("%s_VERSION" % keyword)

    return ver

def preferredVersionMatch(pe, pv, pr, preferred_e, preferred_v, preferred_r):
    """
    Check if the version pe,pv,pr is the preferred one.
    If there is preferred version defined and ends with '%', then pv has to start with that version after removing the '%'
    """
    if pr == preferred_r or preferred_r is None:
        if pe == preferred_e or preferred_e is None:
            if preferred_v == pv:
                return True
            if preferred_v is not None and preferred_v.endswith('%') and pv.startswith(preferred_v[:len(preferred_v)-1]):
                return True
    return False

def findPreferredProvider(pn, cfgData, dataCache, pkg_pn = None, item = None):
    """
    Find the first provider in pkg_pn with REQUIRED_VERSION or PREFERRED_VERSION set.
    """

    preferred_file = None
    preferred_ver = None
    required = False

    required_v = versionVariableMatch(cfgData, "REQUIRED", pn)
    preferred_v = versionVariableMatch(cfgData, "PREFERRED", pn)

    itemstr = ""
    if item:
        itemstr = " (for item %s)" % item

    if required_v is not None:
        if preferred_v is not None:
            logger.warn("REQUIRED_VERSION and PREFERRED_VERSION for package %s%s are both set using REQUIRED_VERSION %s", pn, itemstr, required_v)
        else:
            logger.debug("REQUIRED_VERSION is set for package %s%s", pn, itemstr)
        # REQUIRED_VERSION always takes precedence over PREFERRED_VERSION
        preferred_v = required_v
        required = True

    if preferred_v:
        m = re.match(r'(\d+:)*(.*)(_.*)*', preferred_v)
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
        if preferred_file is None:
            if not required:
                logger.warn("preferred version %s of %s not available%s", pv_str, pn, itemstr)
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
                logger.warn("versions of %s available: %s", pn, ' '.join(available_vers))
            if required:
                logger.error("required version %s of %s not available%s", pv_str, pn, itemstr)
        else:
            if required:
                logger.debug("selecting %s as REQUIRED_VERSION %s of package %s%s", preferred_file, pv_str, pn, itemstr)
            else:
                logger.debug("selecting %s as PREFERRED_VERSION %s of package %s%s", preferred_file, pv_str, pn, itemstr)

    return (preferred_ver, preferred_file, required)

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
    # Find the highest priority provider with a REQUIRED_VERSION or PREFERRED_VERSION set
    (preferred_ver, preferred_file, required) = findPreferredProvider(pn, cfgData, dataCache, sortpkg_pn, item)
    # Find the latest version of the highest priority provider
    (latest, latest_f) = findLatestProvider(pn, cfgData, dataCache, sortpkg_pn[0])

    if not required and preferred_file is None:
        preferred_file = latest_f
        preferred_ver = latest

    return (latest, latest_f, preferred_ver, preferred_file, required)

def _filterProviders(providers, item, cfgData, dataCache):
    """
    Take a list of providers and filter/reorder according to the
    environment variables
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

    logger.debug("providers for %s are: %s", item, list(sorted(pkg_pn.keys())))

    # First add REQUIRED_VERSIONS or PREFERRED_VERSIONS
    for pn in sorted(pkg_pn):
        sortpkg_pn[pn] = sortPriorities(pn, dataCache, pkg_pn)
        preferred_ver, preferred_file, required = findPreferredProvider(pn, cfgData, dataCache, sortpkg_pn[pn], item)
        if required and preferred_file is None:
            return eligible
        preferred_versions[pn] = (preferred_ver, preferred_file)
        if preferred_versions[pn][1]:
            eligible.append(preferred_versions[pn][1])

    # Now add latest versions
    for pn in sorted(sortpkg_pn):
        if pn in preferred_versions and preferred_versions[pn][1]:
            continue
        preferred_versions[pn] = findLatestProvider(pn, cfgData, dataCache, sortpkg_pn[pn][0])
        eligible.append(preferred_versions[pn][1])

    if not eligible:
        return eligible

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
    environment variables
    Takes a "normal" target item
    """

    eligible = _filterProviders(providers, item, cfgData, dataCache)

    prefervar = cfgData.getVar('PREFERRED_PROVIDER_%s' % item)
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

    logger.debug("sorted providers for %s are: %s", item, eligible)

    return eligible, foundUnique

def filterProvidersRunTime(providers, item, cfgData, dataCache):
    """
    Take a list of providers and filter/reorder according to the
    environment variables
    Takes a "runtime" target item
    """

    eligible = _filterProviders(providers, item, cfgData, dataCache)

    # First try and match any PREFERRED_RPROVIDER entry
    prefervar = cfgData.getVar('PREFERRED_RPROVIDER_%s' % item)
    foundUnique = False
    if prefervar:
        for p in eligible:
            pn = dataCache.pkg_fn[p]
            if prefervar == pn:
                logger.verbose("selecting %s to satisfy %s due to PREFERRED_RPROVIDER", pn, item)
                eligible.remove(p)
                eligible = [p] + eligible
                foundUnique = True
                numberPreferred = 1
                break

    # If we didn't find an RPROVIDER entry, try and infer the provider from PREFERRED_PROVIDER entries
    # by looking through the provides of each eligible recipe and seeing if a PREFERRED_PROVIDER was set.
    # This is most useful for virtual/ entries rather than having a RPROVIDER per entry.
    if not foundUnique:
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
                prefervar = cfgData.getVar('PREFERRED_PROVIDER_%s' % provide)
                #logger.debug("checking PREFERRED_PROVIDER_%s (value %s) against %s", provide, prefervar, pns.keys())
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
        logger.error("Trying to resolve runtime dependency %s resulted in conflicting PREFERRED_PROVIDER entries being found.\nThe providers found were: %s\nThe PREFERRED_PROVIDER entries resulting in this conflict were: %s. You could set PREFERRED_RPROVIDER_%s" % (item, preferred, preferred_vars, item))

    logger.debug("sorted runtime providers for %s are: %s", item, eligible)

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
        pattern = pattern.replace(r'+', r"\+")
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
            logger.debug("Assuming %s is a dynamic package, but it may not exist" % rdepend)

    return rproviders

def buildWorldTargetList(dataCache, task=None):
    """
    Build package list for "bitbake world"
    """
    if dataCache.world_target:
        return

    logger.debug("collating packages for \"world\"")
    for f in dataCache.possible_world:
        terminal = True
        pn = dataCache.pkg_fn[f]
        if task and task not in dataCache.task_deps[f]['tasks']:
            logger.debug2("World build skipping %s as task %s doesn't exist", f, task)
            terminal = False

        for p in dataCache.pn_provides[pn]:
            if p.startswith('virtual/'):
                logger.debug2("World build skipping %s due to %s provider starting with virtual/", f, p)
                terminal = False
                break
            for pf in dataCache.providers[p]:
                if dataCache.pkg_fn[pf] != pn:
                    logger.debug2("World build skipping %s due to both us and %s providing %s", f, pf, p)
                    terminal = False
                    break
        if terminal:
            dataCache.world_target.add(pn)
