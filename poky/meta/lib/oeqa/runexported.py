#!/usr/bin/env python3
#
# Copyright (C) 2013 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

# This script should be used outside of the build system to run image tests.
# It needs a json file as input as exported by the build.
# E.g for an already built image:
#- export the tests:
#   TEST_EXPORT_ONLY = "1"
#   TEST_TARGET  = "simpleremote"
#   TEST_TARGET_IP = "192.168.7.2"
#   TEST_SERVER_IP = "192.168.7.1"
# bitbake core-image-sato -c testimage
# Setup your target, e.g for qemu: runqemu core-image-sato
# cd build/tmp/testimage/core-image-sato
# ./runexported.py testdata.json

import sys
import os
import time
import argparse

try:
    import simplejson as json
except ImportError:
    import json

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "oeqa")))

from oeqa.oetest import ExportTestContext
from oeqa.utils.commands import runCmd, updateEnv
from oeqa.utils.sshcontrol import SSHControl

# this isn't pretty but we need a fake target object
# for running the tests externally as we don't care
# about deploy/start we only care about the connection methods (run, copy)
class FakeTarget(object):
    def __init__(self, d):
        self.connection = None
        self.ip = None
        self.server_ip = None
        self.datetime = time.strftime('%Y%m%d%H%M%S',time.gmtime())
        self.testdir = d.getVar("TEST_LOG_DIR")
        self.pn = d.getVar("PN")

    def exportStart(self):
        self.sshlog = os.path.join(self.testdir, "ssh_target_log.%s" % self.datetime)
        sshloglink = os.path.join(self.testdir, "ssh_target_log")
        if os.path.lexists(sshloglink):
            os.remove(sshloglink)
        os.symlink(self.sshlog, sshloglink)
        print("SSH log file: %s" %  self.sshlog)
        self.connection = SSHControl(self.ip, logfile=self.sshlog)

    def run(self, cmd, timeout=None):
        return self.connection.run(cmd, timeout)

    def copy_to(self, localpath, remotepath):
        return self.connection.copy_to(localpath, remotepath)

    def copy_from(self, remotepath, localpath):
        return self.connection.copy_from(remotepath, localpath)


class MyDataDict(dict):
    def getVar(self, key, unused = None):
        return self.get(key, "")

def main():

    parser = argparse.ArgumentParser()
    parser.add_argument("-t", "--target-ip", dest="ip", help="The IP address of the target machine. Use this to \
            overwrite the value determined from TEST_TARGET_IP at build time")
    parser.add_argument("-s", "--server-ip", dest="server_ip", help="The IP address of this machine. Use this to \
            overwrite the value determined from TEST_SERVER_IP at build time.")
    parser.add_argument("-d", "--deploy-dir", dest="deploy_dir", help="Full path to the package feeds, that this \
            the contents of what used to be DEPLOY_DIR on the build machine. If not specified it will use the value \
            specified in the json if that directory actually exists or it will error out.")
    parser.add_argument("-l", "--log-dir", dest="log_dir", help="This sets the path for TEST_LOG_DIR. If not specified \
            the current dir is used. This is used for usually creating a ssh log file and a scp test file.")
    parser.add_argument("-a", "--tag", dest="tag", help="Only run test with specified tag.")
    parser.add_argument("json", help="The json file exported by the build system", default="testdata.json", nargs='?')

    args = parser.parse_args()

    with open(args.json, "r") as f:
        loaded = json.load(f)

    if args.ip:
        loaded["target"]["ip"] = args.ip
    if args.server_ip:
        loaded["target"]["server_ip"] = args.server_ip

    d = MyDataDict()
    for key in loaded["d"].keys():
        d[key] = loaded["d"][key]

    if args.log_dir:
        d["TEST_LOG_DIR"] = args.log_dir
    else:
        d["TEST_LOG_DIR"] = os.path.abspath(os.path.dirname(__file__))
    if args.deploy_dir:
        d["DEPLOY_DIR"] = args.deploy_dir
    else:
        if not os.path.isdir(d["DEPLOY_DIR"]):
            print("WARNING: The path to DEPLOY_DIR does not exist: %s" % d["DEPLOY_DIR"])

    parsedArgs = {}
    parsedArgs["tag"] = args.tag

    extract_sdk(d)

    target = FakeTarget(d)
    for key in loaded["target"].keys():
        setattr(target, key, loaded["target"][key])

    target.exportStart()
    tc = ExportTestContext(d, target, True, parsedArgs)
    tc.loadTests()
    tc.runTests()

    return 0

def extract_sdk(d):
    """
    Extract SDK if needed
    """

    export_dir = os.path.dirname(os.path.realpath(__file__))
    tools_dir = d.getVar("TEST_EXPORT_SDK_DIR")
    tarball_name = "%s.sh" % d.getVar("TEST_EXPORT_SDK_NAME")
    tarball_path = os.path.join(export_dir, tools_dir, tarball_name)
    extract_path = os.path.join(export_dir, "sysroot")
    if os.path.isfile(tarball_path):
        print ("Found SDK tarball %s. Extracting..." % tarball_path)
        result = runCmd("%s -y -d %s" % (tarball_path, extract_path))
        for f in os.listdir(extract_path):
            if f.startswith("environment-setup"):
                print("Setting up SDK environment...")
                env_file = os.path.join(extract_path, f)
                updateEnv(env_file)

if __name__ == "__main__":
    try:
        ret = main()
    except Exception:
        ret = 1
        import traceback
        traceback.print_exc()
    sys.exit(ret)
