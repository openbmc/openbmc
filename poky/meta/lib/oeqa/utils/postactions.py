#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Run a set of actions after tests. The runner provides internal data
# dictionary as well as test context to any action to run.

from oeqa.utils import get_json_result_dir

def create_artifacts_directory(d, tc):
    import shutil

    local_artifacts_dir = os.path.join(get_json_result_dir(d), "artifacts")
    if os.path.isdir(local_artifacts_dir):
        shutil.rmtree(local_artifacts_dir)

    os.makedirs(local_artifacts_dir)

##################################################################
# Host/target statistics
##################################################################

def get_target_disk_usage(d, tc):
    output_file = os.path.join(get_json_result_dir(d), "artifacts", "target_disk_usage.txt")
    try:
        (status, output) = tc.target.run('df -h')
        with open(output_file, 'w') as f:
            f.write(output)
            f.write("\n")
    except Exception as e:
        bb.warn(f"Can not get target disk usage: {e}")

def get_host_disk_usage(d, tc):
    import subprocess

    output_file = os.path.join(get_json_result_dir(d), "artifacts", "host_disk_usage.txt")
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

def retrieve_test_artifacts(target, artifacts_list, target_dir):
    local_artifacts_dir = os.path.join(target_dir, "artifacts")
    for artifact_path in artifacts_list:
        if not os.path.isabs(artifact_path):
            bb.warn(f"{artifact_path} is not an absolute path")
            continue
        try:
            dest_dir = os.path.join(local_artifacts_dir, os.path.dirname(artifact_path[1:]))
            os.makedirs(dest_dir, exist_ok=True)
            target.copyFrom(artifact_path, dest_dir)
        except Exception as e:
            bb.warn(f"Can not retrieve {artifact_path} from test target: {e}")

def list_and_fetch_failed_tests_artifacts(d, tc):
    artifacts_list = get_artifacts_list(tc.target, d.getVar("TESTIMAGE_FAILED_QA_ARTIFACTS"))
    if not artifacts_list:
        bb.warn("Could not load artifacts list, skip artifacts retrieval")
    else:
        retrieve_test_artifacts(tc.target, artifacts_list, get_json_result_dir(d))


##################################################################
# General post actions runner
##################################################################

def run_failed_tests_post_actions(d, tc):
    post_actions=[
        create_artifacts_directory,
        list_and_fetch_failed_tests_artifacts,
        get_target_disk_usage,
        get_host_disk_usage
    ]

    for action in post_actions:
        action(d, tc)
