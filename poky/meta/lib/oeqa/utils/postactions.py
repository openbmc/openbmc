#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Run a set of actions after tests. The runner provides internal data
# dictionary as well as test context to any action to run.

import datetime
import io
import os
import stat
import subprocess
import tempfile
from oeqa.utils import get_artefact_dir

##################################################################
# Host/target statistics
##################################################################

def get_target_disk_usage(d, tc, artifacts_list, outputdir):
    output_file = os.path.join(outputdir, "target_disk_usage.txt")
    try:
        (status, output) = tc.target.run('df -h')
        with open(output_file, 'w') as f:
            f.write(output)
            f.write("\n")
    except Exception as e:
        bb.warn(f"Can not get target disk usage: {e}")

def get_host_disk_usage(d, tc, artifacts_list, outputdir):
    import subprocess

    output_file = os.path.join(outputdir, "host_disk_usage.txt")
    try:
        with open(output_file, 'w') as f:
            output = subprocess.run(['df', '-hl'], check=True, text=True, stdout=f, env={})
    except Exception as e:
        bb.warn(f"Can not get host disk usage: {e}")

##################################################################
# Artifacts retrieval
##################################################################

def get_artifacts_list(target, raw_list):
    result = []
    # Passed list may contains patterns in paths, expand them directly on target
    for raw_path in raw_list.split():
        cmd = f"for p in {raw_path}; do if [ -e $p ]; then echo $p; fi; done"
        try:
            status, output = target.run(cmd)
            if status != 0 or not output:
                raise Exception()
            result += output.split()
        except:
            bb.note(f"No file/directory matching path {raw_path}")

    return result

def list_and_fetch_failed_tests_artifacts(d, tc, artifacts_list, outputdir):
    artifacts_list = get_artifacts_list(tc.target, artifacts_list)
    if not artifacts_list:
        bb.warn("Could not load artifacts list, skip artifacts retrieval")
        return
    try:
        # We need gnu tar for sparse files, not busybox
        cmd = "tar --sparse -zcf - " + " ".join(artifacts_list)
        (status, output) = tc.target.run(cmd, raw = True)
        if status != 0 or not output:
            raise Exception("Error while fetching compressed artifacts")
        archive_name = os.path.join(outputdir, "tests_artifacts.tar.gz")
        with open(archive_name, "wb") as f:
            f.write(output)
    except Exception as e:
        bb.warn(f"Can not retrieve artifacts from test target: {e}")


##################################################################
# General post actions runner
##################################################################

def run_failed_tests_post_actions(d, tc):
    artifacts = d.getVar("TESTIMAGE_FAILED_QA_ARTIFACTS")
    # Allow all the code to be disabled by having no artifacts set, e.g. for systems with no ssh support
    if not artifacts:
        return

    outputdir = get_artefact_dir(d)
    os.makedirs(outputdir, exist_ok=True)
    datestr = datetime.datetime.now().strftime('%Y%m%d')
    outputdir = tempfile.mkdtemp(prefix='oeqa-target-artefacts-%s-' % datestr, dir=outputdir)
    os.chmod(outputdir, stat.S_IRWXU | stat.S_IRGRP | stat.S_IXGRP | stat.S_IROTH | stat.S_IXOTH)

    post_actions=[
        list_and_fetch_failed_tests_artifacts,
        get_target_disk_usage,
        get_host_disk_usage
    ]

    for action in post_actions:
        action(d, tc, artifacts, outputdir)
