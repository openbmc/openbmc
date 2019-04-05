# resulttool - common library/utility functions
#
# Copyright (c) 2019, Intel Corporation.
# Copyright (c) 2019, Linux Foundation
#
# This program is free software; you can redistribute it and/or modify it
# under the terms and conditions of the GNU General Public License,
# version 2, as published by the Free Software Foundation.
#
# This program is distributed in the hope it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
# more details.
#
import os
import json
import scriptpath
import copy
scriptpath.add_oe_lib_path()

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
    "oeselftest": ['TEST_TYPE'],
    "runtime": ['TEST_TYPE', 'DISTRO', 'MACHINE', 'IMAGE_BASENAME'],
    "sdk": ['TEST_TYPE', 'MACHINE', 'SDKMACHINE', 'IMAGE_BASENAME'],
    "sdkext": ['TEST_TYPE', 'MACHINE', 'SDKMACHINE', 'IMAGE_BASENAME'],
    "manual": ['TEST_TYPE', 'TEST_MODULE', 'MACHINE', 'IMAGE_BASENAME']
}

#
# Load the json file and append the results data into the provided results dict
#
def append_resultsdata(results, f, configmap=store_map):
    if type(f) is str:
        with open(f, "r") as filedata:
            data = json.load(filedata)
    else:
        data = f
    for res in data:
        if "configuration" not in data[res] or "result" not in data[res]:
            raise ValueError("Test results data without configuration or result section?")
        if "TESTSERIES" not in data[res]["configuration"]:
            data[res]["configuration"]["TESTSERIES"] = os.path.basename(os.path.dirname(f))
        testtype = data[res]["configuration"].get("TEST_TYPE")
        if testtype not in configmap:
            raise ValueError("Unknown test type %s" % testtype)
        configvars = configmap[testtype]
        testpath = "/".join(data[res]["configuration"].get(i) for i in configmap[testtype])
        if testpath not in results:
            results[testpath] = {}
        results[testpath][res] = data[res]

#
# Walk a directory and find/load results data
# or load directly from a file
#
def load_resultsdata(source, configmap=store_map):
    results = {}
    if os.path.isfile(source):
        append_resultsdata(results, source, configmap)
        return results
    for root, dirs, files in os.walk(source):
        for name in files:
            f = os.path.join(root, name)
            if name == "testresults.json":
                append_resultsdata(results, f, configmap)
    return results

def filter_resultsdata(results, resultid):
    newresults = {}
    for r in results:
        for i in results[r]:
            if i == resultsid:
                 newresults[r] = {}
                 newresults[r][i] = results[r][i]
    return newresults

def strip_ptestresults(results):
    newresults = copy.deepcopy(results)
    #for a in newresults2:
    #  newresults = newresults2[a]
    for res in newresults:
        if 'result' not in newresults[res]:
            continue
        if 'ptestresult.rawlogs' in newresults[res]['result']:
            del newresults[res]['result']['ptestresult.rawlogs']
        if 'ptestresult.sections' in newresults[res]['result']:
            for i in newresults[res]['result']['ptestresult.sections']:
                if 'log' in newresults[res]['result']['ptestresult.sections'][i]:
                    del newresults[res]['result']['ptestresult.sections'][i]['log']
    return newresults

def save_resultsdata(results, destdir, fn="testresults.json", ptestjson=False, ptestlogs=False):
    for res in results:
        if res:
            dst = destdir + "/" + res + "/" + fn
        else:
            dst = destdir + "/" + fn
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        resultsout = results[res]
        if not ptestjson:
            resultsout = strip_ptestresults(results[res])
        with open(dst, 'w') as f:
            f.write(json.dumps(resultsout, sort_keys=True, indent=4))
        for res2 in results[res]:
            if ptestlogs and 'result' in results[res][res2]:
                if 'ptestresult.rawlogs' in results[res][res2]['result']:
                    with open(dst.replace(fn, "ptest-raw.log"), "w+") as f:
                        f.write(results[res][res2]['result']['ptestresult.rawlogs']['log'])
                if 'ptestresult.sections' in results[res][res2]['result']:
                    for i in results[res][res2]['result']['ptestresult.sections']:
                        if 'log' in results[res][res2]['result']['ptestresult.sections'][i]:
                            with open(dst.replace(fn, "ptest-%s.log" % i), "w+") as f:
                                f.write(results[res][res2]['result']['ptestresult.sections'][i]['log'])

def git_get_result(repo, tags):
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
        append_resultsdata(results, obj)

    return results
