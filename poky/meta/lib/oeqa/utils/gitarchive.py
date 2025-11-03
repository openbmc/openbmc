#
# Helper functions for committing data to git and pushing upstream
#
# Copyright (c) 2017, Intel Corporation.
# Copyright (c) 2019, Linux Foundation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import os
import re
import sys
from operator import attrgetter
from collections import namedtuple
from oeqa.utils.git import GitRepo, GitError

class ArchiveError(Exception):
    """Internal error handling of this script"""

def format_str(string, fields):
    """Format string using the given fields (dict)"""
    try:
        return string.format(**fields)
    except KeyError as err:
        raise ArchiveError("Unable to expand string '{}': unknown field {} "
                           "(valid fields are: {})".format(
                               string, err, ', '.join(sorted(fields.keys()))))


def init_git_repo(path, no_create, bare, log):
    """Initialize local Git repository"""
    path = os.path.abspath(path)
    if os.path.isfile(path):
        raise ArchiveError("Invalid Git repo at {}: path exists but is not a "
                           "directory".format(path))
    if not os.path.isdir(path) or not os.listdir(path):
        if no_create:
            raise ArchiveError("No git repo at {}, refusing to create "
                               "one".format(path))
        if not os.path.isdir(path):
            try:
                os.mkdir(path)
            except (FileNotFoundError, PermissionError) as err:
                raise ArchiveError("Failed to mkdir {}: {}".format(path, err))
        if not os.listdir(path):
            log.info("Initializing a new Git repo at %s", path)
            repo = GitRepo.init(path, bare)
    try:
        repo = GitRepo(path, is_topdir=True)
    except GitError:
        raise ArchiveError("Non-empty directory that is not a Git repository "
                           "at {}\nPlease specify an existing Git repository, "
                           "an empty directory or a non-existing directory "
                           "path.".format(path))
    return repo


def git_commit_data(repo, data_dir, branch, message, exclude, notes, log):
    """Commit data into a Git repository"""
    log.info("Committing data into to branch %s", branch)
    tmp_index = os.path.join(repo.git_dir, 'index.oe-git-archive')
    try:
        # Create new tree object from the data
        env_update = {'GIT_INDEX_FILE': tmp_index,
                      'GIT_WORK_TREE': os.path.abspath(data_dir)}
        repo.run_cmd('add .', env_update)

        # Remove files that are excluded
        if exclude:
            repo.run_cmd(['rm', '--cached', '--ignore-unmatch'] + [f for f in exclude], env_update)

        tree = repo.run_cmd('write-tree', env_update)

        # Create new commit object from the tree
        parent = repo.rev_parse(branch)
        if not parent:
            parent = repo.rev_parse("origin/" + branch)
        git_cmd = ['commit-tree', tree, '-m', message]
        if parent:
            git_cmd += ['-p', parent]
        commit = repo.run_cmd(git_cmd, env_update)

        # Create git notes
        for ref, filename in notes:
            ref = ref.format(branch_name=branch)
            repo.run_cmd(['notes', '--ref', ref, 'add',
                          '-F', os.path.abspath(filename), commit])

        # Update branch head
        git_cmd = ['update-ref', 'refs/heads/' + branch, commit]
        repo.run_cmd(git_cmd)

        # Update current HEAD, if we're on branch 'branch'
        if not repo.bare and repo.get_current_branch() == branch:
            log.info("Updating %s HEAD to latest commit", repo.top_dir)
            repo.run_cmd('reset --hard')

        return commit
    finally:
        if os.path.exists(tmp_index):
            os.unlink(tmp_index)

def get_tags(repo, log, pattern=None, url=None):
    """ Fetch remote tags from current repository

    A pattern can be provided to filter returned tags list
    An URL can be provided if local repository has no valid remote configured
    """

    base_cmd = ['ls-remote', '--refs', '--tags', '-q']
    cmd = base_cmd.copy()

    # First try to fetch tags from repository configured remote
    cmd.append('origin')
    if pattern:
        cmd.append("refs/tags/"+pattern)
    try:
        tags_refs = repo.run_cmd(cmd)
        tags = ["".join(d.split()[1].split('/', 2)[2:]) for d in tags_refs.splitlines()]
    except GitError as e:
        # If it fails, retry with repository url if one is provided
        if url:
            log.info("No remote repository configured, use provided url")
            cmd = base_cmd.copy()
            cmd.append(url)
            if pattern:
                cmd.append(pattern)
            tags_refs = repo.run_cmd(cmd)
            tags = ["".join(d.split()[1].split('/', 2)[2:]) for d in tags_refs.splitlines()]
        else:
            log.info("Read local tags only, some remote tags may be missed")
            cmd = ["tag"]
            if pattern:
                cmd += ["-l", pattern]
            tags = repo.run_cmd(cmd).splitlines()

    return tags

def expand_tag_strings(repo, name_pattern, msg_subj_pattern, msg_body_pattern,
                       url, log, keywords):
    """Generate tag name and message, with support for running id number"""
    keyws = keywords.copy()
    # Tag number is handled specially: if not defined, we autoincrement it
    if 'tag_number' not in keyws:
        # Fill in all other fields than 'tag_number'
        keyws['tag_number'] = '{tag_number}'
        tag_re = format_str(name_pattern, keyws)
        # Replace parentheses for proper regex matching
        tag_re = tag_re.replace('(', '\(').replace(')', '\)') + '$'
        # Inject regex group pattern for 'tag_number'
        tag_re = tag_re.format(tag_number='(?P<tag_number>[0-9]{1,5})')

        keyws['tag_number'] = 0
        for existing_tag in get_tags(repo, log, url=url):
            match = re.match(tag_re, existing_tag)

            if match and int(match.group('tag_number')) >= keyws['tag_number']:
                keyws['tag_number'] = int(match.group('tag_number')) + 1

    tag_name = format_str(name_pattern, keyws)
    msg_subj= format_str(msg_subj_pattern.strip(), keyws)
    msg_body = format_str(msg_body_pattern, keyws)
    return tag_name, msg_subj + '\n\n' + msg_body

def gitarchive(data_dir, git_dir, no_create, bare, commit_msg_subject, commit_msg_body, branch_name, no_tag, tagname, tag_msg_subject, tag_msg_body, exclude, notes, push, keywords, log):

    if not os.path.isdir(data_dir):
        raise ArchiveError("Not a directory: {}".format(data_dir))

    data_repo = init_git_repo(git_dir, no_create, bare, log)

    # Expand strings early in order to avoid getting into inconsistent
    # state (e.g. no tag even if data was committed)
    commit_msg = format_str(commit_msg_subject.strip(), keywords)
    commit_msg += '\n\n' + format_str(commit_msg_body, keywords)
    branch_name = format_str(branch_name, keywords)
    tag_name = None
    if not no_tag and tagname:
        tag_name, tag_msg = expand_tag_strings(data_repo, tagname,
                                               tag_msg_subject,
                                               tag_msg_body,
                                               push, log, keywords)

    # Commit data
    commit = git_commit_data(data_repo, data_dir, branch_name,
                             commit_msg, exclude, notes, log)

    # Create tag
    if tag_name:
        log.info("Creating tag %s", tag_name)
        data_repo.run_cmd(['tag', '-a', '-m', tag_msg, tag_name, commit])

    # Push data to remote
    if push:
        cmd = ['push', '--tags']
        # If no remote is given we push with the default settings from
        # gitconfig
        if push is not True:
            notes_refs = ['refs/notes/' + ref.format(branch_name=branch_name)
                           for ref, _ in notes]
            cmd.extend([push, branch_name] + notes_refs)
        log.info("Pushing data to remote")
        data_repo.run_cmd(cmd)

    return tag_name

# Container class for tester revisions
TestedRev = namedtuple('TestedRev', 'commit commit_number tags')

def get_test_runs(log, repo, tag_name, **kwargs):
    """Get a sorted list of test runs, matching given pattern"""
    # First, get field names from the tag name pattern
    field_names = [m.group(1) for m in re.finditer(r'{(\w+)}', tag_name)]
    undef_fields = [f for f in field_names if f not in kwargs.keys()]

    # Fields for formatting tag name pattern
    str_fields = dict([(f, '*') for f in field_names])
    str_fields.update(kwargs)

    # Get a list of all matching tags
    tag_pattern = tag_name.format(**str_fields)
    tags = get_tags(repo, log, pattern=tag_pattern)
    log.debug("Found %d tags matching pattern '%s'", len(tags), tag_pattern)

    # Parse undefined fields from tag names
    str_fields = dict([(f, r'(?P<{}>[\w\-.()]+)'.format(f)) for f in field_names])
    str_fields['branch'] = r'(?P<branch>[\w\-.()/]+)'
    str_fields['commit'] = '(?P<commit>[0-9a-f]{7,40})'
    str_fields['commit_number'] = '(?P<commit_number>[0-9]{1,7})'
    str_fields['tag_number'] = '(?P<tag_number>[0-9]{1,5})'
    # escape parenthesis in fields in order to not messa up the regexp
    fixed_fields = dict([(k, v.replace('(', r'\(').replace(')', r'\)')) for k, v in kwargs.items()])
    str_fields.update(fixed_fields)
    tag_re = re.compile(tag_name.format(**str_fields))

    # Parse fields from tags
    revs = []
    for tag in tags:
        m = tag_re.match(tag)
        if not m:
            continue
        groups = m.groupdict()
        revs.append([groups[f] for f in undef_fields] + [tag])

    # Return field names and a sorted list of revs
    return undef_fields, sorted(revs)

def get_test_revs(log, repo, tag_name, **kwargs):
    """Get list of all tested revisions"""
    fields, runs = get_test_runs(log, repo, tag_name, **kwargs)

    revs = {}
    commit_i = fields.index('commit')
    commit_num_i = fields.index('commit_number')
    for run in runs:
        commit = run[commit_i]
        commit_num = run[commit_num_i]
        tag = run[-1]
        if not commit in revs:
            revs[commit] = TestedRev(commit, commit_num, [tag])
        else:
            if commit_num != revs[commit].commit_number:
                # Historically we have incorrect commit counts of '1' in the repo so fix these up
                if int(revs[commit].commit_number) < 5:
                    tags = revs[commit].tags
                    revs[commit] = TestedRev(commit, commit_num, [tags])
                elif int(commit_num) < 5:
                    pass
                else:
                    sys.exit("Commit numbers for commit %s don't match (%s vs %s)" % (commit, commit_num, revs[commit].commit_number))
            revs[commit].tags.append(tag)

    # Return in sorted table
    revs = sorted(revs.values(), key=attrgetter('commit_number'))
    log.debug("Found %d tested revisions:\n    %s", len(revs),
              "\n    ".join(['{} ({})'.format(rev.commit_number, rev.commit) for rev in revs]))
    return revs

def rev_find(revs, attr, val):
    """Search from a list of TestedRev"""
    for i, rev in enumerate(revs):
        if getattr(rev, attr) == val:
            return i
    raise ValueError("Unable to find '{}' value '{}'".format(attr, val))

