
import os
import subprocess
import bb.process

def detect_revision(d):
    path = get_scmbasepath(d)
    return get_metadata_git_revision(path)

def detect_branch(d):
    path = get_scmbasepath(d)
    return get_metadata_git_branch(path)

def get_scmbasepath(d):
    return os.path.join(d.getVar('COREBASE'), 'meta')

def get_metadata_git_branch(path):
    try:
        rev, _ = bb.process.run('git rev-parse --abbrev-ref HEAD', cwd=path)
    except bb.process.ExecutionError:
        rev = '<unknown>'
    return rev.strip()

def get_metadata_git_revision(path):
    try:
        rev, _ = bb.process.run('git rev-parse HEAD', cwd=path)
    except bb.process.ExecutionError:
        rev = '<unknown>'
    return rev.strip()

def get_metadata_git_toplevel(path):
    try:
        toplevel, _ = bb.process.run('git rev-parse --show-toplevel', cwd=path)
    except bb.process.ExecutionError:
        return ""
    return toplevel.strip()

def get_metadata_git_remotes(path):
    try:
        remotes_list, _ = bb.process.run('git remote', cwd=path)
        remotes = remotes_list.split()
    except bb.process.ExecutionError:
        remotes = []
    return remotes

def get_metadata_git_remote_url(path, remote):
    try:
        uri, _ = bb.process.run('git remote get-url {remote}'.format(remote=remote), cwd=path)
    except bb.process.ExecutionError:
        return ""
    return uri.strip()

def get_metadata_git_describe(path):
    try:
        describe, _ = bb.process.run('git describe --tags', cwd=path)
    except bb.process.ExecutionError:
        return ""
    return describe.strip()

def is_layer_modified(path):
    try:
        subprocess.check_output("""cd %s; export PSEUDO_UNLOAD=1; set -e;
                                git diff --quiet --no-ext-diff
                                git diff --quiet --no-ext-diff --cached""" % path,
                                shell=True,
                                stderr=subprocess.STDOUT)
        return ""
    except subprocess.CalledProcessError as ex:
        # Silently treat errors as "modified", without checking for the
        # (expected) return code 1 in a modified git repo. For example, we get
        # output and a 129 return code when a layer isn't a git repo at all.
        return " -- modified"

def get_layer_revisions(d):
    layers = (d.getVar("BBLAYERS") or "").split()
    revisions = []
    for i in layers:
        revisions.append((i, os.path.basename(i), get_metadata_git_branch(i).strip(), get_metadata_git_revision(i), is_layer_modified(i)))
    return revisions
