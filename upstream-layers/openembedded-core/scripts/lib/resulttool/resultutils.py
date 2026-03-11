# resulttool - common library/utility functions
#
# Copyright (c) 2019, Intel Corporation.
# Copyright (c) 2019, Linux Foundation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import os
import base64
import zlib
import json
import scriptpath
import copy
import urllib.request
import posixpath
import logging
scriptpath.add_oe_lib_path()

logger = logging.getLogger('resulttool')

flatten_map = {
    "oeselftest": [],
    "runtime": [],
    "sdk": [],
    "sdkext": [],
    "manual": []
}
regression_map = {
    "oeselftest": ['TEST_TYPE', 'MACHINE'],
    "runtime": ['TESTSERIES', 'TEST_TYPE', 'IMAGE_BASENAME', 'MACHINE', 'IMAGE_PKGTYPE', 'DISTRO'],
    "sdk": ['TESTSERIES', 'TEST_TYPE', 'IMAGE_BASENAME', 'MACHINE', 'SDKMACHINE'],
    "sdkext": ['TESTSERIES', 'TEST_TYPE', 'IMAGE_BASENAME', 'MACHINE', 'SDKMACHINE'],
    "manual": ['TEST_TYPE', 'TEST_MODULE', 'IMAGE_BASENAME', 'MACHINE']
}
store_map = {
    "oeselftest": ['TEST_TYPE', 'TESTSERIES', 'MACHINE'],
    "runtime": ['TEST_TYPE', 'DISTRO', 'MACHINE', 'IMAGE_BASENAME'],
    "sdk": ['TEST_TYPE', 'MACHINE', 'SDKMACHINE', 'IMAGE_BASENAME'],
    "sdkext": ['TEST_TYPE', 'MACHINE', 'SDKMACHINE', 'IMAGE_BASENAME'],
    "manual": ['TEST_TYPE', 'TEST_MODULE', 'MACHINE', 'IMAGE_BASENAME']
}

rawlog_sections = {
    "ptestresult.rawlogs": "ptest",
    "ltpresult.rawlogs": "ltp",
    "ltpposixresult.rawlogs": "ltpposix"
}

def is_url(p):
    """
    Helper for determining if the given path is a URL
    """
    return p.startswith('http://') or p.startswith('https://')

extra_configvars = {'TESTSERIES': ''}

#
# Load the json file and append the results data into the provided results dict
#
def append_resultsdata(results, f, configmap=store_map, configvars=extra_configvars):
    if type(f) is str:
        if is_url(f):
            with urllib.request.urlopen(f) as response:
                data = json.loads(response.read().decode('utf-8'))
            url = urllib.parse.urlparse(f)
            testseries = posixpath.basename(posixpath.dirname(url.path))
        else:
            with open(f, "r") as filedata:
                try:
                    data = json.load(filedata)
                except json.decoder.JSONDecodeError:
                    print("Cannot decode {}. Possible corruption. Skipping.".format(f))
                    data = ""
            testseries = os.path.basename(os.path.dirname(f))
    else:
        data = f
    for res in data:
        if "configuration" not in data[res] or "result" not in data[res]:
            raise ValueError("Test results data without configuration or result section?")
        for config in configvars:
            if config == "TESTSERIES" and "TESTSERIES" not in data[res]["configuration"]:
                data[res]["configuration"]["TESTSERIES"] = testseries
                continue
            if config not in data[res]["configuration"]:
                data[res]["configuration"][config] = configvars[config]
        testtype = data[res]["configuration"].get("TEST_TYPE")
        if testtype not in configmap:
            raise ValueError("Unknown test type %s" % testtype)
        testpath = "/".join(data[res]["configuration"].get(i) for i in configmap[testtype])
        if testpath not in results:
            results[testpath] = {}
        results[testpath][res] = data[res]

#
# Walk a directory and find/load results data
# or load directly from a file
#
def load_resultsdata(source, configmap=store_map, configvars=extra_configvars):
    results = {}
    if is_url(source) or os.path.isfile(source):
        append_resultsdata(results, source, configmap, configvars)
        return results
    for root, dirs, files in os.walk(source):
        for name in files:
            f = os.path.join(root, name)
            if name == "testresults.json":
                append_resultsdata(results, f, configmap, configvars)
    return results

def filter_resultsdata(results, resultid):
    newresults = {}
    for r in results:
        for i in results[r]:
            if i == resultsid:
                 newresults[r] = {}
                 newresults[r][i] = results[r][i]
    return newresults

def strip_logs(results):
    newresults = copy.deepcopy(results)
    for res in newresults:
        if 'result' not in newresults[res]:
            continue
        for logtype in rawlog_sections:
            if logtype in newresults[res]['result']:
                del newresults[res]['result'][logtype]
        if 'ptestresult.sections' in newresults[res]['result']:
            for i in newresults[res]['result']['ptestresult.sections']:
                if 'log' in newresults[res]['result']['ptestresult.sections'][i]:
                    del newresults[res]['result']['ptestresult.sections'][i]['log']
    return newresults

# For timing numbers, crazy amounts of precision don't make sense and just confuse
# the logs. For numbers over 1, trim to 3 decimal places, for numbers less than 1,
# trim to 4 significant digits
def trim_durations(results):
    for res in results:
        if 'result' not in results[res]:
            continue
        for entry in results[res]['result']:
            if 'duration' in results[res]['result'][entry]:
                duration = results[res]['result'][entry]['duration']
                if duration > 1:
                    results[res]['result'][entry]['duration'] = float("%.3f" % duration)
                elif duration < 1:
                    results[res]['result'][entry]['duration'] = float("%.4g" % duration)
    return results

def handle_cleanups(results):
    # Remove pointless path duplication from old format reproducibility results
    for res2 in results:
        try:
            section = results[res2]['result']['reproducible']['files']
            for pkgtype in section:
                for filelist in section[pkgtype].copy():
                    if section[pkgtype][filelist] and type(section[pkgtype][filelist][0]) == dict:
                        newlist = []
                        for entry in section[pkgtype][filelist]:
                            newlist.append(entry["reference"].split("/./")[1])
                        section[pkgtype][filelist] = newlist

        except KeyError:
            pass
    # Remove pointless duplicate rawlogs data
    try:
        del results[res2]['result']['reproducible.rawlogs']
    except KeyError:
        pass

def decode_log(logdata):
    if isinstance(logdata, str):
        return logdata
    elif isinstance(logdata, dict):
        if "compressed" in logdata:
            data = logdata.get("compressed")
            data = base64.b64decode(data.encode("utf-8"))
            data = zlib.decompress(data)
            return data.decode("utf-8", errors='ignore')
    return None

def generic_get_log(sectionname, results, section):
    if sectionname not in results:
        return None
    if section not in results[sectionname]:
        return None

    ptest = results[sectionname][section]
    if 'log' not in ptest:
        return None
    return decode_log(ptest['log'])

def ptestresult_get_log(results, section):
    return generic_get_log('ptestresult.sections', results, section)

def generic_get_rawlogs(sectname, results):
    if sectname not in results:
        return None
    if 'log' not in results[sectname]:
        return None
    return decode_log(results[sectname]['log'])

def save_resultsdata(results, destdir, fn="testresults.json", ptestjson=False, ptestlogs=False):
    for res in results:
        if res:
            dst = destdir + "/" + res + "/" + fn
        else:
            dst = destdir + "/" + fn
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        resultsout = results[res]
        if not ptestjson:
            resultsout = strip_logs(results[res])
        trim_durations(resultsout)
        handle_cleanups(resultsout)
        with open(dst, 'w') as f:
            f.write(json.dumps(resultsout, sort_keys=True, indent=1))
        for res2 in results[res]:
            if ptestlogs and 'result' in results[res][res2]:
                seriesresults = results[res][res2]['result']
                for logtype in rawlog_sections:
                    logdata = generic_get_rawlogs(logtype, seriesresults)
                    if logdata is not None:
                        logger.info("Extracting " + rawlog_sections[logtype] + "-raw.log")
                        with open(dst.replace(fn, rawlog_sections[logtype] + "-raw.log"), "w+") as f:
                            f.write(logdata)
                if 'ptestresult.sections' in seriesresults:
                    for i in seriesresults['ptestresult.sections']:
                        sectionlog = ptestresult_get_log(seriesresults, i)
                        if sectionlog is not None:
                            with open(dst.replace(fn, "ptest-%s.log" % i), "w+") as f:
                                f.write(sectionlog)

def git_get_result(repo, tags, configmap=store_map):
    git_objs = []
    for tag in tags:
        files = repo.run_cmd(['ls-tree', "--name-only", "-r", tag]).splitlines()
        git_objs.extend([tag + ':' + f for f in files if f.endswith("testresults.json")])

    def parse_json_stream(data):
        """Parse multiple concatenated JSON objects"""
        objs = []
        json_d = ""
        for line in data.splitlines():
            if line == '}{':
                json_d += '}'
                objs.append(json.loads(json_d))
                json_d = '{'
            else:
                json_d += line
        objs.append(json.loads(json_d))
        return objs

    # Optimize by reading all data with one git command
    results = {}
    for obj in parse_json_stream(repo.run_cmd(['show'] + git_objs + ['--'])):
        append_resultsdata(results, obj, configmap=configmap)

    return results

def test_run_results(results):
    """
    Convenient generator function that iterates over all test runs that have a
    result section.

    Generates a tuple of:
        (result json file path, test run name, test run (dict), test run "results" (dict))
    for each test run that has a "result" section
    """
    for path in results:
        for run_name, test_run in results[path].items():
            if not 'result' in test_run:
                continue
            yield path, run_name, test_run, test_run['result']

